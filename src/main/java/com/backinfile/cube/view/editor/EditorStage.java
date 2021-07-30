package com.backinfile.cube.view.editor;

import com.backinfile.cube.Log;
import com.backinfile.cube.Res;
import com.backinfile.cube.model.MapData;
import com.backinfile.cube.model.WorldData;
import com.backinfile.cube.support.Assertion;
import com.backinfile.cube.support.ObjectPool;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EditorStage extends Stage {

	private WorldData worldData = new WorldData();
	private MapData curMapData;
	private CubeGroup cubeGroup;
	private MapBarGroup mapBarGroup;
	private Image background = new Image(Res.TEX_DARK);
	private Label curCoorLabel = Res.newDefaultLabel("[coor]");

	public EditorStage(Viewport viewport) {
		super(viewport);

		init();

		updateView();
	}

	private void init() {
		// 初始化数据
		curMapData = new MapData();
		curMapData = WorldData.parse(Res.DefaultWorldConfString).getMapData("aa");
		curMapData.setSize(9, 9);
		curMapData.coor = "a";
		worldData.getDatas().add(curMapData);

		// 初始化view
		background.setSize(getWidth(), getHeight());
		addActor(background);
		curCoorLabel.setPosition(getWidth() / 2, getHeight() - Res.CUBE_SIZE);
		addActor(curCoorLabel);
		mapBarGroup = new MapBarGroup(getWidth() / 5, getHeight() * 5 / 6);
		mapBarGroup.setPosition(0, getHeight() / 12);
		addActor(mapBarGroup);

		// 初始化cube
		cubeGroup = new CubeGroup();
		cubeGroup.setPosition(getWidth() / 5, 0);
		addActor(cubeGroup);

		// 使TextField失去焦点
		getRoot().addCaptureListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (!(event.getTarget() instanceof TextField))
					setKeyboardFocus(null);
				return false;
			}
		});

		// 手动调整世界大小
		addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (!focused && actor instanceof TextField) {
					if (actor.getParent() instanceof MapBar) {
						MapBar mapBar = (MapBar) actor.getParent();
						String inputText = mapBar.getInputText();
						Log.view.info("unfocused:{}", inputText);
						try {
							String[] split = inputText.split("\\*");
							Assertion.assertEqual(split.length, 2);
							int width = Integer.valueOf(split[0]);
							int height = Integer.valueOf(split[1]);
							Assertion.assertLargeThen(CubeGroup.CUBE_LENGTH, width);
							Assertion.assertLargeThen(CubeGroup.CUBE_LENGTH, height);
							MapData mapData = worldData.getMapData(mapBar.getText());
							mapData.setSize(width, height);
							if (curMapData == mapData) {
								updateView();
							}
						} catch (Exception e) {
							mapBar.setInputText("9*9");
						}
					}
				}
			}
		});

	}

	public void updateView() {
		curCoorLabel.setText(curMapData.coor);
		mapBarGroup.setByData(worldData, curMapData.coor);
		cubeGroup.setByData(curMapData);
	}
}
