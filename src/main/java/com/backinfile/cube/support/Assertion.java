package com.backinfile.cube.support;

import java.text.MessageFormat;

public class Assertion {
	public static void assertEqual(int value, int target) {
		if (value != target) {
			String str = MessageFormat.format("Assertion error {0} != {1}", value, target);
			throw new SysException(str);
		}
	}

	public static void assertLargeThen(int value, int target) {
		if (value <= target) {
			String str = MessageFormat.format("Assertion error {0} <= {1}", value, target);
			throw new SysException(str);
		}
	}
}
