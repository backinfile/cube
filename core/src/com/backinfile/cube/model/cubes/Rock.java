package com.backinfile.cube.model.cubes;

public class Rock extends Cube {
	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}
}
