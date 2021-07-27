package com.backinfile.cube.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.backinfile.cube.Res;
import com.backinfile.cube.actor.WorldView;

public class GameManager {
	public static final GameManager instance = new GameManager();

	public WorldData worldData;
	public WorldView worldView;

	private String curCoor;
	private LinkedList<History> histories = new LinkedList<History>();

	private static final int[] dx = new int[] { 0, 0, -1, 1 };
	private static final int[] dy = new int[] { 1, -1, 0, 0 };

	public void init() {
		worldData = WorldData.parse(Res.getDefaultWorldConf());
	}

	public void resetGame() {

	}

	public void undo() {
		if (!histories.isEmpty()) {
			History history = histories.pollLast();
			history.playback();
		}
	}

	public void moveHuman(int dura) {
		Vector d = new Vector(dx[dura], dy[dura]);
	}

	public WorldData getWorldData() {
		return worldData;
	}

	public Vector getWorldSize() {
		MapData curMapData = worldData.getMapData(curCoor);
		return new Vector(Res.CUBE_SIZE * curMapData.width, Res.CUBE_SIZE * curMapData.height);
	}

	private List<Integer> getSplitByTransPosList(List<Pos> posList) {
		List<Integer> indexList = new ArrayList<>();
		for (int i = posList.size() - 1; i >= 0; i--) {
			if (isPosTrans(posList.get(i))) {
				indexList.add(i);
			}
		}
		return indexList;
	}

	private void _moveCubePosList(ArrayList<Position> posList) {
	}

}
