package dev.kyro.pitsim.adarkzone;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.objects.PitPlayer;

public class DarkzoneLeveling {

	public static int getLevel(int xp) {
		int level = 0;

		while(xp >= getXPForLevel(getLevel(xp) + 1)) {
			xp -= getXPForLevel(getLevel(xp) + 1);
			level++;
		}

		return level;
	}

	public static double getXPForLevel(int level) {
		return (Math.pow(level, 2) * 100);
	}

	public static int getTotalXP(int level, int currentXP) {
		int xp = 0;

		for(int i = 1; i <= level; i++) {
			xp += getXPForLevel(i);
		}

		return xp + currentXP;
	}

	public void giveXP(PitPlayer pitPlayer, int amount) {
		double xp = pitPlayer.altarXP + amount;
		int level = pitPlayer.altarLevel;

		double neededXP = getXPForLevel(level + 1);
		while(xp >= neededXP) {
			xp -= neededXP;
			AOutput.send(pitPlayer.player, "&4&lALTAR LEVEL UP! &c" + level + " &7\u279F &c" + (level + 1));
			level++;

			neededXP = getXPForLevel(level + 1);
		}
	}
}
