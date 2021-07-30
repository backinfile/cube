package com.backinfile.cube.view.editor;

import java.util.ArrayList;
import java.util.List;

import com.backinfile.cube.Res;
import com.backinfile.cube.model.MapData;
import com.backinfile.cube.model.Vector;
import com.backinfile.cube.model.WorldData;
import com.backinfile.cube.support.ObjectPool;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;

public class MapBarGroup extends Group {
	private VerticalGroup verticalGroup;
	private ScrollPane scrollPane;
	private Image background = new Image(Res.CUBE_BORDER_WHITE);
	private ObjectPool<MapBar> mapBarPool;
	private List<MapBar> mapBarInView = new ArrayList<>();

	public MapBarGroup(float width, float height) {
		mapBarPool = new ObjectPool<>(() -> new MapBar(getWidth(), Res.CUBE_SIZE));

		background.setSize(width, height);
		addActor(background);

		verticalGroup = new VerticalGroup();
		verticalGroup.setSize(width, height);
		verticalGroup.align(Align.center);
		scrollPane = new ScrollPane(verticalGroup);
		scrollPane.setSize(width, height);
		scrollPane.setScrollingDisabled(true, false);
		addActor(scrollPane);
//		background.setSize(width, height);
//		addActor(verticalGroup);
		scrollPane.layout();

		setSize(width, height);
	}

	public void setByData(WorldData worldData, String coor) {
		for (Actor actor : verticalGroup.getChildren()) {
			if (actor instanceof MapBar) {
				actor.remove();
				mapBarPool.putBack((MapBar) actor);
			}
		}

		for (MapData mapData : worldData.getOrderedDatas()) {
			MapBar mapBar = mapBarPool.apply();
			mapBar.setText(mapData.coor);
			mapBar.setInputText(mapData.width + "*" + mapData.height);
			verticalGroup.addActor(mapBar);
		}
	}

}
