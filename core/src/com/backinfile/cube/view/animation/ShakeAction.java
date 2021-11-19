package com.backinfile.cube.view.animation;

import com.backinfile.cube.support.Utils;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class ShakeAction extends TemporalAction {

	private float startX, startY;
	private float heavy;
	private boolean h;
	private boolean v;

	public ShakeAction(float duration, boolean h, boolean v) {
		super(duration);
		this.h = h;
		this.v = v;
	}

	protected void begin() {
		startX = actor.getX();
		startY = actor.getY();
		heavy = 2f;
	}

	protected void update(float percent) {
		if (h) {
			actor.setX(startX + (int) Utils.nextDouble(-heavy, heavy));
		}
		if (v) {
			actor.setY(startY + (int) Utils.nextDouble(-heavy, heavy));
		}
	}

	@Override
	protected void end() {
		actor.setPosition(startX, startY);
	}
}
