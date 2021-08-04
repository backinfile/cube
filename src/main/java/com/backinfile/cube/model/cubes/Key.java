package com.backinfile.cube.model.cubes;

public class Key extends MapCube {

	public Key(String targetCoor) {
		super(targetCoor);
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean canEnter() {
		return false;
	}

	@Override
	public boolean isSelectedFirst() {
		return false;
	}
}
