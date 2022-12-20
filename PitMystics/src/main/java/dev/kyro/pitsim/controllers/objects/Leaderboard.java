package dev.kyro.pitsim.controllers.objects;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class Leaderboard {
	public List<LeaderboardPosition> orderedLeaderboard = new ArrayList<>();

	public int slot;
	public static List<Integer> slots = new ArrayList<>(Arrays.asList(10, 11, 13, 14, 15, 16, 20, 21, 22, 23, 24));
//	public static List<Integer> slots = new ArrayList<>(Arrays.asList(10, 11, 12, 13, 14, 15, 16, 20, 21, 22, 23, 24));
	public String refName;

	public Leaderboard(String refName) {
		this.slot = slots.remove(0);
		this.refName = refName;
	}

	public abstract ItemStack getDisplayStack(UUID uuid);
	public abstract String getDisplayValue(LeaderboardPosition position);
	public abstract String getDisplayValue(PitPlayer pitPlayer);
	public abstract void setPosition(LeaderboardPosition position);
	public abstract boolean isMoreThanOrEqual(LeaderboardPosition position, LeaderboardPosition otherPosition);

	public static String getRankColor(UUID uuid) {
		return Misc.getRankColor(uuid);
	}

	public List<String> getTopPlayers(UUID uuid) {
		ALoreBuilder aLoreBuilder = new ALoreBuilder();
		boolean isOnLeaderboard = false;
		for(int i = 0; i < 10; i++) {
			if(orderedLeaderboard.size() < i + 1)  {
				System.out.println(orderedLeaderboard.size());
				aLoreBuilder.addLore("&e" + (i + 1) + ". &cERROR");
				System.out.println("Error 1");
				continue;
			}
			LeaderboardPosition position = orderedLeaderboard.get(i);
			LeaderboardData data = LeaderboardData.getLeaderboardData(this);
			if(position.uuid.equals(uuid)) isOnLeaderboard = true;
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(position.uuid);
			String rankColor = getRankColor(position.uuid);
			if(data.getPrefix(position.uuid) == null) {
				aLoreBuilder.addLore("&e" + (i + 1) + ". &cERROR");
				System.out.println("Error 2");
				continue;
			}
			aLoreBuilder.addLore("&e" + (i + 1) + ". " + data.getPrefix(position.uuid) + " " + rankColor + offlinePlayer.getName() + "&7 - " + getDisplayValue(position));
		}

		LeaderboardPlayerData data = LeaderboardPlayerData.getData(uuid);
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
		if(offlinePlayer.isOnline()) {
			Player player = offlinePlayer.getPlayer();
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

			if(isOnLeaderboard) {
			} else if(data == null) {
				aLoreBuilder.addLore("", "&7You are not on this leaderboard");
			} else {
				PitSim.LUCKPERMS.getUserManager().loadUser(uuid);
				String rankColor = getRankColor(uuid);
				aLoreBuilder.addLore("&7...", "&e" + data.getData(this) + ". " + getPrestigeBrackets(pitPlayer) + " " +
						rankColor + offlinePlayer.getName() + "&7 - " + getDisplayValue(pitPlayer));
			}
		}

		return aLoreBuilder.getLore();
	}

	private static String getPrestigeBrackets(PitPlayer pitPlayer) {
		return PrestigeValues.getPlayerPrefix(pitPlayer.prestige, pitPlayer.level);
	}

	public void calculate(UUID uuid) {
		remove(uuid);

		LeaderboardPosition leaderboardPosition = new LeaderboardPosition(this, uuid);
		setPosition(leaderboardPosition);
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
