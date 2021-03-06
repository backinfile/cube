package com.backinfile.cube.controller;

import com.backinfile.cube.Log;
import com.backinfile.cube.Res;
import com.backinfile.cube.model.*;
import com.backinfile.cube.model.History.Movement;
import com.backinfile.cube.model.cubes.*;
import com.backinfile.cube.support.ActionUtils;
import com.backinfile.cube.support.Time2;
import com.backinfile.cube.support.Tuple2;
import com.backinfile.cube.support.Utils;
import com.backinfile.cube.view.CubeView;
import com.backinfile.cube.view.CubeViewGroup;
import com.backinfile.cube.view.WorldStage;
import com.backinfile.cube.view.animation.ZoomCameraAction;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameViewManager {
    public static GameViewManager instance;

    private static final float MainViewScale = 1.2f;
    public static float ANI_DURATION = 0.1f;

    public void updateCubeView(History history) {
        adjustCubeViewOwnGroup(history);

        WorldStage worldStage = GameManager.instance.worldStage;
        WorldData worldData = GameManager.instance.worldData;

        preSetAniDuration(history);

        GameManager.instance.limitMoveForAWhile();

        // 预先计算好group的位置和大小
        preCalcGroupPosAndSize();

        // 收集要移动的方块
        Set<Cube> movedCubes = new HashSet<>();
        Set<Cube> movedMapCubes = new HashSet<>();
        for (Movement movement : history.getMovements()) {
            movedCubes.add(movement.cube);
        }
        while (true) {
            boolean hasMapCube = false;
            for (Cube cube : new ArrayList<>(movedCubes)) {
                if (cube instanceof MapCube) {
                    hasMapCube = true;
                    MapCube mapCube = (MapCube) cube;
                    movedCubes.remove(mapCube);
                    movedCubes.addAll(worldData.getMapData(mapCube.getTargetCoor()).cubeMap.getUnitList());
                    movedMapCubes.add(mapCube);
                } else {
                    movedCubes.add(cube);
                }
            }
            if (!hasMapCube) {
                break;
            }
        }
        movedCubes.addAll(movedMapCubes);

        // !! 应该只更新发生移动的mapcube对应group的pos
        // 先通过动画移动到确定位置
        for (Cube cube : movedCubes) {
            CubeViewGroup cubeGroup = worldStage.getCubeGroup(cube.position.worldCoor);
            float cubeWidth = cubeGroup.getWidth() / cubeGroup.getMapData().width;
            float cubeHeight = cubeGroup.getHeight() / cubeGroup.getMapData().height;
            float targetX = cube.position.x * cubeWidth + cubeGroup.getSavePos().x;
            float targetY = cube.position.y * cubeHeight + cubeGroup.getSavePos().y;

            CubeView cubeView = cubeGroup.getCubeView(cube);
            ActionUtils.moveTo(cubeView, targetX, targetY, ANI_DURATION);
            ActionUtils.sizeTo(cubeView, cubeWidth, cubeHeight, ANI_DURATION);

        }

        moveHumanEye(GameManager.instance.human.lastMove, ANI_DURATION);

        // 然后控制相机缩放
        CubeViewGroup cubeGroup = worldStage.getHumanCubeViewGroup();
        Vector savePos = cubeGroup.getSavePos();
        ZoomCameraAction action = new ZoomCameraAction();
        action.setDuration(ANI_DURATION);
        action.setEndSize(cubeGroup.getWidth() * MainViewScale * (worldStage.getWidth() / worldStage.getHeight()),
                cubeGroup.getHeight() * MainViewScale);
        action.setEndPos(savePos.x + cubeGroup.getWidth() / 2, savePos.y + cubeGroup.getHeight() / 2);
        cubeGroup.addAction(action);

        // 结束后，悄悄替换场景
        GameManager.instance.enableController = false;
        GameManager.instance.timerQueue.applyTimer((long) (ANI_DURATION * Time2.SEC) + 10, () -> {
            GameManager.instance.enableController = true;
            staticSetView();
            Log.game.debug("done");
        });
    }

    public void moveHumanEye(Vector move, float duration) {
        CubeView humanCubeView = GameManager.instance.worldStage.getHumanCubeView();
        CubeViewGroup cubeGroup = GameManager.instance.worldStage.getHumanCubeViewGroup();

        float cubeWidth = cubeGroup.getWidth() / cubeGroup.getMapData().width;
        float cubeHeight = cubeGroup.getHeight() / cubeGroup.getMapData().height;

        if (humanCubeView != null) {
            humanCubeView.setHumanEyeOffset(move.x * cubeWidth / 10, move.y * cubeHeight / 10, duration);
        }
    }

    private void preSetAniDuration(History history) {
        Player human = GameManager.instance.human;
        Position lastPosition = human.getLastPosition();
        if (lastPosition != null && !lastPosition.worldCoor.equals(human.position.worldCoor)) {
            ANI_DURATION = 0.4f;
            return;
        }
        for (Movement movement : history.getMovements()) {
            Position lastPosition2 = movement.cube.getLastPosition();
            if (lastPosition2 != null && !movement.cube.position.worldCoor.equals(lastPosition2.worldCoor)) {
                ANI_DURATION = 0.25f;
                return;
            }
        }
        ANI_DURATION = 0.1f;
    }

    private void preCalcGroupPosAndSize() {

        WorldData worldData = GameManager.instance.worldData;
        MapData mapData = worldData.getMapData(GameManager.instance.human.getLastPosition().worldCoor);
        Tuple2<Integer, MapData> tuple = getUpperMapdata(mapData);
        preCalcGroupPosAndSize(tuple.value2.coor, 0, 0, tuple.value2.width * Res.CUBE_SIZE * tuple.value1,
                tuple.value2.height * Res.CUBE_SIZE * tuple.value1);
    }

    private void preCalcGroupPosAndSize(String coor, float x, float y, float width, float height) {
        WorldStage worldStage = GameManager.instance.worldStage;
        WorldData worldData = GameManager.instance.worldData;
        CubeViewGroup cubeGroup = worldStage.getCubeGroup(coor);
        MapData mapData = worldData.getMapData(coor);
        cubeGroup.savePos(x, y);
        cubeGroup.setSize(width, height);
        float cubeWidth = width / mapData.width;
        float cubeHeight = height / mapData.height;
        for (CubeView cubeView : worldStage.getCubeViews(coor)) {
            Cube cube = cubeView.getCube();
            if (cube instanceof MapCube) {
                MapCube mapCube = (MapCube) cube;
                if (!mapCube.isFitKey()) {
                    cubeView.setMainImageVisible(mapCube.isFitKey());
                }
                preCalcGroupPosAndSize(mapCube.getTargetCoor(), x + cube.position.x * cubeWidth,
                        y + cube.position.y * cubeHeight, cubeWidth, cubeHeight);
            }
        }
    }

    private Tuple2<Integer, MapData> getUpperMapdata(MapData curMapData) {
        MapData upperMapdata = curMapData.preMapData;
        if (upperMapdata == null) {
            return new Tuple2<Integer, MapData>(1, curMapData);
        }
        MapData upperupperMapdata = upperMapdata.preMapData;
        if (upperupperMapdata == null) {
            return new Tuple2<Integer, MapData>(2, upperMapdata);
        }
        return new Tuple2<Integer, MapData>(3, upperupperMapdata);
    }

    public void staticSetView() {
        WorldData worldData = GameManager.instance.worldData;
        WorldStage worldStage = GameManager.instance.worldStage;
        MapData curMapData = worldData.getHumanMapData();

        if (!Utils.isNullOrEmpty(curMapData.tip)) {
            GameManager.instance.uiStage.setTipText(true, curMapData.tip);
        } else {
            GameManager.instance.uiStage.setTipText(false, "");
        }

        // 重置group
        for (MapData mapData : worldData.getMapDatas()) {
            CubeViewGroup cubeGroup = worldStage.getCubeGroup(mapData.coor);
            cubeGroup.setLayer(0);
            cubeGroup.setVisible(false);
        }

        // 绘制所有方块
        {
            Tuple2<Integer, MapData> tuple = getUpperMapdata(curMapData);
            staticSetView(tuple.value2.coor, 0, 0, tuple.value2.width * Res.CUBE_SIZE * tuple.value1,
                    tuple.value2.height * Res.CUBE_SIZE * tuple.value1, 1f, 0);
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
            Log.game.debug("{}, {}, {}", camera.position, camera.viewportWidth, camera.viewportHeight);
            camera.update();
            worldStage.getBatch().setProjectionMatrix(camera.combined);
        }
    }

    private void staticSetView(String coor, float x, float y, float width, float height, float alpha, int layer) {
        if (layer <= -10) {
            return;
        }
        Log.game.debug("show coor:{} layer:{} x:{} y:{} width:{} height:{}", coor, layer, x, y, width, height);
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
                float nextAlpha = (alpha < 1f || cube instanceof FixedKey) ? Res.FLOOR_ELE_ALPHA : 1f;
                staticSetView(mapCube.getTargetCoor(), x + cube.position.x * cubeWidth,
                        y + cube.position.y * cubeHeight, cubeWidth, cubeHeight, nextAlpha, layer - 2);
            } else if (cube instanceof Wall) {
                List<Integer> adjWallDirections = GameManager.instance.getAdjWallDirections(cube.position);
                cubeView.setAdjWallDirections(adjWallDirections);
            } else if (cube instanceof Human) {
                Human human = (Human) cube;
                cubeView.setHumanEyeOffset(human.lastMove.x * cubeWidth / 10, human.lastMove.y * cubeHeight / 10, 0);
            } else if (cube instanceof Lock) {
                cubeView.setLocked(((Lock) cube).isLocked());
                group.addActorAt(0, cubeView);
            }
        }
    }

    public void adjustCubeViewOwnGroup(History history, boolean reverse) {
        WorldStage worldStage = GameManager.instance.worldStage;
        // 调整方块到合适的group
        for (Movement movement : history.getMovements()) {
            Position from = movement.position;
            Position to = movement.cube.position;
            if (reverse) {
                Position tmp = from;
                from = to;
                to = tmp;
            }
            if (!from.worldCoor.equals(to.worldCoor)) {
                CubeView cubeView = worldStage.removeCubeView(from.worldCoor, movement.cube);
                worldStage.addCubeView(to.worldCoor, cubeView);
            }
        }
    }

    private void adjustCubeViewOwnGroup(History history) {
        adjustCubeViewOwnGroup(history, false);
    }

    public void moveCameraAuto() {

    }
}
