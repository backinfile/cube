package com.backinfile.cube.view.editor;

import com.backinfile.cube.Res;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.utils.Align;

public class MapBar extends Group {
	private TextButton textButton;
	private TextField textField;

	public MapBar(float width, float height) {

		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.font = Res.DefaultFont;
		textButtonStyle.fontColor = Color.WHITE;
		textButton = new LineButton("default");
		textButton.setSize(width * 2 / 3, height * 4 / 5);
		textButton.setPosition(0, height / 10);

		TextFieldStyle textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = Res.DefaultFont;
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.cursor = Res.TextFieldCursor;
		textFieldStyle.selection = Res.TEX_BLUE;
		textField = new TextField("9*9", textFieldStyle);
		textField.setSize(width / 3, height);
		textField.setPosition(width * 2 / 3, 0);
		textField.setAlignment(Align.center);

		addActor(textButton);
		addActor(textField);
		setSize(width, height);
	}

	public void setText(String text) {
		textButton.setText(text);
	}

	public String getText() {
		return String.valueOf(textButton.getText());
	}

	public void setInputText(String text) {
		textField.setText(text);
	}

	public String getInputText() {
		return textField.getText();
	}

}
