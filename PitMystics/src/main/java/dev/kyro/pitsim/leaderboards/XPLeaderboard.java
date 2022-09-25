package dev.kyro.pitsim.leaderboards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.Leaderboard;
import dev.kyro.pitsim.controllers.objects.LeaderboardPosition;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class XPLeaderboard extends Leaderboard {
	@Override
	public ItemStack getDisplayStack(UUID uuid) {
		ItemStack itemStack = new AItemStackBuilder(Material.WHEAT)
				.setName("&b" +
						"Total XP")
				.setLore(new ALoreBuilder(
						"&7Players who have &bearned &7the", "&7most &bXP", ""
				).addLore(getTopPlayers(uuid)).addLore(
						"", "&eClick to pick!"
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public String getDisplayValue(LeaderboardPosition position) {
		return "&b" + Misc.formatLarge(position.longValue);
	}

	@Override
	public void setPosition(LeaderboardPosition position, FileConfiguration playerData) {
		position.longValue = PrestigeValues.getTotalXP(playerData.getInt("prestige"), playerData.getInt("level"), playerData.getLong("xp"));
	}

	@Override
	public boolean isMoreThanOrEqual(LeaderboardPosition position, LeaderboardPosition otherPosition) {
		return position.longValue >= otherPosition.longValue;
	}
}
