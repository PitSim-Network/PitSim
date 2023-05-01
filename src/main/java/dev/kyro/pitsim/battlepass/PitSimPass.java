package dev.kyro.pitsim.battlepass;

import dev.kyro.pitsim.battlepass.rewards.*;
import dev.kyro.pitsim.controllers.FirestoreManager;
import dev.kyro.pitsim.misc.Misc;

import java.util.*;

public class PitSimPass {
	public static LinkedHashMap<Class<? extends PassReward>, Double> weightedRandomRewardMap = new LinkedHashMap<>();
	public Date startDate;
	public int tiers;
	public Random random;

	public Map<Integer, PassReward> freePassRewards = new HashMap<>();
	public Map<Integer, PassReward> premiumPassRewards = new HashMap<>();

	public Map<PassQuest, PassQuest.QuestLevel> weeklyQuests = new HashMap<>();

	static {
		weightedRandomRewardMap.put(PassXPReward.class, 12.0);
		weightedRandomRewardMap.put(PassGoldReward.class, 12.0);

		weightedRandomRewardMap.put(PassRenownReward.class, 10.0);
		weightedRandomRewardMap.put(PassFeatherReward.class, 10.0);
		weightedRandomRewardMap.put(PassShardReward.class, 5.0);
		weightedRandomRewardMap.put(PassVileReward.class, 15.0);

		weightedRandomRewardMap.put(PassSwordReward.class, 10.0);
		weightedRandomRewardMap.put(PassBowReward.class, 10.0);
		weightedRandomRewardMap.put(PassPantsReward.class, 10.0);
		weightedRandomRewardMap.put(PassScytheReward.class, 10.0);
		weightedRandomRewardMap.put(PassChestplateReward.class, 10.0);

		weightedRandomRewardMap.put(PassDarkzoneDropReward.class, 15.0);

		weightedRandomRewardMap.put(PassKeyReward.class, 5.0);
		weightedRandomRewardMap.put(PassBoosterReward.class, 5.0);
	}

	public PitSimPass(Date startDate) {
		this.startDate = startDate;
		this.random = new Random(Misc.hashLong(startDate.getTime()));
	}

	public void build() {
		for(Map.Entry<Integer, PassReward> entry : freePassRewards.entrySet())
			if(entry.getKey() > tiers) tiers = entry.getKey();
		for(Map.Entry<Integer, PassReward> entry : premiumPassRewards.entrySet())
			if(entry.getKey() > tiers) tiers = entry.getKey();
	}

	public PitSimPass registerStandardRewards() {
		for(int i = 0; i < 36; i++) {
			int tier = i + 1;
			if(tier % 18 == 0) continue;
			if((tier - 1) % 9 % 2 == 0) registerRandomReward(PitSimPass.RewardType.FREE, tier);
			if(tier % 9 == 0) continue;
			registerRandomReward(PitSimPass.RewardType.PREMIUM, tier);
		}
		return this;
	}

	public PitSimPass registerRandomReward(RewardType rewardType, int tier) {
		LinkedHashMap<Class<? extends PassReward>, Double> weightedRandomRewardMap = new LinkedHashMap<>(PitSimPass.weightedRandomRewardMap);
		List<PassReward> currentRewards = new ArrayList<>();
		currentRewards.addAll(freePassRewards.values());
		currentRewards.addAll(premiumPassRewards.values());
		for(PassReward currentReward : currentRewards) {
			if(currentReward instanceof PassKeyReward) weightedRandomRewardMap.remove(PassKeyReward.class);
			else if(currentReward instanceof PassBoosterReward) weightedRandomRewardMap.remove(PassBoosterReward.class);
		}
		if(rewardType == RewardType.FREE) {
			weightedRandomRewardMap.remove(PassKeyReward.class);
			weightedRandomRewardMap.remove(PassBoosterReward.class);
		}

		for(int i = tier - 2; i <= tier + 2; i++) {
			if(freePassRewards.containsKey(i)) weightedRandomRewardMap.remove(freePassRewards.get(i).getClass());
			if(premiumPassRewards.containsKey(i)) weightedRandomRewardMap.remove(premiumPassRewards.get(i).getClass());
		}

		Class<? extends PassReward> choice = Misc.weightedRandom(weightedRandomRewardMap, random);
		Random subRandom = new Random(Misc.hashLong(startDate.getTime() + tier + 1 + rewardType.ordinal() * 100));

		if(choice == PassXPReward.class) {
			registerReward(new PassXPReward(Misc.intBetween(2_500, 15_000, subRandom)), rewardType, tier);
		} else if(choice == PassGoldReward.class) {
			registerReward(new PassGoldReward(Misc.intBetween(10_000, 40_000, subRandom)), rewardType, tier);
		}

		else if(choice == PassRenownReward.class) {
			registerReward(new PassRenownReward(Misc.intBetween(5, 10, subRandom)), rewardType, tier);
		} else if(choice == PassFeatherReward.class) {
			registerReward(new PassFeatherReward(Misc.intBetween(1, 3, subRandom)), rewardType, tier);
		} else if(choice == PassShardReward.class) {
			registerReward(new PassShardReward(Misc.intBetween(1, 3, subRandom)), rewardType, tier);
		} else if(choice == PassVileReward.class) {
			registerReward(new PassVileReward(Misc.intBetween(5, 10, subRandom)), rewardType, tier);
		}

		else if(choice == PassSwordReward.class) {
			registerReward(new PassSwordReward(Misc.intBetween(1, 2, subRandom)), rewardType, tier);
		} else if(choice == PassBowReward.class) {
			registerReward(new PassBowReward(Misc.intBetween(1, 2, subRandom)), rewardType, tier);
		} else if(choice == PassPantsReward.class) {
			registerReward(new PassPantsReward(Misc.intBetween(1, 2, subRandom)), rewardType, tier);
		} else if(choice == PassScytheReward.class) {
			registerReward(new PassScytheReward(Misc.intBetween(2, 4, subRandom)), rewardType, tier);
		} else if(choice == PassChestplateReward.class) {
			registerReward(new PassChestplateReward(Misc.intBetween(2, 4, subRandom)), rewardType, tier);
		}

		else if(choice == PassDarkzoneDropReward.class) {
			registerReward(new PassDarkzoneDropReward(subRandom.nextInt(3) + 8,
					Misc.intBetween(1, 5, subRandom)), rewardType, tier);
		}

		else if(choice == PassKeyReward.class) {
			registerReward(new PassKeyReward(PassKeyReward.KeyType.values()[subRandom.nextInt(PassKeyReward.KeyType.values().length)], 1), rewardType, tier);
		} else if(choice == PassBoosterReward.class) {
			registerReward(new PassBoosterReward(subRandom.nextDouble() < 0.5 ? "xp" : "gold"), rewardType, tier);
		}
		return this;
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

	public static class RandomRewardInfo {
		public PitSimPass.RewardType rewardType;
		public int tier;

		public RandomRewardInfo(PitSimPass.RewardType rewardType, int tier) {
			this.rewardType = rewardType;
			this.tier = tier;
		}
	}
}
