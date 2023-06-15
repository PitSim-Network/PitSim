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

public class GoldLeaderboard extends Leaderboard {
	public GoldLeaderboard() {
		super("gold", "&6Gold");
	}

	@Override
	public ItemStack getDisplayStack(UUID uuid) {
		ItemStack itemStack = new AItemStackBuilder(Material.GOLD_INGOT, 1)
				.setName("&6Gold")
				.setLore(new ALoreBuilder(
						"&7Players who have the most &6Gold", ""
				).addLore(getTopPlayers(uuid)).addLore(
						"", "&eClick to pick!"
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public String getDisplayValue(LeaderboardPosition position) {
		return "&6" + Formatter.formatLarge(position.doubleValue) + "g";
	}

	@Override
	public String getDisplayValue(PitPlayer pitPlayer) {
		return "&6" + Formatter.formatLarge(pitPlayer.gold) + "g";
	}

	@Override
	public void setPosition(LeaderboardPosition position) {
		LeaderboardData data = LeaderboardData.getLeaderboardData(this);

		position.doubleValue = data.getValue(position.uuid).primaryValue;
	}

	@Override
	public boolean isMoreThanOrEqual(LeaderboardPosition position, LeaderboardPosition otherPosition) {
		return position.doubleValue >= otherPosition.doubleValue;
	}
}
