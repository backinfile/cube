package com.backinfile.cube.model;

import java.util.Objects;

public class Vector {
	public float x = 0;
	public float y = 0;

	public Vector() {
	}

	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void set(Vector vector) {
		this.x = vector.x;
		this.y = vector.y;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof Vector) {
			Vector obj = (Vector) other;
			return this.x == obj.x && this.y == obj.y;
		}
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
