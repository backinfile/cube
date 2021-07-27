package com.backinfile.cube.actor;

import java.util.ArrayList;
import java.util.List;

public class WorldView extends CubeView {

	public List<CubeView> cubeViews = new ArrayList<>();
	public boolean requreFlushCamera = true;

	public WorldView() {
	}

	@Override
	public void act(float delta) {
		super.act(delta);

//		if (requreFlushCamera) {
//			Pos worldSize = GameManager.instance.getWorldSize();
//			Viewport viewport = getStage().getViewport();
//			viewport.setWorldSize(worldSize.x, worldSize.y);
//			viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
//		}
	}

}
