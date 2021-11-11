import java.util.LinkedHashMap;
import java.util.Map;

public class LevelSystem {

	private static final Map<Integer, Integer> xpLevelMap = new LinkedHashMap<>();

	private static final Map<Integer, PrestigeInfo> prestigeMap = new LinkedHashMap<>();
	static class PrestigeInfo {
		public int prestige;
		public double xpMultiplier;
		public double goldReq;
		public double killReq;
		public int renownReward;
		private final String bracketColor;

		public String getOpenBracket() {
			if(prestige == 50) return "&f&k||&r";
			return bracketColor + "[&r";
		}
		public String getCloseBracket() {
			if(prestige == 50) return "&f&k||&r";
			return bracketColor + "]&r";
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

		prestigeMap.put(0, new PrestigeInfo(0, 1.0, 20_000, 10, 40, "&7"));
		prestigeMap.put(1, new PrestigeInfo(1, 1.1, 40_000, 11, 10, "&9")); //tenacity gboost xp
		prestigeMap.put(2, new PrestigeInfo(2, 1.2, 60_000, 12, 10, "&9"));
		prestigeMap.put(3, new PrestigeInfo(3, 1.3, 80_000, 13, 10, "&9"));
		prestigeMap.put(4, new PrestigeInfo(4, 1.4, 100_000, 14, 12, "&9"));
		prestigeMap.put(5, new PrestigeInfo(5, 1.5, 120_000, 15, 12, "&e")); //lucky kill
		prestigeMap.put(6, new PrestigeInfo(6, 1.7, 140_000, 16, 12, "&e")); //impatient
		prestigeMap.put(7, new PrestigeInfo(7, 2.0, 160_000, 17, 12, "&e")); //streaker
		prestigeMap.put(8, new PrestigeInfo(8, 2.5, 180_000, 18, 12, "&e"));
		prestigeMap.put(9, new PrestigeInfo(9, 3.0, 200_000, 19, 14, "&e")); //doubledeath
		prestigeMap.put(10, new PrestigeInfo(10, 4.0, 220_000, 40, 14, "&6")); //firststrike
		prestigeMap.put(11, new PrestigeInfo(11, 5.0, 240_000, 42, 14, "&6"));
		prestigeMap.put(12, new PrestigeInfo(12, 6.0, 270_000, 44, 14, "&6")); //lifeinsurance
		prestigeMap.put(13, new PrestigeInfo(13, 7.0, 300_000, 46, 14, "&6"));
		prestigeMap.put(14, new PrestigeInfo(14, 8.0, 340_000, 48, 16, "&6"));
		prestigeMap.put(15, new PrestigeInfo(15, 9.0, 400_000, 50, 16, "&c")); //helmetry
		prestigeMap.put(16, new PrestigeInfo(16, 10.0, 500_000, 52, 16, "&c")); //beast
		prestigeMap.put(17, new PrestigeInfo(17, 12.0, 600_000, 54, 16, "&c")); //divine
		prestigeMap.put(18, new PrestigeInfo(18, 14.0, 700_000, 56, 16, "&c")); //withercraft
		prestigeMap.put(19, new PrestigeInfo(19, 16.0, 800_000, 58, 20, "&c"));
		prestigeMap.put(20, new PrestigeInfo(20, 18.0, 900_000, 100, 20, "&5")); //uber
		prestigeMap.put(21, new PrestigeInfo(21, 20.0, 1_000_000, 105, 20, "&5"));
		prestigeMap.put(22, new PrestigeInfo(22, 23.0, 1_200_000, 110, 20, "&5")); //taxevasion
		prestigeMap.put(23, new PrestigeInfo(23, 26.0, 1_400_000, 115, 20, "&5")); //complex
		prestigeMap.put(24, new PrestigeInfo(24, 30.0, 1_600_000, 120, 24, "&5"));
		prestigeMap.put(25, new PrestigeInfo(25, 35.0, 2_000_000, 125, 24, "&d")); //highlander
		prestigeMap.put(26, new PrestigeInfo(26, 41.0, 2_100_000, 130, 24, "&d"));
		prestigeMap.put(27, new PrestigeInfo(27, 48.0, 2_200_000, 135, 24, "&d")); //uberincrease
		prestigeMap.put(28, new PrestigeInfo(28, 55.0, 2_300_000, 140, 24, "&d"));
		prestigeMap.put(29, new PrestigeInfo(29, 62.0, 2_400_000, 145, 30, "&d"));
		prestigeMap.put(30, new PrestigeInfo(30, 70.0, 2_500_000, 200, 30, "&f")); //shard
		prestigeMap.put(31, new PrestigeInfo(31, 80.0, 2_600_000, 210, 30, "&f"));
		prestigeMap.put(32, new PrestigeInfo(32, 90.0, 2_800_000, 220, 30, "&f"));
		prestigeMap.put(33, new PrestigeInfo(33, 120.0, 3_000_000, 230, 30, "&f")); //moon
		prestigeMap.put(34, new PrestigeInfo(34, 125.0, 3_400_000, 240, 36, "&f"));
		prestigeMap.put(35, new PrestigeInfo(35, 130.0, 4_000_000, 250, 36, "&4")); //withercraft
		prestigeMap.put(36, new PrestigeInfo(36, 135.0, 4_600_000, 260, 36, "&4"));
		prestigeMap.put(37, new PrestigeInfo(37, 140.0, 5_200_000, 270, 36, "&4"));
		prestigeMap.put(38, new PrestigeInfo(38, 145.0, 6_000_000, 280, 36, "&4"));
		prestigeMap.put(39, new PrestigeInfo(39, 150.0, 6_800_000, 290, 42, "&4"));
		prestigeMap.put(40, new PrestigeInfo(40, 155.0, 8_000_000, 300, 42, "&a")); //celebrity
		prestigeMap.put(41, new PrestigeInfo(41, 160.0, 8_400_000, 310, 42, "&a"));
		prestigeMap.put(42, new PrestigeInfo(42, 165.0, 8_800_000, 320, 42, "&a"));
		prestigeMap.put(43, new PrestigeInfo(43, 170.0, 9_200_000, 330, 42, "&a"));
		prestigeMap.put(44, new PrestigeInfo(44, 175.0, 9_600_000, 340, 48, "&a"));
		prestigeMap.put(45, new PrestigeInfo(45, 185.0, 10_000_000, 350, 54, "&2"));
		prestigeMap.put(46, new PrestigeInfo(46, 195.0, 11_000_000, 375, 60, "&2"));
		prestigeMap.put(47, new PrestigeInfo(47, 205.0, 12_000_000, 400, 70, "&2"));
		prestigeMap.put(48, new PrestigeInfo(48, 225.0, 13_000_000, 500, 80, "&2"));
		prestigeMap.put(49, new PrestigeInfo(49, 250.0, 20_000_000, 1000, 100, "&2"));
		prestigeMap.put(50, new PrestigeInfo(50, 2000.0, 0, 0, 300, ""));
	}
}