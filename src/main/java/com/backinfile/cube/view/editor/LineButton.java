package com.backinfile.cube.view.editor;

import com.backinfile.cube.Log;
import com.backinfile.cube.Res;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class LineButton extends TextButton {

	private ShapeRenderer shapeRenderer;
	private boolean hover = false;

	private class LineButtonEventListener extends InputListener {
		@Override
		public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
			hover = true;
		}

		@Override
		public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
			hover = false;
		}
	}

	private class LineButtonClickListener extends ClickListener {

		@Override
		public void clicked(InputEvent event, float x, float y) {
			if (getDebug()) {
				Log.view.debug("click LineButton(\"{}\")", getLabel().getText());
			}
		}

	}

	public LineButton(String text) {
		this(text, Res.DefaultFont);
	}

	public LineButton(String text, BitmapFont font) {
		super(text, new TextButtonStyle(null, null, null, font));
		addListener(new LineButtonEventListener());
		addListener(new LineButtonClickListener());

		shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (hover) {
//			batch.draw(ResourceManager.White, getX(), getY(), getWidth(), getHeight() / 16);
			batch.end();

			shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
			shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.rect(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2);
			shapeRenderer.end();

			batch.begin();
		}

		super.draw(batch, parentAlpha);
	}
}
