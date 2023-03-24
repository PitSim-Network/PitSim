package dev.kyro.pitsim.controllers.objects;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.BoosterManager;
import dev.kyro.pitsim.controllers.FirestoreManager;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class Booster implements Listener {
	private static final List<Integer> randomTickList = new ArrayList<>();

	public String name;
	public String refName;
	public int minutes;
	public UUID activatorUUID;
	public double toShare;
	public int slot;
	public ChatColor color;

	static {
		for(int i = 0; i < 20 * 10 + 1; i += 3) randomTickList.add(i);
	}

	public Booster(String name, String refName, int slot, ChatColor color) {
		this.name = name;
		this.refName = refName;
		this.slot = slot;
		this.color = color;
		this.minutes = FirestoreManager.CONFIG.boosters.getOrDefault(refName, 0);
		if(FirestoreManager.CONFIG.boosterActivatorMap.containsKey(refName))
			this.activatorUUID = UUID.fromString(FirestoreManager.CONFIG.boosterActivatorMap.get(refName));
	}

	public abstract ItemStack getBaseDisplayItem();

	public ItemStack getDisplayItem(Player player) {
		int amount = Booster.getBoosterAmount(player, this);

		ItemStack itemStack = getBaseDisplayItem();
		AItemStackBuilder builder = new AItemStackBuilder(getBaseDisplayItem());
		ALoreBuilder loreBuilder = new ALoreBuilder(itemStack.getItemMeta().getLore());

		loreBuilder.addLore("");
		if(isActive()) {
			builder.setName("&a" + name);
			loreBuilder.addLore(
					"&7Status: &aActive!",
					"&7Expires in: &e" + minutes + " minutes"
				);
		} else {
			builder.setName("&c" + name);
			loreBuilder.addLore(
					"&7Status: &cInactive!",
					"&7Use a booster to activate"
			);
		}

		loreBuilder.addLore("", "&7You have: &e" + amount);
		if(minutes == 0 && amount != 0) {
			loreBuilder.addLore("", "&eClick to use booster!");
		} else {
			loreBuilder.addLore("", "&eClick to buy booster!");
		}
		builder.setLore(loreBuilder);
		itemStack = builder.getItemStack();

		if(isActive()) {
			Misc.addEnchantGlint(itemStack);
			itemStack.setAmount(Math.min(minutes, 64));
		}
		return itemStack;
	}

	public void share(Player player, int amount) {}

	public void queueOnlineShare(Player player, int amount) {
		int splits = 40;
		int shareAmount = amount / splits;
		List<Integer> randomTickList = new ArrayList<>(Booster.randomTickList);
		Collections.shuffle(randomTickList);
		for(int i = 0; i < splits; i++) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if(!player.isOnline()) return;
					share(player, shareAmount);
				}
			}.runTaskLater(PitSim.INSTANCE, randomTickList.remove(0));
		}
	}

	public void queueShare(double amount) {
		toShare += amount;
	}

	public void disable() {
		minutes = 0;
		activatorUUID = null;
		toShare = 0;
		FirestoreManager.CONFIG.boosterActivatorMap.remove(refName);
		updateTime();
		onDisable();
	}

	public void onDisable() {
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lBOOSTER!&7 " + color + name + "&7 no longer active"));
	}

	public boolean isActive() {
		return minutes > 0;
	}

	public void updateTime() {
		FirestoreManager.CONFIG.boosters.put(refName, minutes);
	}

	public static int getBoosterAmount(Player player, Booster booster) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return pitPlayer.boosters.getOrDefault(booster.refName, 0);
	}

	public static int getBoosterAmount(Player player, String booster) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		for(Booster booster1 : BoosterManager.boosterList) {
			if(booster1.refName.equals(booster)) {
				return pitPlayer.boosters.getOrDefault(booster1.refName, 0);
			}
		}
		return 0;
	}

	public static Booster getBooster(String booster) {
		for(Booster booster1 : BoosterManager.boosterList) {
			if(booster1.refName.equals(booster)) {
				return booster1;
			}
		}
		return null;
	}

	public static void setBooster(Player player, Booster booster, int amount) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.boosters.put(booster.refName, amount);
	}

	public static void setBooster(Player player, String booster, int amount) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		for(Booster booster1 : BoosterManager.boosterList) {
			if(booster1.refName.equals(booster)) {
				pitPlayer.boosters.put(booster1.refName, amount);
			}
		}
	}
}
