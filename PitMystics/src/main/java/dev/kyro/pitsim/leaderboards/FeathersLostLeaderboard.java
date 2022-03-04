package dev.kyro.pitsim.leaderboards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Leaderboard;
import dev.kyro.pitsim.controllers.objects.LeaderboardPosition;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class FeathersLostLeaderboard extends Leaderboard {
	@Override
	public ItemStack getDisplayStack(UUID uuid) {
		ItemStack itemStack = new AItemStackBuilder(Material.FEATHER)
				.setName("&fLost Feathers")
				.setLore(new ALoreBuilder(
						"&7Players who have &flost &7the", "&7most &ffeathers", ""
				).addLore(getTopPlayers(uuid)).addLore(
						"", "&eClick to pick!"
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public String getDisplayValue(LeaderboardPosition position) {
		return "&f" + Misc.formatLarge(position.intValue) + " feather" + (position.intValue == 1 ? "" : "s");
	}

	@Override
	public void setPosition(LeaderboardPosition position, FileConfiguration playerData) {
		position.intValue = playerData.getInt("stats.misc.feathers-lost");
	}

	@Override
	public boolean isMoreThanOrEqual(LeaderboardPosition position, LeaderboardPosition otherPosition) {
		return position.intValue >= otherPosition.intValue;
	}
}
