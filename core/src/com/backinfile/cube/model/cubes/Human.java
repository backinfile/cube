package com.backinfile.cube.model.cubes;

import com.backinfile.cube.model.Vector;

public class Human extends Cube {
	public Vector lastMove = new Vector();

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

}
