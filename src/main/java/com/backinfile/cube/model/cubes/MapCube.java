package com.backinfile.cube.model.cubes;

public class MapCube extends Cube {
	protected String targetCoor;
	protected boolean movable = true;
	protected boolean enterable = true;

	// 是否被Key解锁了
	protected boolean fitKey = false;

	public MapCube(String targetCoor) {
		this.targetCoor = targetCoor;
	}

	public String getTargetCoor() {
		return targetCoor;
	}

	public void setMovable(boolean movable) {
		this.movable = movable;
	}

	public void setFitKey(boolean fitKey) {
		this.fitKey = fitKey;
	}

	public boolean isFitKey() {
		return fitKey;
	}

	@Override
	public boolean isPushable() {
		return movable;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	public boolean canEnter() {
		return enterable;
	}
}
