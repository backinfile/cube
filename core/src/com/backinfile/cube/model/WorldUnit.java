package com.backinfile.cube.model;

public class WorldUnit {
	public Position position = new Position(); // 当前位置
	public Position originPosition = new Position(); // 出生位置
	public Vector direction = new Vector(); // 朝向

	/**
	 * 如有两个unit在同一个地方，则首先获取这个unit
	 */
	public boolean isSelectedFirst() {
		return true;
	}

	/**
	 * 重置位置到出生位置
	 */
	public void resetPosition() {
		this.position.setPosition(originPosition);
	}
	

	@Override
	public String toString() {
		return getClass().getSimpleName() + position.toString();
	}
}
