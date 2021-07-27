package com.backinfile.cube.actor;

import com.backinfile.cube.Settings;
import com.backinfile.cube.core.GameManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class WorldStage extends Stage {

	private WorldView worldView;

	public WorldStage() {
		super(new ExtendViewport(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT, new OrthographicCamera()),
				new SpriteBatch());

		worldView = new WorldView();
		worldView.setSize(this.getWidth(), this.getHeight());
		GameManager.instance.worldView = worldView;
		addActor(worldView);
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
