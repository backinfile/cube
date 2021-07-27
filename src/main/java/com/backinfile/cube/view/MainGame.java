package com.backinfile.cube.view;

import com.backinfile.cube.Res;
import com.backinfile.cube.controller.GameManager;
import com.badlogic.gdx.Game;

public class MainGame extends Game {

	@Override
	public void create() {
		// 加载资源
		Res.init();
		// 初始化地图
		GameManager.instance.init();
		// 加载场景
		setScreen(new GameScreen());
		
//		Gdx.input.setCursorImage(Res.Cursor, 0, 0);
		
	}

}