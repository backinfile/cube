package com.backinfile.cube.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

public class GamePlayInputProcessor implements InputProcessor {

	private GameManager gameManager;

	public GamePlayInputProcessor(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (!gameManager.enableController) {
			return false;
		}

		switch (keycode) {
		case Keys.UP:
		case Keys.W:
			gameManager.moveHuman(0);
			break;
		case Keys.DOWN:
		case Keys.S:
			gameManager.moveHuman(1);
			break;
		case Keys.LEFT:
		case Keys.A:
			gameManager.moveHuman(2);
			break;
		case Keys.RIGHT:
		case Keys.D:
			gameManager.moveHuman(3);
			break;
		case Keys.R:
			gameManager.resetGame();
			break;
		case Keys.Z:
			gameManager.undo();
			break;
		case Keys.ESCAPE:
			Gdx.app.exit();
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
