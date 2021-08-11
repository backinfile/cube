package com.backinfile.cube.view.animation;

import com.backinfile.cube.support.Utils;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class ShakeCameraAction extends TemporalAction {
	private float startX, startY;
	private OrthographicCamera camera;
	private int heavy;
	private boolean h;
	private boolean v;

	public ShakeCameraAction(float duration, int heavy, boolean h, boolean v) {
		super(duration);
		this.heavy = heavy;
		this.h = h;
		this.v = v;
	}

	protected void begin() {
		Camera cam = target.getStage().getCamera();
		if (cam instanceof OrthographicCamera) {
			camera = (OrthographicCamera) cam;
		}
		startX = camera.position.x;
		startY = camera.position.y;
	}

	protected void update(float percent) {
		if (h) {
			camera.position.x = startX + (int) Utils.nextDouble(-heavy, heavy);
		}
		if (v) {
			camera.position.y = startY + (int) Utils.nextDouble(-heavy, heavy);
		}
		camera.update();
		actor.getStage().getBatch().setProjectionMatrix(camera.combined);
	}

	@Override
	protected void end() {
		camera.position.x = startX;
		camera.position.y = startY;
		camera.update();
		actor.getStage().getBatch().setProjectionMatrix(camera.combined);
	}

}
