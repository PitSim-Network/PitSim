package dev.kyro.pitsim.controllers.objects;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class Leaderboard {
	public List<LeaderboardPosition> orderedLeaderboard = new ArrayList<>();

	public int slot;
	public static List<Integer> slots = new ArrayList<>(Arrays.asList(10, 11, 12, 13, 14, 15, 16, 20, 21, 22, 23, 24));

	public Leaderboard() {
		this.slot = slots.remove(0);
	}

	public abstract ItemStack getDisplayStack(UUID uuid);
	public abstract String getDisplayValue(LeaderboardPosition position);
	public abstract void setPosition(LeaderboardPosition position, FileConfiguration playerData);
	public abstract boolean isMoreThanOrEqual(LeaderboardPosition position, LeaderboardPosition otherPosition);

	public static String getRankColor(UUID uuid) {
		return Misc.getRankColor(uuid);
	}

	public List<String> getTopPlayers(UUID uuid) {
		ALoreBuilder aLoreBuilder = new ALoreBuilder();
		boolean isOnLeaderboard = false;
		for(int i = 0; i < 10; i++) {
			LeaderboardPosition position = orderedLeaderboard.get(i);
			if(position.uuid.equals(uuid)) isOnLeaderboard = true;
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(position.uuid);
			String rankColor = getRankColor(position.uuid);
			aLoreBuilder.addLore("&e" + (i + 1) + ". " + getPrestigeBrackets(position.uuid) + " " + rankColor + offlinePlayer.getName() + "&7 - " + getDisplayValue(position));
		}
		LeaderboardPosition position = null;
		for(LeaderboardPosition leaderboardPosition : new ArrayList<>(orderedLeaderboard)) {
			if(!leaderboardPosition.uuid.equals(uuid)) continue;
			position = leaderboardPosition;
			break;
		}
		if(isOnLeaderboard) {
		} else if(position == null) {
			aLoreBuilder.addLore("", "&7You are not on this leaderboard");
		} else {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
			PitSim.LUCKPERMS.getUserManager().loadUser(position.uuid);
			String rankColor = getRankColor(uuid);
			aLoreBuilder.addLore("&7...", "&e" + (orderedLeaderboard.indexOf(position) + 1) + ". " + getPrestigeBrackets(position.uuid) + " " +
					rankColor + offlinePlayer.getName() + "&7 - " + getDisplayValue(position));
		}

		return aLoreBuilder.getLore();
	}

	private static String getPrestigeBrackets(UUID uuid) {
		FileConfiguration playerData = APlayerData.getPlayerData(uuid).playerData;
		return PrestigeValues.getPlayerPrefix(playerData.getInt("prestige"), playerData.getInt("level"));
	}

	public void calculate(UUID uuid, APlayer aPlayer) {
		remove(uuid);

		LeaderboardPosition leaderboardPosition = new LeaderboardPosition(this, uuid);
		setPosition(leaderboardPosition, aPlayer.playerData);
		for(int i = 0; i < orderedLeaderboard.size(); i++) {
			LeaderboardPosition testPosition = orderedLeaderboard.get(i);
			if(testPosition.isMoreThanOrEqual(leaderboardPosition)) continue;
			orderedLeaderboard.add(i, leaderboardPosition);
			return;
		}
		orderedLeaderboard.add(leaderboardPosition);
	}

	public void remove(UUID uuid) {
		for(LeaderboardPosition leaderboardPosition : orderedLeaderboard) {
			if(!leaderboardPosition.uuid.equals(uuid)) continue;
			orderedLeaderboard.remove(leaderboardPosition);
			break;
		}
	}
}
