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

public class BossesKilledLeaderboard extends Leaderboard {
	@Override
	public ItemStack getDisplayStack(UUID uuid) {
		ItemStack itemStack = new AItemStackBuilder(Material.MOB_SPAWNER)
				.setName("&5Bosses Killed")
				.setLore(new ALoreBuilder(
						"&7Players who have &ckilled &7the most", "&5Bosses &7in the &5Darkzone", ""
				).addLore(getTopPlayers(uuid)).addLore(
						"", "&eClick to pick!"
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public String getDisplayValue(LeaderboardPosition position) {
		return "&5" + Misc.formatLarge(position.intValue) + " boss" + (position.intValue == 1 ? "" : "es");
	}

	@Override
	public void setPosition(LeaderboardPosition position, FileConfiguration playerData) {
		position.intValue = playerData.getInt("stats.darkzone.bosses-killed");
	}

	@Override
	public boolean isMoreThanOrEqual(LeaderboardPosition position, LeaderboardPosition otherPosition) {
		return position.intValue >= otherPosition.intValue;
	}
}
