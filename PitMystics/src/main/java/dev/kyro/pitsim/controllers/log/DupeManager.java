package dev.kyro.pitsim.controllers.log;

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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@SuppressWarnings("ConstantConditions")
public class DupeManager implements Listener {
	public static BukkitTask runnable;

	public static List<TrackedItem> dupedItems = new ArrayList<>();
	public static List<TrackedMiscItem> miscItems = new ArrayList<>();
	public static List<BukkitRunnable> helmetSpam = new ArrayList<>();

	static {
		miscItems.add(new TrackedMiscItem("Feathers", "feathers",
				"***REMOVED***"));
		miscItems.add(new TrackedMiscItem("Gem Shards", "shards",
				"***REMOVED***"));
		miscItems.add(new TrackedMiscItem("Gems", "gems",
				"***REMOVED***"));

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!AConfig.getString("server").equals("pitsim-main")) return;
				dupeCheck();
			}
		}.runTaskLater(PitSim.INSTANCE, 200L);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(helmetSpam.isEmpty()) return;
				BukkitRunnable runnable = helmetSpam.remove(0);
				runnable.runTaskAsynchronously(PitSim.INSTANCE);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 100L);
	}

	public static void run() {
		if(runnable != null) runnable.cancel();
		dupedItems.clear();
		for(TrackedMiscItem miscItem : miscItems) miscItem.itemMap.clear();
		helmetSpam.clear();

		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				dupeCheck();
			}
		}.runTaskAsynchronously(PitSim.INSTANCE);
	}

	public static void setupMiscItemTracking(UUID uuid) {
		for(TrackedMiscItem miscItem : miscItems) miscItem.itemMap.put(uuid, 0);
	}

	public static void trackMiscItem(UUID uuid, ItemStack itemStack, NBTItem nbtItem) {
		if(nbtItem.hasKey(NBTTag.IS_GHELMET.getRef())) {
			int gold = nbtItem.getInteger(NBTTag.GHELMET_GOLD.getRef());
			OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
			if((gold >= 100_000_000 || gold < 0) && !player.isOp()) {
				DiscordWebhook discordWebhook = new DiscordWebhook(
						"https://discord.com/api/webhooks/933584807068307496/jPA-MDDCpfV82PHRyjo_MusYersSyW_aCMuV37d6f_j2UzceVGTG7gXeo9lD0XlpcifO");
				DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject()
						.setTitle(player.getName())
						.setDescription("Helmet Gold: " + gold)
						.setColor(Color.BLACK);
				discordWebhook.addEmbed(embedObject);

				helmetSpam.add(new BukkitRunnable() {
					@Override
					public void run() {
						try {
							discordWebhook.execute();
						} catch(IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}

		if(nbtItem.hasKey(NBTTag.IS_FEATHER.getRef())) {
			Map<UUID, Integer> trackMap = getTrack("feathers").itemMap;
			trackMap.put(uuid, trackMap.get(uuid) + itemStack.getAmount());
		}
		if(nbtItem.hasKey(NBTTag.IS_SHARD.getRef())) {
			Map<UUID, Integer> trackMap = getTrack("shards").itemMap;
			trackMap.put(uuid, trackMap.get(uuid) + itemStack.getAmount());
		}
		if(nbtItem.hasKey(NBTTag.IS_GEM.getRef()) || nbtItem.hasKey(NBTTag.IS_GEMMED.getRef())) {
			Map<UUID, Integer> trackMap = getTrack("gems").itemMap;
			trackMap.put(uuid, trackMap.get(uuid) + itemStack.getAmount());
		}
	}

	public static void dupeCheck() {
		List<TrackedItem> trackedItems = new ArrayList<>();
		JSONParser jsonParser = new JSONParser();

		AOutput.log("Stage 1 initiated");

		List<UUID> allPlayerUUIDs = new ArrayList<>();
		for(Map.Entry<UUID, FileConfiguration> entry : APlayerData.getAllData().entrySet()) allPlayerUUIDs.add(entry.getKey());

		int count = 0; int total = allPlayerUUIDs.size();
		for(UUID uuid : allPlayerUUIDs) {
			if(count++ % 100 == 0) AOutput.log("Stage 1: " + count + "/" + total);

			setupMiscItemTracking(uuid);

			try (FileReader reader = new FileReader("mstore/galacticvaults_players/" + uuid + ".json"))
			{
				JSONObject data = (JSONObject) jsonParser.parse(reader);
				JSONObject vaults = (JSONObject) data.get("vaultContents");
				for(int i = 1; i < 19; i++) {
					JSONObject vault = (JSONObject) vaults.get(i + "");
					if(vault == null) continue;
					for(int j = 8; j < 36; j++) {
						String base64String = (String) vault.get(j + "");
						if(base64String == null) continue;
						ItemStack itemStack = Base64.itemFrom64(base64String);

						if(Misc.isAirOrNull(itemStack)) continue;
						NBTItem nbtItem = new NBTItem(itemStack);

						trackMiscItem(uuid, itemStack, nbtItem);

						if(nbtItem.hasKey(NBTTag.GHELMET_UUID.getRef())) trackedItems.add(new TrackedItem(uuid, nbtItem, i, j));
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

						trackMiscItem(uuid, itemStack, nbtItem);

						if(nbtItem.hasKey(NBTTag.GHELMET_UUID.getRef())) trackedItems.add(new TrackedItem(uuid, nbtItem, i));
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
			if(count++ % 100 == 0) System.out.println("Stage 2: " + count + "/" + trackedItems.size());
			UUID trackedItemUUID = trackedItem.itemUUID;
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

		for(TrackedMiscItem miscItem : miscItems) {
			List<UUID> toRemove = new ArrayList<>();
			for(Map.Entry<UUID, Integer> entry : miscItem.itemMap.entrySet()) {
				OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
				if(player.isOp()) toRemove.add(entry.getKey());
			}
			for(UUID uuid : toRemove) miscItem.itemMap.remove(uuid);
			int total = miscItem.itemMap.values().stream().mapToInt(i -> i).sum();

			DiscordWebhook discordWebhook = new DiscordWebhook(miscItem.webhookURI);
			DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject()
					.setTitle(miscItem.displayName)
					.setDescription("Total " + miscItem.displayName + ": " + total)
					.setColor(Color.BLACK);
			discordWebhook.addEmbed(embedObject);

			Stream<Map.Entry<UUID,Integer>> sorted = miscItem.itemMap.entrySet().stream()
					.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
					.limit(40);
			AtomicInteger mapCount = new AtomicInteger(1);
			sorted.forEach(entry -> {
				OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
				embedObject.addField(mapCount.getAndIncrement() + ". " + player.getName(), entry.getValue() + " " + miscItem.displayName, true);
			});

			try {
				discordWebhook.execute();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Results posted");
	}

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

			try {
				this.itemUUID = UUID.fromString(nbtItem.getString(NBTTag.ITEM_UUID.getRef()));
			} catch(Exception ignored) {
				this.itemUUID = UUID.fromString(nbtItem.getString(NBTTag.GHELMET_UUID.getRef()));
			}
		}

		public TrackedItem(UUID playerUUID, NBTItem nbtItem, Integer inventorySlot) {
			this.playerUUID = playerUUID;
			this.nbtItem = nbtItem;
			this.inventorySlot = inventorySlot;

			try {
				this.itemUUID = UUID.fromString(nbtItem.getString(NBTTag.ITEM_UUID.getRef()));
			} catch(Exception ignored) {
				this.itemUUID = UUID.fromString(nbtItem.getString(NBTTag.GHELMET_UUID.getRef()));
			}
		}

		public void populate() {
			for(TrackedItem trackedItem : dupedItems) {
				if(trackedItem == this || !trackedItem.itemUUID.equals(itemUUID)) continue;
				if(!sharedWith.contains(trackedItem.playerUUID)) sharedWith.add(trackedItem.playerUUID);
			}
		}
	}

	public static class TrackedMiscItem {
		public String displayName;
		public String refName;
		public String webhookURI;

		public Map<UUID, Integer> itemMap = new HashMap<>();

		public TrackedMiscItem(String displayName, String refName, String webhookURI) {
			this.displayName = displayName;
			this.refName = refName;
			this.webhookURI = webhookURI;
		}
	}

	public static TrackedMiscItem getTrack(String refName) {
		for(TrackedMiscItem miscItem : miscItems) {
			if(miscItem.refName.equalsIgnoreCase(refName)) return miscItem;
		}
		return null;
	}
}
