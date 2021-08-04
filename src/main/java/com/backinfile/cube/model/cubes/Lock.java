package com.backinfile.cube.model.cubes;

public class Lock extends Cube {
	private boolean locked = true;

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return !locked;
	}

}
