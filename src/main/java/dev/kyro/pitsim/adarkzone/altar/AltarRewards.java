package dev.kyro.pitsim.adarkzone.altar;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.DarkzoneBalancing;
import dev.kyro.pitsim.adarkzone.altar.pedestals.TurmoilPedestal;
import dev.kyro.pitsim.adarkzone.altar.pedestals.WealthPedestal;
import dev.kyro.pitsim.adarkzone.progression.ProgressionManager;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
import dev.kyro.pitsim.adarkzone.progression.skillbranches.AltarBranch;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Formatter;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

public class AltarRewards {

	//Primary 3 pedestals only determine chance.
	//Base amount is based on LOW/MEDIUM/HIGH system (All base numbers should be doubles)
	//Multipliers are Wealth, Turmoil, and Souls/Base roll cost

	public static final int MAX_TURMOIL_TICKS = 60;

	public static Map<AltarPedestal.AltarReward, Double> avgRewardMap = new LinkedHashMap<>();
	public static final int ROLLS = PitSim.isDev() ? 10_000 : 1;

	public static void rewardPlayer(Player player, double turmoilMultiplier) {
		avgRewardMap.clear();
		for(int i = 0; i < ROLLS; i++) {
			for(AltarPedestal.AltarReward reward : AltarPedestal.AltarReward.values()) {
				double chance = Math.random() * 100;
				if(reward.pedestal.isActivated(player)) chance += AltarPedestal.getRewardChance(player, reward);
				AltarPedestal.RewardSize size = AltarPedestal.RewardSize.getFromChance(chance);
				if(reward == AltarPedestal.AltarReward.ALTAR_XP && size == AltarPedestal.RewardSize.NONE) size = AltarPedestal.RewardSize.LOW;
				double rewardCount = reward.getRewardCount(size);

				rewardCount *= getSoulMultiplier(player);

				if(AltarPedestal.getPedestal(WealthPedestal.class).isActivated(player)) rewardCount *= DarkzoneBalancing.PEDESTAL_WEALTH_MULTIPLIER;

				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

				if(reward == AltarPedestal.AltarReward.ALTAR_XP) {
					double increase = ProgressionManager.getUnlockedEffectAsValue(pitPlayer, AltarBranch.INSTANCE,
							SkillBranch.PathPosition.FIRST_PATH, "altar-xp");
					rewardCount *= 1 + increase / 100.0;
				} else if(reward == AltarPedestal.AltarReward.RENOWN) {
					double increase = ProgressionManager.getUnlockedEffectAsValue(pitPlayer, AltarBranch.INSTANCE,
							SkillBranch.PathPosition.SECOND_PATH, "altar-renown");
					rewardCount *= 1 + increase / 100.0;
				} else if(reward == AltarPedestal.AltarReward.VOUCHERS) {
					double increase = ProgressionManager.getUnlockedEffectAsValue(pitPlayer, AltarBranch.INSTANCE,
							SkillBranch.PathPosition.SECOND_PATH, "altar-vouchers");
					rewardCount *= 1 + increase / 100.0;
				}

				if(ProgressionManager.isUnlocked(pitPlayer, AltarBranch.INSTANCE, SkillBranch.MajorUnlockPosition.LAST))
					rewardCount *= 1 + (AltarBranch.getTurboIncrease() / 100.0);

				rewardCount *= turmoilMultiplier;

				avgRewardMap.put(reward, avgRewardMap.getOrDefault(reward, 0.0) + rewardCount);
				if(i == 0) reward.rewardPlayer(player, getIntReward(rewardCount));
//			System.out.println(reward.name() + " " + chance + " " + size.name() + " " + rewardCount);
			}
		}

		Sounds.ALTAR_ROLL.play(player);

		if(PitSim.isDev()) {
			int totalCost = AltarPedestal.getTotalCost(player);
			AOutput.send(player, "&4&m-------------------&4<&c&lALTAR&4>&m-------------------");
			for(Map.Entry<AltarPedestal.AltarReward, Double> entry : avgRewardMap.entrySet()) {
				double value = entry.getValue() / ROLLS;
				AOutput.send(player, "&4" + entry.getKey().name() + ": &c" + Formatter.decimalCommaFormat.format(value) +
						" &4per 100: &c" + Formatter.decimalCommaFormat.format(value * 100 / totalCost));
			}
			AOutput.send(player, "&4&m-------------------&4<&c&lALTAR&4>&m-------------------");
		}
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
		return AltarPedestal.getTotalCost(player) / 100.0;
	}

	public static int getIntReward(double doubleReward) {
		int intReward = (int) Math.floor(doubleReward);
		doubleReward -= intReward;
		if(Math.random() < doubleReward) intReward++;
		return intReward;
	}
}
