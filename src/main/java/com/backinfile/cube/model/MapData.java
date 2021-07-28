package com.backinfile.cube.model;

import com.backinfile.cube.model.cubes.Cube;
import com.backinfile.cube.model.cubes.Floor;
import com.backinfile.cube.model.cubes.Human;

public class MapData {
	public String coor = ""; // 所处位置
	public int width;
	public int height;
	public MMap<Cube> cubeMap; // 方块
	public MMap<Floor> floorMap; // 地板标记
	public String view = "";
	public String tipText = "";

	public void initMap(int width, int height) {
		this.width = width;
		this.height = height;
		cubeMap = new MMap<Cube>(width, height);
		floorMap = new MMap<Floor>(width, height);
	}

	public boolean hasHuman() {
		for (Cube cube : cubeMap.getUnitList()) {
			if (cube instanceof Human) {
				return true;
			}
		}
		return false;
	}
}