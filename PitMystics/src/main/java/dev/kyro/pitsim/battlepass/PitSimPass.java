package dev.kyro.pitsim.battlepass;

import dev.kyro.pitsim.controllers.FirestoreManager;

import java.util.*;

public class PitSimPass {
	public Date startDate;
	public int tiers;

	public Map<Integer, PassReward> freePassRewards = new HashMap<>();
	public Map<Integer, PassReward> premiumPassRewards = new HashMap<>();

	public Map<PassQuest, PassQuest.QuestLevel> weeklyQuests = new HashMap<>();

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

//	Only call this if you are sure that the current pass is supposed to be current
	public void writeToConfig() {
		FirestoreManager.CONFIG.currentPassData.activeWeeklyQuests.clear();
		for(Map.Entry<PassQuest, PassQuest.QuestLevel> entry : weeklyQuests.entrySet())
			FirestoreManager.CONFIG.currentPassData.activeWeeklyQuests.put(entry.getKey().refName, entry.getValue().rewardIndex);
	}

	public enum RewardType {
		FREE,
		PREMIUM
	}
}
