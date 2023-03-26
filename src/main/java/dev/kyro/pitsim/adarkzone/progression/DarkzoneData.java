package dev.kyro.pitsim.adarkzone.progression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DarkzoneData {
	public List<String> mainProgressionUnlocks = new ArrayList<>();
	public Map<String, SkillBranchData> skillBranchUnlocks = new HashMap<>();
	public int preDarkzoneUpdatePrestige = -1;
	public FastTravelData fastTravelData = new FastTravelData();
	public double altarXP = 0;
	public int demonicVouchers = 0;

	public static class FastTravelData {
		public List<Integer> unlockedLocations = new ArrayList<>();
	}

	public static class SkillBranchData {
		public List<String> majorUnlocks = new ArrayList<>();
		public Map<String, Integer> pathUnlocks = new HashMap<>();
	}
}
