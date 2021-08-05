package com.backinfile.cube.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.backinfile.cube.Res;
import com.backinfile.cube.controller.GameManager;
import com.backinfile.cube.model.MapData;
import com.backinfile.cube.model.cubes.Cube;
import com.backinfile.cube.model.cubes.Human;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

public class WorldStage extends Stage {

	private Group cubeGroupRoot;
	private TextField tipText;
	private Map<String, CubeViewGroup> viewGroups = new HashMap<>();

	public WorldStage(Viewport viewport) {
		super(viewport);

		init();
	}

	private void init() {
		// 初始化方块
		cubeGroupRoot = new Group();
		cubeGroupRoot.setSize(this.getWidth(), this.getHeight());
		addActor(cubeGroupRoot);
		for (MapData mapData : GameManager.instance.worldData.getMapDatas()) {
			Group group = getCubeGroup(mapData.coor);
			cubeGroupRoot.addActor(group);
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

	public CubeViewGroup getCubeGroup(String coor) {
		return viewGroups.computeIfAbsent(coor,
				key -> new CubeViewGroup(GameManager.instance.worldData.getMapData(key)));
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

	public void updateCubeGroupLayer() {
		List<CubeViewGroup> values = new ArrayList<>();
		for (CubeViewGroup group : viewGroups.values()) {
			if (group.isVisible()) {
				values.add(group);
			}
		}
		if (values.isEmpty()) {
			return;
		}
		for (CubeViewGroup group : values) {
			group.remove();
		}
		Collections.sort(values, new Comparator<CubeViewGroup>() {
			@Override
			public int compare(CubeViewGroup o1, CubeViewGroup o2) {
				return Integer.compare(o1.getLayer(), o2.getLayer());
			}
		});
		for (CubeViewGroup group : values) {
			cubeGroupRoot.addActor(group);
		}
	}

	public Group getMainView() {
		return cubeGroupRoot;
	}

	public void setTipText(boolean visible, String content) {
		tipText.setVisible(visible);
		if (visible) {
			tipText.setText(content);
		}
	}

	public CubeView getHumanCubeView() {
		for (CubeView cubeView : getCubeViews()) {
			if (cubeView.getCube() instanceof Human) {
				return cubeView;
			}
		}
		return null;
	}

	public CubeViewGroup getHumanCubeViewGroup() {
		for (CubeViewGroup cubeViewGroup : viewGroups.values()) {
			for (Actor actor : cubeViewGroup.getChildren()) {
				if (actor instanceof CubeView) {
					if (((CubeView) actor).getCube() instanceof Human) {
						return cubeViewGroup;
					}
				}
			}
		}
		return null;
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
