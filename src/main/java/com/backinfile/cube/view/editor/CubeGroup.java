package com.backinfile.cube.view.editor;

import com.backinfile.cube.Log;
import com.backinfile.cube.Res;
import com.backinfile.cube.model.MapCube;
import com.backinfile.cube.model.MapData;
import com.backinfile.cube.model.cubes.Cube;
import com.backinfile.cube.model.cubes.Human;
import com.backinfile.cube.model.cubes.Rock;
import com.backinfile.cube.model.cubes.Wall;
import com.badlogic.gdx.scenes.scene2d.Group;

public class CubeGroup extends Group {
	private EditorCubeView[][] cubeViews;

	public static final int CUBE_LENGTH = 11;

	public CubeGroup() {

		cubeViews = new EditorCubeView[CUBE_LENGTH][CUBE_LENGTH];
		for (int i = 0; i < CUBE_LENGTH; i++) {
			for (int j = 0; j < CUBE_LENGTH; j++) {
				EditorCubeView editorCubeView = new EditorCubeView();
				editorCubeView.setVisible(false);
				editorCubeView.setSize(Res.CUBE_SIZE, Res.CUBE_SIZE);
				editorCubeView.setPosition(i * Res.CUBE_SIZE + getX(), j * Res.CUBE_SIZE + getY());
				cubeViews[i][j] = editorCubeView;
				addActor(editorCubeView);
			}
		}
	}

	public void setByData(MapData mapData) {
		for (int i = 0; i < CUBE_LENGTH; i++) {
			for (int j = 0; j < CUBE_LENGTH; j++) {
				cubeViews[i][j].setVisible(false);
			}
		}
		int offsetX = (CUBE_LENGTH - mapData.width) / 2;
		int offsetY = (CUBE_LENGTH - mapData.height) / 2;
		for (int i = 0; i < mapData.width; i++) {
			for (int j = 0; j < mapData.height; j++) {
				EditorCubeView editorCubeView = cubeViews[i + offsetX][j + offsetY];
				editorCubeView.setVisible(true);
				editorCubeView.setCube(mapData.cubeMap.get(i, j));
			}
		}
	}

	public static Cube getCubeNextType(Cube cube, String nextCoor) {
		if (cube == null) {
			return new Wall();
		} else if (cube instanceof Wall) {
			return new Rock();
		} else if (cube instanceof Rock) {
			return new Human();
		} else if (cube instanceof Human) {
			return new MapCube(nextCoor);
		} else if (cube instanceof MapCube) {
		}
		return null;
	}
}
