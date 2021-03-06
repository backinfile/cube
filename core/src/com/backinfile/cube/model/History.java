package com.backinfile.cube.model;

import java.util.ArrayList;
import java.util.List;

import com.backinfile.cube.model.cubes.Cube;

public class History {
	public static class Movement {
		public Cube cube;
		public Position position;

		public Movement() {
		}

		public Movement(Cube cube, Position position) {
			this.cube = cube;
			this.position = position;
		}

		@Override
		public String toString() {
			return cube.toString() + "<-" + position;
		}
	}

	private List<Movement> movements = new ArrayList<>();

	private History() {
	}

	public static History getHistory(List<Cube> cubes) {
		History history = new History();
		for (Cube cube : cubes) {
			Movement movement = new Movement();
			movement.cube = cube;
			movement.position = cube.position.copy();
			history.movements.add(movement);
		}
		return history;
	}

	public List<Movement> getMovements() {
		return movements;
	}

	@Override
	public String toString() {
		return movements.toString();
	}
}
