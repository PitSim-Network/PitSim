package dev.kyro.pitsim.controllers;

import de.myzelyam.api.vanish.VanishAPI;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.arcticguilds.ArcticGuilds;
import dev.kyro.arcticguilds.events.GuildWithdrawalEvent;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.*;
import dev.kyro.pitsim.enums.ItemType;
import dev.kyro.pitsim.events.MessageEvent;
import dev.kyro.pitsim.storage.EditSession;
import dev.kyro.pitsim.storage.StorageManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ProxyMessaging implements Listener {

	public static final int COMMAND_QUEUE_COOLDOWN_MS = 500;

	public static int playersOnline = 0;

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				sendServerData();
			}
		}.runTaskTimer(PitSim.INSTANCE, 20 * 5, 20 * 5);
	}


	public static void sendStartup() {
		System.out.println(PitSim.serverName);
		new PluginMessage().writeString("INITIATE STARTUP").writeString(PitSim.serverName).send();
	}

	public static void sendShutdown() {
		new PluginMessage().writeString("INITIATE FINAL SHUTDOWN").writeString(PitSim.serverName).send();


		for(Player player : Bukkit.getOnlinePlayers()) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			LobbySwitchManager.setSwitchingPlayer(player);

			if(PitSim.getStatus() == PitSim.ServerStatus.DARKZONE) {
				darkzoneSwitchPlayer(player, 0);
			} else if(PitSim.getStatus() == PitSim.ServerStatus.PITSIM) {
				switchPlayer(player, 0);
			}
		}
	}

	public static void sendBoosterUse(Booster booster, Player player, int time, boolean message) {

		String playerName = "%luckperms_prefix%" + player.getName();
		String playerNameColored = PlaceholderAPI.setPlaceholders(player, playerName);

		String announcement = message ? ChatColor.
				translateAlternateColorCodes('&', "&6&lBOOSTER! " + playerNameColored + " &7has used a " + booster.color + booster.name) : "";

		new PluginMessage().writeString("BOOSTER USE").writeString(booster.refName).writeString(announcement).writeInt(time).send();
	}

	public static void sendServerData() {
		PluginMessage message = new PluginMessage();
		message.writeString("SERVER DATA").writeString(PitSim.serverName);
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(VanishAPI.isInvisible(onlinePlayer)) continue;

			String builder = PrestigeValues.getPlayerPrefix(onlinePlayer) +
					PlaceholderAPI.setPlaceholders(onlinePlayer, " %luckperms_prefix%%player_name%");
			message.writeString(builder);
		}
		message.send();
	}

	@EventHandler
	public void onMessage(MessageEvent event) {
		PluginMessage message = event.getMessage();
		List<String> strings = event.getMessage().getStrings();
		List<Integer> integers = event.getMessage().getIntegers();
		List<Boolean> booleans = event.getMessage().getBooleans();

		if(strings.size() > 1 && strings.get(0).equals("NITRO PLAYERS")) {
			strings.remove(0);
			NonManager.updateNons(strings);
		}

		if(strings.size() >= 1 && strings.get(0).equals("SERVER DATA")) {
			strings.remove(0);

			for(int i = 0; i < integers.size(); i++) {

				new ServerData(i, strings, integers, booleans);
			}
		}

		if(strings.size() >= 1 && booleans.size() >= 1 && strings.get(0).equals("SHUTDOWN")) {

			int minutes = 5;
			if(integers.size() >= 1 && integers.get(0) > 0) {
				minutes = integers.get(0);
			}

			ShutdownManager.isRestart = booleans.get(0);
			ShutdownManager.initiateShutdown(minutes);
		}

		if(strings.size() >= 2 && strings.get(0).equals("DARKZONE JOIN")) {
			if(booleans.size() >= 1 && booleans.get(0)) {
				UUID uuid = UUID.fromString(strings.get(1));
				LobbySwitchManager.joinedFromDarkzone.add(uuid);
				System.out.println(uuid + "joined from darkzone");

				new BukkitRunnable() {
					@Override
					public void run() {
						LobbySwitchManager.joinedFromDarkzone.remove(uuid);
					}
				}.runTaskLater(PitSim.INSTANCE, 20 * 5);
			}
		}

		if(strings.size() >= 1 && strings.get(0).equals("PLAYER COUNT")) {
			playersOnline = integers.get(0);
		}

		if(strings.size() >= 1 && strings.get(0).equals("CANCEL SHUTDOWN")) {
			ShutdownManager.cancelShutdown();
		}

		if(strings.size() >= 2 && strings.get(0).equals("LEADERBOARD PLAYER DATA")) {
			UUID uuid = UUID.fromString(strings.get(1));

			new LeaderboardPlayerData(uuid, integers);
		}

		if(strings.size() >= 2 && strings.get(0).equals("LEADERBOARD DATA")) {
			strings.remove(0);

			for(int i = 0; i < strings.size(); i++) {
				Leaderboard leaderboard = LeaderboardManager.leaderboards.get(i);

				new LeaderboardData(leaderboard, strings.get(i));
			}

		}

		if(strings.size() >= 3 && strings.get(0).equals("BOOSTER USE")) {
			String boosterString = strings.get(1);
			Booster booster = BoosterManager.getBooster(boosterString);

			assert booster != null;
			booster.minutes += integers.get(0);
			booster.updateTime();
			FirestoreManager.CONFIG.save();

			String announcement = strings.get(2);
			if(!announcement.isEmpty()) Bukkit.broadcastMessage(strings.get(2));
		}

		if(strings.size() >= 2 && strings.get(0).equals("AUCTION ITEM REQUEST")) {
			UUID uuid = UUID.fromString(strings.get(4));
			Player winner = Bukkit.getPlayer(uuid);
			int id = integers.get(0);
			int itemData = integers.get(1);
			int highestBid = integers.get(2);

			ItemType item = ItemType.getItemType(id);
			assert item != null;

			PitPlayer.getPitPlayer(winner.getPlayer()).stats.auctionsWon++;

			if(itemData == 0) {
				AUtil.giveItemSafely(winner.getPlayer(), item.item.clone(), true);
			} else {
				ItemStack jewel = ItemType.getJewelItem(item.id, itemData);

				AUtil.giveItemSafely(winner.getPlayer(), jewel, true);

				AOutput.send(winner.getPlayer(), "&5&lDARK AUCTION! &7Received " + item.itemName + "&7.");
			}

		}

		if(strings.size() >= 2 && strings.get(0).equals("MIGRATE")) {
			UUID uuid = UUID.fromString(strings.get(1));

			migrate(uuid);
		}

		if(strings.size() >= 2 && strings.get(0).equals("REQUEST SWITCH")) {
			UUID uuid = UUID.fromString(strings.get(1));
			Player player = Bukkit.getPlayer(uuid);
			if(player == null) return;

			if(CombatManager.isInCombat(player)) {
				AOutput.error(player, "You may not queue while in combat!");
				return;
			}

			int requestedServer = 0;

			if(integers.size() >= 1) {
				requestedServer = integers.get(0);
			}

			switchPlayer(player, requestedServer);
		}


		if(strings.size() >= 2 && strings.get(0).equals("REQUEST DARKZONE SWITCH")) {
			UUID uuid = UUID.fromString(strings.get(1));
			Player player = Bukkit.getPlayer(uuid);
			if(player == null) return;

			if(CombatManager.isInCombat(player)) {
				AOutput.error(player, "You may not queue while in combat!");
				return;
			}

			int requestedServer = 0;

			if(integers.size() >= 1) {
				requestedServer = integers.get(0);
			}

			darkzoneSwitchPlayer(player, requestedServer);
		}

		if(strings.size() >= 2 && strings.get(0).equals("DEPOSIT")) {
			UUID uuid = UUID.fromString(strings.get(1));
			Player player = Bukkit.getPlayer(uuid);
			if(player == null) return;

			int toRemove = integers.get(0);
			boolean success = false;

			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

			double currentBalance = pitPlayer.gold;
			if(currentBalance >= toRemove && !LobbySwitchManager.switchingPlayers.contains(player)) {
				success = true;

				new BukkitRunnable() {
					@Override
					public void run() {
						pitPlayer.gold = currentBalance - toRemove;
					}
				}.runTask(PitSim.INSTANCE);

			}

			if(LobbySwitchManager.switchingPlayers.contains(player)) success = false;

			System.out.println("Sending result of deposit: " + success);
			dev.kyro.arcticguilds.misc.PluginMessage response = new dev.kyro.arcticguilds.misc.PluginMessage().writeString("DEPOSIT").writeString(player.getUniqueId().toString()).writeBoolean(success);
			response.send();
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				sendServerData();
			}
		}.runTaskLater(PitSim.INSTANCE, 5L);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				sendServerData();
			}
		}.runTaskLater(PitSim.INSTANCE, 5L);
	}

	public static void switchPlayer(Player player, int requestedServer) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.lastCommand + COMMAND_QUEUE_COOLDOWN_MS > System.currentTimeMillis()) {
			AOutput.error(player, "&cYou cannot queue since you ran a command too recently.");
			return;
		}

		if(StorageManager.isBeingEdited(player.getUniqueId())) {
			EditSession session = StorageManager.getSession(player.getUniqueId());
			assert session != null;
			session.end();

			AOutput.error(session.getStaffMember(), "&cYour session ended because the player switched instances!");
			session.getStaffMember().closeInventory();
		}

		if(StorageManager.isEditing(player)) {
			EditSession session = StorageManager.getSession(player);
			assert session != null;
			session.end();
		}


		new BukkitRunnable() {
			@Override
			public void run() {
				LobbySwitchManager.setSwitchingPlayer(player);
			}
		}.runTask(PitSim.INSTANCE);

		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {

				BukkitRunnable itemRunnable = new BukkitRunnable() {
					@Override
					public void run() {
						new PluginMessage().writeString("QUEUE").writeString(player.getName()).writeInt(requestedServer).writeBoolean(PitSim.getStatus() == PitSim.ServerStatus.DARKZONE).send();
					}
				};

				StorageManager.getProfile(player).saveData(itemRunnable, true);

			}
		};

		try {
			pitPlayer.save(true, runnable, false);
		} catch(ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		}

	}

	public static void darkzoneSwitchPlayer(Player player, int requestedServer) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.lastCommand + COMMAND_QUEUE_COOLDOWN_MS > System.currentTimeMillis()) {
			AOutput.error(player, "&cYou cannot queue since you ran a command too recently.");
			return;
		}

		if(StorageManager.isBeingEdited(player.getUniqueId())) {
			EditSession session = StorageManager.getSession(player.getUniqueId());
			assert session != null;
			session.end();

			AOutput.error(session.getStaffMember(), "&cYour session ended because the player switched instances!");
			session.getStaffMember().closeInventory();
		}

		if(StorageManager.isEditing(player)) {
			EditSession session = StorageManager.getSession(player);
			assert session != null;
			session.end();
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				LobbySwitchManager.setSwitchingPlayer(player);
			}
		}.runTask(PitSim.INSTANCE);

		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {

				BukkitRunnable itemRunnable = new BukkitRunnable() {
					@Override
					public void run() {
						new PluginMessage().writeString("QUEUE DARKZONE").writeString(player.getName()).writeInt(requestedServer).send();
					}
				};

				StorageManager.getProfile(player).saveData(itemRunnable, true);

			}
		};

		try {
			pitPlayer.save(true, runnable, false);
		} catch(ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		}

	}

	public static void migrate(UUID uuid) {
		PitPlayer pitPlayer = new PitPlayer(Bukkit.getOfflinePlayer(uuid).getUniqueId());
		pitPlayer.save(false, false);

		StorageManager.getInitialProfile(uuid);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWithdrawal(GuildWithdrawalEvent event) {
		boolean success = !event.isCancelled() && !LobbySwitchManager.switchingPlayers.contains(event.getPlayer());

		PluginMessage response = new PluginMessage().writeString("WITHDRAW").writeString(event.getPlayer().getUniqueId().toString()).writeBoolean(success);
		response.send();

		if(success) {
			new BukkitRunnable() {
				@Override
				public void run() {
					int amount = event.getAmount();
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
					pitPlayer.gold += amount;
				}
			}.runTask(ArcticGuilds.INSTANCE);
		}
	}

}
