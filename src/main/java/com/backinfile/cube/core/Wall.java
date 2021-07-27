package com.backinfile.cube.core;

public class Wall extends Cube {
	@Override
	public boolean isPushable() {
		return false;
	}
}
