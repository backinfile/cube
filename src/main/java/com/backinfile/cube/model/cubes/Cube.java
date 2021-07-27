package com.backinfile.cube.model.cubes;

import com.backinfile.cube.model.WorldUnit;

public abstract class Cube extends WorldUnit {
	public abstract boolean isPushable();

	public boolean isMapCube() {
		return false;
	}
}
