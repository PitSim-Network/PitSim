package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

public class PrestigeValues {
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
		public int prestige;
		public double xpMultiplier;
		public double goldReq;
		public double soulReq;
		public int renownReward;
		public final String bracketColor;

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

		public PrestigeInfo(int prestige, double xpMultiplier, double goldReq, double soulReq, int renownReward, String bracketColor) {
			this.prestige = prestige;
			this.xpMultiplier = xpMultiplier;
			this.goldReq = goldReq;
			this.soulReq = soulReq;
			this.renownReward = renownReward;
			this.bracketColor = bracketColor;
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

		prestigeMap.put(0, new PrestigeInfo(0, 1, 20_000, 0, 20, "&7"));
		prestigeMap.put(1, new PrestigeInfo(1, 1.2, 40_000, 0, 10, "&9")); //tenacity gboost xp
		prestigeMap.put(2, new PrestigeInfo(2, 1.5, 60_000, 0, 10, "&9"));
		prestigeMap.put(3, new PrestigeInfo(3, 2, 80_000, 0, 11, "&9"));
		prestigeMap.put(4, new PrestigeInfo(4, 2.5, 100_000, 0, 12, "&9"));
		prestigeMap.put(5, new PrestigeInfo(5, 3, 120_000, 0, 12, "&e")); //lucky kill
		prestigeMap.put(6, new PrestigeInfo(6, 3.5, 140_000, 0, 12, "&e")); //impatient
		prestigeMap.put(7, new PrestigeInfo(7, 4, 160_000, 0, 13, "&e")); //streaker
		prestigeMap.put(8, new PrestigeInfo(8, 4.5, 180_000, 0, 13, "&e"));
		prestigeMap.put(9, new PrestigeInfo(9, 5, 200_000, 0, 14, "&e")); //doubledeath
		prestigeMap.put(10, new PrestigeInfo(10, 6, 300_000, 1, 14, "&6")); //firststrike
		prestigeMap.put(11, new PrestigeInfo(11, 7, 400_000, 5, 14, "&6")); //report access
		prestigeMap.put(12, new PrestigeInfo(12, 8, 500_000, 10, 15, "&6")); //lifeinsurance
		prestigeMap.put(13, new PrestigeInfo(13, 10, 650_000, 15, 15, "&6")); //beast
		prestigeMap.put(14, new PrestigeInfo(14, 12, 800_000, 20, 16, "&6")); //counter-janitor
		prestigeMap.put(15, new PrestigeInfo(15, 14, 1_000_000, 25, 16, "&c")); //helmetry
		prestigeMap.put(16, new PrestigeInfo(16, 16, 1_300_000, 30, 16, "&c")); //divine
		prestigeMap.put(17, new PrestigeInfo(17, 18, 1_600_000, 35, 16, "&c")); //highlander
		prestigeMap.put(18, new PrestigeInfo(18, 21, 1_700_000, 40, 18, "&c")); //withercraft
		prestigeMap.put(19, new PrestigeInfo(19, 24, 1_800_000, 45, 20, "&c"));
		prestigeMap.put(20, new PrestigeInfo(20, 27, 2_000_000, 50, 20, "&5")); //uber
		prestigeMap.put(21, new PrestigeInfo(21, 30, 2_300_000, 60, 20, "&5"));
		prestigeMap.put(22, new PrestigeInfo(22, 35, 2_600_000, 70, 20, "&5")); //taxevasion
		prestigeMap.put(23, new PrestigeInfo(23, 40, 2_900_000, 80, 22, "&5")); //complex
		prestigeMap.put(24, new PrestigeInfo(24, 45, 3_200_000, 90, 24, "&5"));
		prestigeMap.put(25, new PrestigeInfo(25, 50, 3_500_000, 100, 24, "&d")); //uberincrease
		prestigeMap.put(26, new PrestigeInfo(26, 60, 3_800_000, 110, 24, "&d"));
		prestigeMap.put(27, new PrestigeInfo(27, 70, 3_100_000, 120, 26, "&d")); //killsteal
		prestigeMap.put(28, new PrestigeInfo(28, 80, 4_400_000, 130, 28, "&d")); //shard
		prestigeMap.put(29, new PrestigeInfo(29, 90, 4_700_000, 140, 30, "&d"));
		prestigeMap.put(30, new PrestigeInfo(30, 200, 5_000_000, 150, 30, "&f")); //moon
		prestigeMap.put(31, new PrestigeInfo(31, 210, 5_300_000, 165, 30, "&f"));
		prestigeMap.put(32, new PrestigeInfo(32, 220, 5_600_000, 180, 32, "&f"));
		prestigeMap.put(33, new PrestigeInfo(33, 230, 6_000_000, 195, 34, "&f"));
		prestigeMap.put(34, new PrestigeInfo(34, 240, 6_400_000, 210, 36, "&f"));
		prestigeMap.put(35, new PrestigeInfo(35, 250, 6_800_000, 225, 36, "&b")); //withercraft
		prestigeMap.put(36, new PrestigeInfo(36, 260, 7_200_000, 240, 36, "&b"));
		prestigeMap.put(37, new PrestigeInfo(37, 270, 7_600_000, 255, 38, "&b"));
		prestigeMap.put(38, new PrestigeInfo(38, 280, 8_000_000, 270, 40, "&b")); //Fast pass
		prestigeMap.put(39, new PrestigeInfo(39, 290, 9_000_000, 285, 42, "&b"));
		prestigeMap.put(40, new PrestigeInfo(40, 400, 40_000_000, 300, 42, "&3")); //celebrity
		prestigeMap.put(41, new PrestigeInfo(41, 410, 45_000_000, 320, 42, "&3"));
		prestigeMap.put(42, new PrestigeInfo(42, 425, 50_000_000, 340, 44, "&3"));
		prestigeMap.put(43, new PrestigeInfo(43, 450, 55_000_000, 360, 46, "&3"));
		prestigeMap.put(44, new PrestigeInfo(44, 475, 60_000_000, 380, 48, "&3"));
		prestigeMap.put(45, new PrestigeInfo(45, 500, 65_000_000, 400, 50, "&1"));
		prestigeMap.put(46, new PrestigeInfo(46, 550, 70_000_000, 420, 60, "&1"));
		prestigeMap.put(47, new PrestigeInfo(47, 600, 80_000_000, 440, 70, "&1"));
		prestigeMap.put(48, new PrestigeInfo(48, 750, 90_000_000, 460, 80, "&1"));
		prestigeMap.put(49, new PrestigeInfo(49, 1_000, 100_000_000, 480, 100, "&1"));
		prestigeMap.put(50, new PrestigeInfo(50, 5_000, 150_000_000, 500, 300, "&2"));
		prestigeMap.put(51, new PrestigeInfo(51, 6_000, 200_000_000, 1000, 100, "&2"));
		prestigeMap.put(52, new PrestigeInfo(52, 7_000, 250_000_000, 1500, 100, "&2"));
		prestigeMap.put(53, new PrestigeInfo(53, 8_000, 300_000_000, 2000, 100, "&2"));
		prestigeMap.put(54, new PrestigeInfo(54, 9_000, 400_000_000, 2500, 100, "&2"));
		prestigeMap.put(55, new PrestigeInfo(55, 25_000, 500_000_000, 3000, 120, "&0"));
		prestigeMap.put(56, new PrestigeInfo(56, 40_000, 600_000_000, 3500, 140, "&0"));
		prestigeMap.put(57, new PrestigeInfo(57, 60_000, 700_000_000, 4000, 160, "&0"));
		prestigeMap.put(58, new PrestigeInfo(58, 80_000, 850_000_000, 4500, 180, "&0"));
		prestigeMap.put(59, new PrestigeInfo(59, 100_000, 1_000_000_000, 5000, 10_000, "&0"));
		prestigeMap.put(60, new PrestigeInfo(60, 1_000_000, 0, 0, 120, "&4"));

		for(int i = 0; i <= PrestigeValues.MAX_PRESTIGE; i++) {
			PrestigeInfo prestigeInfo = getPrestigeInfo(i);
			long totalXp = 0;
			for(int j = 1; j < 120; j++) totalXp += getXPForLevel(j) * prestigeInfo.xpMultiplier;
			totalXPMap.put(i, totalXp);
		}
	}
}