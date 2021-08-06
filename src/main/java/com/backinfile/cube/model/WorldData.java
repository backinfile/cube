package com.backinfile.cube.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.backinfile.cube.Log;
import com.backinfile.cube.Res;
import com.backinfile.cube.model.cubes.Cube;
import com.backinfile.cube.model.cubes.Human;
import com.backinfile.cube.model.cubes.Key;
import com.backinfile.cube.model.cubes.MapCube;
import com.backinfile.cube.model.cubes.Rock;
import com.backinfile.cube.model.cubes.Wall;
import com.backinfile.cube.support.Utils;

public class WorldData {
	private List<MapData> datas = new ArrayList<>();

	public List<MapData> getMapDatas() {
		return datas;
	}

	public List<MapData> getOrderedDatas() {
		Collections.sort(datas, new Comparator<MapData>() {
			@Override
			public int compare(MapData o1, MapData o2) {
				return o1.coor.compareTo(o2.coor);
			}
		});
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
		Log.game.error("search coor:{} not exist!", coor);
		return null;
	}

	private static final String R_INDEX = "index=";
	private static final String R_SIZE = "size=";
	private static final String R_MAP = "map=";
	private static final String R_FLOOR = "floor=";
	private static final String R_TEXT = "text=";
	private static final String R_VIEW = "view=";

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
			} else if (curLine.startsWith(R_TEXT)) {
				curMapData.tipText = curLine.substring(R_TEXT.length()).trim();
			} else if (curLine.startsWith(R_VIEW)) {
				curMapData.view = curLine.substring(R_VIEW.length()).trim();
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
						if (Character.isLowerCase(curChar)) {
							cube = new MapCube(curMapData.coor + curChar);
						} else if (Character.isDigit(curChar)) {
							cube = new Key(curMapData.coor + curChar);
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
				index += curMapData.height;
			}
		}
		if (curMapData != null) {
			world.datas.add(curMapData);
		}
		world.setupRely();
		return world;
	}

	private void setupRely() {
		for (MapData mapData : datas) {
			for (Cube cube : mapData.cubeMap.getUnitList()) {
				if (cube instanceof MapCube) {
					MapCube mapCube = (MapCube) cube;
					MapData targetMapData = getMapData(mapCube.getTargetCoor());
					if (targetMapData == null) {
						Log.game.warn("map data missing coor:{}", mapCube.getTargetCoor());
						continue;
					}
					targetMapData.preCube = mapCube;
					targetMapData.preMapData = mapData;
				}
			}
		}
	}

	public static WorldData parseFromTiled(String conf) {
		WorldData worldData = new WorldData();
		Vector mainSize = new Vector();
		JSONObject jsonConf = JSONObject.parseObject(conf);
		mainSize.x = jsonConf.getInteger("width");
		mainSize.y = jsonConf.getInteger("height");

		JSONArray mapConfArray = jsonConf.getJSONArray("layers");
		for (int i = 0; i < mapConfArray.size(); i++) {
			JSONObject mapConf = mapConfArray.getJSONObject(i);
			Vector size = new Vector(mainSize);
			String sizeConf = getPropValue(mapConf, "size");
			if (!Utils.isNullOrEmpty(sizeConf)) {
				int[] sizeArray = Utils.str2IntArray(sizeConf);
				size.set(sizeArray[0], sizeArray[1]);
			}
			String mapCubeConf = getPropValue(mapConf, "mapcube");
			MapCubeConfs mapCubeConfs = MapCubeConfs.parseMapCubes(mapCubeConf);
			MapData mapData = new MapData();
			worldData.datas.add(mapData);
			mapData.initMap((int) size.x, (int) size.y);
			mapData.coor = mapConf.getString("name");
			List<Integer> dataArray = JSONObject.parseArray(mapConf.getString("data"), Integer.class);
			for (int x = 0; x < size.x; x++) {
				for (int y = 0; y < size.y; y++) {
					int type = dataArray.get(x + y * (int) mainSize.y);
					Cube cube = null;
					switch (type) {
					case 1:
						cube = new Wall();
						break;
					case 2:
						cube = new Rock();
						break;
					case 3:
						cube = new Human();
						break;
					case 4: {
						String targetCoor = mapCubeConfs.getCoor(x, y);
						cube = new MapCube(targetCoor);
						if (Utils.isNullOrEmpty(targetCoor)) {
							Log.game.warn("{},{},{} coor empty!!", mapData.coor, x, y);
						}
						break;
					}
					case 5: {
						String targetCoor = mapCubeConfs.getCoor(x, y);
						MapCube mapCube = new MapCube(targetCoor);
						mapCube.setMovable(false);
						cube = mapCube;
						if (Utils.isNullOrEmpty(targetCoor)) {
							Log.game.warn("{},{},{} coor empty!!", mapData.coor, x, y);
						}
						break;
					}
					default:
						break;
					}
					if (cube != null) {
						cube.originPosition.x = x;
						cube.originPosition.y = mapData.height - y - 1;
						cube.originPosition.worldCoor = mapData.coor;
						cube.resetPosition();
						mapData.cubeMap.add(cube);
					}
				}
			}
		}
		worldData.setupRely();
		return worldData;
	}

	private static class MapCubeConfs {
		private List<MapCubeConf> confs = new ArrayList<>();

		private static class MapCubeConf {
			public int x;
			public int y;
			public String coor;

			public MapCubeConf(int x, int y, String coor) {
				this.x = x;
				this.y = y;
				this.coor = coor;
			}

		}

		public static MapCubeConfs parseMapCubes(String mapCubeConf) {
			MapCubeConfs mapCubeConfs = new MapCubeConfs();
			if (Utils.isNullOrEmpty(mapCubeConf)) {
				return mapCubeConfs;
			}
			String[] values = mapCubeConf.split(",");
			for (int i = 0; i < values.length; i += 3) {
				mapCubeConfs.confs.add(
						new MapCubeConf(Integer.valueOf(values[i]), Integer.valueOf(values[i + 1]), values[i + 2]));
			}
			return mapCubeConfs;
		}

		public String getCoor(int x, int y) {
			for (MapCubeConf conf : confs) {
				if (conf.x == x && conf.y == y) {
					return conf.coor;
				}
			}
			return "";
		}

	}

	private static String getPropValue(JSONObject mapConf, String name) {
		JSONArray propConf = mapConf.getJSONArray("properties");
		for (int i = 0; i < propConf.size(); i++) {
			JSONObject prop = propConf.getJSONObject(i);
			if (name.equals(prop.getString("name"))) {
				return prop.getString("value");
			}
		}
		return "";
	}
}
