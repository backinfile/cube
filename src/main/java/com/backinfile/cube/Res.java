package com.backinfile.cube;

import com.backinfile.cube.support.DrawUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

@SuppressWarnings("unused")
public class Res {
	public static final int CUBE_SIZE = Settings.SCREEN_HEIGHT / 12;
	public static final int CUBE_BORDER_WIDTH = CUBE_SIZE * 5 / 50;
	public static final int CUBE_BORDER_WIDTH_THIN = 1;

	public static TextureRegionDrawable TEX_WHITE;
	public static TextureRegionDrawable TEX_BLACK;
	public static TextureRegionDrawable TEX_GRAY;
	public static TextureRegionDrawable TEX_LIGHT;
	public static TextureRegionDrawable TEX_DARK;
	public static TextureRegionDrawable TEX_BLUE;

	public static TextureRegionDrawable CUBE_BORDER_BLACK;
	public static TextureRegionDrawable CUBE_BORDER_WHITE;
	public static TextureRegionDrawable CUBE_BORDER_DARK;
	public static TextureRegionDrawable[] CUBE_BORDER_ASIDE;

	public static TextureRegionDrawable CUBE_FLOWER;
	public static TextureRegionDrawable CUBE_WALL;
	public static TextureRegionDrawable CUBE_ROCK;
	public static TextureRegionDrawable CUBE_TARGET;
	public static TextureRegionDrawable CUBE_HUMAN;
	public static TextureRegionDrawable CUBE_HUMAN_EYE;

	public static Pixmap Cursor;
	public static TextureRegionDrawable TextFieldCursor;

	public static BitmapFont DefaultFontSmallSamll;
	public static BitmapFont DefaultFontSmall;
	public static BitmapFont DefaultFontLarge;
	public static BitmapFont DefaultFont;

	public static String DefaultWorldConfString = "";

	public static void init() {
		initImage();
		initText();
		initFont();
	}

	private static void initText() {
		DefaultWorldConfString = getDefaultWorldConf();
	}

	private static void initImage() {

		TEX_BLACK = newColorTexture(8, 8, Color.BLACK);
		TEX_DARK = newColorTexture(8, 8, Color.DARK_GRAY);
		TEX_GRAY = newColorTexture(8, 8, Color.GRAY);
		TEX_LIGHT = newColorTexture(8, 8, Color.LIGHT_GRAY);
		TEX_WHITE = newColorTexture(8, 8, Color.WHITE);
		TEX_BLUE = newColorTexture(8, 8, Color.BLUE);

		CUBE_BORDER_BLACK = newBorderImage(Color.BLACK);
		CUBE_BORDER_WHITE = newBorderImage(Color.WHITE);
		CUBE_BORDER_DARK = newBorderImage(Color.DARK_GRAY);
		CUBE_BORDER_ASIDE = newBorderAsideImage(Color.BLACK);

		CUBE_FLOWER = newColorTexture(CUBE_SIZE, CUBE_SIZE, Color.LIGHT_GRAY);
		CUBE_WALL = newColorTexture(CUBE_SIZE, CUBE_SIZE, Color.DARK_GRAY);
		CUBE_ROCK = newColorTexture(CUBE_SIZE, CUBE_SIZE, new Color(0.67f, 0.67f, 0.67f, 1));
		CUBE_HUMAN = newHumanImage();
		CUBE_HUMAN_EYE = newHumanEyeImage();

		Cursor = newCursorPixmap();
		TextFieldCursor = newTextFieldCursorPixmap();
	}

	private static TextureRegionDrawable newTextFieldCursorPixmap() {
		Pixmap pixmap = new Pixmap(3, Res.CUBE_SIZE, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.drawLine(2, 0, 2, Res.CUBE_SIZE);
		TextureRegion texture = new TextureRegion(new Texture(pixmap));
		pixmap.dispose();
		return new TextureRegionDrawable(texture);
	}

	private static void initFont() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/msyh.ttc"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + DefaultWorldConfString;

//		parameter.size = 12;
//		DefaultFontSmallSamll = generator.generateFont(parameter);
//		parameter.size = 16;
//		DefaultFontSmall = generator.generateFont(parameter);
		parameter.size = 20;
		DefaultFont = generator.generateFont(parameter);
//		parameter.size = 24;
//		DefaultFontLarge = generator.generateFont(parameter);
		generator.dispose();
	}

	private static TextureRegionDrawable newHumanEyeImage() {
		Pixmap pixmap = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap.setColor(Color.BLACK);
		pixmap.fillCircle(CUBE_SIZE * 3 / 10, CUBE_SIZE * 3 / 7, CUBE_SIZE / 12);
		pixmap.fillCircle(CUBE_SIZE * 7 / 10, CUBE_SIZE * 3 / 7, CUBE_SIZE / 12);
		TextureRegion texture = new TextureRegion(new Texture(pixmap));
		pixmap.dispose();
		return new TextureRegionDrawable(texture);
	}

	private static TextureRegionDrawable newHumanImage() {
		Pixmap pixmap = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap.setColor(new Color(0.8f, 0.1f, 0.1f, 1f));
		pixmap.fillRectangle(CUBE_BORDER_WIDTH_THIN, CUBE_BORDER_WIDTH_THIN, CUBE_SIZE - CUBE_BORDER_WIDTH_THIN * 2,
				CUBE_SIZE - CUBE_BORDER_WIDTH_THIN * 2);
		TextureRegion texture = new TextureRegion(new Texture(pixmap));
		pixmap.dispose();
		return new TextureRegionDrawable(texture);
	}

	private static TextureRegionDrawable newBorderImage(Color color) {
		Pixmap pixmap = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap.setColor(color);
		DrawUtils.drawBorder(pixmap, 1);
		TextureRegion texture = new TextureRegion(new Texture(pixmap));
		pixmap.dispose();
		return new TextureRegionDrawable(texture);
	}

	private static TextureRegionDrawable[] newBorderAsideImage(Color color) {
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
		return new TextureRegionDrawable[] { new TextureRegionDrawable(texture0), new TextureRegionDrawable(texture1),
				new TextureRegionDrawable(texture2), new TextureRegionDrawable(texture3) };
	}

	private static Pixmap newCursorPixmap() {
		Pixmap pixmap = new Pixmap(8, 8, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		return pixmap;
	}

	private static TextureRegionDrawable newColorTexture(int width, int height, Color color) {
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fill();
		TextureRegion texture = new TextureRegion(new Texture(pixmap));
		pixmap.dispose();
		return new TextureRegionDrawable(texture);
	}

	private static String getDefaultWorldConf() {
		return Gdx.files.internal("map.txt").readString("utf8");
	}

	private static String getDefaultZHCharacter() {
		return Gdx.files.internal("font/zh3500.txt").readString("utf8");
	}

	public static Label newDefaultLabel(String text) {
		return new Label(text, new LabelStyle(DefaultFont, Color.WHITE));
	}
}
