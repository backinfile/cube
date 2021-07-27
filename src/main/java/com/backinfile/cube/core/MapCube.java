package com.backinfile.cube.core;

public class MapCube extends Cube {
	private char targetMap;

	public MapCube(char targetMap) {
		super();
		this.targetMap = targetMap;
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	public boolean canEnter() {
		return true;
	}
}
