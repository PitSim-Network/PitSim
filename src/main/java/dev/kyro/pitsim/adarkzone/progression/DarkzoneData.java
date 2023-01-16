package dev.kyro.pitsim.adarkzone.progression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DarkzoneData {
	public List<String> mainProgressionUnlocks = new ArrayList<>();
	public Map<String, SkillBranchData> skillBranchUnlocks = new HashMap<>();

	public static class SkillBranchData {
		public List<String> majorUnlocks = new ArrayList<>();
		public Map<String, Integer> pathUnlocks = new HashMap<>();
	}
}
