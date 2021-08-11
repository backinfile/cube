package com.backinfile.cube.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.backinfile.cube.Log;
import com.backinfile.cube.Res;
import com.backinfile.cube.Settings;
import com.backinfile.cube.model.History;
import com.backinfile.cube.model.History.Movement;
import com.backinfile.cube.model.MapData;
import com.backinfile.cube.model.Position;
import com.backinfile.cube.model.Vector;
import com.backinfile.cube.model.WorldData;
import com.backinfile.cube.model.cubes.Cube;
import com.backinfile.cube.model.cubes.Player;
import com.backinfile.cube.model.cubes.FixedKey;
import com.backinfile.cube.model.cubes.Lock;
import com.backinfile.cube.model.cubes.MapCube;
import com.backinfile.cube.model.cubes.Wall;
import com.backinfile.cube.support.SysException;
import com.backinfile.cube.support.TimerQueue;
import com.backinfile.cube.support.Timing;
import com.backinfile.cube.support.Tuple2;
import com.backinfile.cube.support.Utils;
import com.backinfile.cube.view.UIStage;
import com.backinfile.cube.view.WorldStage;
import com.backinfile.cube.view.animation.ShakeAction;

public class GameManager {
	public static GameManager instance;

	public WorldData worldData;
	public WorldStage worldStage;
	public UIStage uiStage;
	public TimerQueue timerQueue;

	public Player human;
	private LinkedList<History> histories = new LinkedList<History>();
	private Vector lastHumanMove = new Vector();
	public boolean enableController = true;
	private Map<String, Position> firstEnterMapPosition = new HashMap<>();

	public static final int[] dx = new int[] { 0, 0, -1, 1 };
	public static final int[] dy = new int[] { 1, -1, 0, 0 };
	public static final int[] dx8 = new int[] { 0, 0, -1, 1, -1, 1, -1, 1 };
	public static final int[] dy8 = new int[] { 1, -1, 0, 0, -1, 1, 1, -1 };

	@Timing("world data init")
	public void init() {
		// 解析配置数据
		worldData = WorldData.parseFromTiled(Res.DefaultWorldConfStringByTiled);

		// 初始化human位置
		int humanCnt = 0;
		for (MapData mapData : worldData.getMapDatas()) {
			for (Cube cube : mapData.cubeMap.getUnitList()) {
				if (cube instanceof Player) {
					human = (Player) cube;
					humanCnt++;
				}
			}
		}
		if (humanCnt != 1) {
			throw new SysException("humanCnt=" + humanCnt);
		}

		firstEnterMapPosition.clear();
		firstEnterMapPosition.put(human.position.worldCoor, human.originPosition);
		enableController = true;
		histories.clear();

		checkFitKey();
	}

	public void resetGame() {
		if (!Settings.DEV) {
			return;
		}

		init();
		worldStage.clear();
		worldStage.init();
		GameViewManager.instance.staticSetView();
	}

	public void undo() {
		if (!histories.isEmpty()) {
			lastHumanMove.set(0, 0);
			History history = histories.pollLast();
			doMovePosition(history.getMovements(), false);
		}
	}

	public WorldData getWorldData() {
		return worldData;
	}

	public Vector getWorldPixelSize() {
		MapData curMapData = worldData.getHumanMapData();
		return new Vector(Res.CUBE_SIZE * curMapData.width, Res.CUBE_SIZE * curMapData.height);
	}

