package com.backinfile.cube.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.backinfile.cube.Log;
import com.backinfile.cube.Res;
import com.backinfile.cube.model.History;
import com.backinfile.cube.model.History.Movement;
import com.backinfile.cube.model.MapCube;
import com.backinfile.cube.model.MapData;
import com.backinfile.cube.model.Position;
import com.backinfile.cube.model.Vector;
import com.backinfile.cube.model.WorldData;
import com.backinfile.cube.model.cubes.Cube;
import com.backinfile.cube.model.cubes.Human;
import com.backinfile.cube.model.cubes.Wall;
import com.backinfile.cube.support.Utils;
import com.backinfile.cube.view.CubeView;
import com.backinfile.cube.view.WorldStage;
import com.badlogic.gdx.scenes.scene2d.Group;

public class GameManager {
	public static final GameManager instance = new GameManager();

	public WorldData worldData;
	public WorldStage worldStage;

	public String curWorldCoor;
	private Human human;
	private LinkedList<History> histories = new LinkedList<History>();
	private Vector lastHumanMove = new Vector();

	private static final int[] dx = new int[] { 0, 0, -1, 1 };
	private static final int[] dy = new int[] { 1, -1, 0, 0 };

	public void init() {
		// 解析配置数据
		worldData = WorldData.parse(Res.DefaultWorldConfString);

		// 初始化human位置
		MapData firstMapData = worldData.getHumanMapData();
		curWorldCoor = "";
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
		MapData curMapData = getCurMap();
		return new Vector(Res.CUBE_SIZE * curMapData.width, Res.CUBE_SIZE * curMapData.height);
	}

	public void updateGameView(History history) {
		// 调整方块到合适的group
		for (Movement movement : history.getMovements()) {
			if (!movement.position.worldCoor.equals(movement.cube.position.worldCoor)) {
				CubeView cubeView = worldStage.removeCubeView(movement.position.worldCoor, movement.cube);
				worldStage.addCubeView(movement.cube.position.worldCoor, cubeView);
			}
		}
		updateGameView();
	}

	private String lastWorldCoor;

	public void updateGameView() {
		// 设置当前所在地图
		lastWorldCoor = curWorldCoor;
		MapData humanMapData = worldData.getHumanMapData();
		if (humanMapData.view.length() > 0) {
			curWorldCoor = humanMapData.view;
		} else {
			curWorldCoor = humanMapData.coor;
		}
		MapData curMap = worldData.getMapData(curWorldCoor);
		if (!lastWorldCoor.equals(curWorldCoor)) {
			worldStage.setTipText(curMap.tipText.length() > 0, curMap.tipText);
		}

		// 设置主视图居中
		Group mainView = worldStage.getMainView();
		int width = Res.CUBE_SIZE * curMap.width;
		int height = Res.CUBE_SIZE * curMap.height;
		mainView.setSize(width, height);
		mainView.setPosition((worldStage.getWidth() - width) / 2, (worldStage.getHeight() - height) / 2);

		// 先隐藏所有方块
		for (MapData mapData : worldData.getDatas()) {
			Group group = worldStage.getCubeGroup(mapData.coor);
			group.setVisible(false);
		}
		// 显示需要显示的方块
		updateCubeGroupView(curWorldCoor, 0, 0, curMap.width * Res.CUBE_SIZE, curMap.height * Res.CUBE_SIZE);
	}

