package dev.kyro.pitsim.controllers;

import java.util.LinkedHashMap;
import java.util.Map;

public class PrestigeValues {

	private static final Map<Integer, Integer> xpLevelMap = new LinkedHashMap<>();

	private static final Map<Integer, PrestigeInfo> prestigeMap = new LinkedHashMap<>();
	public static class PrestigeInfo {
		public double xpMultiplier;
		public double goldReq;
		public double killReq;

		public PrestigeInfo(double xpMultiplier, double goldReq, double killReq) {
			this.xpMultiplier = xpMultiplier;
			this.goldReq = goldReq;
			this.killReq = killReq;
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

//		prestigeXPMultiplierMap.put(0, 1.0);
//		prestigeXPMultiplierMap.put(1, 1.1);
//		prestigeXPMultiplierMap.put(2, 1.2);
//		prestigeXPMultiplierMap.put(3, 1.3);
//		prestigeXPMultiplierMap.put(4, 1.4);
//		prestigeXPMultiplierMap.put(5, 1.5);
//		prestigeXPMultiplierMap.put(6, 1.75);
//		prestigeXPMultiplierMap.put(7, 2.0);
//		prestigeXPMultiplierMap.put(8, 2.5);
//		prestigeXPMultiplierMap.put(9, 3.0);
//		prestigeXPMultiplierMap.put(10, 4.0);
//		prestigeXPMultiplierMap.put(11, 5.0);
//		prestigeXPMultiplierMap.put(12, 6.0);
//		prestigeXPMultiplierMap.put(13, 7.0);
//		prestigeXPMultiplierMap.put(14, 8.0);
//		prestigeXPMultiplierMap.put(15, 9.0);
//		prestigeXPMultiplierMap.put(16, 10.0);
//		prestigeXPMultiplierMap.put(17, 12.0);
//		prestigeXPMultiplierMap.put(18, 14.0);
//		prestigeXPMultiplierMap.put(19, 16.0);
//		prestigeXPMultiplierMap.put(20, 18.0);
//		prestigeXPMultiplierMap.put(21, 20.0);
//		prestigeXPMultiplierMap.put(22, 24.0);
//		prestigeXPMultiplierMap.put(23, 28.0);
//		prestigeXPMultiplierMap.put(24, 32.0);
//		prestigeXPMultiplierMap.put(25, 36.0);
//		prestigeXPMultiplierMap.put(26, 40.0);
//		prestigeXPMultiplierMap.put(27, 45.0);
//		prestigeXPMultiplierMap.put(28, 50.0);
//		prestigeXPMultiplierMap.put(29, 75.0);
//		prestigeXPMultiplierMap.put(30, 100.0);
//		prestigeXPMultiplierMap.put(31, 101.0);
//		prestigeXPMultiplierMap.put(32, 101.0);
//		prestigeXPMultiplierMap.put(33, 101.0);
//		prestigeXPMultiplierMap.put(34, 101.0);
//		prestigeXPMultiplierMap.put(35, 101.0);

		prestigeMap.put(0, new PrestigeInfo(1.0, 10_000, 10));
		prestigeMap.put(1, new PrestigeInfo(1.1, 20_000, 11)); //tenacity gboost xp
		prestigeMap.put(2, new PrestigeInfo(1.2, 30_000, 12));
		prestigeMap.put(3, new PrestigeInfo(1.3, 40_000, 13));
		prestigeMap.put(4, new PrestigeInfo(1.4, 50_000, 14));
		prestigeMap.put(5, new PrestigeInfo(1.5, 60_000, 15)); //lucky kill
		prestigeMap.put(6, new PrestigeInfo(1.7, 70_000, 16)); //impatient
		prestigeMap.put(7, new PrestigeInfo(2.0, 80_000, 17)); //streaker
		prestigeMap.put(8, new PrestigeInfo(2.5, 90_000, 18));
		prestigeMap.put(9, new PrestigeInfo(3.0, 100_000, 19)); //doubledeath
		prestigeMap.put(10, new PrestigeInfo(4.0, 110_000, 40)); //firststrike
		prestigeMap.put(11, new PrestigeInfo(5.0, 120_000, 42));
		prestigeMap.put(12, new PrestigeInfo(6.0, 135_000, 44)); //lifeinsurance
		prestigeMap.put(13, new PrestigeInfo(7.0, 150_000, 46));
		prestigeMap.put(14, new PrestigeInfo(8.0, 170_000, 48));
		prestigeMap.put(15, new PrestigeInfo(9.0, 200_000, 50)); //helmetry
		prestigeMap.put(16, new PrestigeInfo(10.0, 250_000, 52)); //beast
		prestigeMap.put(17, new PrestigeInfo(12.0, 300_000, 54)); //divine
		prestigeMap.put(18, new PrestigeInfo(14.0, 350_000, 56)); //withercraft
		prestigeMap.put(19, new PrestigeInfo(16.0, 400_000, 58));
		prestigeMap.put(20, new PrestigeInfo(18.0, 450_000, 100)); //uber
		prestigeMap.put(21, new PrestigeInfo(20.0, 500_000, 105));
		prestigeMap.put(22, new PrestigeInfo(23.0, 600_000, 110)); //taxevasion
		prestigeMap.put(23, new PrestigeInfo(26.0, 700_000, 115)); //complex
		prestigeMap.put(24, new PrestigeInfo(30.0, 800_000, 120));
		prestigeMap.put(25, new PrestigeInfo(34.0, 1_000_000, 125)); //highlander
		prestigeMap.put(26, new PrestigeInfo(38.0, 1_050_000, 130));
		prestigeMap.put(27, new PrestigeInfo(42.0, 1_100_000, 135)); //uberincrease
		prestigeMap.put(28, new PrestigeInfo(46.0, 1_150_000, 140));
		prestigeMap.put(29, new PrestigeInfo(50.0, 1_200_000, 145));
		prestigeMap.put(30, new PrestigeInfo(55.0, 1_250_000, 200)); //shard
		prestigeMap.put(31, new PrestigeInfo(60.0, 1_300_000, 210));
		prestigeMap.put(32, new PrestigeInfo(65.0, 1_400_000, 220));
		prestigeMap.put(33, new PrestigeInfo(70.0, 1_500_000, 230)); //moon
		prestigeMap.put(34, new PrestigeInfo(75.0, 1_700_000, 240));
		prestigeMap.put(35, new PrestigeInfo(76.0, 2_000_000, 250)); //withercraft
		prestigeMap.put(36, new PrestigeInfo(77.0, 2_300_000, 260));
		prestigeMap.put(37, new PrestigeInfo(78.0, 2_600_000, 270));
		prestigeMap.put(38, new PrestigeInfo(79.0, 3_000_000, 280));
		prestigeMap.put(39, new PrestigeInfo(80.0, 3_400_000, 290));
		prestigeMap.put(40, new PrestigeInfo(82.0, 4_000_000, 300)); //celebrity
		prestigeMap.put(41, new PrestigeInfo(85.0, 4_200_000, 310));
		prestigeMap.put(42, new PrestigeInfo(90.0, 4_400_000, 320));
		prestigeMap.put(43, new PrestigeInfo(95.0, 4_600_000, 330));
		prestigeMap.put(44, new PrestigeInfo(100.0, 4_800_000, 340));
		prestigeMap.put(45, new PrestigeInfo(105.0, 5_000_000, 350));
		prestigeMap.put(46, new PrestigeInfo(110.0, 5_500_000, 375));
		prestigeMap.put(47, new PrestigeInfo(115.0, 6_000_000, 400));
		prestigeMap.put(48, new PrestigeInfo(120.0, 6_500_000, 500));
		prestigeMap.put(49, new PrestigeInfo(150.0, 10_000_000, 1000));
		prestigeMap.put(50, new PrestigeInfo(1000.0, 0, 0));
	}
}
