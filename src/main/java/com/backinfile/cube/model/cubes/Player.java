package com.backinfile.cube.model.cubes;

public class Player extends Human {

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

}
