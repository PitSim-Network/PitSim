package dev.kyro.pitsim.battlepass;

import java.util.*;

public class PitSimPass {
	public Date startDate;
	public int day = 0;

	public Map<Integer, PassReward> freePassRewards = new HashMap<>();
	public Map<Integer, PassReward> premiumPassRewards = new HashMap<>();

	public List<PassQuest> dailyQuests = new ArrayList<>();
	public List<PassQuest> weeklyQuests = new ArrayList<>();

	public void runDailyTasks() {
		if(day % 7 == 0) {
			List<PassQuest> possibleWeeklyQuests = PassManager.getWeeklyQuests();
			possibleWeeklyQuests.removeAll(weeklyQuests);
			Collections.shuffle(possibleWeeklyQuests);
			for(int i = 0; i < 6; i++) {
				if(weeklyQuests.isEmpty()) break;
				weeklyQuests.add(possibleWeeklyQuests.remove(0));
			}
		}

		dailyQuests.clear();
		dailyQuests.addAll(PassManager.getDailyQuests());
	}
}
