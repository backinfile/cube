package com.backinfile.cube.view.animation;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class ZoomCameraAction extends TemporalAction {
	private float startWidth, startHeight;
	private float endWidth, endHeight;
	private float startX, startY;
	private float endX, endY;
	private OrthographicCamera camera;

	protected void begin() {
		Camera cam = target.getStage().getCamera();
		if (cam instanceof OrthographicCamera) {
			camera = (OrthographicCamera) cam;
		}
		startWidth = camera.viewportWidth;
		startHeight = camera.viewportHeight;
		startX = camera.position.x;
		startY = camera.position.y;
	}

	protected void update(float percent) {
		camera.viewportWidth = startWidth + (endWidth - startWidth) * percent;
		camera.viewportHeight = startHeight + (endHeight - startHeight) * percent;
		camera.position.x = startX + (endX - startX) * percent;
		camera.position.y = startY + (endY - startY) * percent;
		camera.update();
		target.getStage().getBatch().setProjectionMatrix(camera.combined);
	}

	public void reset() {
		super.reset();
	}

	public void setEndSize(float width, float height) {
		this.endWidth = width;
		this.endHeight = height;
	}

	public void setEndPos(float x, float y) {
		this.endX = x;
		this.endY = y;
	}

}
