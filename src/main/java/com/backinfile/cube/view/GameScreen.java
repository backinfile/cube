package com.backinfile.cube.view;

import com.backinfile.cube.Log;
import com.backinfile.cube.Settings;
import com.backinfile.cube.controller.GameManager;
import com.backinfile.cube.controller.GamePlayInputProcessor;
import com.backinfile.cube.controller.GameViewManager;
import com.backinfile.cube.support.TimerQueue;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class GameScreen implements Screen {

	private TimerQueue timerQueue;
	private WorldStage worldStage;
	private UIStage uiStage;

	@Override
	public void show() {
		if (timerQueue == null) {
			timerQueue = new TimerQueue();
			GameManager.instance.timerQueue = timerQueue;
		}
		if (worldStage == null) {
			worldStage = new WorldStage(new ExtendViewport(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT));
			Gdx.input.setInputProcessor(new GamePlayInputProcessor(GameManager.instance));
			GameManager.instance.worldStage = worldStage;
		}

		if (uiStage == null) {
			uiStage = new UIStage(new ExtendViewport(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT));
			GameManager.instance.uiStage = uiStage;
		}

		GameManager.instance.timerQueue.applyTimer(0, () -> {
			GameViewManager.instance.staticSetView();
		});
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (timerQueue != null) {
			timerQueue.update();
		}
		if (worldStage != null) {
			worldStage.act(delta);
		}
		if (uiStage != null) {
			uiStage.act(delta);
		}

		if (worldStage != null) {
			worldStage.draw();
		}
		if (uiStage != null) {
			uiStage.draw();
		}
	}

	@Override
	public void resize(int width, int height) {
		worldStage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
		Log.game.info("pause");
	}

	@Override
	public void resume() {
		GameManager.instance.timerQueue.applyTimer(5, () -> {
			GameViewManager.instance.staticSetView();
		});
		Log.game.info("resume");
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		if (worldStage != null) {
			worldStage.dispose();
		}
	}

}
