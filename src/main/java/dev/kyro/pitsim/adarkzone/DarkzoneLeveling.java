package dev.kyro.pitsim.adarkzone;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.adarkzone.altar.AltarManager;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;

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
		return (level - 2) * DarkzoneBalancing.EVERY_LEVEL_XP_INCREASE + DarkzoneBalancing.FIRST_LEVEL_XP;
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

		AltarManager.hologram.updateHologram(pitPlayer.player);
		if(newLevel == currentLevel) return;

		AOutput.send(pitPlayer.player, "&4&lALTAR LEVEL UP! &c" + currentLevel + " &7\u279F &c" + newLevel);

		Misc.sendTitle(pitPlayer.player, "&4&lLEVEL UP!", 40);
		Misc.sendSubTitle(pitPlayer.player, "&4 " + currentLevel + " &7\u279F &4" + newLevel, 40);
	}

	public static double getReductionMultiplier(int altarLevel, int darkzoneLevel) {
		int levelDifference = Math.min(Math.max(darkzoneLevel - altarLevel, -50), 30);
		if(levelDifference < 0) levelDifference /= 2;
		return 1 - (levelDifference / 100.0);
	}

	public static double getReductionMultiplier(PitPlayer pitPlayer) {
		if(pitPlayer == null || !PlayerManager.isRealPlayer(pitPlayer.player)) return 1;
		PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
		return getReductionMultiplier(getLevel(pitPlayer.darkzoneData.altarXP), prestigeInfo.getDarkzoneLevel());
	}

	public static String getReductionPercent(PitPlayer pitPlayer) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		String reduction = decimalFormat.format((1 - getReductionMultiplier(pitPlayer)) * -100);
		return (Double.parseDouble(reduction) >= 0 ? "+" : "") + reduction;
	}
}
