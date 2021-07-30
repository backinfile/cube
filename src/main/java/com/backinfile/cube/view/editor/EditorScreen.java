package com.backinfile.cube.view.editor;

import com.backinfile.cube.Settings;
import com.backinfile.cube.controller.GameManager;
import com.backinfile.cube.controller.GamePlayInputProcessor;
import com.backinfile.cube.support.TimerQueue;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class EditorScreen implements Screen {

	private TimerQueue timerQueue;
	private EditorStage editorStage;

	@Override
	public void show() {
		if (timerQueue == null) {
			timerQueue = new TimerQueue();
		}
		if (editorStage == null) {
			editorStage = new EditorStage(new ExtendViewport(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT));
			Gdx.input.setInputProcessor(editorStage);
		}

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (timerQueue != null) {
			timerQueue.update();
		}
		if (editorStage != null) {
			editorStage.act();
			editorStage.draw();
		}
	}

	@Override
	public void resize(int width, int height) {
		if (editorStage != null) {
			editorStage.getViewport().update(width, height, true);
		}
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		if (editorStage != null) {
			editorStage.dispose();
		}
	}

}
