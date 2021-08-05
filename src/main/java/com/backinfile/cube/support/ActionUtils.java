package com.backinfile.cube.support;

import com.backinfile.cube.Log;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SizeToAction;

public class ActionUtils {

	public static void moveTo(Actor actor, float x, float y, float duration) {
		MoveToAction moveToAction = new MoveToAction();
		moveToAction.setDuration(duration);
		moveToAction.setPosition(x, y);
		actor.addAction(moveToAction);
		Log.game.info("from {},{} to {},{}", actor.getX(), actor.getY(), x, y);
	}

	public static void moveTo(Actor actor, float x, float y) {
		moveTo(actor, x, y, 0.1f);
	}

	public static void sizeTo(Actor actor, float w, float h, float duration) {
		SizeToAction sizeToAction = new SizeToAction();
		sizeToAction.setDuration(duration);
		sizeToAction.setSize(w, h);
		actor.addAction(sizeToAction);

	}

	public static void sizeTo(Actor actor, float w, float h) {
		sizeTo(actor, w, h, 0.1f);
	}
}
