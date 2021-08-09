package com.backinfile.cube.support;

import com.backinfile.cube.Log;

public class TimeLogger {
	private long startTime;
	private String name;

	public TimeLogger(String name) {
		this.name = name;
		startTime = Time2.currentTimeMillis();
	}

	public void log() {
		Log.timer.info("{} using {} second", name, (Time2.currentTimeMillis() - startTime) / (float) Time2.SEC);
	}
}
