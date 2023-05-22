package dev.kyro.pitsim.controllers;

import de.myzelyam.api.vanish.VanishAPI;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.arcticguilds.ArcticGuilds;
import dev.kyro.arcticguilds.GuildManager;
import dev.kyro.arcticguilds.events.GuildWithdrawalEvent;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.*;
import dev.kyro.pitsim.enums.ItemType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.MessageEvent;
import dev.kyro.pitsim.events.PitJoinEvent;
import dev.kyro.pitsim.events.PitQuitEvent;
import dev.kyro.pitsim.inventories.AdminGUI;
import dev.kyro.pitsim.misc.MinecraftSkin;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.storage.EditSession;
import dev.kyro.pitsim.storage.StorageManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class ProxyMessaging implements Listener {

	public static final int COMMAND_QUEUE_COOLDOWN_MS = 500;

	public static Map<UUID, String> joinTeleportMap = new HashMap<>();
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
		AOutput.log("Server Online: " + PitSim.serverName);
		new PluginMessage().writeString("INITIATE STARTUP").writeString(PitSim.serverName).send();
	}

	public static void sendShutdown() {
		new PluginMessage().writeString("INITIATE FINAL SHUTDOWN").writeString(PitSim.serverName).send();

		for(Player player : Bukkit.getOnlinePlayers()) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			LobbySwitchManager.setSwitchingPlayer(player);

			if(PitSim.getStatus() == PitSim.ServerStatus.DARKZONE) {
				darkzoneSwitchPlayer(player, 0);
			} else if(PitSim.getStatus() == PitSim.ServerStatus.OVERWORLD) {
				switchPlayer(player, 0);
			}
		}
	}

	public static void sendBoosterUse(Booster booster, Player player, int time, boolean message) {
		String playerName = "%luckperms_prefix%" + player.getName();
		String playerNameColored = PlaceholderAPI.setPlaceholders(player, playerName);

		String announcement = message ? ChatColor.translateAlternateColorCodes('&',
				"&6&lBOOSTER! " + playerNameColored + " &7has used a " + booster.color + booster.name) : "";

		new PluginMessage().writeString("BOOSTER USE")
				.writeString(booster.refName)
				.writeString(announcement)
				.writeString(player.getUniqueId().toString())
				.writeInt(time)
				.send();
	}

	public static void sendServerData() {
		PluginMessage message = new PluginMessage();
		message.writeString("SERVER DATA").writeString(PitSim.serverName);
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(VanishAPI.isInvisible(onlinePlayer)) continue;

			String builder = PrestigeValues.getPlayerPrefix(onlinePlayer) +
					PlaceholderAPI.setPlaceholders(onlinePlayer, " %luckperms_prefix%%player_name%");
			message.writeString(onlinePlayer.getName() + ":" + builder);
		}
		message.send();
	}

	@EventHandler
	public void onMessage(MessageEvent event) {
		PluginMessage message = event.getMessage();
		List<String> strings = event.getMessage().getStrings();
		List<Integer> integers = event.getMessage().getIntegers();
		List<Boolean> booleans = event.getMessage().getBooleans();
		List<Long> longs = event.getMessage().getLongs();

		if(strings.size() > 1 && strings.get(0).equals("NITRO PLAYERS")) {
			strings.remove(0);

			List<MinecraftSkin> skins = new ArrayList<>();
			for(String string : strings) {
				String[] info = string.split(",");

				if(info.length < 3) {
					System.out.println("Error with loading skin: " + Arrays.toString(info));
					continue;
				}
				skins.add(new MinecraftSkin(info[0], info[1], info[2]));
			}

			NonManager.updateNons(skins);
		}

		if(strings.size() >= 1 && strings.get(0).equals("SERVER DATA")) {
			strings.remove(0);

			for(int i = 0; i < integers.size(); i++) {

				new ServerData(i, false, strings, integers, booleans);
			}
		}

		if(strings.size() >= 1 && strings.get(0).equals("DARKZONE SERVER DATA")) {
			strings.remove(0);

			for(int i = 0; i < integers.size(); i++) {

				new ServerData(i, true, strings, integers, booleans);
			}
		}

		if(strings.size() >= 1 && booleans.size() >= 1 && strings.get(0).equals("SHUTDOWN")) {

			int minutes = 5;
			if(integers.size() >= 1 && integers.get(0) >= 0) {
				minutes = integers.get(0);
			}

			ShutdownManager.isRestart = booleans.get(0);
			ShutdownManager.initiateShutdown(minutes);
		}

		if(strings.size() >= 2 && strings.get(0).equals("DARKZONE JOIN")) {
			if(booleans.size() >= 1 && booleans.get(0)) {
				UUID uuid = UUID.fromString(strings.get(1));
				LobbySwitchManager.joinedFromDarkzone.add(uuid);

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
			UUID activatorUUID = UUID.fromString(strings.get(3));

			assert booster != null;
			booster.minutes += integers.get(0);
			booster.activatorUUID = activatorUUID;
			booster.updateTime();
			FirestoreManager.CONFIG.boosterActivatorMap.put(booster.refName, activatorUUID.toString());
			FirestoreManager.CONFIG.save();
			for(Player player : ChatTriggerManager.getSubscribedPlayers())
				ChatTriggerManager.sendBoosterInfo(PitPlayer.getPitPlayer(player));

			String announcement = strings.get(2);
			if(!announcement.isEmpty()) Bukkit.broadcastMessage(strings.get(2));
		}

		if(strings.size() >= 1 && strings.get(0).equals("BOOSTER_SHARE")) {
			String boosterString = strings.get(1);
			Booster booster = BoosterManager.getBooster(boosterString);
			assert booster != null;
			UUID activatorUUID = UUID.fromString(strings.get(2));
			Player activator = Bukkit.getPlayer(activatorUUID);
			int amount = integers.get(0);
			if(activator != null) booster.queueOnlineShare(activator, amount);
		}

		if(strings.size() >= 1 && strings.get(0).equals("AUCTION ITEM REWARD")) {
			long itemSeed = longs.get(0);
			long dataSeed = longs.get(1);

			UUID uuid = UUID.fromString(strings.get(1));

			Player player = Bukkit.getPlayer(uuid);
			if(player == null) return;

			ItemType item = ItemType.getItem(itemSeed);
			if(item == null) return;

			int itemData = ItemType.getJewelData(item.item, dataSeed);

			ItemStack reward = itemData == 0 ? item.item.clone() : ItemType.getJewelItem(item.id, itemData);
			NBTItem nbtItem = new NBTItem(reward, true);
			nbtItem.setBoolean(NBTTag.IS_AUCTION_REWARD.getRef(), true);

			new BukkitRunnable() {
				@Override
				public void run() {
					AUtil.giveItemSafely(player, reward, true);
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
					pitPlayer.stats.auctionsWon++;

					Sounds.BOOSTER_REMIND.play(player);
					AOutput.send(player, "&5&lDARK AUCTION!&7 Received " + item.itemName + "&7.");
				}
			}.runTask(PitSim.INSTANCE);
		}

		if(strings.size() >= 1 && strings.get(0).equals("AUCTION SOUL REWARD")) {
			int soulAmount = integers.get(0);

			UUID uuid = UUID.fromString(strings.get(1));

			Player player = Bukkit.getPlayer(uuid);
			if(player == null) return;

			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			pitPlayer.giveSouls(soulAmount, false);

			Sounds.BOOSTER_REMIND.play(player);
			AOutput.send(player, "&5&lDARK AUCTION!&7 Received " + soulAmount + " Soul" + (soulAmount == 1 ? "" : "s") + "&7.");
		}

		if(strings.size() >= 1 && strings.get(0).equals("AUCTION ANNOUNCEMENT")) {
			String announcement = strings.get(1);
			long itemSeed = longs.get(0);

			ItemType item = ItemType.getItem(itemSeed);
			if(item == null) return;


			announcement = announcement.replaceAll("%item%", item.itemName);
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', announcement));
			return;
		}

		if(strings.size() >= 2 && (strings.get(0).equals("REQUEST SWITCH") || strings.get(0).equals("REQUEST DARKZONE SWITCH"))) {
			UUID uuid = UUID.fromString(strings.get(1));
			Player player = Bukkit.getPlayer(uuid);
			if(player == null) return;

			if(!player.isOp() && CombatManager.isInCombat(player)) {
				AOutput.error(player, "You may not queue while in combat!");
				return;
			}

			int requestedServer = 0;

			if(integers.size() >= 1) {
				requestedServer = integers.get(0);
			}

			switchPlayer(player, requestedServer);
		}

		if(strings.size() >= 3 && strings.get(0).equals("TELEPORT JOIN")) {
			UUID uuid = UUID.fromString(strings.get(1));
			String player = strings.get(2);
			if(player == null) return;

			joinTeleportMap.put(uuid, player);
		}

		if(strings.size() >= 2 && strings.get(0).equals("ADMIN GUI OPEN")) {
			UUID uuid = UUID.fromString(strings.get(1));
			Player player = Bukkit.getPlayer(uuid);
			if(!player.isOnline()) return;

			new BukkitRunnable() {
				@Override
				public void run() {
					AdminGUI gui = new AdminGUI(player);
					gui.open();
				}
			}.runTask(PitSim.INSTANCE);
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

			dev.kyro.arcticguilds.misc.PluginMessage response = new dev.kyro.arcticguilds.misc.PluginMessage()
					.writeString("DEPOSIT")
					.writeString(player.getUniqueId().toString())
					.writeBoolean(success);
			response.send();
		}

		if(strings.size() > 0 && strings.get(0).equals("OUTPOST DATA")) {
			OutpostManager.controllingGuild = GuildManager.getGuild(UUID.fromString(strings.get(1)));
			OutpostManager.isActive = booleans.get(0);

			new BukkitRunnable() {
				@Override
				public void run() {
					if(PitSim.getStatus().isDarkzone()) OutpostManager.setPercentControlled(100);
				}
			}.runTask(PitSim.INSTANCE);

		}

	}

	@EventHandler
	public void onJoin(PitJoinEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				sendServerData();
			}
		}.runTaskLater(PitSim.INSTANCE, 5L);
	}

	@EventHandler
	public void onLeave(PitQuitEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				sendServerData();
			}
		}.runTaskLater(PitSim.INSTANCE, 5L);
	}

	public static void switchPlayer(Player player, int requestedServer) {

		LobbySwitchManager.setSwitchingPlayer(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				switchCheck(player);
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

				if(StorageManager.isEditing(player)) {
					EditSession session = StorageManager.getSession(player);
					assert session != null;
					session.end();
				}

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
		}.runTaskLater(PitSim.INSTANCE, 10);

	}

	private static void switchCheck(Player player) {
		if(StorageManager.isBeingEdited(player.getUniqueId())) {
			EditSession session = StorageManager.getSession(player.getUniqueId());
			assert session != null;
			session.end();

			AOutput.error(session.getStaffMember(), "&cYour session ended because the player switched instances!");
			session.getStaffMember().closeInventory();
		}
	}

	public static void darkzoneSwitchPlayer(Player player, int requestedServer) {
		LobbySwitchManager.setSwitchingPlayer(player);
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				switchCheck(player);

				if(StorageManager.isEditing(player)) {
					EditSession session = StorageManager.getSession(player);
					session.end();
				}

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
		}.runTaskLater(PitSim.INSTANCE, 10);
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
