package com.backinfile.cube;

import com.backinfile.cube.support.log.LogManager;
import com.backinfile.cube.support.log.Logger;

public class Log {
	public static Logger core = LogManager.getLogger("CORE");
	public static Logger game = LogManager.getLogger("GAME");
	public static Logger view = LogManager.getLogger("VIEW");
	public static Logger timer = LogManager.getLogger("TIMER");
	public static Logger reflection = LogManager.getLogger("REFLECTION");
	public static Logger level = LogManager.getLogger("LEVEL");

}
