package com.backinfile.cube.view;

import com.badlogic.gdx.scenes.scene2d.Group;

public class CubeViewGroup extends Group {
	// 默认为0，越小距离屏幕越远
	private int layer = 0;

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public int getLayer() {
		return layer;
	}
}
