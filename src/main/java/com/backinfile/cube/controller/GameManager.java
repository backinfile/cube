package com.backinfile.cube.controller;

import java.util.LinkedList;

import com.backinfile.cube.Res;
import com.backinfile.cube.model.History;
import com.backinfile.cube.model.MapData;
import com.backinfile.cube.model.Vector;
import com.backinfile.cube.model.WorldData;
import com.backinfile.cube.model.cubes.Cube;
import com.backinfile.cube.model.cubes.Human;
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

	private static final int[] dx = new int[] { 0, 0, -1, 1 };
	private static final int[] dy = new int[] { 1, -1, 0, 0 };

	public void init() {
		// 解析配置数据
		worldData = WorldData.parse(Res.getDefaultWorldConf());

		// 初始化human位置
		MapData firstMapData = worldData.getHumanMapData();
		curWorldCoor = firstMapData.coor;
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
			history.playback();
		}
	}

	public void moveHuman(int dura) {
		Vector d = new Vector(dx[dura], dy[dura]);

		human.position.move(d);

		updateGameView();
	}

	public WorldData getWorldData() {
		return worldData;
	}

	public Vector getWorldPixelSize() {
		MapData curMapData = getCurMap();
		return new Vector(Res.CUBE_SIZE * curMapData.width, Res.CUBE_SIZE * curMapData.height);
	}

	public void updateGameView() {
		MapData curMap = getCurMap();
		// 设置主视图居中
		Group mainView = worldStage.getMainView();
		int width = Res.CUBE_SIZE * curMap.width;
		int height = Res.CUBE_SIZE * curMap.height;
		mainView.setSize(width, height);
		mainView.setPosition((worldStage.getWidth() - width) / 2, (worldStage.getHeight() - height) / 2);

		// 调整方块位置大小
		for (CubeView cubeView : worldStage.getCubeViews()) {
			Cube cube = cubeView.getCube();
			if (cube.position.worldCoor.equals(curWorldCoor)) {
				cubeView.setPosition(Res.CUBE_SIZE * cube.position.x, Res.CUBE_SIZE * cube.position.y);
				cubeView.setSize(Res.CUBE_SIZE, Res.CUBE_SIZE);
				cubeView.setVisible(true);
			} else {
				cubeView.setVisible(false);
			}
		}
	}

	public MapData getCurMap() {
		return worldData.getMapData(curWorldCoor);
	}

}
