package com.backinfile.cube;

import com.backinfile.cube.support.ReflectionUtils;
import com.backinfile.cube.view.MainGame;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main(String[] args) {
		if (Settings.DEV) {
			ReflectionUtils.initTimingClass();
		}

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Settings.SCREEN_WIDTH;
		config.height = Settings.SCREEN_HEIGHT;
		config.resizable = Settings.RESIZABLE;
		Log.core.info("creating game {}*{}", config.width, config.height);
		new LwjglApplication(new MainGame(), config);
	}
}
