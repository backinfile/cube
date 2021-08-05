package com.backinfile.cube.view;

import java.util.List;

import com.backinfile.cube.Log;
import com.backinfile.cube.Res;
import com.backinfile.cube.controller.GameManager;
import com.backinfile.cube.model.cubes.Cube;
import com.backinfile.cube.model.cubes.Human;
import com.backinfile.cube.model.cubes.MapCube;
import com.backinfile.cube.model.cubes.Rock;
import com.backinfile.cube.model.cubes.Wall;
import com.backinfile.cube.support.Utils;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class CubeView extends Group {
	private Cube cube;
	private float alpha = 1f;

	private Image mainImage;
	private Image borderImage;
	private Image[] borderAsideImages;
	private Image humanEyeImage;

	public CubeView(Cube cube) {
		this.cube = cube;

		if (cube instanceof Rock) {
			mainImage = new Image(Res.CUBE_ROCK);
			borderImage = new Image(Res.CUBE_BORDER_DARK);
			addActor(mainImage);
			addActor(borderImage);
		} else if (cube instanceof Wall) {
			mainImage = new Image(Res.CUBE_WALL);
			borderAsideImages = new Image[Res.CUBE_BORDER_ASIDE.length];
			addActor(mainImage);
			for (int i = 0; i < borderAsideImages.length; i++) {
				borderAsideImages[i] = new Image(Res.CUBE_BORDER_ASIDE[i]);
				addActor(borderAsideImages[i]);
			}
		} else if (cube instanceof Human) {
			mainImage = new Image(Res.CUBE_HUMAN);
			humanEyeImage = new Image(Res.CUBE_HUMAN_EYE);
			addActor(mainImage);
			addActor(humanEyeImage);
		} else if (cube instanceof MapCube) {
			mainImage = new Image(Res.CUBE_BORDER_BLUE);
			mainImage.setVisible(false);
			addActor(mainImage);
		}
	}

	public void setBorder(boolean show) {
		borderImage.setVisible(show);
	}

	public void setAsideBorder(int index, boolean open) {
		borderAsideImages[index].setVisible(!open);
	}

	public void setAdjWallDirections(List<Integer> adjWallDirections) {
		for (int i = 0; i < borderAsideImages.length; i++) {
			borderAsideImages[i].setVisible(true);
		}
		for (int dir : adjWallDirections) {
			if (dir < 4) {
				borderAsideImages[dir].setVisible(false);
			}
		}
		for (int dir = 4; dir < 8; dir++) {
			borderAsideImages[dir].setVisible(false);
			if (!adjWallDirections.contains(dir)) {
				int dir1 = Utils.indexOf(GameManager.dx, GameManager.dx8[dir]);
				int dir2 = Utils.indexOf(GameManager.dy, GameManager.dy8[dir]);
				if (adjWallDirections.contains(dir1) && adjWallDirections.contains(dir2)) {
					borderAsideImages[dir].setVisible(true);
//					Log.game.info("{} dir:{} adj:{}", cube, dir, adjWallDirections);
				}
			}
		}
	}

	public void setHumanEyeOffset(float x, float y, float duration) {
		if (humanEyeImage != null) {
			MoveToAction moveToAction = new MoveToAction();
			moveToAction.setDuration(duration);
			moveToAction.setPosition(x, y);
			humanEyeImage.addAction(moveToAction);
		}
	}

	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		for (Actor actor : getChildren()) {
			if (actor instanceof Image) {
				actor.setSize(width, height);
			}
		}
	}

	public Cube getCube() {
		return cube;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public void setMainImageVisible(boolean visible) {
		mainImage.setVisible(visible);
	}

	@Override
	public void addAction(Action action) {
		super.addAction(action);
		if (cube instanceof Human) {
			Log.game.info("{}", action);
		}
	}

	@Override
	public void act(float delta) {
		super.act(delta);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha * alpha);
	}

}
