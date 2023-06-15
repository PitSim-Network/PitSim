package net.pitsim.spigot.controllers;

import java.util.ArrayList;
import java.util.List;

public class OldLevelManager {

	public static List<Long> levelMap = new ArrayList<>();

	static {

		for(int i = 0; i < 2000; i++) {
			levelMap.add(getXP(i));
		}

		for(int i = 0; i < levelMap.size(); i++) {
//            System.out.println(i + " " + levelMap.get(i));
		}
	}

	public static long getXP(long level) {

		return (long) (9 + 10 * level + Math.pow(level, 2.3) + Math.pow(1.015, level)) * 100;
	}

	public static int getRenownFromLevel(int level) {
//        if(level >= 0 && level < 5) return 0;
//        if(level > 4 && level < 10) return 1;
//        if(level > 14 && level < 20) return 3;
//        if(level > 24 && level < 30) return 5;
//        if(level > 34 && level < 40) return 7;
//        if(level > 44 && level < 50) return 9;
//        return 11;
		return (level > 50 ? 10 : level / 5) * 2;
	}
}
