package com.backinfile.cube.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
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

	public List<T> getAll(int x, int y) {
		List<T> list = new ArrayList<>();
		checkSize(x, y);
		for (T unit : unitList) {
			if (unit.position.x == x && unit.position.y == y) {
				list.add(unit);
			}
		}
		return list;
	}

	public T get(int x, int y) {
		List<T> all = getAll(x, y);
		for (T unit : all) {
			if (unit.isSelectedFirst()) {
				return unit;
			}
		}
		if (!all.isEmpty()) {
			return all.get(0);
		}
		return null;
	}

	public T get(Position position) {
		return get(position.x, position.y);
	}

	/**
	 * 按对象删除
	 */
	public boolean remove(T unit) {
		for (Iterator<T> iter = unitList.iterator(); iter.hasNext();) {
			T value = iter.next();
			if (value == unit) {
				iter.remove();
				return true;
			}
		}
		return false;
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

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		for (T unit : getUnitList()) {
			if (!inMap(unit.position.x, unit.position.y)) {
				remove(unit);
			}
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
