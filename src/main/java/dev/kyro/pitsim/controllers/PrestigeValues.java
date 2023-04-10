package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.adarkzone.DarkzoneLeveling;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

public class PrestigeValues {
	public static int currentDarkzoneXP = 0;
	public static int MAX_PRESTIGE = 60;
	private static final Map<Integer, Long> xpLevelMap = new LinkedHashMap<>();
	private static final Map<Integer, PrestigeInfo> prestigeMap = new LinkedHashMap<>();

	public static Map<Integer, Long> totalXPMap = new LinkedHashMap<>();

	public static long getTotalXPForPrestige(int prestige, int level, long remainingXP) {
		PrestigeInfo prestigeInfo = getPrestigeInfo(prestige);
		long totalXP = 0;
		for(int i = 1; i < level + 1; i++) totalXP += getXPForLevel(i) * prestigeInfo.xpMultiplier;
		return totalXP - remainingXP;
	}

	public static long getTotalXPForPrestige(int prestige) {
		return totalXPMap.get(prestige);
	}

	public static long getTotalXP(int prestige, int level, long remainingXP) {
		return getXpUpToPrestige(prestige) + getTotalXPForPrestige(prestige, level, remainingXP);
	}

	public static long getXpUpToPrestige(int prestige) {
		long totalXP = 0;
		for(int i = 0; i < prestige; i++) totalXP += totalXPMap.get(i);
		return totalXP;
	}

	public static class PrestigeInfo {
		private final int prestige;
		private final double xpMultiplier;
		private final double goldReq;
		private final int darkzoneXP;
		private final int renownReward;
		private final String bracketColor;

		public PrestigeInfo(int prestige, double xpMultiplier, double goldReq, int darkzoneXP, int renownReward, String bracketColor) {
			this.prestige = prestige;
			this.xpMultiplier = xpMultiplier;
			this.goldReq = goldReq;
			this.darkzoneXP = currentDarkzoneXP += darkzoneXP;
			this.renownReward = renownReward;
			this.bracketColor = bracketColor;
		}

		public String getOpenBracket() {
//			if(prestige == 50) return "&f&k|";
			return bracketColor + "[";
		}

		public String getCloseBracket() {
//			if(prestige == 50) return "&f&k|&r";
			return bracketColor + "]";
		}

		public String getOpenBracketNameTag() {
//			if(prestige == 50) return "&f|";
			return bracketColor + "[";
		}

		public String getCloseBracketNameTag() {
//			if(prestige == 50) return "&f|";
			return bracketColor + "]";
		}

		public int getPrestige() {
			return prestige;
		}

		public double getXpMultiplier() {
			return xpMultiplier;
		}

		public double getGoldReq() {
			return goldReq;
		}

		public int getDarkzoneLevel() {
			return DarkzoneLeveling.getLevel(darkzoneXP);
		}

		public int getRenownReward() {
			return renownReward;
		}

		public String getBracketColor() {
			return bracketColor;
		}
	}

	public static PrestigeInfo getPrestigeInfo(int prestige) {
		return prestigeMap.get(prestige);
	}

	public static long getXPForLevel(int level) {
		if(level >= 120) return 0;
		if(level >= 110) return xpLevelMap.get(110);
		if(xpLevelMap.containsKey(level)) return xpLevelMap.get(level);
		for(Map.Entry<Integer, Long> entry : xpLevelMap.entrySet()) {
			if(entry.getKey() < level) continue;
			double diff = (entry.getValue() - xpLevelMap.get(entry.getKey() - 10)) / 10D;
			return (long) (entry.getValue() - diff * (entry.getKey() - level));
		}
		return -1;
	}

	public static String getLevelColor(int level) {
		if(level >= 1 && level <= 9) return "&7";
		if(level >= 10 && level <= 19) return "&9";
		if(level >= 20 && level <= 29) return "&3";
		if(level >= 30 && level <= 39) return "&2";
		if(level >= 40 && level <= 49) return "&a";
		if(level >= 50 && level <= 59) return "&e";
		if(level >= 60 && level <= 69) return "&6&l";
		if(level >= 70 && level <= 79) return "&c&l";
		if(level >= 80 && level <= 89) return "&4&l";
		if(level >= 90 && level <= 99) return "&5&l";
		if(level >= 100 && level <= 109) return "&d&l";
		if(level >= 110 && level <= 119) return "&f&l";
		if(level >= 120) return "&b&l";
		else return "&7";
	}

	public static String getPlayerPrefixNameTag(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
		return prestigeInfo.getOpenBracketNameTag() + getLevelColor(pitPlayer.level) + pitPlayer.level + prestigeInfo.getCloseBracketNameTag() + " ";
	}

