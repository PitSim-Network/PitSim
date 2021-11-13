package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

public class PrestigeValues {

	private static final Map<Integer, Integer> xpLevelMap = new LinkedHashMap<>();

	private static final Map<Integer, PrestigeInfo> prestigeMap = new LinkedHashMap<>();
	public static class PrestigeInfo {
		public int prestige;
		public double xpMultiplier;
		public double goldReq;
		public double killReq;
		public int renownReward;
		public final String bracketColor;

		public String getOpenBracket() {
			if(prestige == 50) return "&f&k|";
			return bracketColor + "[";
		}
		public String getCloseBracket() {
			if(prestige == 50) return "&f&k|&r";
			return bracketColor + "]";
		}

		public String getOpenBracketNameTag() {
			if(prestige == 50) return "&f|";
			return bracketColor + "[";
		}
		public String getCloseBracketNameTag() {
			if(prestige == 50) return "&f|";
			return bracketColor + "]";
		}

		public PrestigeInfo(int prestige, double xpMultiplier, double goldReq, double killReq, int renownReward, String bracketColor) {
			this.prestige = prestige;
			this.xpMultiplier = xpMultiplier;
			this.goldReq = goldReq;
			this.killReq = killReq;
			this.renownReward = renownReward;
			this.bracketColor = bracketColor;
		}
	}

	public static PrestigeInfo getPrestigeInfo(int prestige) {

		return prestigeMap.get(prestige);
	}

