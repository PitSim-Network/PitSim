package dev.kyro.pitsim.adarkzone.altar;

import dev.kyro.pitsim.adarkzone.altar.pedestals.TurmoilPedestal;
import org.bukkit.entity.Player;

import java.util.Random;

public class AltarRewards {

	public static void rewardPlayer(Player player) {
		boolean turmoil = AltarPedestal.getPedestal(TurmoilPedestal.class).isActivated(player);
		boolean positiveTurmoil = new Random().nextBoolean();

		for(AltarPedestal.ALTAR_REWARD reward : AltarPedestal.ALTAR_REWARD.values()) {
			Random random = new Random();

			AltarPedestal.REWARD_SIZE size = AltarPedestal.REWARD_SIZE.SMALL;

			for(AltarPedestal.REWARD_SIZE value : AltarPedestal.REWARD_SIZE.values()) {
				if(random.nextInt(100) <= value.base - (reward.pedestal.isActivated(player) ? reward.increase : 0)) {
					size = value;
					break;
				}
			}

			int rewardCount = reward.getRewardCount(size, player);

			if(turmoil) {
				double breakChance = positiveTurmoil ? 0.03 : 0.1;

				double multiplier = 1;
				while(Math.random() > breakChance) multiplier += 0.1;
				rewardCount *= multiplier;
			}

			reward.rewardPlayer(player, rewardCount);
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
}
