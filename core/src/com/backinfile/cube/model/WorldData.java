package com.backinfile.cube.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.backinfile.cube.Log;
import com.backinfile.cube.model.cubes.*;
import com.backinfile.cube.support.SysException;
import com.backinfile.cube.support.Tuple2;
import com.backinfile.cube.support.Utils;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import java.util.*;

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

    public void setupRely() {
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
        HashMap<String, MapConfInfo> mapConfs = new HashMap<>();
        // ????????????????????????
        for (int i = 0; i < mapConfArray.size(); i++) {
            JSONObject mapConf = mapConfArray.getJSONObject(i);
            String oriCoor = mapConf.getString("name");
            if (!Utils.isNullOrEmpty(oriCoor)) {
                mapConfs.put(oriCoor, MapConfInfo.parseMapCubes(mapConf));
                Log.level.info("read {}", oriCoor);
            }
        }
        // ??????????????????
        for (String mapCoor : new ArrayList<>(mapConfs.keySet())) {
            MapConfInfo mapConf = mapConfs.get(mapCoor);
            LinkedList<Tuple2<String, List<MapCubeConf>>> confQueue = new LinkedList<>();
            confQueue.add(new Tuple2<String, List<MapCubeConf>>(mapCoor, mapConf.getAllMapCubeConf()));
            while (!confQueue.isEmpty()) {
                Tuple2<String, List<MapCubeConf>> tuple = confQueue.poll();
                List<MapCubeConf> array = tuple.value2;
                String prefix = tuple.value1;
                for (MapCubeConf mapCubeConf : array) {
                    if (!mapCubeConf.copy) {
                        continue;
                    }
                    Log.level.info("{},{},{}->{}", mapCubeConf.x, mapCubeConf.y, prefix, mapCubeConf.finalCoor);
                    MapConfInfo target = mapConfs.get(mapCubeConf.targetCoor);
                    if (target == null) {
                        throw new SysException("not find coor: " + mapCubeConf.targetCoor);
                    }
                    MapConfInfo copy = target.getCopyWithPrefix(prefix);
                    mapConfs.put(copy.mapCoor, copy);
//					Log.level.info("{} + {} -> {}", prefix, mapCubeConf.targetCoor, copy.mapCoor);
                    confQueue.add(new Tuple2<String, List<MapCubeConf>>(copy.mapCoor, copy.confs));
                }
            }
        }
        for (String mapCoor : mapConfs.keySet()) {
            MapConfInfo mapInfo = mapConfs.get(mapCoor);
            JSONObject mapConf = mapInfo.mapConf;
            Vector size = new Vector(mapInfo.size);
            MapData mapData = new MapData();
            worldData.datas.add(mapData);
            mapData.initMap((int) size.x, (int) size.y);
            mapData.coor = mapCoor;
            mapData.tip = parseTip(getPropValue(mapConf, "tip"));
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
                            cube = new Player();
                            break;
                        case 4: {
                            cube = new MapCube(mapInfo.getMapCubeTargetCoor(x, y));
                            break;
                        }
                        case 5: {
                            MapCube mapCube = new MapCube(mapInfo.getMapCubeTargetCoor(x, y));
                            mapCube.setMovable(false);
                            cube = mapCube;
                            break;
                        }
                        case 6: {
                            FixedKey fixedKey = new FixedKey(mapInfo.getMapCubeTargetCoor(x, y));
                            cube = fixedKey;
                            break;
                        }
                        case 7: {
                            cube = new Lock();
                            break;
                        }
                        default:
                            break;
                    }
                    if (cube instanceof MapCube) {
                        if (Utils.isNullOrEmpty(((MapCube) cube).getTargetCoor())) {
                            Log.game.error("{},{},{} coor empty!!", mapData.coor, x, y);
                        }
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

    private static String parseTip(String str) {
        if (Utils.isNullOrEmpty(str)) {
            return "";
        }
        if (str.contains("|")) {
            String[] split = str.split("\\|");
            if (split.length == 2) {
                if (Gdx.app.getType() == Application.ApplicationType.Android) {
                    return split[0];
                }
                return split[1];
            }
        }
        return str;
    }

    private static class MapCubeConf {
        public int x;
        public int y;
        public String oriCoorStr = ""; // ?????????????????????
        public String targetCoor = ""; // ?????????????????????????????????????????????mapcube????????????
        public String finalCoor = ""; // ???????????????map, ?????????+targetCoor
        public boolean copy = false;

        public MapCubeConf copy(String parentCoor) {
            MapCubeConf conf = new MapCubeConf();
            conf.x = this.x;
            conf.y = this.y;
            conf.oriCoorStr = this.oriCoorStr;
            conf.targetCoor = this.targetCoor;
            conf.finalCoor = parentCoor + "|" + conf.targetCoor;
            conf.copy = true;
            return conf;
        }
    }

    private static class MapConfInfo {
        private String mapCoor;
        private JSONObject mapConf;
        private Vector size = new Vector();
        private List<MapCubeConf> confs = new ArrayList<>();

        public static MapConfInfo parseMapCubes(JSONObject mapConf) {
            MapConfInfo mapInfo = new MapConfInfo();
            mapInfo.mapConf = mapConf;
            mapInfo.mapCoor = mapConf.getString("name");

            // ??????????????????
            String sizeConf = getPropValue(mapConf, "size");
            if (!Utils.isNullOrEmpty(sizeConf)) {
                int[] sizeArray = Utils.str2IntArray(sizeConf);
                mapInfo.size.set(sizeArray[0], sizeArray[1]);
            } else {
                int maxSize = 1;
                List<Integer> dataArray = JSONObject.parseArray(mapConf.getString("data"), Integer.class);
                for (int x = 0; x < 11; x++) {
                    for (int y = 0; y < 11; y++) {
                        int type = dataArray.get(x + y * 11);
                        if (type > 0) {
                            maxSize = Math.max(maxSize, Math.max(x, y) + 1);
                        }
                    }
                }
                mapInfo.size.set(maxSize, maxSize);
            }

            // ????????????????????????
            String mapCubeConf = getPropValue(mapConf, "mapcube");
            if (Utils.isNullOrEmpty(mapCubeConf)) {
                return mapInfo;
            }
            String[] values = mapCubeConf.split(",");
            for (int i = 0; i < values.length; i += 3) {
                MapCubeConf conf = new MapCubeConf();
                conf.x = Integer.valueOf(values[i + 0].trim());
                conf.y = Integer.valueOf(values[i + 1].trim());
                conf.oriCoorStr = values[i + 2].trim();
                conf.targetCoor = conf.oriCoorStr.replaceAll("\\#", "");
                conf.copy = conf.oriCoorStr.contains("#");
                if (conf.copy) {
                    conf.finalCoor = mapInfo.mapCoor + "|" + conf.targetCoor;
                } else {
                    conf.finalCoor = conf.targetCoor;
                }
                mapInfo.confs.add(conf);
            }
            return mapInfo;
        }

        public MapConfInfo getCopyWithPrefix(String prefix) {
            MapConfInfo thisCopy = new MapConfInfo();
            thisCopy.mapCoor = prefix + "|" + this.mapCoor;
            thisCopy.mapConf = this.mapConf;
            thisCopy.size.set(this.size);
            for (MapCubeConf conf : this.confs) {
                thisCopy.confs.add(conf.copy(thisCopy.mapCoor));
            }
            return thisCopy;
        }

        public String getMapCubeTargetCoor(int x, int y) {
            for (MapCubeConf conf : confs) {
                if (conf.x == x && conf.y == y) {
                    return conf.finalCoor;
                }
            }
            return "";
        }

        public List<MapCubeConf> getAllMapCubeConf() {
            return confs;
        }
    }

    private static String getPropValue(JSONObject mapConf, String name) {
        JSONArray propConf = mapConf.getJSONArray("properties");
        if (propConf == null) {
            return "";
        }
        name = name.toLowerCase();
        for (int i = 0; i < propConf.size(); i++) {
            JSONObject prop = propConf.getJSONObject(i);
            String propValue = prop.getString("name");
            if (propValue != null && name.equals(propValue.toLowerCase())) {
                return prop.getString("value");
            }
        }
        return "";
    }
}
