package com.backinfile.cube.core;

public abstract class Cube extends WorldUnit {
	public abstract boolean isPushable();

	public boolean canEnter() {
		return false;
	}

	@Override
	public String toString() {
		return position.toString();
	}

}
