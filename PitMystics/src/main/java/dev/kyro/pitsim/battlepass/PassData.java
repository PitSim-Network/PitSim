package dev.kyro.pitsim.battlepass;

import com.google.cloud.firestore.annotation.Exclude;

import java.util.*;

public class PassData {
	public Date currentPassDate = new Date(0);
	public int totalPoints;
	public boolean hasPremium = false;

//	Unique weekly quest data
	public List<String> uniquePlayersPunched = new ArrayList<>();

//	Daily quest data
	public Map<String, Double> questCompletion = new HashMap<>();

	@Exclude
	public Map<Integer, Boolean> claimedFreeRewards = new HashMap<>();
	private Map<String, Boolean> stringClaimedFreeRewards = new HashMap<>();
	public Map<String, Boolean> getStringClaimedFreeRewards() {
		claimedFreeRewards.forEach((tier, claimed) -> stringClaimedFreeRewards.put(tier.toString(), claimed));
		return stringClaimedFreeRewards;
	}
	public void setStringClaimedFreeRewards(Map<String, Boolean> stringClaimedFreeRewards) {
		this.stringClaimedFreeRewards = stringClaimedFreeRewards;
		stringClaimedFreeRewards.forEach((tier, claimed) -> claimedFreeRewards.put(Integer.parseInt(tier), claimed));
	}

	@Exclude
	public Map<Integer, Boolean> claimedPremiumRewards = new HashMap<>();
	private Map<String, Boolean> stringClaimedPremiumRewards = new HashMap<>();
	public Map<String, Boolean> getStringClaimedPremiumRewards() {
		claimedPremiumRewards.forEach((tier, claimed) -> stringClaimedPremiumRewards.put(tier.toString(), claimed));
		return stringClaimedPremiumRewards;
	}
	public void setStringClaimedPremiumRewards(Map<String, Boolean> stringClaimedPremiumRewards) {
		this.stringClaimedPremiumRewards = stringClaimedPremiumRewards;
		stringClaimedPremiumRewards.forEach((tier, claimed) -> claimedPremiumRewards.put(Integer.parseInt(tier), claimed));
	}

	public PassData() {
	}

	public PassData(Date currentPassDate) {
		this.currentPassDate = currentPassDate;
	}

	@Exclude
	public int getCompletedTiers() {
		return totalPoints / PassManager.POINTS_PER_TIER;
	}

	@Exclude
	public int getPointsForTier() {
		return totalPoints % PassManager.POINTS_PER_TIER;
	}
}
