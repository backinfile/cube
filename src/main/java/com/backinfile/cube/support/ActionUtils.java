package com.backinfile.cube.support;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SizeToAction;

public class ActionUtils {
	public static void moveTo(Actor actor, float x, float y, float duration) {
		MoveToAction moveToAction = new MoveToAction();
		moveToAction.setDuration(duration);
		moveToAction.setPosition(x, y);
		actor.addAction(moveToAction);
	}

	public static void moveTo(Actor actor, float x, float y) {
		moveTo(actor, x, y, 0.1f);
	}

	public static void sizeTo(Actor actor, float w, float h) {
		SizeToAction sizeToAction = new SizeToAction();
		sizeToAction.setDuration(0.2f);
		sizeToAction.setSize(w, h);
		actor.addAction(sizeToAction);
	}
}