	public List<Integer> getAdjWallDirections(Position position) {
		List<Integer> directions = new ArrayList<Integer>();
		MapData mapData = worldData.getMapData(position.worldCoor);
		for (int i = 0; i < dx8.length; i++) {
			Vector d = new Vector(dx8[i], dy8[i]);
			Position translated = position.getTranslated(d);
			if (mapData.cubeMap.inMap(translated.x, translated.y)) {
				Cube sideCube = mapData.cubeMap.get(translated);
				if (sideCube != null && sideCube instanceof Wall) {
					directions.add(i);
				}
			} else if (mapData.preCube != null) { // 与相邻方块内部的方块相邻
				MapCube preCube = mapData.preCube;
				MapData preMapData = mapData.preMapData;
				Position nextCubePostion = new Position((translated.x + mapData.width) % mapData.width,
						(translated.y + mapData.height) % mapData.height);
				Vector preD = new Vector();
				{
					if (!mapData.cubeMap.inMap(position.x + (int) d.x, position.y)) {
						preD.x = d.x;
					}
					if (!mapData.cubeMap.inMap(position.x, position.y + (int) d.y)) {
						preD.y = d.y;
					}
				}
				Position preNextCubePosition = preCube.position.getTranslated(preD);
				if (preMapData.cubeMap.inMap(preNextCubePosition.x, preNextCubePosition.y)) {
					Cube preNextCube = preMapData.cubeMap.get(preNextCubePosition);
					if (preNextCube != null && preNextCube instanceof MapCube) {
						MapCube preNextMapCube = (MapCube) preNextCube;
						MapData preNextMapData = worldData.getMapData(preNextMapCube.getTargetCoor());
						if (!preNextMapData.isSameSize(mapData)) {
							continue;
						}
						Cube preNextMapDataInnerCube = preNextMapData.cubeMap.get(nextCubePostion);
						if (preNextMapDataInnerCube != null && preNextMapDataInnerCube instanceof Wall) {
							directions.add(i);
							Log.game.debug("near {} from {} in {}", preNextMapDataInnerCube.position, position,
									preMapData.coor);
						}
					}
				}

			}
		}
		return directions;
	}

	public void moveHuman(int dura) {
		lastHumanMove = new Vector(dx[dura], dy[dura]);
		ArrayList<Position> passPosList = new ArrayList<>();
		passPosList.add(human.position);
		if (testCubeMove(human.position, lastHumanMove, passPosList)) {
			doMovePosition(passPosList);
		} else {
//			worldStage.addAction(new ShakeCameraAction(0.05f, 2, true, true));
			worldStage.getHumanCubeView().addAction(new ShakeAction(0.05f, true, true));
			GameViewManager.instance.moveHumanEye(lastHumanMove, 0.05f);
		}
		Log.game.info("move {} human:{}", lastHumanMove, human.position);
	}

	private void doMovePosition(ArrayList<Position> passPosList) {
		List<Movement> movements = new ArrayList<>();
		for (int i = 0; i < passPosList.size() - 1; i++) {
			Movement movement = new Movement();
			movement.cube = getCube(passPosList.get(i));
			movement.position = new Position(passPosList.get(i + 1));
			movements.add(movement);
		}
		doMovePosition(movements, true);
	}

	private void doMovePosition(List<Movement> movements, boolean record) {
		List<Cube> movedCubes = new ArrayList<>();
		for (Movement movement : movements) {
			movedCubes.add(movement.cube);
		}

		// 先记录下移动之前的情况
		History history = History.getHistory(movedCubes);
		if (record) {
			histories.addLast(history);
		}

		// 记录第一次进入房间的位置
		for (Movement movement : movements) {
			if (movement.cube instanceof Player) {
				if (!firstEnterMapPosition.containsKey(movement.position.worldCoor)) {
					firstEnterMapPosition.put(movement.position.worldCoor, movement.position);
				}
			}
		}

		// 进行移动
		for (Movement movement : movements) {
			Position position = movement.cube.position;
			Position newPosition = movement.position;
			Cube cube = movement.cube;
			if (!position.worldCoor.equals(newPosition.worldCoor)) {
				worldData.getMapData(position.worldCoor).cubeMap.remove(cube);
				cube.moveTo(newPosition);
				worldData.getMapData(newPosition.worldCoor).cubeMap.add(cube);
			} else {
				cube.moveTo(newPosition);
			}
			if (cube instanceof Player) {
				((Player) cube).lastMove.set(lastHumanMove);
			}
		}
		// 检查解锁
		checkFitKey();

		worldData.setupRely();

		// 刷新界面
		GameViewManager.instance.updateCubeView(history);
	}

