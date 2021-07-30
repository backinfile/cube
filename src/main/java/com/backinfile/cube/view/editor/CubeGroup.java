package com.backinfile.cube.view.editor;

import com.backinfile.cube.Res;
import com.backinfile.cube.model.MapData;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class CubeGroup extends Group {
	private EditorCubeView[][] cubeViews;
	private Image background = new Image(Res.CUBE_BORDER_WHITE);

	public static final int CUBE_LENGTH = 11;

	public CubeGroup() {
		setSize(Res.CUBE_SIZE * CUBE_LENGTH, Res.CUBE_SIZE * CUBE_LENGTH);
		background.setSize(getWidth(), getHeight());
		addActor(background);

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
				EditorCubeView editorCubeView = cubeViews[i][j];
				editorCubeView.setVisible(false);
			}
		}
		for (int i = 0; i < mapData.width; i++) {
			for (int j = 0; j < mapData.height; j++) {
				EditorCubeView editorCubeView = cubeViews[i][j];
				editorCubeView.setVisible(true);
				editorCubeView.setType(mapData.cubeMap.get(i, j));
			}
		}
	}
}
