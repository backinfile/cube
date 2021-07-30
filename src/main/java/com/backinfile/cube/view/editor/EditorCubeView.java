package com.backinfile.cube.view.editor;

import com.backinfile.cube.Res;
import com.backinfile.cube.model.MapCube;
import com.backinfile.cube.model.cubes.Cube;
import com.backinfile.cube.model.cubes.Human;
import com.backinfile.cube.model.cubes.Rock;
import com.backinfile.cube.model.cubes.Wall;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

public class EditorCubeView extends Group {
	private Image mainImage = new Image(Res.CUBE_ROCK);
	private Image borderImage = new Image(Res.CUBE_BORDER_DARK);
	private Image humanEyeImage = new Image(Res.CUBE_HUMAN_EYE);
	private Image[] borderAsideImages = new Image[] { new Image(Res.CUBE_BORDER_ASIDE[0]),
			new Image(Res.CUBE_BORDER_ASIDE[1]), new Image(Res.CUBE_BORDER_ASIDE[2]),
			new Image(Res.CUBE_BORDER_ASIDE[3]), };

	private Label label = new Label("", new LabelStyle(Res.DefaultFont, Color.WHITE));

	public EditorCubeView() {

		label.setSize(this.getWidth(), this.getHeight());
		label.setAlignment(Align.center);

		addActor(mainImage);
		addActor(borderImage);
		addActor(humanEyeImage);
		addActor(borderAsideImages[0]);
		addActor(borderAsideImages[1]);
		addActor(borderAsideImages[2]);
		addActor(borderAsideImages[3]);
		addActor(label);

		hideChildren();
	}

	private void hideChildren() {
		for (Actor actor : getChildren()) {
			actor.setVisible(false);
		}
	}

	public void setType(Cube cube) {
		hideChildren();
		if (cube == null) {

		} else if (cube instanceof Rock) {
			mainImage.setDrawable(Res.CUBE_ROCK);
			mainImage.setVisible(true);
		} else if (cube instanceof Wall) {
			mainImage.setDrawable(Res.CUBE_WALL);
			mainImage.setVisible(true);
		} else if (cube instanceof Human) {
			mainImage.setDrawable(Res.CUBE_HUMAN);
			mainImage.setVisible(true);
			humanEyeImage.setVisible(true);
		} else if (cube instanceof MapCube) {
			mainImage.setDrawable(Res.CUBE_ROCK);
			mainImage.setVisible(true);
			label.setVisible(true);
			MapCube mapCube = (MapCube) cube;
			label.setText(mapCube.getTargetCoor());
		}
	}

	public void setBorder(boolean show) {
		borderImage.setVisible(show);
	}

	public void setAsideBorder(int index, boolean open) {
		borderAsideImages[index].setVisible(!open);
	}

	public void setHumanEyeOffset(float x, float y) {
		if (humanEyeImage != null) {
			MoveToAction moveToAction = new MoveToAction();
			moveToAction.setDuration(0.06f);
			moveToAction.setPosition(x, y);
			humanEyeImage.addAction(moveToAction);
		}
	}

	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		for (Actor actor : getChildren()) {
			actor.setSize(width, height);
		}
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