	private boolean testCubeMove(Position startPosition, Vector d) {
		ArrayList<Position> passPosList = new ArrayList<Position>();
		passPosList.add(startPosition);
		return testCubeMove(startPosition, d, passPosList);
	}

	private boolean testCubeMove(Position startPosition, Vector d, ArrayList<Position> passPosList) {
		Position curPos = startPosition;
		Position nextPos = startPosition;
		for (int loopCnt = 0; loopCnt < 10000; loopCnt++) {
			nextPos = getNextPos(curPos, d);
			// 自我循环了，不能移动
			if (nextPos != null && passPosList.contains(nextPos)) {
				return false;
			}
			// 超出边界或者遇见墙了
			if (nextPos == null || isPosStop(nextPos)) {
				if (nextPos != null && isPosMapCube(nextPos)) {
					passPosList.add(nextPos);
				}
				List<Integer> mapCubePosIndexs = getSplitByMapCubePosList(passPosList);
				// 没有进入方块的可能了，不能移动
				if (mapCubePosIndexs.isEmpty()) {
					return false;
				}
				// 尝试推进方块
				for (int index : mapCubePosIndexs) {
					Position startPosition2 = passPosList.get(index);
					ArrayList<Position> newPassPosList = Utils.subList(passPosList, 0, index);
					MapCube mapCube = (MapCube) getCube(startPosition2);
					Position edgePos = getEdgePos(mapCube.getTargetCoor(), d, newPassPosList.get(index - 1));
					if (edgePos != null) {
						newPassPosList.add(edgePos);
						if (isPosEmpty(edgePos)) { // 方块入口为空，直接移动过去
							passPosList.clear();
							passPosList.addAll(newPassPosList);
							return true;
						} else {
							if (testCubeMove(edgePos, d, newPassPosList)) {
								passPosList.clear();
								passPosList.addAll(newPassPosList);
								return true;
							}
						}
					}
				}
				// 尝试用方块吞噬
				for (int index : mapCubePosIndexs) {
					if (index + 1 >= passPosList.size()) {
						continue;
					}
					Position startPosition2 = passPosList.get(index);
					Position nextPosition2 = passPosList.get(index + 1);
					if (isPosStop(nextPosition2)) {
						continue;
					}
					ArrayList<Position> newPassPosList = Utils.subList(passPosList, 0, index);
					MapCube mapCube = (MapCube) getCube(startPosition2);
					Position edgePos = getEdgePos(mapCube.getTargetCoor(), d.getOppsite(),
							newPassPosList.get(index - 1));
					if (edgePos != null) {
						newPassPosList.add(startPosition2);
						newPassPosList.add(nextPosition2);
						newPassPosList.add(edgePos);
						if (isPosEmpty(edgePos)) { // 方块入口为空，直接移动过去
							passPosList.clear();
							passPosList.addAll(newPassPosList);
							return true;
						} else {
							if (testCubeMove(edgePos, d.getOppsite(), newPassPosList)) {
								passPosList.clear();
								passPosList.addAll(newPassPosList);
								return true;
							}
						}
					}
				}
				return false;
			}
			// 碰到空地了，可以直接移动
			if (isPosEmpty(nextPos)) {
				passPosList.add(nextPos);
				return true;
			}
			passPosList.add(nextPos);
			curPos = nextPos;
		}
		return false;
	}

	private List<Integer> getSplitByMapCubePosList(List<Position> posList) {
		List<Integer> indexList = new ArrayList<>();
		for (int i = posList.size() - 1; i >= 0; i--) {
//		for (int i = 0; i < posList.size(); i++) {
			Cube cube = getCube(posList.get(i));
			if (cube != null && cube instanceof MapCube) {
				indexList.add(i);
			}
		}
		return indexList;
	}

