package com.backinfile.cube.model.cubes;

import com.backinfile.cube.model.Position;
import com.backinfile.cube.model.WorldUnit;

public abstract class Cube extends WorldUnit {
	private static long idMax = 0;
	private long id;
	private Position lastPosition = null;

	public void moveTo(Position newPosition) {
		if (newPosition.equals(this.position)) {
			return;
		}
		lastPosition = new Position(this.position);
		position.setPosition(newPosition);
	}

	public Position getLastPosition() {
		return lastPosition;
	}

	public Cube() {
		id = idMax++;
	}
	
	public long getId() {
		return id;
	}

	/**
	 * 是否可以视为空地
	 */
	public abstract boolean isEmpty();

	/**
	 * 是否可以推动
	 */
	public abstract boolean isPushable();

	public boolean canEnter() {
		return false;
	}
}
