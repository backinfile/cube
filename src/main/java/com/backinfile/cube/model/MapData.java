package com.backinfile.cube.model;

import com.backinfile.cube.model.cubes.Cube;
import com.backinfile.cube.model.cubes.Human;
import com.backinfile.cube.model.cubes.Wall;

public class MapData {
	public String coor = ""; // 所处位置
	public int width;
	public int height;
	public MMap<Cube> cubeMap; // 方块
	public String view = "";
	public String tipText = "";

	public void initMap(int width, int height) {
		this.width = width;
		this.height = height;
		cubeMap = new MMap<Cube>(width, height);
	}

	public boolean hasHuman() {
		for (Cube cube : cubeMap.getUnitList()) {
			if (cube instanceof Human) {
				return true;
			}
		}
		return false;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		if (cubeMap != null) {
			cubeMap.setSize(width, height);
		} else {
			cubeMap = new MMap<Cube>(width, height);
		}
	}

	public boolean isMatchWith(MapData other) {
		if (width != other.width || height != other.height) {
			return false;
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int cnt = 0;
				if (cubeMap.get(i, j) instanceof Wall) {
					cnt++;
				}
				if (other.cubeMap.get(i, j) instanceof Wall) {
					cnt++;
				}
				if (cnt != 1) {
					return false;
				}
			}
		}
		return true;
	}
}