package com.backinfile.cube.model;

import com.backinfile.cube.model.cubes.Cube;

public class MapCube extends Cube {
	private char targetMapChar;

	public MapCube(char targetMap) {
		this.targetMapChar = targetMap;
	}

	public char getTargetMapChar() {
		return targetMapChar;
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
