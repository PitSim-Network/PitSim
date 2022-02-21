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

public class BotKillsLeaderboard extends Leaderboard {
	@Override
	public ItemStack getDisplayStack(UUID uuid) {
		ItemStack itemStack = new AItemStackBuilder(Material.IRON_SWORD)
				.setName("&cBot Kills")
				.setLore(new ALoreBuilder(
						"&7Players who have &ckilled &7the", "&7most &cbots", ""
				).addLore(getTopPlayers(uuid)).addLore(
						"", "&eClick to pick!"
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public String getDisplayValue(LeaderboardPosition position) {
		return "&c" + Misc.formatLarge(position.intValue) + " kills";
	}

	@Override
	public void setPosition(LeaderboardPosition position, FileConfiguration playerData) {
		position.intValue = playerData.getInt("stats.combat.bot-kills");
	}

	@Override
	public boolean isMoreThanOrEqual(LeaderboardPosition position, LeaderboardPosition otherPosition) {
		return position.intValue >= otherPosition.intValue;
	}
}
