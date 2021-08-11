package com.backinfile.cube.view;

import com.backinfile.cube.Res;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

public class UIStage extends Stage {
	private TextField tipText;

	public UIStage(Viewport viewport) {
		super(viewport);

		init();
	}

	public void init() {
		// 初始化其他
		TextFieldStyle style = new TextFieldStyle();
		style.font = Res.DefaultFont;
		style.fontColor = Color.WHITE;
		tipText = new TextField("[text]", style);
		tipText.setSize(getWidth(), getHeight() / 4);
		tipText.setAlignment(Align.center);
		tipText.setPosition(getWidth() / 2, getHeight() / 8, Align.center);
		addActor(tipText);
	}

	public void setTipText(boolean visible, String content) {
		tipText.setVisible(visible);
		if (visible) {
			tipText.setText(content);
		}
	}
}
