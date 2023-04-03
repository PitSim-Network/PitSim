package dev.kyro.pitsim.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.battlepass.PassReward;
import dev.kyro.pitsim.controllers.BoosterManager;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PassBoosterReward extends PassReward {
	public Booster boosterType;

	public PassBoosterReward(String boosterName) {
		this.boosterType = BoosterManager.getBooster(boosterName);
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {

		Booster.setBooster(pitPlayer.player, boosterType, pitPlayer.boosters.get(boosterType.refName) + 1);
		AOutput.send(pitPlayer.player, "&6&lBOOSTER! &7Received &f1 " + boosterType.color + boosterType.name + "&7.");

		return true;
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = new AItemStackBuilder(Material.NETHER_STAR)
				.setName("&a&lBooster Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: " + boosterType.color + boosterType.name
				)).getItemStack();
		return itemStack;
	}
}
