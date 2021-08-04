package com.backinfile.cube;

import java.nio.ByteBuffer;

import com.backinfile.cube.support.DrawUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
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
	public static String DefaultWorldConfStringByTiled = "";

	public static void init() {
		initImage();
		initText();
		initFont();

		saveImagesToFile();
	}

	private static TextureRegionDrawable getDrawable(Pixmap pixmap) {
		TextureRegion region = new TextureRegion(new Texture(pixmap));
		pixmap.dispose();
		return new TextureRegionDrawable(region);
	}

	private static void initText() {
		DefaultWorldConfString = getDefaultWorldConf();
	}

	private static void initImage() {

		TEX_BLACK = getDrawable(newColorPixmap(8, 8, Color.BLACK));
		TEX_DARK = getDrawable(newColorPixmap(8, 8, Color.DARK_GRAY));
		TEX_GRAY = getDrawable(newColorPixmap(8, 8, Color.GRAY));
		TEX_LIGHT = getDrawable(newColorPixmap(8, 8, Color.LIGHT_GRAY));
		TEX_WHITE = getDrawable(newColorPixmap(8, 8, Color.WHITE));
		TEX_BLUE = getDrawable(newColorPixmap(8, 8, Color.BLUE));

		CUBE_BORDER_BLACK = getDrawable(newBorderImage(Color.BLACK));
		CUBE_BORDER_WHITE = getDrawable(newBorderImage(Color.WHITE));
		CUBE_BORDER_DARK = getDrawable(newBorderImage(Color.DARK_GRAY));
		Pixmap[] newBorderAsideImage = newBorderAsideImage(Color.BLACK);
		CUBE_BORDER_ASIDE = new TextureRegionDrawable[] { getDrawable(newBorderAsideImage[0]),
				getDrawable(newBorderAsideImage[1]), getDrawable(newBorderAsideImage[2]),
				getDrawable(newBorderAsideImage[3]), };

		CUBE_FLOWER = getDrawable(newColorPixmap(CUBE_SIZE, CUBE_SIZE, Color.LIGHT_GRAY));
		CUBE_WALL = getDrawable(newWallImage(CUBE_SIZE));
		CUBE_ROCK = getDrawable(newRockImage(CUBE_SIZE));
		CUBE_HUMAN = getDrawable(newHumanImage(CUBE_SIZE));
		CUBE_HUMAN_EYE = getDrawable(newHumanEyeImage(CUBE_SIZE));

		Cursor = newCursorPixmap();
		TextFieldCursor = getDrawable(newTextFieldCursorPixmap());
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

	private static Pixmap newWallImage(int size) {
		return newColorPixmap(size, size, Color.DARK_GRAY);
	}

	private static Pixmap newRockImage(int size) {
		return newColorPixmap(size, size, new Color(0.67f, 0.67f, 0.67f, 1));
	}

	private static Pixmap newTextFieldCursorPixmap() {
		Pixmap pixmap = new Pixmap(3, Res.CUBE_SIZE, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.drawLine(2, 0, 2, Res.CUBE_SIZE);
		return pixmap;
	}

	private static Pixmap newHumanEyeImage(int size) {
		Pixmap pixmap = new Pixmap(size, size, Format.RGBA8888);
		pixmap.setColor(Color.BLACK);
		pixmap.fillCircle(size * 3 / 10, size * 3 / 7, size / 12);
		pixmap.fillCircle(size * 7 / 10, size * 3 / 7, size / 12);
		return pixmap;
	}

	private static Pixmap newHumanImage(int size) {
		Pixmap pixmap = new Pixmap(size, size, Format.RGBA8888);
		pixmap.setColor(new Color(0.8f, 0.1f, 0.1f, 1f));
		pixmap.fillRectangle(CUBE_BORDER_WIDTH_THIN, CUBE_BORDER_WIDTH_THIN, CUBE_SIZE - CUBE_BORDER_WIDTH_THIN * 2,
				CUBE_SIZE - CUBE_BORDER_WIDTH_THIN * 2);
		return pixmap;
	}

	private static Pixmap newBorderImage(Color color) {
		Pixmap pixmap = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap.setColor(color);
		DrawUtils.drawBorder(pixmap, 1);
		return pixmap;
	}

	private static Pixmap[] newBorderAsideImage(Color color) {
		Pixmap pixmap0 = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap0.setColor(color);
		pixmap0.fillRectangle(0, 0, CUBE_SIZE, CUBE_BORDER_WIDTH);
		Pixmap pixmap1 = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap1.setColor(color);
		pixmap1.fillRectangle(0, CUBE_SIZE - CUBE_BORDER_WIDTH, CUBE_SIZE, CUBE_SIZE);
		Pixmap pixmap2 = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap2.setColor(color);
		pixmap2.fillRectangle(0, 0, CUBE_BORDER_WIDTH, CUBE_SIZE);
		Pixmap pixmap3 = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap3.setColor(color);
		pixmap3.fillRectangle(CUBE_SIZE - CUBE_BORDER_WIDTH, 0, CUBE_SIZE, CUBE_SIZE);
		return new Pixmap[] { pixmap0, pixmap1, pixmap2, pixmap3 };
	}

	private static Pixmap newCursorPixmap() {
		Pixmap pixmap = new Pixmap(8, 8, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		return pixmap;
	}

	private static Pixmap newColorPixmap(int width, int height, Color color) {
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fill();
		return pixmap;
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

	private static void saveImagesToFile() {
		int SIZE = 32;
		int width = SIZE * 3;
		int height = SIZE;
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);

		Pixmap wallImage = newWallImage(SIZE);
		pixmap.drawPixmap(wallImage, 0, 0);
		wallImage.dispose();

		Pixmap rockImage = newRockImage(SIZE);
		pixmap.drawPixmap(rockImage, SIZE, 0);
		rockImage.dispose();

		Pixmap humanImage = newHumanImage(SIZE);
		pixmap.drawPixmap(humanImage, SIZE * 2, 0);
		humanImage.dispose();

		Pixmap newHumanEyeImage = newHumanEyeImage(SIZE);
		pixmap.drawPixmap(newHumanEyeImage, SIZE * 2, 0);
		newHumanEyeImage.dispose();

		PixmapIO.writePNG(Gdx.files.local("gen/cubes.png"), pixmap);
		pixmap.dispose();
	}
}