	public static String getPlayerPrefix(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return getPlayerPrefix(pitPlayer.prestige, pitPlayer.level);
	}

	public static String getPlayerPrefix(int prestige, int level) {
		PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(prestige);
		return prestigeInfo.getOpenBracket() + getLevelColor(level) + level + prestigeInfo.getCloseBracket();
	}

	public static String getLeaderboardPrefix(int prestige, int level) {
		PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(prestige);
		return prestigeInfo.getOpenBracket() + ChatColor.YELLOW + AUtil.toRoman(prestige) + prestigeInfo.bracketColor + "-" +
				getLevelColor(level) + level + prestigeInfo.getCloseBracket();
	}

	static {
		xpLevelMap.put(0, 15L);
		xpLevelMap.put(10, 30L);
		xpLevelMap.put(20, 50L);
		xpLevelMap.put(30, 100L);
		xpLevelMap.put(40, 250L);
		xpLevelMap.put(50, 400L);
		xpLevelMap.put(60, 750L);
		xpLevelMap.put(70, 1000L);
		xpLevelMap.put(80, 1200L);
		xpLevelMap.put(90, 1400L);
		xpLevelMap.put(100, 1700L);
		xpLevelMap.put(110, 2500L);

		prestigeMap.put(0, new PrestigeInfo(0, 1.0, 20_000, 0, 20, "&7"));
		prestigeMap.put(1, new PrestigeInfo(1, 1.2, 40_000, 0, 10, "&9")); //tenacity gboost xp
		prestigeMap.put(2, new PrestigeInfo(2, 1.5, 60_000, 0, 10, "&9"));
		prestigeMap.put(3, new PrestigeInfo(3, 2.0, 80_000, 0, 11, "&9"));
		prestigeMap.put(4, new PrestigeInfo(4, 2.5, 100_000, 0, 12, "&9"));
		prestigeMap.put(5, new PrestigeInfo(5, 3.0, 120_000, 50, 12, "&e")); //lucky kill
		prestigeMap.put(6, new PrestigeInfo(6, 3.5, 140_000, 50, 12, "&e")); //impatient
		prestigeMap.put(7, new PrestigeInfo(7, 4.0, 160_000, 50, 13, "&e")); //streaker
		prestigeMap.put(8, new PrestigeInfo(8, 4.5, 180_000, 50, 13, "&e"));
		prestigeMap.put(9, new PrestigeInfo(9, 5.0, 200_000, 50, 14, "&e")); //doubledeath
		prestigeMap.put(10, new PrestigeInfo(10, 6.0, 300_000, 100, 14, "&6")); //firststrike
		prestigeMap.put(11, new PrestigeInfo(11, 7.0, 400_000, 100, 14, "&6"));
		prestigeMap.put(12, new PrestigeInfo(12, 8.0, 500_000, 100, 15, "&6")); //lifeinsurance
		prestigeMap.put(13, new PrestigeInfo(13, 10.0, 650_000, 100, 15, "&6")); //beast
		prestigeMap.put(14, new PrestigeInfo(14, 12.0, 800_000, 100, 16, "&6")); //counter-janitor
		prestigeMap.put(15, new PrestigeInfo(15, 14.0, 1_000_000, 150, 16, "&c")); //helmetry
		prestigeMap.put(16, new PrestigeInfo(16, 16.0, 1_300_000, 150, 16, "&c")); //divine
		prestigeMap.put(17, new PrestigeInfo(17, 18.0, 1_600_000, 150, 16, "&c")); //highlander
		prestigeMap.put(18, new PrestigeInfo(18, 21.0, 1_700_000, 150, 18, "&c")); //withercraft
		prestigeMap.put(19, new PrestigeInfo(19, 24.0, 1_800_000, 150, 20, "&c"));
		prestigeMap.put(20, new PrestigeInfo(20, 27.0, 2_000_000, 300, 20, "&5")); //uber
		prestigeMap.put(21, new PrestigeInfo(21, 30.0, 2_300_000, 300, 20, "&5"));
		prestigeMap.put(22, new PrestigeInfo(22, 35.0, 2_600_000, 300, 20, "&5")); //taxevasion
		prestigeMap.put(23, new PrestigeInfo(23, 40.0, 2_900_000, 300, 22, "&5")); //complex
		prestigeMap.put(24, new PrestigeInfo(24, 45.0, 3_200_000, 300, 24, "&5"));
		prestigeMap.put(25, new PrestigeInfo(25, 50.0, 3_500_000, 500, 24, "&d")); //uberincrease
		prestigeMap.put(26, new PrestigeInfo(26, 60.0, 3_800_000, 500, 24, "&d"));
		prestigeMap.put(27, new PrestigeInfo(27, 70.0, 3_100_000, 500, 26, "&d")); //killsteal
		prestigeMap.put(28, new PrestigeInfo(28, 80.0, 4_400_000, 500, 28, "&d")); //shard
		prestigeMap.put(29, new PrestigeInfo(29, 90.0, 4_700_000, 500, 30, "&d"));
		prestigeMap.put(30, new PrestigeInfo(30, 200.0, 5_000_000, 2_000, 30, "&f")); //moon
		prestigeMap.put(31, new PrestigeInfo(31, 210.0, 5_300_000, 2_000, 30, "&f"));
		prestigeMap.put(32, new PrestigeInfo(32, 220.0, 5_600_000, 2_000, 32, "&f"));
		prestigeMap.put(33, new PrestigeInfo(33, 230.0, 6_000_000, 2_000, 34, "&f"));
		prestigeMap.put(34, new PrestigeInfo(34, 240.0, 6_400_000, 2_000, 36, "&f"));
		prestigeMap.put(35, new PrestigeInfo(35, 250.0, 6_800_000, 2_500, 36, "&b")); //withercraft
		prestigeMap.put(36, new PrestigeInfo(36, 260.0, 7_200_000, 2_500, 36, "&b"));
		prestigeMap.put(37, new PrestigeInfo(37, 270.0, 7_600_000, 2_500, 38, "&b"));
		prestigeMap.put(38, new PrestigeInfo(38, 280.0, 8_000_000, 2_500, 40, "&b")); //Fast pass
		prestigeMap.put(39, new PrestigeInfo(39, 290.0, 9_000_000, 2_500, 42, "&b"));
		prestigeMap.put(40, new PrestigeInfo(40, 400.0, 40_000_000, 7_000, 42, "&3")); //celebrity
		prestigeMap.put(41, new PrestigeInfo(41, 410.0, 45_000_000, 7_000, 42, "&3"));
		prestigeMap.put(42, new PrestigeInfo(42, 425.0, 50_000_000, 7_000, 44, "&3"));
		prestigeMap.put(43, new PrestigeInfo(43, 450.0, 55_000_000, 7_000, 46, "&3"));
		prestigeMap.put(44, new PrestigeInfo(44, 475.0, 60_000_000, 7_000, 48, "&3"));
		prestigeMap.put(45, new PrestigeInfo(45, 500.0, 65_000_000, 10_000, 50, "&1"));
		prestigeMap.put(46, new PrestigeInfo(46, 550.0, 70_000_000, 10_000, 60, "&1"));
		prestigeMap.put(47, new PrestigeInfo(47, 600.0, 80_000_000, 10_000, 70, "&1"));
		prestigeMap.put(48, new PrestigeInfo(48, 750.0, 90_000_000, 10_000, 80, "&1"));
		prestigeMap.put(49, new PrestigeInfo(49, 1_000.0, 100_000_000, 10_000, 100, "&1"));
		prestigeMap.put(50, new PrestigeInfo(50, 5_000.0, 150_000_000, 20_000, 300, "&2"));
		prestigeMap.put(51, new PrestigeInfo(51, 6_000.0, 200_000_000, 20_000, 100, "&2"));
		prestigeMap.put(52, new PrestigeInfo(52, 7_000.0, 250_000_000, 20_000, 100, "&2"));
		prestigeMap.put(53, new PrestigeInfo(53, 8_000.0, 300_000_000, 20_000, 100, "&2"));
		prestigeMap.put(54, new PrestigeInfo(54, 9_000.0, 400_000_000, 20_000, 100, "&2"));
		prestigeMap.put(55, new PrestigeInfo(55, 25_000.0, 500_000_000, 25_000, 120, "&0"));
		prestigeMap.put(56, new PrestigeInfo(56, 40_000.0, 600_000_000, 25_000, 140, "&0"));
		prestigeMap.put(57, new PrestigeInfo(57, 60_000.0, 700_000_000, 25_000, 160, "&0"));
		prestigeMap.put(58, new PrestigeInfo(58, 80_000.0, 850_000_000, 25_000, 180, "&0"));
		prestigeMap.put(59, new PrestigeInfo(59, 100_000.0, 1_000_000_000, 25_000, 10_000, "&0"));
		prestigeMap.put(60, new PrestigeInfo(60, 1_000_000.0, 0, 100_000, 120, "&4"));

		for(int i = 0; i <= PrestigeValues.MAX_PRESTIGE; i++) {
			PrestigeInfo prestigeInfo = getPrestigeInfo(i);
			long totalXp = 0;
			for(int j = 1; j < 120; j++) totalXp += getXPForLevel(j) * prestigeInfo.xpMultiplier;
			totalXPMap.put(i, totalXp);
		}
	}
}