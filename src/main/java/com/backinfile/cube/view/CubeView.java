package com.backinfile.cube.view;

import com.backinfile.cube.Res;
import com.backinfile.cube.model.cubes.Cube;
import com.backinfile.cube.model.cubes.Human;
import com.backinfile.cube.model.cubes.Rock;
import com.backinfile.cube.model.cubes.Wall;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class CubeView extends Group {
	private Cube cube;

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
			borderAsideImages = new Image[] { new Image(Res.CUBE_BORDER_ASIDE[0]), new Image(Res.CUBE_BORDER_ASIDE[1]),
					new Image(Res.CUBE_BORDER_ASIDE[2]), new Image(Res.CUBE_BORDER_ASIDE[3]), };
			addActor(mainImage);
			addActor(borderAsideImages[0]);
			addActor(borderAsideImages[1]);
			addActor(borderAsideImages[2]);
			addActor(borderAsideImages[3]);
		} else if (cube instanceof Human) {
			mainImage = new Image(Res.CUBE_HUMAN);
			humanEyeImage = new Image(Res.CUBE_HUMAN_EYE);
			addActor(mainImage);
			addActor(humanEyeImage);
		}
	}

	public void setBorder(boolean show) {
		borderImage.setVisible(show);
	}

	public void setAsideBorder(int index, boolean open) {
		borderAsideImages[index].setVisible(!open);
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

	@Override
	public void act(float delta) {
		super.act(delta);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}

}
