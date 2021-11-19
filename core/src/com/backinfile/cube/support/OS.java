package com.backinfile.cube.support;

public class OS {
    private static final String os = System.getProperty("os.name");

    public static boolean isWin() {
        return os.toLowerCase().startsWith("win");
    }
}
