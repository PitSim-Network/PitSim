package dev.kyro.pitsim.leaderboards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.controllers.objects.Leaderboard;
import dev.kyro.pitsim.controllers.objects.LeaderboardData;
import dev.kyro.pitsim.controllers.objects.LeaderboardPosition;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class JewelsCompletedLeaderboard extends Leaderboard {
	public JewelsCompletedLeaderboard() {
		super("jewels-completed", "&3Jewels Completed");
	}

	@Override
	public ItemStack getDisplayStack(UUID uuid) {
		ItemStack itemStack = new AItemStackBuilder(MysticFactory.getFreshItem(MysticType.PANTS, PantColor.JEWEL))
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
	public String getDisplayValue(PitPlayer pitPlayer) {
		return "&c" + Misc.formatLarge(pitPlayer.stats.jewelsCompleted) + " jewel" + (pitPlayer.stats.jewelsCompleted == 1 ? "" : "s");
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
