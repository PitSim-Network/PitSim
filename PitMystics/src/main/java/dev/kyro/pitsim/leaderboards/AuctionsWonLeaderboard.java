package dev.kyro.pitsim.leaderboards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.*;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AuctionsWonLeaderboard extends Leaderboard {
	public AuctionsWonLeaderboard() {
		super("auctions-won");
	}

	@Override
	public ItemStack getDisplayStack(UUID uuid) {
		ItemStack itemStack = new AItemStackBuilder(Material.BANNER, 1 , (byte) 5)
				.setName("&dAuctions Won")
				.setLore(new ALoreBuilder(
						"&7Players who have &dwon &7the", "&7most &dDark Auctions", ""
				).addLore(getTopPlayers(uuid)).addLore(
						"", "&eClick to pick!"
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public String getDisplayValue(LeaderboardPosition position) {
		return "&d" + Misc.formatLarge(position.intValue) + " auction" + (position.intValue == 1 ? "" : "s");
	}

	@Override
	public String getDisplayValue(PitPlayer pitPlayer) {
		return "&d" + Misc.formatLarge(pitPlayer.stats.auctionsWon) + " auction" + (pitPlayer.stats.auctionsWon == 1 ? "" : "s");
	}

	@Override
	public void setPosition(LeaderboardPosition position) {
		LeaderboardData data = LeaderboardData.getLeaderboardData(this);

		position.intValue = (int) data.getValue(position.uuid).primaryValue;
	}

	@Override
	public boolean isMoreThanOrEqual(LeaderboardPosition position, LeaderboardPosition otherPosition) {
		return position.intValue >= otherPosition.intValue;
	}
}
