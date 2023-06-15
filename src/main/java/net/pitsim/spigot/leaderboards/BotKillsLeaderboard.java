package net.pitsim.spigot.leaderboards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.controllers.objects.Leaderboard;
import net.pitsim.spigot.controllers.objects.LeaderboardData;
import net.pitsim.spigot.controllers.objects.LeaderboardPosition;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.misc.Formatter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BotKillsLeaderboard extends Leaderboard {
	public BotKillsLeaderboard() {
		super("bot-kills", "&cBot Kills");
	}

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
		return "&c" + Formatter.formatLarge(position.intValue) + " kill" + (position.intValue == 1 ? "" : "s");
	}

	@Override
	public String getDisplayValue(PitPlayer pitPlayer) {
		return "&c" + Formatter.formatLarge(pitPlayer.stats.botKills) + " kill" + (pitPlayer.stats.botKills == 1 ? "" : "s");
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
