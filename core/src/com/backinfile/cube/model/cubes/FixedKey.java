package com.backinfile.cube.model.cubes;

// 地板上的透明钥匙方块，可与相合的MapCube组合
public class FixedKey extends MapCube {
	

	public FixedKey(String targetCoor) {
		super(targetCoor);
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean canEnter() {
		return false;
	}

	@Override
	public boolean isSelectedFirst() {
		return false;
	}
}
