package net.pitsim.pitsim.leaderboards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.controllers.objects.Leaderboard;
import net.pitsim.pitsim.controllers.objects.LeaderboardData;
import net.pitsim.pitsim.controllers.objects.LeaderboardPosition;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.UUID;

public class PlaytimeLeaderboard extends Leaderboard {
	public PlaytimeLeaderboard() {
		super("minutes-played", "&eTotal Playtime");
	}

	@Override
	public ItemStack getDisplayStack(UUID uuid) {
		ItemStack itemStack = new AItemStackBuilder(Material.WATCH)
				.setName("&eTotal Playtime")
				.setLore(new ALoreBuilder(
						"&7Players who have &eplayed &7the", "&7most", ""
				).addLore(getTopPlayers(uuid)).addLore(
						"", "&eClick to pick!"
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public String getDisplayValue(LeaderboardPosition position) {
		return "&e" + new DecimalFormat("0.#").format(position.intValue / 60.0) + " hour" + (new DecimalFormat("0.#").format(position.intValue / 60.0).equals("1") ? "" : "s");
	}

	@Override
	public String getDisplayValue(PitPlayer pitPlayer) {
		return "&e" + new DecimalFormat("0.#").format(pitPlayer.stats.minutesPlayed / 60.0) + " hour" + (new DecimalFormat("0.#").format(pitPlayer.stats.minutesPlayed / 60.0).equals("1") ? "" : "s");
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
