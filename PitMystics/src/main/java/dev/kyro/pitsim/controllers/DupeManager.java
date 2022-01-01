package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.data.AConfig;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.libs.discord.DiscordWebhook;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Base64;
import dev.kyro.pitsim.misc.Misc;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class DupeManager implements Listener {
	public static List<TrackedItem> dupedItems = new ArrayList<>();
	public static Map<UUID, Integer> featherMap = new HashMap<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!AConfig.getString("server").equals("pitsim-main")) return;
				dupeCheck();
			}
		}.runTaskLaterAsynchronously(PitSim.INSTANCE, 200L);
	}

	public static void dupeCheck() {
		List<TrackedItem> trackedItems = new ArrayList<>();
		JSONParser jsonParser = new JSONParser();

		AOutput.log("Stage 1 initiated");

		List<UUID> allPlayerUUIDs = new ArrayList<>();
		for(Map.Entry<UUID, FileConfiguration> entry : APlayerData.getAllData().entrySet()) allPlayerUUIDs.add(entry.getKey());

		int count = 0; int total = allPlayerUUIDs.size();
		for(UUID uuid : allPlayerUUIDs) {
			featherMap.put(uuid, 0);

			AOutput.log("Stage 1: " + ++count + "/" + total);

			try (FileReader reader = new FileReader("mstore/galacticvaults_players/" + uuid + ".json"))
			{
				JSONObject data = (JSONObject) jsonParser.parse(reader);
				JSONObject vaults = (JSONObject) data.get("vaultContents");
				for(int i = 1; i < 15; i++) {
					JSONObject vault = (JSONObject) vaults.get(i + "");
					if(vault == null) continue;
					for(int j = 8; j < 36; j++) {
						String base64String = (String) vault.get(j + "");
						if(base64String == null) continue;
						ItemStack itemStack = Base64.itemFrom64(base64String);

						if(Misc.isAirOrNull(itemStack)) continue;
						NBTItem nbtItem = new NBTItem(itemStack);

						if(nbtItem.hasKey(NBTTag.IS_FEATHER.getRef())) {
							featherMap.put(uuid, featherMap.get(uuid) + itemStack.getAmount());
							continue;
						}

						if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) || !nbtItem.hasKey(NBTTag.ITEM_JEWEL_ENCHANT.getRef())) continue;
						trackedItems.add(new TrackedItem(uuid, nbtItem, i, j));
					}
				}
			} catch (IOException | ParseException e) {
				if(!(e instanceof FileNotFoundException)) e.printStackTrace();
			}

			try {
				File inventoryFile = new File("world/playerdata/" + uuid + ".dat");
				NBTTagCompound nbt = NBTCompressedStreamTools.a(new FileInputStream(inventoryFile));
				NBTTagList inventory = (NBTTagList) nbt.get("Inventory");
//                Inventory inv = new CraftInventoryCustom(null, inventory.size());
				for (int i = 0; i < inventory.size(); i++) {
					NBTTagCompound compound = inventory.get(i);
					if (!compound.isEmpty()) {
						ItemStack itemStack = CraftItemStack.asBukkitCopy(net.minecraft.server.v1_8_R3.ItemStack.createStack(compound));
//                        inv.setItem(i, itemStack);

						if(Misc.isAirOrNull(itemStack)) continue;
						NBTItem nbtItem = new NBTItem(itemStack);

						if(nbtItem.hasKey(NBTTag.IS_FEATHER.getRef())) {
							featherMap.put(uuid, featherMap.get(uuid) + itemStack.getAmount());
							continue;
						}

						if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) || !nbtItem.hasKey(NBTTag.ITEM_JEWEL_ENCHANT.getRef())) continue;
						trackedItems.add(new TrackedItem(uuid, nbtItem, i));
					}
				}

			} catch (IOException e) {
				if(!(e instanceof FileNotFoundException)) e.printStackTrace();
			}
		}

		checkForDuplicates(trackedItems);
	}

	public static void checkForDuplicates(List<TrackedItem> trackedItems) {
		List<UUID> playerUUIDs = new ArrayList<>();

		AOutput.log("Stage 2 initiated");

		int count = 0;
		for(TrackedItem trackedItem : trackedItems) {
			System.out.println("Stage 2: " + ++count + "/" + trackedItems.size());
			UUID trackedItemUUID = UUID.fromString(trackedItem.nbtItem.getString(NBTTag.ITEM_UUID.getRef()));
			for(TrackedItem checkItem : trackedItems) {
				if(checkItem == trackedItem) continue;
				if(!checkItem.itemUUID.equals(trackedItemUUID)) continue;

				if(!dupedItems.contains(trackedItem)) dupedItems.add(trackedItem);
				if(!dupedItems.contains(checkItem)) dupedItems.add(checkItem);

				if(!playerUUIDs.contains(trackedItem.playerUUID)) playerUUIDs.add(trackedItem.playerUUID);
				if(!playerUUIDs.contains(checkItem.playerUUID)) playerUUIDs.add(checkItem.playerUUID);
			}
		}

		AOutput.log("Stage 3 initiated");

		for(TrackedItem trackedItem : trackedItems) trackedItem.populate();
		for(UUID playerUUID : playerUUIDs) {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
			System.out.println(offlinePlayer.getName());
		}

		AOutput.log("Check completed, posting results");

		List<UUID> toRemove = new ArrayList<>();
		for(Map.Entry<UUID, Integer> entry : featherMap.entrySet()) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
			if(player.isOp()) toRemove.add(entry.getKey());
		}
		for(UUID uuid : toRemove) featherMap.remove(uuid);
		int totalFeathers = featherMap.values().stream().mapToInt(i -> i).sum();

		DiscordWebhook discordWebhook = new DiscordWebhook(
				"***REMOVED***");
		DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject()
				.setTitle("Feathers")
				.setDescription("There are a total of " + totalFeathers + " feathers in the game")
				.setColor(Color.BLACK);
		discordWebhook.addEmbed(embedObject);

		Stream<Map.Entry<UUID,Integer>> sorted = featherMap.entrySet().stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.limit(40);
		AtomicInteger mapCount = new AtomicInteger(1);
		sorted.forEach(entry -> {
			OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
			embedObject.addField(mapCount.getAndIncrement() + ". " + player.getName(), entry.getValue() + " Feathers", true);
		});

		try {
			discordWebhook.execute();
		} catch(IOException e) {
			e.printStackTrace();
		}

		System.out.println("Results posted");
	}

	public static void handleDuper(Player player) {

		AOutput.broadcast(player.getName() + " likes to dupe items");
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		Bukkit.dispatchCommand(console, "sudo * c:" + player.getName() + " nice items");
		String command = "ipban " + player.getName() + " Dupe Detection: Account appears to be compromising the integrity of the game";
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.dispatchCommand(console, command);
			}
		}.runTaskLater(PitSim.INSTANCE, 20L);
	}

