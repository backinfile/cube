package com.backinfile.cube.core;

import java.util.Objects;

public class Position {
	public int x;
	public int y;
	public int world;

	public Position() {
		this(0, 0, 0);
	}

	public Position(int x, int y) {
		this(x, y, 0);
	}

	public Position(int x, int y, int world) {
		this.x = x;
		this.y = y;
		this.world = world;
	}

	public void setPosition(Position position) {
		this.x = position.x;
		this.y = position.y;
		this.world = position.world;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setPosition(Vector vector) {
		this.x = vector.x;
		this.y = vector.y;
	}

	public void move(Vector vector) {
		this.x += vector.x;
		this.y += vector.y;
	}

	public Position getTranslated(Vector vector) {
		return new Position(x + vector.x, y + vector.y, world);
	}

	public Position copy() {
		return new Position(x, y, world);
	}

	public Position getOppsite() {
		return new Position(-x, -y, world);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof Position) {
			Position obj = (Position) other;
			return this.x == obj.x && this.y == obj.y && this.world == obj.world;
		}
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, world);
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + "," + world + ")";
	}
}
