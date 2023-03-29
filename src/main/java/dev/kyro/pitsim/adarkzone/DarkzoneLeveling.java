package dev.kyro.pitsim.adarkzone;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

public class DarkzoneLeveling {
	private static final Map<Integer, Double> xpLevelMap = new LinkedHashMap<>();

	static {
		for(int i = 1; i <= 1_000_000; i++) xpLevelMap.put(i, generateXPForLevel(i));
	}

	public static int getLevel(double xp) {
		for(Map.Entry<Integer, Double> entry : xpLevelMap.entrySet()) {
			int level = entry.getKey();
			double levelXP = entry.getValue();
			if(xp < levelXP) return level - 1;
			xp -= levelXP;
		}
		return -1;
	}

	private static double generateXPForLevel(int level) {
		if(level == 1) return 0;
		return (level - 2) * 0.75 + 15;
	}

	public static double getXPForLevel(int level) {
		return xpLevelMap.get(level);
	}

	public static double getXPProgressToNextLevel(double currentXP) {
		int level = getLevel(currentXP);
		return currentXP - getXPToLevel(level);
	}

	public static double getXPToLevel(int level) {
		double totalXP = 0;
		for(int i = 1; i <= level; i++) totalXP += getXPForLevel(i);
		return totalXP;
	}

	public static void giveXP(PitPlayer pitPlayer, double amount) {
		int currentLevel = getLevel(pitPlayer.darkzoneData.altarXP);
		pitPlayer.darkzoneData.altarXP += amount;
		int newLevel = getLevel(pitPlayer.darkzoneData.altarXP);

		DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
		AOutput.send(pitPlayer.player, "&4&lALTAR!&7 Gained &c+" + decimalFormat.format(amount) + " Altar XP");
		Sounds.XP_GAIN.play(pitPlayer.player);

		if(newLevel == currentLevel) return;

		AOutput.send(pitPlayer.player, "&4&lALTAR LEVEL UP! &c" + currentLevel + " &7\u279F &c" + newLevel);

		if(!Misc.isKyro(pitPlayer.player.getUniqueId())) Sounds.ALTAR_LEVEL_UP.play(pitPlayer.player);
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

	public static void updateAltarXP(PitPlayer pitPlayer) {
		double altarXP = pitPlayer.darkzoneData.altarXP;
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.darkzoneData.preDarkzoneUpdatePrestige);
		double expectedMinimumXP = getXPToLevel(prestigeInfo.darkzoneLevelIncrease);
		if(altarXP >= expectedMinimumXP) return;
		pitPlayer.darkzoneData.altarXP = expectedMinimumXP;
		DecimalFormat decimalFormat = new DecimalFormat("#,##0");
		AOutput.send(pitPlayer.player, "&4&lALTAR UPDATE!&7 An update has increased your &cAltar XP &7to level &c" +
				decimalFormat.format(prestigeInfo.darkzoneLevelIncrease));
	}
}
