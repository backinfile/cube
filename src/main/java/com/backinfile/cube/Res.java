package com.backinfile.cube;

import com.backinfile.cube.support.DrawUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Res {
	public static final int CUBE_SIZE = 50;
	public static final int CUBE_BORDER_WIDTH = 3;
	public static final int CUBE_BORDER_WIDTH_THIN = 1;

	public static TextureRegion CUBE_BORDER_BLACK;
	public static TextureRegion CUBE_BORDER_WHITE;
	public static TextureRegion CUBE_BORDER_DARK;
	public static TextureRegion[] CUBE_BORDER_ASIDE;

	public static TextureRegion CUBE_FLOWER;
	public static TextureRegion CUBE_WALL;
	public static TextureRegion CUBE_ROCK;
	public static TextureRegion CUBE_TARGET;
	public static TextureRegion CUBE_HUMAN;

	public static Pixmap Cursor;

	public static void init() {
		CUBE_BORDER_BLACK = newBorderImage(Color.BLACK);
		CUBE_BORDER_WHITE = newBorderImage(Color.WHITE);
		CUBE_BORDER_DARK = newBorderImage(Color.DARK_GRAY);
		CUBE_BORDER_ASIDE = newBorderAsideImage(Color.BLACK);

		CUBE_FLOWER = newColorTexture(CUBE_SIZE, CUBE_SIZE, Color.LIGHT_GRAY);
		CUBE_WALL = newColorTexture(CUBE_SIZE, CUBE_SIZE, Color.DARK_GRAY);
		CUBE_ROCK = newColorTexture(CUBE_SIZE, CUBE_SIZE, new Color(0.67f, 0.67f, 0.67f, 1));
		CUBE_HUMAN = newHumanImage();

		Cursor = newCursorPixmap();
	}

	private static TextureRegion newHumanImage() {
		Pixmap pixmap = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap.setColor(new Color(0.8f, 0.1f, 0.1f, 1f));
		pixmap.fillRectangle(CUBE_BORDER_WIDTH_THIN, CUBE_BORDER_WIDTH_THIN, CUBE_SIZE - CUBE_BORDER_WIDTH_THIN * 2,
				CUBE_SIZE - CUBE_BORDER_WIDTH_THIN * 2);
		pixmap.setColor(Color.BLACK);
		pixmap.fillCircle(CUBE_SIZE * 3 / 10, CUBE_SIZE * 3 / 7, CUBE_SIZE / 12);
		pixmap.fillCircle(CUBE_SIZE * 7 / 10, CUBE_SIZE * 3 / 7, CUBE_SIZE / 12);
		TextureRegion texture = new TextureRegion(new Texture(pixmap));
		pixmap.dispose();
		return texture;
	}

	private static TextureRegion newBorderImage(Color color) {
		Pixmap pixmap = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap.setColor(color);
		DrawUtils.drawBorder(pixmap, 1);
		TextureRegion texture = new TextureRegion(new Texture(pixmap));
		pixmap.dispose();
		return texture;
	}

	private static TextureRegion[] newBorderAsideImage(Color color) {
		Pixmap pixmap = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fillRectangle(0, 0, CUBE_SIZE, CUBE_BORDER_WIDTH);
		TextureRegion texture0 = new TextureRegion(new Texture(pixmap));
		pixmap.dispose();
		pixmap = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fillRectangle(0, CUBE_SIZE - CUBE_BORDER_WIDTH, CUBE_SIZE, CUBE_SIZE);
		TextureRegion texture1 = new TextureRegion(new Texture(pixmap));
		pixmap.dispose();
		pixmap = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fillRectangle(0, 0, CUBE_BORDER_WIDTH, CUBE_SIZE);
		TextureRegion texture2 = new TextureRegion(new Texture(pixmap));
		pixmap.dispose();
		pixmap = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fillRectangle(CUBE_SIZE - CUBE_BORDER_WIDTH, 0, CUBE_SIZE, CUBE_SIZE);
		TextureRegion texture3 = new TextureRegion(new Texture(pixmap));
		pixmap.dispose();
		return new TextureRegion[] { texture0, texture1, texture2, texture3 };
	}

	private static Pixmap newCursorPixmap() {
		Pixmap pixmap = new Pixmap(8, 8, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		return pixmap;
	}

	private static TextureRegion newColorTexture(int width, int height, Color color) {
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fill();
		TextureRegion texture = new TextureRegion(new Texture(pixmap));
		pixmap.dispose();
		return texture;
	}

	public static String getDefaultWorldConf() {
		return Gdx.files.internal("map.txt").readString("utf-8");
	}
}
