package com.backinfile.cube.support;

import java.util.ArrayList;
import java.util.List;

import org.reflections.Reflections;

public class ReflectionUtils {
	public static List<Class<?>> getAllSubClass(Class<?> clazz) {
		List<Class<?>> classList = new ArrayList<Class<?>>();
		Reflections reflections = new Reflections("com.backinfile");
		for (Class<?> cls : reflections.getSubTypesOf(clazz)) {
			classList.add(cls);
		}
		return classList;
	}
}
