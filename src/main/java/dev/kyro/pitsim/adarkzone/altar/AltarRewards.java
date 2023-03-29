package dev.kyro.pitsim.adarkzone.altar;

import dev.kyro.pitsim.adarkzone.DarkzoneBalancing;
import dev.kyro.pitsim.adarkzone.altar.pedestals.TurmoilPedestal;
import dev.kyro.pitsim.adarkzone.altar.pedestals.WealthPedestal;
import dev.kyro.pitsim.adarkzone.progression.ProgressionManager;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
import dev.kyro.pitsim.adarkzone.progression.skillbranches.AltarBranch;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.entity.Player;

public class AltarRewards {

	//Primary 3 pedestals only determine chance.
	//Base amount is based on LOW/MEDIUM/HIGH system (All base numbers should be doubles)
	//Multipliers are Wealth, Turmoil, and Souls/Base roll cost

	public static final int MAX_TURMOIL_TICKS = 60;

	public static void rewardPlayer(Player player, double turmoilMultiplier) {

		for(AltarPedestal.ALTAR_REWARD reward : AltarPedestal.ALTAR_REWARD.values()) {
			double chance = Math.random() * 100;
			if(reward.pedestal.isActivated(player)) chance += AltarPedestal.getRewardChance(player, reward);
			AltarPedestal.RewardSize size = AltarPedestal.RewardSize.getFromChance(chance);
			if(reward == AltarPedestal.ALTAR_REWARD.ALTAR_XP && size == AltarPedestal.RewardSize.NONE) size = AltarPedestal.RewardSize.LOW;
			double rewardCount = reward.getRewardCount(size);

			rewardCount *= getSoulMultiplier(player);

			if(AltarPedestal.getPedestal(WealthPedestal.class).isActivated(player)) rewardCount *= DarkzoneBalancing.PEDESTAL_WEALTH_MULTIPLIER;

			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

			if(reward == AltarPedestal.ALTAR_REWARD.ALTAR_XP) {
				double multiplier = 1;
				for(Double value : ProgressionManager.getUnlockedEffectAsList(pitPlayer, AltarBranch.INSTANCE,
						SkillBranch.PathPosition.FIRST_PATH, "altar-xp")) {
					multiplier *= value;
				}
				rewardCount *= multiplier;
			} else if(reward == AltarPedestal.ALTAR_REWARD.RENOWN) {
				double increase = ProgressionManager.getUnlockedEffectAsValue(pitPlayer, AltarBranch.INSTANCE,
						SkillBranch.PathPosition.SECOND_PATH, "altar-renown");
				rewardCount *= 1 + increase / 100.0;
			} else if(reward == AltarPedestal.ALTAR_REWARD.VOUCHERS) {
				double increase = ProgressionManager.getUnlockedEffectAsValue(pitPlayer, AltarBranch.INSTANCE,
						SkillBranch.PathPosition.SECOND_PATH, "altar-vouchers");
				rewardCount *= 1 + increase / 100.0;
			}

			if(ProgressionManager.isUnlocked(pitPlayer, AltarBranch.INSTANCE, SkillBranch.MajorUnlockPosition.LAST))
				rewardCount *= 1 + (AltarBranch.getTurboIncrease() / 100.0);

			rewardCount *= turmoilMultiplier;

			reward.rewardPlayer(player, getIntReward(rewardCount));
			System.out.println(reward.name() + " " + chance + " " + size.name() + " " + rewardCount);
		}

		//Weighted map of LOW/MEDIUM/HIGH
		//Chance multiplier changes weight of certain catag

		//XP:
		//Calculate chance for LOW/MED/HIGH using base chance and XP chance increase
		//if != LOW, add small randomization to amount
		//Add static XP multiplier for XP pedestal being on
		//Add static wealth multiplier if wealth is on
		//Apply Turmoil

		//Renown and Vouchers: same as XP

		//Turmoil:
		//good/bad calc 50/50 (determines break chance of while loop)
		//starting multiplier < 1, increases by 0.1 each loop
		//when loop breaks, multiply total items by multiplier

		//Remove itemstack chance indicators
		//Copy soul explosion code for xp orb count


	}

	public static int getTurmoilTicks(Player player) {
		TurmoilPedestal pedestal = AltarPedestal.getPedestal(TurmoilPedestal.class);
		if(!pedestal.isActivated(player)) return 1;

		double breakChance = Math.random() < 0.2 ? 0.025 : 0.14;

		int ticks = 1;
		while(Math.random() > breakChance && ticks < MAX_TURMOIL_TICKS) ticks++;
		return ticks;
	}

	public static double getSoulMultiplier(Player player) {
		return AltarPedestal.getTotalCost(player) / (double) AltarPedestal.BASE_COST;
	}

	public static int getIntReward(double doubleReward) {
		int intReward = (int) Math.floor(doubleReward);
		doubleReward -= intReward;
		if(Math.random() < doubleReward) intReward++;
		return intReward;
	}
}
