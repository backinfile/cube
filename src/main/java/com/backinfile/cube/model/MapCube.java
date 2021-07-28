package com.backinfile.cube.model;

import com.backinfile.cube.model.cubes.Cube;

public class MapCube extends Cube {
	private String targetCoor;

	public MapCube(String targetCoor) {
		this.targetCoor = targetCoor;
	}

	public String getTargetCoor() {
		return targetCoor;
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	public boolean isMapCube() {
		return true;
	}
}