	/**
	 * 获取下一个位置
	 * 
	 * @param startPosition
	 * @param d
	 * @param forceEnter    是否强制进入小方块
	 * @return
	 */
	private Position getNextPos(Position startPosition, Vector d) {
		Position translated = startPosition.getTranslated(d);
		MapData mapData = worldData.getMapData(translated.worldCoor);
		if (!mapData.cubeMap.inMap(translated.x, translated.y)) {
			// 可能在外层地图，尝试出去
			int length = startPosition.worldCoor.length();
			if (length > 1) {
				for (MapData outMapData : worldData.getMapDatas()) {
					for (Cube cube : outMapData.cubeMap.getUnitList()) {
						if (cube.canEnter() && ((MapCube) cube).getTargetCoor().equals(startPosition.worldCoor)) {
							return getNextPos(cube.position, d);
						}
					}
				}
			}
			return null;
		}
		Cube cube = mapData.cubeMap.get(translated.x, translated.y);
		if (cube == null || cube.isEmpty()) { // 空白格子
			return translated;
		}
		return cube.position;
	}

	private List<Position> getAllEdgePos(String worldCoor, Vector d) {
		MapData mapData = worldData.getMapData(worldCoor);
		Queue<Position> toFindEdgeQueue = new LinkedList<>();
		List<Position> edgePositions = new ArrayList<>();

		if (d.x == 0) { // 向上或向下
			for (int x = 0; x < mapData.width; x++) {
				toFindEdgeQueue.add(new Position(x, d.y < 0 ? mapData.height - 1 : 0, worldCoor));
			}
		} else { // 向左或向右
			for (int y = 0; y < mapData.height; y++) {
				toFindEdgeQueue.add(new Position(d.x < 0 ? mapData.width - 1 : 0, y, worldCoor));
			}
		}

		while (!toFindEdgeQueue.isEmpty()) {
			Position pos = toFindEdgeQueue.poll();
			Cube cube = getCube(pos);
			if (cube == null) {
				edgePositions.add(pos);
				continue;
			}
			if (cube.isEmpty() || cube.isPushable()) {
				edgePositions.add(cube.position);
				continue;
			}
			if (cube instanceof MapCube) {
				List<Position> inner = getAllEdgePos(((MapCube) cube).getTargetCoor(), d);
				toFindEdgeQueue.addAll(inner);
			}
		}

		return edgePositions;
	}

	// 查找地图边界上的空位
	private Position getEdgePos(String worldCoor, Vector d, Position lastPosition) {
		List<Position> posiblePosition = getAllEdgePos(worldCoor, d);
		Log.game.debug("posiblePosition={} lastPosition={}", posiblePosition, lastPosition);
		for (Position pos : new ArrayList<>(posiblePosition)) {
			if (!lastPosition.worldCoor.equals(pos.worldCoor)) {
				MapData lastMapData = worldData.getMapData(lastPosition.worldCoor);
				MapData thisMapData = worldData.getMapData(pos.worldCoor);
				if (lastMapData.preMapData != null && lastMapData.preMapData == thisMapData.preMapData) {
					// 相同层次的方块只有size相同才允许进入
					if (!lastMapData.isSameSize(thisMapData)) {
						posiblePosition.remove(pos);
						continue;
					}
					if (d.x == 0) {
						if (pos.x != lastPosition.x) {
							posiblePosition.remove(pos);
							continue;
						}
					} else {
						if (pos.y != lastPosition.y) {
							posiblePosition.remove(pos);
							continue;
						}
					}
				}
			}
		}
		if (posiblePosition.isEmpty()) {
			return null;
		}

		Position retPostion = posiblePosition.stream().map(pos -> {
			MapData innerMapData = worldData.getMapData(pos.worldCoor);
			if (d.x == 0) {
				return new Tuple2<>(pos, Math.abs((pos.x + 0.5f) / (float) innerMapData.width - 0.5f));
			} else {
				return new Tuple2<>(pos, Math.abs((pos.y + 0.5f) / (float) innerMapData.height - 0.5f));
			}
		}).sorted(new Comparator<Tuple2<Position, Float>>() {
			@Override
			public int compare(Tuple2<Position, Float> o1, Tuple2<Position, Float> o2) {
				return Float.compare(o1.value2, o2.value2);
			}
		}).findFirst().get().value1;

		Cube cube = getCube(retPostion);
		if (cube != null) {
			return cube.position;
		}

		return retPostion;
	}

