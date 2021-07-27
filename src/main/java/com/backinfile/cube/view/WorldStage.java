package com.backinfile.cube.view;

import java.util.ArrayList;
import java.util.List;

import com.backinfile.cube.controller.GameManager;
import com.backinfile.cube.model.MapData;
import com.backinfile.cube.model.cubes.Cube;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class WorldStage extends Stage {

	private Group mainView;
	private List<CubeView> cubeViews = new ArrayList<>();

	public WorldStage(Viewport viewport) {
		super(viewport);

		init();
	}

	private void init() {
		mainView = new Group();
		mainView.setSize(this.getWidth(), this.getHeight());
		addActor(mainView);

		MapData mapData = GameManager.instance.getCurMap();
		for (Cube cube : mapData.cubeMap.getUnitList()) {
			CubeView cubeView = new CubeView(cube);
			cubeViews.add(cubeView);
			mainView.addActor(cubeView);
		}

	}

	public List<CubeView> getCubeViews() {
		return cubeViews;
	}

	public Group getMainView() {
		return mainView;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
	}

	@Override
	public void draw() {
		super.draw();
	}

}