	private void updateCubeGroupView(String coor, float x, float y, float width, float height) {
		if (coor.length() > curWorldCoor.length() + 2) {
			return;
		}
//		Log.game.info("show coor:{} x:{} y:{} width:{} height:{}", coor, x, y, width, height);
		MapData mapData = worldData.getMapData(coor);
		Group group = worldStage.getCubeGroup(coor);
		group.setSize(width, height);
		group.setPosition(x, y);
		group.setVisible(true);
		float cubeWidth = width / mapData.width;
		float cubeHeight = height / mapData.height;
		for (CubeView cubeView : worldStage.getCubeViews(coor)) {
			Cube cube = cubeView.getCube();
			cubeView.setPosition(cube.position.x * cubeWidth, cube.position.y * cubeHeight);
			cubeView.setSize(cubeWidth, cubeHeight);
			if (cube instanceof MapCube) {
				updateCubeGroupView(((MapCube) cube).getTargetCoor(), x + cube.position.x * cubeWidth,
						y + cube.position.y * cubeHeight, cubeWidth, cubeHeight);
			} else if (cube instanceof Wall) {
				List<Integer> adjWallDirections = getAdjWallDirections(cube.position);
				for (int i = 0; i < 4; i++) {
					cubeView.setAsideBorder(i, adjWallDirections.contains(i));
				}
			} else if (cube instanceof Human) {
				cubeView.setHumanEyeOffset(lastHumanMove.x * cubeWidth / 10, lastHumanMove.y * cubeHeight / 10);
			}
		}

	}

	private List<Integer> getAdjWallDirections(Position position) {
		List<Integer> directions = new ArrayList<Integer>();
		MapData mapData = worldData.getMapData(position.worldCoor);
		for (int i = 0; i < dx.length; i++) {
			Position translated = position.getTranslated(dx[i], dy[i]);
			if (mapData.cubeMap.inMap(translated.x, translated.y)) {
				Cube sideCube = mapData.cubeMap.get(translated);
				if (sideCube != null && sideCube instanceof Wall) {
					directions.add(i);
				}
			}
		}
		return directions;
	}

	public MapData getCurMap() {
		return worldData.getMapData(curWorldCoor);
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
		List<Cube> movedCubes = new ArrayList<>();
		for (int i = 0; i < passPosList.size() - 1; i++) {
			movedCubes.add(getCube(passPosList.get(i)));
		}

		// 先记录下移动之前的情况
		History history = History.getHistory(movedCubes);
		histories.addLast(history);

		// 进行移动
		for (int i = 0; i < passPosList.size() - 1; i++) {
			Position position = passPosList.get(i);
			Position newPosition = passPosList.get(i + 1);
			if (!position.worldCoor.equals(newPosition.worldCoor)) {
				Cube cube = movedCubes.get(i);
				worldData.getMapData(position.worldCoor).cubeMap.remove(cube);
				position.setPosition(newPosition);
				worldData.getMapData(newPosition.worldCoor).cubeMap.add(cube);
			} else {
				position.setPosition(newPosition);
			}
		}

		// 刷新界面
		updateGameView(history);
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
			if (cube != null && cube.isMapCube()) {
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
				for (MapData outMapData : worldData.getDatas()) {
					for (Cube cube : outMapData.cubeMap.getUnitList()) {
						if (cube.isMapCube() && ((MapCube) cube).getTargetCoor().equals(startPosition.worldCoor)) {
							return getNextPos(cube.position, d);
						}
					}
				}
			}
			return null;
		}
		Cube cube = mapData.cubeMap.get(translated.x, translated.y);
		if (cube == null) { // 空白格子
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

	private boolean isPosEmpty(Position position) {
		MapData mapData = worldData.getMapData(position.worldCoor);
		return mapData.cubeMap.get(position.x, position.y) == null;
	}

	private boolean isPosStop(Position position) {
		MapData mapData = worldData.getMapData(position.worldCoor);
		Cube cube = mapData.cubeMap.get(position.x, position.y);
		return cube != null && !cube.isPushable();
	}

	private boolean isPosPushable(Position position) {
		MapData mapData = worldData.getMapData(position.worldCoor);
		Cube cube = mapData.cubeMap.get(position.x, position.y);
		return cube.isPushable();
	}

	private Cube getCube(Position position) {
		MapData mapData = worldData.getMapData(position.worldCoor);
		return mapData.cubeMap.get(position);
	}

}
