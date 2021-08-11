package com.backinfile.cube.model;

import com.backinfile.cube.model.cubes.Cube;
import com.backinfile.cube.model.cubes.Player;
import com.backinfile.cube.model.cubes.MapCube;

public class MapData {
	public String coor = ""; // 所处位置
	public int width;
	public int height;
	public MMap<Cube> cubeMap; // 方块
	public String view = "";
	public String viewAll = "";
	public String tipText = "";
	public MapCube preCube = null;
	public MapData preMapData = null;

	public void initMap(int width, int height) {
		this.width = width;
		this.height = height;
		cubeMap = new MMap<Cube>(width, height);
	}

	public boolean isSameSize(MapData mapData) {
		return this.width == mapData.width && this.height == mapData.height;
	}

	public boolean hasHuman() {
		for (Cube cube : cubeMap.getUnitList()) {
			if (cube instanceof Player) {
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

	public boolean isMatchWith(WorldData worldData, MapData other) {
		if (width != other.width || height != other.height) {
			return false;
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Cube thisCube = cubeMap.get(i, j);
				Cube otherCube = other.cubeMap.get(i, j);
				if (!testFillOther(worldData, thisCube, otherCube) && !testFillOther(worldData, otherCube, thisCube)) {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean testFillOther(WorldData worldData, Cube cube, Cube other) {
		if (other == null || other.isEmpty()) {
			return isCubeFilled(worldData, cube);
		}
		if (other instanceof MapCube) {
			if (cube == null || cube.isEmpty()) {
				return isCubeFilled(worldData, cube);
			}
			if (cube instanceof MapCube) {
				MapData thisMap = worldData.getMapData(((MapCube) cube).getTargetCoor());
				MapData otherMap = worldData.getMapData(((MapCube) other).getTargetCoor());
				return thisMap.isMatchWith(worldData, otherMap);
			}
		}
		return false;
	}

	private static boolean isCubeFilled(WorldData worldData, Cube cube) {
		if (cube == null) {
			return false;
		}
		if (cube instanceof MapCube) {
			MapData mapData = worldData.getMapData((((MapCube) cube).getTargetCoor()));
			for (int x = 0; x < mapData.width; x++) {
				for (int y = 0; y < mapData.height; y++) {
					if (!isCubeFilled(worldData, mapData.cubeMap.get(x, y))) {
						return false;
					}
				}
			}
		}
		return !cube.isEmpty();
	}

	@Override
	public String toString() {
		return coor + "(" + width + "," + height + ")";
	}
}