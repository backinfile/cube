package com.backinfile.cube.model.cubes;

public class Wall extends Cube {
	@Override
	public boolean isPushable() {
		return false;
	}
}
