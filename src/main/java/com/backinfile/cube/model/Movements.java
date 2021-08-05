package com.backinfile.cube.model;

import java.util.ArrayList;
import java.util.List;

import com.backinfile.cube.model.History.Movement;
import com.backinfile.cube.model.cubes.Cube;

public class Movements extends ArrayList<Movement> {
	private static final long serialVersionUID = 1L;

	public List<Cube> getCubeList() {
		List<Cube> cubes = new ArrayList<Cube>();
		for (Movement movement : this) {
			cubes.add(movement.cube);
		}
		return cubes;
	}
}
