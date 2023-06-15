package net.pitsim.pitsim.leaderboards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.controllers.PrestigeValues;
import net.pitsim.pitsim.controllers.objects.Leaderboard;
import net.pitsim.pitsim.controllers.objects.LeaderboardData;
import net.pitsim.pitsim.controllers.objects.LeaderboardPosition;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.misc.Formatter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class XPLeaderboard extends Leaderboard {
	public XPLeaderboard() {
		super("xp", "&bTotal XP");
	}

	@Override
	public ItemStack getDisplayStack(UUID uuid) {
		ItemStack itemStack = new AItemStackBuilder(Material.WHEAT)
				.setName("&b" + "Total XP")
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
		return "&b" + Formatter.formatLarge(position.longValue);
	}

	@Override
	public String getDisplayValue(PitPlayer pitPlayer) {
		return "&b" + Formatter.formatLarge(PrestigeValues.getTotalXP(pitPlayer.prestige, pitPlayer.level, pitPlayer.remainingXP));
	}

	@Override
	public void setPosition(LeaderboardPosition position) {
		LeaderboardData data = LeaderboardData.getLeaderboardData(this);
		LeaderboardData.PlayerData playerData = data.getValue(position.uuid);

		position.longValue = PrestigeValues.getTotalXP(playerData.prestige, playerData.level, 0);
	}

	@Override
	public boolean isMoreThanOrEqual(LeaderboardPosition position, LeaderboardPosition otherPosition) {
		return position.longValue >= otherPosition.longValue;
	}
}
