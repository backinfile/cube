package com.backinfile.cube.support;

import java.util.LinkedList;

public class ObjectPool<T> {
	private LinkedList<T> objs = new LinkedList<>();
	private Function<T> objCreator;

	public ObjectPool(Function<T> objCreator) {
		this(0, objCreator);
	}

	public ObjectPool(int num, Function<T> objCreator) {
		this.objCreator = objCreator;

		for (int i = 0; i < num; i++) {
			objs.add(objCreator.invoke());
		}
	}

	public int getNumCount() {
		return objs.size();
	}

	public T apply() {
		T obj;
		if (objs.isEmpty()) {
			obj = objCreator.invoke();
		} else {
			obj = objs.pollLast();
		}
		return obj;
	}

	public void putBack(T t) {
		objs.add(t);
	}

}
