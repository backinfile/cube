package com.backinfile.cube.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.backinfile.cube.Log;
import com.backinfile.cube.Res;
import com.backinfile.cube.model.History;
import com.backinfile.cube.model.History.Movement;
import com.backinfile.cube.model.MapData;
import com.backinfile.cube.model.Position;
import com.backinfile.cube.model.Vector;
import com.backinfile.cube.model.WorldData;
import com.backinfile.cube.model.cubes.Cube;
import com.backinfile.cube.model.cubes.Human;
import com.backinfile.cube.model.cubes.Key;
import com.backinfile.cube.model.cubes.MapCube;
import com.backinfile.cube.model.cubes.Wall;
import com.backinfile.cube.support.TimerQueue;
import com.backinfile.cube.support.Utils;
import com.backinfile.cube.view.WorldStage;

public class GameManager {
	public static final GameManager instance = new GameManager();

	public WorldData worldData;
	public WorldStage worldStage;
	public TimerQueue timerQueue;

	private Human human;
	private LinkedList<History> histories = new LinkedList<History>();
	public Vector lastHumanMove = new Vector();
	public boolean enableController = true;

	public static final int[] dx = new int[] { 0, 0, -1, 1 };
	public static final int[] dy = new int[] { 1, -1, 0, 0 };
	public static final int[] dx8 = new int[] { 0, 0, -1, 1, -1, 1, -1, 1 };
	public static final int[] dy8 = new int[] { 1, -1, 0, 0, -1, 1, 1, -1 };

	public void init() {
		// 解析配置数据
		worldData = WorldData.parse(Res.DefaultWorldConfString);

		// 初始化human位置
		MapData firstMapData = worldData.getHumanMapData();
		for (Cube cube : firstMapData.cubeMap.getUnitList()) {
			if (cube instanceof Human) {
				human = (Human) cube;
				break;
			}
		}
	}

	public void resetGame() {
		human.resetPosition();
	}

	public void undo() {
		if (!histories.isEmpty()) {
			History history = histories.pollLast();
//			history.playback();
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
			Position translated = position.getTranslated(dx8[i], dy8[i]);
			if (mapData.cubeMap.inMap(translated.x, translated.y)) {
				Cube sideCube = mapData.cubeMap.get(translated);
				if (sideCube != null && sideCube instanceof Wall) {
					directions.add(i);
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
		doMovePosition(movements);
	}

	private void doMovePosition(List<Movement> movements) {
		List<Cube> movedCubes = new ArrayList<>();
		for (Movement movement : movements) {
			movedCubes.add(movement.cube);
		}

		// 先记录下移动之前的情况
		History history = History.getHistory(movedCubes);
		histories.addLast(history);

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
		}
		// 检查解锁
		checkFitKey();

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
					Position edgePos = getEdgePos(mapCube.getTargetCoor(), d);
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
//		for (int i = posList.size() - 1; i >= 0; i--) {
		for (int i = 0; i < posList.size(); i++) {
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

	// 查找地图边界上的空位
	private Position getEdgePos(String worldCoor, Vector d) {
		MapData mapData = worldData.getMapData(worldCoor);
		Position position = new Position();
		position.worldCoor = worldCoor;
		if (d.x == 0) {
			// 先找空格
			for (int x = 0; x < mapData.width; x++) {
				position.setPosition(x, d.y < 0 ? mapData.height - 1 : 0);
				if (mapData.cubeMap.get(position) == null) {
					return position;
				}
			}
			// 再找可以被推开的
			for (int x = 0; x < mapData.width; x++) {
				position.setPosition(x, d.y < 0 ? mapData.height - 1 : 0);
				Cube cube = mapData.cubeMap.get(position);
				if (cube != null && cube.isPushable()) {
					return cube.position;
				}
			}
		} else {
			for (int y = 0; y < mapData.width; y++) {
				position.setPosition(d.x < 0 ? mapData.height - 1 : 0, y);
				if (mapData.cubeMap.get(position) == null) {
					return position;
				}
			}
			for (int y = 0; y < mapData.width; y++) {
				position.setPosition(d.x < 0 ? mapData.height - 1 : 0, y);
				Cube cube = mapData.cubeMap.get(position);
				if (cube != null && cube.isPushable()) {
					return cube.position;
				}
			}
		}
		return null;
	}

	public void checkFitKey() {
		for (MapData mapData : worldData.getMapDatas()) {
			for (Cube cube : mapData.cubeMap.getUnitList()) {
				if (cube instanceof MapCube) {
					((MapCube) cube).setFitKey(false);
				}
			}
		}

		for (MapData mapData : worldData.getMapDatas()) {
			for (Cube cube : mapData.cubeMap.getUnitList()) {
				if (cube instanceof Key) {
					for (Cube matchCube : mapData.cubeMap.getAll(cube.position.x, cube.position.y)) {
						if (matchCube == cube) {
							continue;
						}
						if (!(matchCube instanceof MapCube)) {
							continue;
						}
						if (matchCube instanceof Key) {
							continue;
						}
						MapData keyMapData = worldData.getMapData(((Key) cube).getTargetCoor());
						MapData cubeMapData = worldData.getMapData(((MapCube) matchCube).getTargetCoor());
						if (keyMapData.isMatchWith(cubeMapData)) {
							((MapCube) matchCube).setFitKey(true);
						}
					}

				}
			}
		}
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
