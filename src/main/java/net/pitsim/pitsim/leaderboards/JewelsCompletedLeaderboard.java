package net.pitsim.pitsim.leaderboards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.aitems.MysticFactory;
import net.pitsim.pitsim.controllers.objects.Leaderboard;
import net.pitsim.pitsim.controllers.objects.LeaderboardData;
import net.pitsim.pitsim.controllers.objects.LeaderboardPosition;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.MysticType;
import net.pitsim.pitsim.enums.PantColor;
import net.pitsim.pitsim.misc.Formatter;
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
		return "&c" + Formatter.formatLarge(position.intValue) + " jewel" + (position.intValue == 1 ? "" : "s");
	}

	@Override
	public String getDisplayValue(PitPlayer pitPlayer) {
		return "&c" + Formatter.formatLarge(pitPlayer.stats.jewelsCompleted) + " jewel" + (pitPlayer.stats.jewelsCompleted == 1 ? "" : "s");
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