	public static int getXPForLevel(int level) {
		if(level >= 120) return 0;
		if(level >= 110) return xpLevelMap.get(110);
		if(xpLevelMap.containsKey(level)) return xpLevelMap.get(level);
		for(Map.Entry<Integer, Integer> entry : xpLevelMap.entrySet()) {
			if(entry.getKey() < level) continue;
			double diff = (entry.getValue() - xpLevelMap.get(entry.getKey() - 10)) / 10D;
			return (int) (entry.getValue() - diff * (entry.getKey() - level));
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
		PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(pitPlayer.prestige);
		return prestigeInfo.getOpenBracket() + getLevelColor(pitPlayer.level) + pitPlayer.level + prestigeInfo.getCloseBracket();
	}

	static {
		xpLevelMap.put(0, 15);
		xpLevelMap.put(10, 30);
		xpLevelMap.put(20, 50);
		xpLevelMap.put(30, 75);
		xpLevelMap.put(40, 125);
		xpLevelMap.put(50, 300);
		xpLevelMap.put(60, 600);
		xpLevelMap.put(70, 800);
		xpLevelMap.put(80, 900);
		xpLevelMap.put(90, 1000);
		xpLevelMap.put(100, 1200);
		xpLevelMap.put(110, 1500);

		prestigeMap.put(0, new PrestigeInfo(0, 1.5, 20_000, 10, 20, "&7"));
		prestigeMap.put(1, new PrestigeInfo(1, 1.65, 40_000, 11, 10, "&9")); //tenacity gboost xp
		prestigeMap.put(2, new PrestigeInfo(2, 1.8, 60_000, 12, 10, "&9"));
		prestigeMap.put(3, new PrestigeInfo(3, 1.95, 80_000, 13, 11, "&9"));
		prestigeMap.put(4, new PrestigeInfo(4, 2.1, 100_000, 14, 12, "&9"));
		prestigeMap.put(5, new PrestigeInfo(5, 2.25, 120_000, 15, 12, "&e")); //lucky kill
		prestigeMap.put(6, new PrestigeInfo(6, 2.55, 140_000, 16, 12, "&e")); //impatient
		prestigeMap.put(7, new PrestigeInfo(7, 3, 160_000, 17, 13, "&e")); //streaker
		prestigeMap.put(8, new PrestigeInfo(8, 3.75, 180_000, 18, 13, "&e"));
		prestigeMap.put(9, new PrestigeInfo(9, 4.5, 200_000, 19, 14, "&e")); //doubledeath
		prestigeMap.put(10, new PrestigeInfo(10, 6, 220_000, 40, 14, "&6")); //firststrike
		prestigeMap.put(11, new PrestigeInfo(11, 7.5, 240_000, 42, 14, "&6")); //report access
		prestigeMap.put(12, new PrestigeInfo(12, 9, 270_000, 44, 15, "&6")); //lifeinsurance
		prestigeMap.put(13, new PrestigeInfo(13, 10.5, 300_000, 46, 15, "&6")); //beast
		prestigeMap.put(14, new PrestigeInfo(14, 12, 340_000, 48, 16, "&6")); //counter-janitor
		prestigeMap.put(15, new PrestigeInfo(15, 13.5, 400_000, 50, 16, "&c")); //helmetry
		prestigeMap.put(16, new PrestigeInfo(16, 15, 500_000, 52, 16, "&c")); //divine
		prestigeMap.put(17, new PrestigeInfo(17, 18, 600_000, 54, 16, "&c")); //highlander
		prestigeMap.put(18, new PrestigeInfo(18, 21, 700_000, 56, 18, "&c")); //withercraft
		prestigeMap.put(19, new PrestigeInfo(19, 24, 800_000, 58, 20, "&c"));
		prestigeMap.put(20, new PrestigeInfo(20, 27, 900_000, 100, 20, "&5")); //uber
		prestigeMap.put(21, new PrestigeInfo(21, 30, 1_000_000, 105, 20, "&5"));
		prestigeMap.put(22, new PrestigeInfo(22, 34.5, 1_200_000, 110, 20, "&5")); //taxevasion
		prestigeMap.put(23, new PrestigeInfo(23, 39, 1_400_000, 115, 22, "&5")); //complex
		prestigeMap.put(24, new PrestigeInfo(24, 45, 1_700_000, 120, 24, "&5"));
		prestigeMap.put(25, new PrestigeInfo(25, 52.5, 2_000_000, 125, 24, "&d")); //uberincrease
		prestigeMap.put(26, new PrestigeInfo(26, 61.5, 2_300_000, 130, 24, "&d"));
		prestigeMap.put(27, new PrestigeInfo(27, 72, 2_600_000, 135, 26, "&d")); //killsteal
		prestigeMap.put(28, new PrestigeInfo(28, 82.5, 3_000_000, 140, 28, "&d")); //shard
		prestigeMap.put(29, new PrestigeInfo(29, 93, 3_400_000, 145, 30, "&d"));
		prestigeMap.put(30, new PrestigeInfo(30, 105, 3_800_000, 200, 30, "&f")); //moon
		prestigeMap.put(31, new PrestigeInfo(31, 120, 4_200_000, 210, 30, "&f"));
		prestigeMap.put(32, new PrestigeInfo(32, 135, 4_600_000, 220, 32, "&f"));
		prestigeMap.put(33, new PrestigeInfo(33, 180, 5_000_000, 230, 34, "&f"));
		prestigeMap.put(34, new PrestigeInfo(34, 187.5, 5_400_000, 240, 36, "&f"));
		prestigeMap.put(35, new PrestigeInfo(35, 195, 5_800_000, 250, 36, "&a")); //withercraft
		prestigeMap.put(36, new PrestigeInfo(36, 202.5, 6_200_000, 260, 36, "&a"));
		prestigeMap.put(37, new PrestigeInfo(37, 210, 6_600_000, 270, 38, "&a"));
		prestigeMap.put(38, new PrestigeInfo(38, 217.5, 7_000_000, 280, 40, "&a")); //Fast pass
		prestigeMap.put(39, new PrestigeInfo(39, 225, 7_500_000, 290, 42, "&a"));
		prestigeMap.put(40, new PrestigeInfo(40, 232.5, 10_000_000, 300, 42, "&2")); //celebrity
		prestigeMap.put(41, new PrestigeInfo(41, 240, 11_000_000, 310, 42, "&2"));
		prestigeMap.put(42, new PrestigeInfo(42, 247.5, 12_000_000, 320, 44, "&2"));
		prestigeMap.put(43, new PrestigeInfo(43, 255, 14_000_000, 330, 46, "&2"));
		prestigeMap.put(44, new PrestigeInfo(44, 262.5, 16_000_000, 340, 48, "&2"));
		prestigeMap.put(45, new PrestigeInfo(45, 277.5, 18_000_000, 350, 50, "&4"));
		prestigeMap.put(46, new PrestigeInfo(46, 292.5, 20_000_000, 375, 60, "&4"));
		prestigeMap.put(47, new PrestigeInfo(47, 307.5, 22_000_000, 400, 70, "&4"));
		prestigeMap.put(48, new PrestigeInfo(48, 337.5, 25_000_000, 500, 80, "&4"));
		prestigeMap.put(49, new PrestigeInfo(49, 375, 30_000_000, 1000, 100, "&4"));
		prestigeMap.put(50, new PrestigeInfo(50, 3000, 0, 0, 300, ""));
	}
}