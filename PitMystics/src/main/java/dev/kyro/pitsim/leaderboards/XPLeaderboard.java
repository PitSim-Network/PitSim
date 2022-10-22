package dev.kyro.pitsim.leaderboards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.Leaderboard;
import dev.kyro.pitsim.controllers.objects.LeaderboardData;
import dev.kyro.pitsim.controllers.objects.LeaderboardPosition;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class XPLeaderboard extends Leaderboard {
	public XPLeaderboard() {
		super("xp");
	}

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
	public String getDisplayValue(PitPlayer pitPlayer) {
		return "&b" + Misc.formatLarge(PrestigeValues.getTotalXP(pitPlayer.prestige, pitPlayer.level, pitPlayer.remainingXP));
	}

	@Override
	public void setPosition(LeaderboardPosition position) {
		LeaderboardData data = LeaderboardData.getLeaderboardData(this);
		LeaderboardData.PlayerData playerData = data.getValue(position.uuid);

		position.longValue = (long) playerData.primaryValue;
	}

	@Override
	public boolean isMoreThanOrEqual(LeaderboardPosition position, LeaderboardPosition otherPosition) {
		return position.longValue >= otherPosition.longValue;
	}
}
