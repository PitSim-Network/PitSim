package dev.kyro.pitsim.battlepass;

import java.util.*;

public class PitSimPass {
	public Date startDate;
	public int tiers;

	public Map<Integer, PassReward> freePassRewards = new HashMap<>();
	public Map<Integer, PassReward> premiumPassRewards = new HashMap<>();

	public List<PassQuest> weeklyQuests = new ArrayList<>();

	public PitSimPass(Date startDate) {
		this.startDate = startDate;
	}

	public void build() {
		for(Map.Entry<Integer, PassReward> entry : freePassRewards.entrySet()) {
			if(entry.getKey() > tiers) tiers = entry.getKey();
		}
		for(Map.Entry<Integer, PassReward> entry : premiumPassRewards.entrySet()) {
			if(entry.getKey() > tiers) tiers = entry.getKey();
		}
	}

	public PitSimPass registerReward(PassReward passReward, RewardType rewardType, int tier) {
		if(rewardType == RewardType.FREE) {
			freePassRewards.put(tier, passReward);
		} else if(rewardType == RewardType.PREMIUM) {
			premiumPassRewards.put(tier, passReward);
		}

		return this;
	}

	public void runDailyTasks() {
		List<PassQuest> possibleWeeklyQuests = PassManager.getWeeklyQuests();
		possibleWeeklyQuests.removeAll(weeklyQuests);
		Collections.shuffle(possibleWeeklyQuests);
		for(int i = 0; i < 6; i++) {
			if(weeklyQuests.isEmpty()) break;
			weeklyQuests.add(possibleWeeklyQuests.remove(0));
		}
	}

	public enum RewardType {
		FREE,
		PREMIUM
	}
}
