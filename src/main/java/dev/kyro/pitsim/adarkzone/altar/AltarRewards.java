package dev.kyro.pitsim.adarkzone.altar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class AltarRewards {

	public static final int BASE_CHANCE = 50;
	public static final int XP_PER_ORB = 100;

	public static void rewardPlayer(Player player, List<AltarPedestal> pedestals) {
		int xpIncrease = AltarPedestal.getRewardChance(player, AltarPedestal.ALTAR_REWARD.ALTAR_XP);
		int renownIncrease = AltarPedestal.getRewardChance(player, AltarPedestal.ALTAR_REWARD.RENOWN);
		int voucherIncrease = AltarPedestal.getRewardChance(player, AltarPedestal.ALTAR_REWARD.VOUCHERS);

		boolean turmoil = AltarPedestal.getPedestal(4).isActivated(player);
		boolean positiveTurmoil = new Random().nextBoolean();

		int xpOrbs = 0;
		int renown = 0;
		int vouchers = 0;

		Bukkit.broadcastMessage("XP: " + (xpIncrease + BASE_CHANCE));
		Bukkit.broadcastMessage(100 / (getSecondaryChance(xpIncrease + BASE_CHANCE, turmoil && positiveTurmoil) / (xpIncrease + BASE_CHANCE)) + "");
		Bukkit.broadcastMessage("Renown: " + (renownIncrease + BASE_CHANCE));
		Bukkit.broadcastMessage(100 / (getSecondaryChance(renownIncrease + BASE_CHANCE, turmoil && positiveTurmoil) / (renownIncrease + BASE_CHANCE)) + "");
		Bukkit.broadcastMessage("Vouchers: " + (voucherIncrease + BASE_CHANCE));
		Bukkit.broadcastMessage(100 / (getSecondaryChance(voucherIncrease + BASE_CHANCE, turmoil && positiveTurmoil) / (voucherIncrease + BASE_CHANCE)) + "");

		Random decisionRandom = new Random();
		if(decisionRandom.nextInt(100) <= xpIncrease + BASE_CHANCE) xpOrbs = 1;
		if(decisionRandom.nextInt(100) <= renown + BASE_CHANCE) renown = 1;
		if(decisionRandom.nextInt(100) <= voucherIncrease + BASE_CHANCE) vouchers = 1;

		boolean xpCheck = false;
		while(decisionRandom.nextInt((int) getSecondaryChance(xpIncrease + BASE_CHANCE, turmoil && positiveTurmoil))
				< xpIncrease + BASE_CHANCE) {
			if(turmoil && !positiveTurmoil && !xpCheck) {
				xpCheck = true;
				continue;
			}

			xpCheck = false;
			xpOrbs++;
		}

		boolean renownCheck = false;
		while(decisionRandom.nextInt((int) getSecondaryChance(renownIncrease + BASE_CHANCE, turmoil && positiveTurmoil))
				< renownIncrease + BASE_CHANCE) {
			if(turmoil && !positiveTurmoil && !renownCheck) {
				renownCheck = true;
				continue;
			}

			renownCheck = false;
			renown++;
		}

		boolean voucherCheck = false;
		while(decisionRandom.nextInt((int) getSecondaryChance(voucherIncrease + BASE_CHANCE, turmoil && positiveTurmoil))
				< voucherIncrease + BASE_CHANCE) {
			if(turmoil && !positiveTurmoil && !voucherCheck) {
				voucherCheck = true;
				continue;
			}

			voucherCheck = false;
			vouchers++;
		}

		AltarXPReward reward = new AltarXPReward(player, xpOrbs * XP_PER_ORB);
		reward.spawn(AltarManager.CONFIRM_LOCATION.clone().add(0, 2, 0));

		AltarRenownReward renownReward = new AltarRenownReward(player, renown);
		renownReward.spawn(AltarManager.CONFIRM_LOCATION.clone().add(0, 2.5, 0));

		AltarVoucherReward heresyReward = new AltarVoucherReward(player, vouchers);
		heresyReward.spawn(AltarManager.CONFIRM_LOCATION.clone().add(0, 2.5, 0));
	}

	public static double getSecondaryChance(int chance, boolean turmoil) {
		double multiplier;

		if(chance <= BASE_CHANCE) multiplier = 2.5;
		else if( chance <= AltarPedestal.DEFAULT_ADDED_CHANCE + BASE_CHANCE) multiplier = 2;
		else if(chance <= (AltarPedestal.DEFAULT_ADDED_CHANCE * AltarPedestal.WEALTH_MULTIPLIER) + BASE_CHANCE) multiplier = 1.5;
		else multiplier = 3;

		if(turmoil) multiplier /= 1.3;
		return Math.max(chance + 1, chance * multiplier);
	}
}
