package com.backinfile.cube.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.backinfile.cube.Res;
import com.backinfile.cube.controller.GameManager;
import com.backinfile.cube.model.MapData;
import com.backinfile.cube.model.cubes.Cube;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

public class WorldStage extends Stage {

	private Group mainView;
	private TextField tipText;
	private Map<String, Group> viewGroups = new HashMap<>();

	public WorldStage(Viewport viewport) {
		super(viewport);

		init();
	}

	private void init() {
		// 初始化方块
		mainView = new Group();
		mainView.setSize(this.getWidth(), this.getHeight());
		addActor(mainView);
		for (MapData mapData : GameManager.instance.worldData.getDatas()) {
			Group group = getCubeGroup(mapData.coor);
			mainView.addActor(group);
			for (Cube cube : mapData.cubeMap.getUnitList()) {
				group.addActor(new CubeView(cube));
			}
		}

		// 初始化其他
		TextFieldStyle style = new TextFieldStyle();
		style.font = Res.DefaultFont;
		style.fontColor = Color.LIGHT_GRAY;
		tipText = new TextField("123asd哈哈", style);
		tipText.setSize(getWidth(), getHeight() / 4);
		tipText.setAlignment(Align.center);
		tipText.setPosition(getWidth() / 2, getHeight() / 4, Align.center);
		addActor(tipText);
	}

	public List<CubeView> getCubeViews() {
		List<CubeView> cubeViews = new ArrayList<>();
		for (Group group : viewGroups.values()) {
			for (Actor actor : group.getChildren()) {
				if (actor instanceof CubeView) {
					cubeViews.add((CubeView) actor);
				}
			}
		}
		return cubeViews;
	}

	public Group getCubeGroup(String coor) {
		return viewGroups.computeIfAbsent(coor, key -> new Group());
	}

	public CubeView removeCubeView(String coor, Cube cube) {
		Group cubeGroup = getCubeGroup(coor);
		for (Actor actor : cubeGroup.getChildren()) {
			if (actor instanceof CubeView) {
				CubeView cubeView = (CubeView) actor;
				if (cubeView.getCube() == cube) {
					cubeView.remove();
					return cubeView;
				}
			}
		}
		return null;
	}

	public void addCubeView(String coor, CubeView cubeView) {
		Group cubeGroup = getCubeGroup(coor);
		cubeGroup.addActor(cubeView);
	}

	public List<CubeView> getCubeViews(String coor) {
		List<CubeView> cubeViews = new ArrayList<>();
		Group group = viewGroups.get(coor);
		if (group == null) {
			return cubeViews;
		}
		for (Actor actor : group.getChildren()) {
			if (actor instanceof CubeView) {
				cubeViews.add((CubeView) actor);
			}
		}
		return cubeViews;
	}

	public Group getMainView() {
		return mainView;
	}

	public void setTipText(boolean visible, String content) {
		tipText.setVisible(visible);
		if (visible) {
			tipText.setText(content);
		}
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