//    public static boolean wasDuped(ItemStack itemStack) {
//        if(Misc.isAirOrNull(itemStack)) return false;
//
//        NBTItem nbtItem = new NBTItem(itemStack);
//        if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) return false;
//
//        UUID uuid = UUID.fromString(nbtItem.getString(NBTTag.ITEM_UUID.getRef()));
//        List<UUID> dupeUUIDs = new ArrayList<>();
//        for(String stringUUID : AConfig.getStringList("duped-uuids")) dupeUUIDs.add(UUID.fromString(stringUUID));
//
//        return dupeUUIDs.contains(uuid);
//    }

	public static class TrackedItem {
		public UUID playerUUID;
		public List<UUID> sharedWith = new ArrayList<>();
		public NBTItem nbtItem;
		public UUID itemUUID;

		public Integer vaultNum;
		public Integer vaultSlot;

		public Integer inventorySlot;

		public TrackedItem(UUID playerUUID, NBTItem nbtItem, Integer vaultNum, Integer vaultSlot) {
			this.playerUUID = playerUUID;
			this.nbtItem = nbtItem;
			this.vaultNum = vaultNum;
			this.vaultSlot = vaultSlot;

			this.itemUUID = UUID.fromString(nbtItem.getString(NBTTag.ITEM_UUID.getRef()));
		}

		public TrackedItem(UUID playerUUID, NBTItem nbtItem, Integer inventorySlot) {
			this.playerUUID = playerUUID;
			this.nbtItem = nbtItem;
			this.inventorySlot = inventorySlot;

			this.itemUUID = UUID.fromString(nbtItem.getString(NBTTag.ITEM_UUID.getRef()));
		}

		public void populate() {
			for(TrackedItem trackedItem : dupedItems) {
				if(trackedItem == this || !trackedItem.itemUUID.equals(itemUUID)) continue;
				if(!sharedWith.contains(trackedItem.playerUUID)) sharedWith.add(trackedItem.playerUUID);
			}
		}
	}
}
