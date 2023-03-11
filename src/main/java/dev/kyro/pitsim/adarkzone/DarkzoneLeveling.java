package dev.kyro.pitsim.adarkzone;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.ActionBarManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;

public class DarkzoneLeveling {

	public static int getLevel(double xp) {
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

	public static void giveXP(PitPlayer pitPlayer, double amount) {
		double xp = pitPlayer.altarXP + amount;
		int level = pitPlayer.altarLevel;

		ActionBarManager.sendActionBar(pitPlayer.player, "&4&l+" + (int) amount + " ALTAR XP");
		Sounds.XP_GAIN.play(pitPlayer.player);

		double neededXP = getXPForLevel(level + 1);
		while(xp >= neededXP) {
			xp -= neededXP;
			AOutput.send(pitPlayer.player, "&4&lALTAR LEVEL UP! &c" + level + " &7\u279F &c" + (level + 1));
			level++;

			Sounds.ALTAR_LEVEL_UP.play(pitPlayer.player);
			Misc.sendTitle(pitPlayer.player, "&4&lLEVEL UP!", 40);
			Misc.sendSubTitle(pitPlayer.player, "&4 " + pitPlayer.altarLevel + " &7\u279F &4" + (pitPlayer.altarLevel + 1), 40);

			neededXP = getXPForLevel(level + 1);
		}

		pitPlayer.altarXP = xp;
		pitPlayer.altarLevel = level;
	}
}
