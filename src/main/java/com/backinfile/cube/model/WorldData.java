package com.backinfile.cube.model;

import java.util.ArrayList;
import java.util.List;

import com.backinfile.cube.model.cubes.Cube;
import com.backinfile.cube.model.cubes.Human;
import com.backinfile.cube.model.cubes.Rock;
import com.backinfile.cube.model.cubes.Wall;

public class WorldData {
	private List<MapData> datas = new ArrayList<>();

	public List<MapData> getDatas() {
		return datas;
	}

	public MapData getHumanMapData() {
		for (MapData data : datas) {
			if (data.hasHuman()) {
				return data;
			}
		}
		return null;
	}

	public MapData getMapData(String coor) {
		for (MapData data : datas) {
			if (data.coor.equals(coor)) {
				return data;
			}
		}
		return null;
	}

	public MapData getNextMapData(MapData mapData, String t) {
		String target = mapData.coor + t;
		for (MapData data : datas) {
			if (data.coor.equals(target)) {
				return data;
			}
		}
		return null;
	}

	public MapData getPreMapData(MapData mapData) {
		String target = mapData.coor.substring(0, mapData.coor.length() - 1);
		for (MapData data : datas) {
			if (data.coor.equals(target)) {
				return data;
			}
		}
		return null;
	}

	private static final String R_INDEX = "index=";
	private static final String R_SIZE = "size=";
	private static final String R_MAP = "map=";
	private static final String R_FLOOR = "floor=";

	private static final char C_HUMAN = 'M';
	private static final char C_WALL = 'W';
	private static final char C_ROCK = 'R';
	private static final char C_EMPTY = ' ';

	public static WorldData parse(String worldConf) {
		WorldData world = new WorldData();
		MapData curMapData = null;
		String[] confs = worldConf.split("\n");
		for (int index = 0; index < confs.length; index++) {
			String curLine = confs[index].trim();
			if (curLine.startsWith(R_INDEX)) {
				if (curMapData != null) {
					world.datas.add(curMapData);
				}
				curMapData = new MapData();
				curMapData.coor = curLine.substring(R_INDEX.length());
			} else if (curLine.startsWith(R_SIZE)) {
				String[] sizeConf = curLine.substring(R_SIZE.length()).split("\\*");
				if (sizeConf.length == 2) {
					int width = Integer.valueOf(sizeConf[0]);
					int height = Integer.valueOf(sizeConf[1]);
					curMapData.initMap(width, height);
				}
			} else if (curLine.startsWith(R_MAP)) {
				for (int h = 0; h < curMapData.height; h++) {
					String line = confs[index + h + 1];
					for (int w = 0; w < curMapData.width; w++) {
						Cube cube = null;
						char curChar = line.charAt(w);
						switch (curChar) {
						case C_HUMAN:
							cube = new Human();
							break;
						case C_ROCK:
							cube = new Rock();
							break;
						case C_WALL:
							cube = new Wall();
							break;
						case C_EMPTY:
							break;
						default:
							break;
						}
						if (Character.isLowerCase(curChar) || Character.isDigit(curChar)) {
							cube = new MapCube(curChar);
						}
						if (cube != null) {
							cube.originPosition.x = w;
							cube.originPosition.y = curMapData.height - h - 1;
							cube.originPosition.worldCoor = curMapData.coor;
							cube.resetPosition();
							curMapData.cubeMap.add(cube);
						}
					}
				}
				index += curMapData.height + 1;
			}
		}
		if (curMapData != null) {
			world.datas.add(curMapData);
		}
		return world;
	}
}
