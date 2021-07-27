package com.backinfile.cube.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import com.backinfile.cube.support.Action3;
import com.backinfile.cube.support.SysException;

public class MMap<T extends WorldUnit> {
	private int width;
	private int height;
	private List<T> unitList = new ArrayList<>();

	public MMap(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void add(T unit) {
		checkSize(unit.position.x, unit.position.y);
		unitList.add(unit);
	}

	public T get(int x, int y) {
		checkSize(x, y);
		for (T unit : unitList) {
			if (unit.position.x == x && unit.position.y == y) {
				return unit;
			}
		}
		return null;
	}

	public T get(Position position) {
		return get(position.x, position.y);
	}

	public List<T> getUnitList() {
		return new ArrayList<T>(unitList);
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public boolean inMap(int x, int y) {
		if (0 <= x && x < width && 0 <= y && y < height) {
			return true;
		}
		return false;
	}

	private void checkSize(int x, int y) {
		if (!inMap(x, y)) {
			String msg = MessageFormat.format("MMap超出长度限制 width={0},height={1},x={2},y={3}", width, height, x, y);
			throw new SysException(msg);
		}
	}

	public void forEach(Action3<Integer, Integer, T> action) {
		for (T unit : getUnitList()) {
			action.invoke(unit.position.x, unit.position.y, unit);
		}
	}

	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner("\n");
		for (T unit : getUnitList()) {
			sj.add(unit.toString());
		}
		return sj.toString();
	}
}
