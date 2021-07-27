package com.backinfile.cube.view;

import com.backinfile.cube.Res;
import com.backinfile.cube.model.cubes.Cube;
import com.backinfile.cube.model.cubes.Human;
import com.backinfile.cube.model.cubes.Rock;
import com.backinfile.cube.model.cubes.Wall;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class CubeView extends Group {
	private Cube cube;

	private Image mainImage;
	private Image borderImage = new Image(Res.CUBE_BORDER_BLACK);

	public CubeView(Cube cube) {
		this.cube = cube;

		if (cube instanceof Rock) {
			mainImage = new Image(Res.CUBE_ROCK);
			addActor(mainImage);
			addActor(borderImage);
		} else if (cube instanceof Wall) {
			mainImage = new Image(Res.CUBE_WALL);
			addActor(mainImage);
			addActor(borderImage);
		} else if (cube instanceof Human) {
			mainImage = new Image(Res.CUBE_HUMAN);
			addActor(mainImage);
			addActor(borderImage);
		}
	}

	public void setBorder(boolean show) {
		borderImage.setVisible(show);
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
