package com.backinfile.cube.desktop;

import com.backinfile.cube.Log;
import com.backinfile.cube.Settings;
import com.backinfile.cube.controller.GameManager;
import com.backinfile.cube.controller.GameViewManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.backinfile.cube.MainGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
//		if (Settings.DEV) {
//			ReflectionUtils.initTimingClass();
//		}

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Settings.SCREEN_WIDTH;
		config.height = Settings.SCREEN_HEIGHT;
		config.resizable = Settings.RESIZABLE;
		config.title = Settings.TITLE;
		Log.core.info("creating game {}*{}", config.width, config.height);
		new LwjglApplication(new MainGame(), config);
	}
}
