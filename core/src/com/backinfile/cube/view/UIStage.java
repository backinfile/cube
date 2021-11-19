package com.backinfile.cube.view;

import com.backinfile.cube.Res;
import com.backinfile.cube.controller.GameManager;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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

        initArrowButton();
        initControlButton();
        if (Gdx.app.getType() == Application.ApplicationType.Android) {

        }
    }

    private void initControlButton() {
        int iconWidth = Res.BUTTON_LEFT.getRegion().getRegionWidth();
        int iconHeight = Res.BUTTON_LEFT.getRegion().getRegionHeight();

        float offsetRate = 0.667f;

        {
            Button btn = new Button(new Button.ButtonStyle(Res.BUTTON_CANCEL, null, null));
            btn.setPosition(getWidth() - iconWidth - iconWidth * offsetRate, iconHeight * offsetRate);
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GameManager.instance.undo();
                }
            });
            addActor(btn);
        }
        {
            Button btn = new Button(new Button.ButtonStyle(Res.BUTTON_EXIT, null, null));
            btn.setPosition(getWidth() - iconWidth - iconWidth * offsetRate, getHeight() - iconWidth * 0.5f - iconHeight * offsetRate);
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.exit();
                }
            });
            addActor(btn);
        }
    }

    private void initArrowButton() {
        int iconWidth = Res.BUTTON_LEFT.getRegion().getRegionWidth();
        int iconHeight = Res.BUTTON_LEFT.getRegion().getRegionHeight();


        float offsetRate = 0.8f;

        Group arrowButtonGroup = new Group();
        addActor(arrowButtonGroup);
        arrowButtonGroup.setPosition(iconWidth / 2f, iconHeight / 2f);

        {
            Button btn = new Button(new Button.ButtonStyle(Res.BUTTON_LEFT, null, null));
            btn.setPosition(0, iconHeight * offsetRate);
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GameManager.instance.moveHuman(2);
                }
            });
            arrowButtonGroup.addActor(btn);
        }
        {
            Button btn = new Button(new Button.ButtonStyle(Res.BUTTON_DOWN, null, null));
            btn.setPosition(iconWidth * offsetRate, 0);
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GameManager.instance.moveHuman(1);
                }
            });
            arrowButtonGroup.addActor(btn);
        }
        {
            Button btn = new Button(new Button.ButtonStyle(Res.BUTTON_UP, null, null));
            btn.setPosition(iconWidth * offsetRate, iconHeight * 2 * offsetRate);
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GameManager.instance.moveHuman(0);
                }
            });
            arrowButtonGroup.addActor(btn);
        }
        {
            Button btn = new Button(new Button.ButtonStyle(Res.BUTTON_RIGHT, null, null));
            btn.setPosition(iconWidth * offsetRate * 2, iconHeight * offsetRate);
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GameManager.instance.moveHuman(3);
                }
            });
            arrowButtonGroup.addActor(btn);
        }
    }

    public void setTipText(boolean visible, String content) {
        tipText.setVisible(visible);
        if (visible) {
            tipText.setText(content);
        }
    }

    public static class ButtonInputListener extends ClickListener {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            event.getTarget().setColor(Color.GRAY);
            return super.touchDown(event, x, y, pointer, button);
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            event.getTarget().setColor(Color.WHITE);
            super.touchUp(event, x, y, pointer, button);
        }
    }
}
