package com.backinfile.cube.view;

import com.backinfile.cube.model.MapData;
import com.backinfile.cube.model.Vector;
import com.badlogic.gdx.scenes.scene2d.Group;

public class CubeViewGroup extends Group {
	// 默认为0，越小距离屏幕越远
	private int layer = 0;
	private MapData mapData;
	private Vector savePos = new Vector();

	public CubeViewGroup(MapData mapData) {
		this.mapData = mapData;
		setVisible(false);
	}

	public void savePos(float x, float y) {
		savePos.set(x, y);
	}

	public Vector getSavePos() {
		return savePos;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public int getLayer() {
		return layer;
	}
}
