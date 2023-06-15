package net.pitsim.spigot.battlepass.rewards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.battlepass.PassReward;
import net.pitsim.spigot.controllers.LevelManager;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.misc.Formatter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PassGoldReward extends PassReward {
	public int gold;

	public PassGoldReward(int gold) {
		this.gold = gold;
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {
		LevelManager.addGold(pitPlayer.player, (int) (gold * getMultiplier(pitPlayer)));
		return true;
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		if(pitPlayer.prestige == 0) return 1;
		return pitPlayer.prestige;
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = new AItemStackBuilder(Material.GOLD_INGOT)
				.setName("&6&lGold Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: &6" + Formatter.formatLarge((int) (gold * getMultiplier(pitPlayer))) + "g"
				)).getItemStack();
		return itemStack;
	}
}
