package com.backinfile.cube.view.editor;

import com.backinfile.cube.Res;
import com.backinfile.cube.model.MapData;
import com.backinfile.cube.model.WorldData;
import com.backinfile.cube.support.ObjectPool;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EditorStage extends Stage {

	private ObjectPool<EditorCubeView> objectPool;
	private WorldData worldData = new WorldData();
	private MapData curMapData;
	private Group cubeGroup = new Group();
	private MapBarGroup mapBarGroup;
	private Image background = new Image(Res.TEX_DARK);

	private LabelStyle labelStyle = new LabelStyle(Res.DefaultFont, Color.WHITE);
	private TextFieldStyle textFieldStyle = new TextFieldStyle();

	public EditorStage(Viewport viewport) {
		super(viewport);

		init();
	}

	private void init() {
		// 初始化数据
		curMapData = new MapData();
		curMapData.width = 9;
		curMapData.height = 9;
		curMapData.coor = "a";

		// 初始化style
		textFieldStyle.background = Res.CUBE_BORDER_DARK;
		textFieldStyle.font = Res.DefaultFont;
		textFieldStyle.fontColor = Color.WHITE;

		// 初始化view
		background.setSize(getWidth(), getHeight());
		addActor(background);
		mapBarGroup = new MapBarGroup(getWidth() / 6, getHeight());
		addActor(mapBarGroup);

		// 初始化cube
		objectPool = new ObjectPool<>(() -> {
			return new EditorCubeView();
		});

		// 使TextField失去焦点
		getRoot().addCaptureListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (!(event.getTarget() instanceof TextField))
					setKeyboardFocus(null);
				return false;
			}
		});
	}
}
