package net.pitsim.pitsim.leaderboards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.controllers.objects.Leaderboard;
import net.pitsim.pitsim.controllers.objects.LeaderboardData;
import net.pitsim.pitsim.controllers.objects.LeaderboardPosition;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.misc.Formatter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BossesKilledLeaderboard extends Leaderboard {
	public BossesKilledLeaderboard() {
		super("bosses-killed", "&5Bosses Killed");
	}

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
		return "&5" + Formatter.formatLarge(position.intValue) + " boss" + (position.intValue == 1 ? "" : "es");
	}

	@Override
	public String getDisplayValue(PitPlayer pitPlayer) {
		return "&5" + Formatter.formatLarge(pitPlayer.stats.bossesKilled) + " boss" + (pitPlayer.stats.bossesKilled == 1 ? "" : "es");
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
