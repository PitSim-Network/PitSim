package dev.kyro.pitsim.leaderboards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.objects.Leaderboard;
import dev.kyro.pitsim.controllers.objects.LeaderboardPosition;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class JewelsCompletedLeaderboard extends Leaderboard {
	@Override
	public ItemStack getDisplayStack(UUID uuid) {
		ItemStack itemStack = new AItemStackBuilder(FreshCommand.getFreshItem(MysticType.PANTS, PantColor.JEWEL))
				.setName("&3Jewels Completed")
				.setLore(new ALoreBuilder(
						"&7Players who have &3completed &7the", "&7most &3jewels", ""
				).addLore(getTopPlayers(uuid)).addLore(
						"", "&eClick to pick!"
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public String getDisplayValue(LeaderboardPosition position) {
		return "&c" + Misc.formatLarge(position.intValue) + " jewel" + (position.intValue == 1 ? "" : "s");
	}

	@Override
	public void setPosition(LeaderboardPosition position, FileConfiguration playerData) {
		position.intValue = playerData.getInt("stats.misc.jewels-completed");
	}

	@Override
	public boolean isMoreThanOrEqual(LeaderboardPosition position, LeaderboardPosition otherPosition) {
		return position.intValue >= otherPosition.intValue;
	}
}
