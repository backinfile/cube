package com.backinfile.cube.model.cubes;

import com.backinfile.cube.model.Position;
import com.backinfile.cube.model.WorldUnit;

public abstract class Cube extends WorldUnit {
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
