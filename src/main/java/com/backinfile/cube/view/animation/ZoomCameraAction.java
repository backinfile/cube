package com.backinfile.cube.view.animation;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class ZoomCameraAction extends TemporalAction {
	private float startWidth, startHeight;
	private float endWidth, endHeight;
	private OrthographicCamera camera;

	protected void begin() {
		Camera cam = target.getStage().getCamera();
		if (cam instanceof OrthographicCamera) {
			OrthographicCamera camera = (OrthographicCamera) cam;
		}
		startWidth = camera.viewportWidth;
		startHeight = camera.viewportHeight;
	}

	protected void update(float percent) {
		camera.setToOrtho(false, startWidth + (endWidth - startWidth) * percent,
				startHeight + (endHeight - startHeight) * percent);
	}

	public void reset() {
		super.reset();
	}

	public void setEnd(float width, float height) {
		this.endWidth = width;
		this.endHeight = height;
	}

}