	public void checkFitKey() {
		// 重置
		for (MapData mapData : worldData.getMapDatas()) {
			for (Cube cube : mapData.cubeMap.getUnitList()) {
				if (cube instanceof MapCube) {
					((MapCube) cube).setFitKey(false);
				} else if (cube instanceof Lock) {
					Lock lock = (Lock) cube;
					// 当开着的开关被占用时，维持开着的状态
					List<Cube> all = mapData.cubeMap.getAll(lock.position.x, lock.position.y);
					if (all.stream().anyMatch(c -> c != lock && !c.isEmpty())) {
						lock.setLocked(false);
					} else {
						lock.setLocked(true);
					}
					if (Settings.OPEN_ALL_LOCK) {
						lock.setLocked(false);
					}
				}
			}
		}

		for (MapData mapData : worldData.getMapDatas()) {
			// 如果所有Key都解锁了，解锁Lock
			if (testMapAllKeyFited(mapData)) {
				for (Cube cube : mapData.cubeMap.getUnitList()) {
					if (cube instanceof Lock) {
						((Lock) cube).setLocked(false);
						Log.game.debug("unLock {}, id:{}", cube, cube.getId());
					}
				}
			}
		}
	}

	private boolean testMapAllKeyFited(MapData mapData) {
		boolean test = true;
		for (Cube cube : mapData.cubeMap.getUnitList()) {
			if (cube instanceof FixedKey) {
				FixedKey fixedKey = (FixedKey) cube;
				for (Cube matchCube : mapData.cubeMap.getAll(cube.position.x, cube.position.y)) {
					if (matchCube == cube) {
						continue;
					}
					if (!(matchCube instanceof MapCube)) {
						continue;
					}
					if (matchCube instanceof FixedKey) {
						continue;
					}
					MapCube matchedMapCube = (MapCube) matchCube;
					MapData keyMapData = worldData.getMapData(fixedKey.getTargetCoor());
					MapData cubeMapData = worldData.getMapData(matchedMapCube.getTargetCoor());
					if (keyMapData.isMatchWith(worldData, cubeMapData)) {
						matchedMapCube.setFitKey(true);
						fixedKey.setFitKey(true);
						break;
					}
				}
				if (!fixedKey.isFitKey()) {
					test = false;
				}
			} else if (cube instanceof MapCube) {
				MapData targetMapData = worldData.getMapData(((MapCube) cube).getTargetCoor());
				if (!testMapAllKeyFited(targetMapData)) {
					test = false;
				}
			}
		}
		return test;
	}

	private boolean isPosEmpty(Position position) {
		MapData mapData = worldData.getMapData(position.worldCoor);
		Cube cube = mapData.cubeMap.get(position.x, position.y);
		return cube == null || cube.isEmpty();
	}

	private boolean isPosStop(Position position) {
		MapData mapData = worldData.getMapData(position.worldCoor);
		Cube cube = mapData.cubeMap.get(position.x, position.y);
		return cube != null && !cube.isEmpty() && !cube.isPushable();
	}

	private boolean isPosMapCube(Position position) {
		MapData mapData = worldData.getMapData(position.worldCoor);
		Cube cube = mapData.cubeMap.get(position.x, position.y);
		return cube instanceof MapCube;
	}

	private boolean isPosPushable(Position position) {
		MapData mapData = worldData.getMapData(position.worldCoor);
		Cube cube = mapData.cubeMap.get(position.x, position.y);
		return cube != null && !cube.isEmpty() && cube.isPushable();
	}

	private Cube getCube(Position position) {
		MapData mapData = worldData.getMapData(position.worldCoor);
		return mapData.cubeMap.get(position);
	}

}
