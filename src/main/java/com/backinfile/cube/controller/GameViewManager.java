package com.backinfile.cube.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.backinfile.cube.Log;
import com.backinfile.cube.Res;
import com.backinfile.cube.model.History;
import com.backinfile.cube.model.MapData;
import com.backinfile.cube.model.Movements;
import com.backinfile.cube.model.Vector;
import com.backinfile.cube.model.WorldData;
import com.backinfile.cube.model.History.Movement;
import com.backinfile.cube.model.cubes.Cube;
import com.backinfile.cube.model.cubes.Human;
import com.backinfile.cube.model.cubes.Key;
import com.backinfile.cube.model.cubes.MapCube;
import com.backinfile.cube.model.cubes.Wall;
import com.backinfile.cube.support.ActionUtils;
import com.backinfile.cube.support.Time2;
import com.backinfile.cube.view.CubeView;
import com.backinfile.cube.view.CubeViewGroup;
import com.backinfile.cube.view.WorldStage;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameViewManager {
	public static final GameViewManager instance = new GameViewManager();

	private static final float MainViewScale = 1.2f;
	private static final float ANI_DURATION = 0.1f;
	private Set<CubeView> lastInView = new HashSet<>();

	public void updateCubeView(History history) {
		if (history != null) {
			adjustCubeViewOwnGroup(history);
		}
		WorldStage worldStage = GameManager.instance.worldStage;

		// 先通过动画移动到确定位置
		for (Movement movement : history.getMovements()) {
			Cube cube = movement.cube;
			CubeViewGroup cubeGroup = worldStage.getCubeGroup(cube.position.worldCoor);
			float cubeWidth = cubeGroup.getWidth() / cubeGroup.getMapData().width;
			float cubeHeight = cubeGroup.getHeight() / cubeGroup.getMapData().height;
			float targetX = cube.position.x * cubeWidth + cubeGroup.getSavePos().x;
			float targetY = cube.position.y * cubeHeight + cubeGroup.getSavePos().y;

			CubeView cubeView = cubeGroup.getCubeView(cube);
			ActionUtils.moveTo(cubeView, targetX, targetY, ANI_DURATION);
			ActionUtils.sizeTo(cubeView, cubeWidth, cubeHeight, ANI_DURATION);
			Log.game.info("{} {},{} {},{}", cube, targetX, targetY, cubeWidth, cubeHeight);

			// 任务移动动画
			if (cube instanceof Human) {
				cubeView.setHumanEyeOffset(GameManager.instance.lastHumanMove.x * cubeWidth / 10,
						GameManager.instance.lastHumanMove.y * cubeHeight / 10, ANI_DURATION);
			}
		}

		// 然后控制相机缩放

		// 结束后，悄悄替换场景

		GameManager.instance.enableController = false;
		GameManager.instance.timerQueue.applyTimer((long) (ANI_DURATION * Time2.SEC), () -> {
			GameManager.instance.enableController = true;
			staticSetView();
			Log.game.info("done");
		});
	}

	public void staticSetView() {
		lastInView.clear();
		WorldData worldData = GameManager.instance.worldData;
		WorldStage worldStage = GameManager.instance.worldStage;
		MapData curMapData = worldData.getHumanMapData();

		worldStage.setTipText(false, "");

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
		CubeViewGroup cubeGroup = worldStage.getCubeGroup(curMapData.coor);
		Vector savePos = cubeGroup.getSavePos();
		if (worldStage.getCamera() instanceof OrthographicCamera) {
			OrthographicCamera camera = (OrthographicCamera) worldStage.getCamera();
			camera.setToOrtho(false,
					cubeGroup.getWidth() * MainViewScale * (worldStage.getWidth() / worldStage.getHeight()),
					cubeGroup.getHeight() * MainViewScale);
			camera.position.set(savePos.x + cubeGroup.getWidth() / 2, savePos.y + cubeGroup.getHeight() / 2, 0);
			Log.game.info("{}, {}, {}", camera.position, camera.viewportWidth, camera.viewportHeight);
			camera.update();
			worldStage.getBatch().setProjectionMatrix(camera.combined);
		}
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
			lastInView.add(cubeView);
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
						GameManager.instance.lastHumanMove.y * cubeHeight / 10, 0);
			}
		}
	}

	public void dealMoveCubes(Movements movements) {

	}

	private void adjustCubeViewOwnGroup(History history) {
		WorldStage worldStage = GameManager.instance.worldStage;
		// 调整方块到合适的group
		for (Movement movement : history.getMovements()) {
			if (!movement.position.worldCoor.equals(movement.cube.position.worldCoor)) {
				CubeView cubeView = worldStage.removeCubeView(movement.position.worldCoor, movement.cube);
				worldStage.addCubeView(movement.cube.position.worldCoor, cubeView);
			}
		}
	}

	public void moveCameraAuto() {

	}
}
