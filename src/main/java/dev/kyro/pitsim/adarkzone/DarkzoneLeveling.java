package dev.kyro.pitsim.adarkzone;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.ActionBarManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Player;

public class DarkzoneLeveling	 {

	public static int getLevel(double xp) {
		int level = 1;

		while(xp >= getXPForLevel(level)) {
			xp -= getXPForLevel(level);
			level++;
		}

		return level;
	}

	public static double getXPForLevel(int level) {
		return level * 5 + 10;
	}

	public static double getRemainingXP(double currentXP) {
		int level = 0;

		while(currentXP >= getXPForLevel(level)) {
			currentXP -= getXPForLevel(level);
			level++;
		}

		return currentXP;
	}

	public static double getXPToLevel(int level) {
		double xp = 0;

		for(int i = 0; i < level; i++) {
			xp += getXPForLevel(i);
		}

		return xp;
	}

	public static void giveXP(PitPlayer pitPlayer, double amount) {
		int currentLevel = getLevel(pitPlayer.darkzoneData.altarXP);
		pitPlayer.darkzoneData.altarXP += amount;
		int newLevel = getLevel(pitPlayer.darkzoneData.altarXP);

		ActionBarManager.sendActionBar(pitPlayer.player, "&4&l+" + (int) amount + " ALTAR XP");
		Sounds.XP_GAIN.play(pitPlayer.player);

		if(newLevel == currentLevel) return;

		AOutput.send(pitPlayer.player, "&4&lALTAR LEVEL UP! &c" + currentLevel + " &7\u279F &c" + newLevel);

		Sounds.ALTAR_LEVEL_UP.play(pitPlayer.player);
		Misc.sendTitle(pitPlayer.player, "&4&lLEVEL UP!", 40);
		Misc.sendSubTitle(pitPlayer.player, "&4 " + currentLevel + " &7\u279F &4" + newLevel, 40);
	}

	public static double getReduction(int altarLevel, int darkzoneLevel) {
		int levelDifference = Math.max(darkzoneLevel - altarLevel, 0);
		return 100 - 100 * Math.pow(0.99, levelDifference);
	}

	public static double getReductionModifier(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
		return getReduction(getLevel(pitPlayer.darkzoneData.altarXP), prestigeInfo.darkzoneLevelIncrease);
	}
}
