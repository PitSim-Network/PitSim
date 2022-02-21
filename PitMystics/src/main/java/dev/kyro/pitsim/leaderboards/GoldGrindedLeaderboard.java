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

public class GoldGrindedLeaderboard extends Leaderboard {
	@Override
	public ItemStack getDisplayStack(UUID uuid) {
		ItemStack itemStack = new AItemStackBuilder(Material.GOLD_INGOT)
				.setName("&6Gold Grinded")
				.setLore(new ALoreBuilder(
						"&7Players who have &6grinded", "&7the most &6gold", ""
				).addLore(getTopPlayers(uuid)).addLore(
						"", "&eClick to pick!"
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public String getDisplayValue(LeaderboardPosition position) {
		return "&6" + Misc.formatLarge(position.doubleValue) + "g";
	}

	@Override
	public void setPosition(LeaderboardPosition position, FileConfiguration playerData) {
		position.doubleValue = playerData.getDouble("stats.progression.total-gold");
	}

	@Override
	public boolean isMoreThanOrEqual(LeaderboardPosition position, LeaderboardPosition otherPosition) {
		return position.doubleValue >= otherPosition.doubleValue;
	}
}
