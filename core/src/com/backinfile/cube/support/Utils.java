package com.backinfile.cube.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {
	public static final String UTF8 = "utf-8";
	private static final Random RANDOM = new Random();

	public static boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}

	public static <T> ArrayList<T> subList(List<T> list, int fromIndex, int toIndex) {
		return new ArrayList<T>(list.subList(fromIndex, toIndex));
	}

	public static int indexOf(int[] array, int value) {
		for (int i = 0; i < array.length; i++) {
			if (value == array[i]) {
				return i;
			}
		}
		return -1;
	}

	public static int[] str2IntArray(String str) {
		String[] strs = str.split(",");
		int[] result = new int[strs.length];
		for (int i = 0; i < strs.length; i++) {
			result[i] = Integer.valueOf(strs[i]);
		}
		return result;
	}

	public static double nextDouble() {
		return RANDOM.nextDouble();
	}

	public static int nextInt(int a, int b) {
		return RANDOM.nextInt(b - a) + a;
	}

	public static double nextDouble(double a, double b) {
		return RANDOM.nextDouble() * (b - a) + a;
	}

	public static String format(String pattern, Object... arguments) {
		String[] argStrings = new String[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			argStrings[i] = arguments[i].toString();
		}

		StringBuilder sb = new StringBuilder();
		int defaultIndex = 0;
		int length = pattern.length();
		for (int i = 0; i < length; i++) {
			char curChar = pattern.charAt(i);
			if (curChar == '{') {
				boolean hasNumber = false;
				boolean stoped = false;
				int number = 0;
				for (int j = i + 1; j < length; j++) {
					char nextChar = pattern.charAt(j);
					if (Character.isDigit(nextChar)) {
						hasNumber = true;
						number *= 10;
						number += nextChar - ('0');
					} else if (nextChar == '}') {
						stoped = true;
						i = j;
						break;
					}
				}
				if (stoped) {
					if (hasNumber) {
						sb.append(argStrings[number]);
					} else {
						sb.append(argStrings[defaultIndex]);
					}
					defaultIndex++;
				} else {
					sb.append(curChar);
				}
			} else {
				sb.append(curChar);
			}
		}

		return sb.toString();
	}

}
