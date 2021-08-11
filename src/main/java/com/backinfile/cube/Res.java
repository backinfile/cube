package com.backinfile.cube;

import java.nio.ByteBuffer;
import java.util.HashSet;

import com.backinfile.cube.support.DrawUtils;
import com.backinfile.cube.support.TimeLogger;
import com.backinfile.cube.support.Timing;
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
	public static final int CUBE_SIZE = 64;
	public static final int CUBE_BORDER_WIDTH = CUBE_SIZE * 5 / 50;
	public static final int CUBE_BORDER_WIDTH_THIN = 1;
	public static final float FLOOR_ELE_ALPHA = 0.3f;

	public static TextureRegionDrawable TEX_WHITE;
	public static TextureRegionDrawable TEX_BLACK;
	public static TextureRegionDrawable TEX_GRAY;
	public static TextureRegionDrawable TEX_LIGHT;
	public static TextureRegionDrawable TEX_DARK;
	public static TextureRegionDrawable TEX_BLUE;

	public static TextureRegionDrawable CUBE_BORDER_BLACK;
	public static TextureRegionDrawable CUBE_BORDER_WHITE;
	public static TextureRegionDrawable CUBE_BORDER_DARK;
	public static TextureRegionDrawable CUBE_BORDER_BLUE;
	public static TextureRegionDrawable[] CUBE_BORDER_ASIDE;

	public static TextureRegionDrawable CUBE_FLOWER;
	public static TextureRegionDrawable CUBE_WALL;
	public static TextureRegionDrawable CUBE_ROCK;
	public static TextureRegionDrawable CUBE_TARGET;
	public static TextureRegionDrawable CUBE_HUMAN;
	public static TextureRegionDrawable CUBE_HUMAN_EYE;
	public static TextureRegionDrawable CUBE_ALPHA_MASK;
	public static TextureRegionDrawable CUBE_LOCK;

	public static Pixmap Cursor;
	public static TextureRegionDrawable TextFieldCursor;

	public static BitmapFont DefaultFontSmallSamll;
	public static BitmapFont DefaultFontSmall;
	public static BitmapFont DefaultFontLarge;
	public static BitmapFont DefaultFont;

	public static String STR_TUTORIAL = "WASD to move, Z to undo";
	public static String STR_THANKS = "Thanks for playing!";

	public static String DefaultWorldConfString = "";
	public static String DefaultWorldConfStringByTiled = "";
	private static FontCharacterCollection fontCharacterCollection = new FontCharacterCollection();

	public static void init() {
		initImage();
		initText();
		initFont();

		if (Settings.DEV) {
			saveImagesToFile();
		}
	}

	private static class FontCharacterCollection {
		private HashSet<Character> characters = new HashSet<Character>();

		public void put(String str) {
			for (int i = 0; i < str.length(); i++) {
				characters.add(str.charAt(i));
			}
		}

		public String getAll() {
			StringBuilder sb = new StringBuilder();
			for (Character ch : characters) {
				sb.append(ch);
			}
			return sb.toString();
		}
	}

	private static TextureRegionDrawable getDrawable(Pixmap pixmap) {
		Texture texture = new Texture(pixmap, true);
		TextureRegion region = new TextureRegion(texture);
		pixmap.dispose();
		return new TextureRegionDrawable(region);
	}

	@Timing
	private static void initText() {
		DefaultWorldConfString = ""; // Gdx.files.internal("map.txt").readString();
		DefaultWorldConfStringByTiled = Gdx.files.local("assets/tiled/world.json").readString();

		fontCharacterCollection.put(DefaultWorldConfString);
		fontCharacterCollection.put(DefaultWorldConfStringByTiled);
	}

	@Timing
	private static void initImage() {

		TEX_BLACK = getDrawable(newColorPixmap(8, 8, Color.BLACK));
		TEX_DARK = getDrawable(newColorPixmap(8, 8, Color.DARK_GRAY));
		TEX_GRAY = getDrawable(newColorPixmap(8, 8, Color.GRAY));
		TEX_LIGHT = getDrawable(newColorPixmap(8, 8, Color.LIGHT_GRAY));
		TEX_WHITE = getDrawable(newColorPixmap(8, 8, Color.WHITE));
		TEX_BLUE = getDrawable(newColorPixmap(8, 8, Color.BLUE));

		CUBE_BORDER_BLACK = getDrawable(newBorderImage(Color.BLACK));
		CUBE_BORDER_WHITE = getDrawable(newBorderImage(new Color(1f, 1f, 1f, 0.6f)));
		CUBE_BORDER_DARK = getDrawable(newBorderImage(Color.DARK_GRAY));
		CUBE_BORDER_BLUE = getDrawable(newBorderImage(new Color(0f, 0f, 1f, 0.6f)));
		Pixmap[] newBorderAsideImage = newBorderAsideImage(Color.BLACK);
		CUBE_BORDER_ASIDE = new TextureRegionDrawable[newBorderAsideImage.length];
		for (int i = 0; i < newBorderAsideImage.length; i++) {
			CUBE_BORDER_ASIDE[i] = getDrawable(newBorderAsideImage[i]);
		}

		CUBE_FLOWER = getDrawable(newColorPixmap(CUBE_SIZE, CUBE_SIZE, Color.LIGHT_GRAY));
		CUBE_WALL = getDrawable(newWallImage(CUBE_SIZE));
		CUBE_ROCK = getDrawable(newWallImage(CUBE_SIZE));
		CUBE_HUMAN = getDrawable(newHumanImage(CUBE_SIZE));
		CUBE_HUMAN_EYE = getDrawable(newHumanEyeImage(CUBE_SIZE));
		CUBE_ALPHA_MASK = getDrawable(newColorPixmap(CUBE_SIZE, CUBE_SIZE, new Color(1, 1, 1, 0.5f)));
		CUBE_LOCK = getDrawable(newLockImage(CUBE_SIZE));

		Cursor = newCursorPixmap();
		TextFieldCursor = getDrawable(newTextFieldCursorPixmap());
	}

	@Timing
	private static void initFont() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("font/JetBrainsMono-VariableFont_wght.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		fontCharacterCollection.put(FreeTypeFontGenerator.DEFAULT_CHARS);
		parameter.characters = fontCharacterCollection.getAll();

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

	private static Pixmap newLockImage(int size) {
		Pixmap pixmap = new Pixmap(size, size, Format.RGBA8888);
		Pixmap targetPixmap = null;
		if (size > 32) {
			targetPixmap = new Pixmap(Gdx.files.internal("cube/lock64.png"));
		} else {
			targetPixmap = new Pixmap(Gdx.files.internal("cube/lock32.png"));
		}
		pixmap.drawPixmap(targetPixmap, 0, 0, 0, 0, targetPixmap.getWidth(), targetPixmap.getHeight());
		targetPixmap.dispose();
		return pixmap;
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
		DrawUtils.drawEye(pixmap, size * 5 / 16, size * 3 / 8, size * 3 / 16);
		DrawUtils.drawEye(pixmap, size * 11 / 16, size * 3 / 8, size * 3 / 16);
		return pixmap;
	}

	private static Pixmap newHumanImage(int size) {
		Pixmap pixmap = new Pixmap(size, size, Format.RGBA8888);
		pixmap.setColor(new Color(0.7f, 0.1f, 0.1f, 1f));
		pixmap.fillRectangle(CUBE_BORDER_WIDTH_THIN, CUBE_BORDER_WIDTH_THIN, CUBE_SIZE - CUBE_BORDER_WIDTH_THIN * 2,
				CUBE_SIZE - CUBE_BORDER_WIDTH_THIN * 2);
		return pixmap;
	}

	private static Pixmap newWhiteBorderImage() {
		Pixmap pixmap = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap.setColor(new Color(1f, 1f, 1f, 0.6f));
		DrawUtils.drawBorder(pixmap, 2);
		return pixmap;
	}

	private static Pixmap newBorderImage(Color color) {
		Pixmap pixmap = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap.setColor(color);
		DrawUtils.drawBorder(pixmap, 2);
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
		Pixmap pixmap4 = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap4.setColor(color);
		pixmap4.fillRectangle(0, 0, CUBE_BORDER_WIDTH, CUBE_BORDER_WIDTH);
		Pixmap pixmap5 = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap5.setColor(color);
		pixmap5.fillRectangle(CUBE_SIZE - CUBE_BORDER_WIDTH, CUBE_SIZE - CUBE_BORDER_WIDTH, CUBE_BORDER_WIDTH,
				CUBE_BORDER_WIDTH);
		Pixmap pixmap6 = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap6.setColor(color);
		pixmap6.fillRectangle(0, CUBE_SIZE - CUBE_BORDER_WIDTH, CUBE_BORDER_WIDTH, CUBE_BORDER_WIDTH);
		Pixmap pixmap7 = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		pixmap7.setColor(color);
		pixmap7.fillRectangle(CUBE_SIZE - CUBE_BORDER_WIDTH, 0, CUBE_BORDER_WIDTH, CUBE_BORDER_WIDTH);
		Pixmap pixmap8 = new Pixmap(CUBE_SIZE, CUBE_SIZE, Format.RGBA8888);
		return new Pixmap[] { pixmap0, pixmap1, pixmap2, pixmap3, pixmap6, pixmap7, pixmap4, pixmap5 };
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

	@Timing
	private static void saveImagesToFile() {
		int SIZE = 32;
		int width = SIZE * 7;
		int height = SIZE;
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);

		{
			Pixmap wallImage = newWallImage(SIZE);
			pixmap.drawPixmap(wallImage, 0, 0);
			wallImage.dispose();
		}
		{
			Pixmap rockImage = newRockImage(SIZE);
			pixmap.drawPixmap(rockImage, SIZE, 0);
			rockImage.dispose();
		}
		// human
		{
			Pixmap humanImage = newHumanImage(SIZE);
			pixmap.drawPixmap(humanImage, SIZE * 2, 0);
			humanImage.dispose();
			Pixmap newHumanEyeImage = newHumanEyeImage(SIZE);
			pixmap.drawPixmap(newHumanEyeImage, SIZE * 2, 0);
			newHumanEyeImage.dispose();
		}
		// mapCube movable
		{
			Pixmap mapCubeImage = newColorPixmap(SIZE, SIZE, Color.LIGHT_GRAY);
			mapCubeImage.setColor(Color.DARK_GRAY);
			mapCubeImage.fillRectangle(SIZE / 8, SIZE / 8, SIZE * 6 / 8, SIZE * 6 / 8);
			mapCubeImage.setColor(Color.WHITE);
			DrawUtils.drawBorder(mapCubeImage, SIZE / 8);
			pixmap.drawPixmap(mapCubeImage, SIZE * 3, 0);
			mapCubeImage.dispose();
		}
		// mapCube fixed
		{
			Pixmap mapCubeImage = newColorPixmap(SIZE, SIZE, Color.LIGHT_GRAY);
			mapCubeImage.setColor(Color.DARK_GRAY);
			mapCubeImage.fillRectangle(SIZE / 8, SIZE / 8, SIZE * 6 / 8, SIZE * 6 / 8);
			mapCubeImage.setColor(Color.BLACK);
			DrawUtils.drawBorder(mapCubeImage, SIZE / 8);
			pixmap.drawPixmap(mapCubeImage, SIZE * 4, 0);
			mapCubeImage.dispose();
		}
		// fixedKey
		{
			Pixmap mapCubeImage = newColorPixmap(SIZE, SIZE, Color.LIGHT_GRAY);
			mapCubeImage.setColor(Color.DARK_GRAY);
			mapCubeImage.fillRectangle(SIZE / 8, SIZE / 8, SIZE * 6 / 8, SIZE * 6 / 8);
			mapCubeImage.setColor(Color.BLACK);
			DrawUtils.drawBorder(mapCubeImage, SIZE / 8);
			mapCubeImage.drawLine(SIZE * 2 / 5, 0, SIZE / 4, SIZE);
			mapCubeImage.drawLine(SIZE * 2 / 5, SIZE / 2, SIZE, SIZE);
			mapCubeImage.drawLine(SIZE * 2 / 5, SIZE / 2, SIZE, 0);
			pixmap.drawPixmap(mapCubeImage, SIZE * 5, 0);
			mapCubeImage.dispose();
		}
		// lock
		{
			Pixmap mapCubeImage = newColorPixmap(SIZE, SIZE, Color.LIGHT_GRAY);
			mapCubeImage.setColor(Color.DARK_GRAY);
			mapCubeImage.fillRectangle(SIZE / 8, SIZE / 8, SIZE * 6 / 8, SIZE * 6 / 8);
			mapCubeImage.setColor(Color.BLACK);
			DrawUtils.drawBorder(mapCubeImage, SIZE / 8);
			mapCubeImage.drawPixmap(newLockImage(SIZE), 0, 0);
			pixmap.drawPixmap(mapCubeImage, SIZE * 6, 0);
			mapCubeImage.dispose();
		}

		PixmapIO.writePNG(Gdx.files.local("gen/cubes.png"), pixmap);
		pixmap.dispose();
		Log.game.info("generate png at gen/cubes.png");
	}
}
