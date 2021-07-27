package com.backinfile.cube.core;

public class WorldUnit {
	public Position position = new Position(); // 当前位置
	public Position originPosition = new Position(); // 出生位置
	public Vector direction = new Vector(); // 朝向

	/**
	 * 重置位置到出生位置
	 */
	public void resetPosition() {
		this.position.setPosition(originPosition);
	}

	@Override
	public String toString() {
		return "[" + this.getClass().getSimpleName() + position.toString() + "]";
	}
}
