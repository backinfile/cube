package com.backinfile.cube.model.cubes;

// 锁，当地图上所有FixedKey解锁时解锁
public class Lock extends Cube {
	private boolean locked = true;

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public boolean isLocked() {
		return locked;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return !locked;
	}

	@Override
	public boolean isSelectedFirst() {
		return false;
	}
}
