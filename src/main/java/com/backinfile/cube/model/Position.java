package com.backinfile.cube.model;

import java.util.Objects;

public class Position {
	public int x;
	public int y;
	public String worldCoor;

	public Position() {
		this(0, 0, "");
	}

	public Position(int x, int y) {
		this(x, y, "");
	}

	public Position(int x, int y, String worldCoor) {
		this.x = x;
		this.y = y;
		this.worldCoor = worldCoor;
	}

	public void setPosition(Position position) {
		this.x = position.x;
		this.y = position.y;
		this.worldCoor = position.worldCoor;
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
		return new Position(x + vector.x, y + vector.y, worldCoor);
	}

	public Position copy() {
		return new Position(x, y, worldCoor);
	}

	public Position getOppsite() {
		return new Position(-x, -y, worldCoor);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof Position) {
			Position obj = (Position) other;
			return this.x == obj.x && this.y == obj.y && this.worldCoor.equals(obj.worldCoor);
		}
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, worldCoor);
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + "," + worldCoor + ")";
	}
}
