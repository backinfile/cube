package com.backinfile.cube.controller;

import java.util.ArrayList;
import java.util.List;

import com.backinfile.cube.Res;
import com.backinfile.cube.model.MapData;
import com.backinfile.cube.model.Movements;
import com.backinfile.cube.model.Position;
import com.backinfile.cube.model.WorldData;
import com.backinfile.cube.model.cubes.Cube;
import com.backinfile.cube.model.cubes.Human;
import com.backinfile.cube.model.cubes.Key;
import com.backinfile.cube.model.cubes.MapCube;
import com.backinfile.cube.model.cubes.Wall;
import com.backinfile.cube.support.ActionUtils;
import com.backinfile.cube.view.CubeView;
import com.backinfile.cube.view.CubeViewGroup;
import com.backinfile.cube.view.WorldStage;

public class GameViewManager {
	public static final GameViewManager instance = new GameViewManager();

	private List<String> visiableMapCoor = new ArrayList<>();

	public void staticSetView() {
		visiableMapCoor.clear();
		WorldData worldData = GameManager.instance.worldData;
		WorldStage worldStage = GameManager.instance.worldStage;
		MapData curMapData = worldData.getHumanMapData();

		// 重置group
		for (MapData mapData : worldData.getMapDatas()) {
			CubeViewGroup cubeGroup = worldStage.getCubeGroup(mapData.coor);
			cubeGroup.setLayer(0);
			cubeGroup.setVisible(false);
		}

		// 绘制所有方块
		{
			MapData upperMapdata = curMapData.preMapData;
			if (upperMapdata != null) {
				MapData upperupperMapdata = upperMapdata.preMapData;
				if (upperupperMapdata != null) {
					staticSetView(upperupperMapdata.coor, 0, 0, upperupperMapdata.width * Res.CUBE_SIZE * 3,
							upperupperMapdata.height * Res.CUBE_SIZE * 3, 1f, 0);
				} else {
					staticSetView(upperMapdata.coor, 0, 0, upperMapdata.width * Res.CUBE_SIZE * 2,
							upperMapdata.height * Res.CUBE_SIZE * 2, 1f, 0);
				}
			} else {
				staticSetView(curMapData.coor, 0, 0, curMapData.width * Res.CUBE_SIZE,
						curMapData.height * Res.CUBE_SIZE, 1f, 0);
			}
		}

		// 调整group绘制次序
		worldStage.updateCubeGroupLayer();

		// 调整摄像机
	}

	private void staticSetView(String coor, float x, float y, float width, float height, float alpha, int layer) {
//		Log.game.info("show coor:{} x:{} y:{} width:{} height:{}", coor, x, y, width, height);
		WorldData worldData = GameManager.instance.worldData;
		WorldStage worldStage = GameManager.instance.worldStage;

		MapData mapData = worldData.getMapData(coor);
		CubeViewGroup group = worldStage.getCubeGroup(coor);

		// alpha小于1的要显示为地板
		group.setLayer(layer - (alpha < 1f ? 1 : 0));
		group.setVisible(true);
		group.savePos(x, y);
		group.setSize(width, height);

		float cubeWidth = width / mapData.width;
		float cubeHeight = height / mapData.height;
		for (CubeView cubeView : worldStage.getCubeViews(coor)) {
			Cube cube = cubeView.getCube();
			float targetX = cube.position.x * cubeWidth + x;
			float targetY = cube.position.y * cubeHeight + y;

			cubeView.setPosition(targetX, targetY);
			cubeView.setSize(cubeWidth, cubeHeight);
			cubeView.setAlpha(alpha);
			if (cube instanceof MapCube) {
				MapCube mapCube = (MapCube) cube;
				cubeView.setMainImageVisible(mapCube.isFitKey());
				float nextAlpha = (alpha < 1f || cube instanceof Key) ? Res.FLOOR_ELE_ALPHA : 1f;
				staticSetView(mapCube.getTargetCoor(), x + cube.position.x * cubeWidth,
						y + cube.position.y * cubeHeight, cubeWidth, cubeHeight, nextAlpha, layer - 2);
			} else if (cube instanceof Wall) {
				List<Integer> adjWallDirections = GameManager.instance.getAdjWallDirections(cube.position);
				cubeView.setAdjWallDirections(adjWallDirections);
			} else if (cube instanceof Human) {
				cubeView.setHumanEyeOffset(GameManager.instance.lastHumanMove.x * cubeWidth / 10,
						GameManager.instance.lastHumanMove.y * cubeHeight / 10);
				cubeView.setSize(cubeWidth, cubeHeight);
			}
		}
	}

	public void doMoveCubes(Movements movements) {

	}

	public void moveCameraAuto() {

	}
}
