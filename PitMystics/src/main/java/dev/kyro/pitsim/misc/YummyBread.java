package dev.kyro.pitsim.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class YummyBread implements Listener {
	public static Map<Player, Integer> breadStacks = new HashMap<>();

	public static Map<UUID, Integer> breadCooldownLength = new HashMap<>();
	public static Map<UUID, Integer> breadCooldown = new HashMap<>();
	public static DecimalFormat breadCooldownFormat = new DecimalFormat("0.0");

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Map.Entry<UUID, Integer> entry : new HashMap<>(breadCooldown).entrySet()) {
					if(entry.getValue() <= 1) {
						breadCooldown.remove(entry.getKey());
					} else {
						breadCooldown.put(entry.getKey(), entry.getValue() - 1);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		if(!attackEvent.attackerIsPlayer) return;
		if(breadStacks.containsKey(attackEvent.attackerPlayer)) {
			if(NonManager.getNon(attackEvent.defender) == null) return;
			attackEvent.increasePercent += ((10 * breadStacks.get(attackEvent.attackerPlayer)) / 100D);
		}
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.deadIsPlayer) return;
		breadCooldownLength.remove(killEvent.deadPlayer.getUniqueId());
		breadCooldown.remove(killEvent.deadPlayer.getUniqueId());
	}

	@EventHandler
	public void onOof(OofEvent event) {
		Player player = event.getPlayer();
		breadCooldownLength.remove(player.getUniqueId());
		breadCooldown.remove(player.getUniqueId());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		breadCooldownLength.remove(player.getUniqueId());
		breadCooldown.remove(player.getUniqueId());
	}

	@EventHandler
	public void onSpawn(PlayerSpawnEvent event) {
		Player player = event.getPlayer();
		breadCooldownLength.remove(player.getUniqueId());
		breadCooldown.remove(player.getUniqueId());
	}

	@EventHandler
	public void onEat(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		event.getPlayer().setFoodLevel(19);

		if(Misc.isAirOrNull(event.getItem())) return;
		NBTItem nbtItem = new NBTItem(event.getItem());
		if(nbtItem.hasKey(NBTTag.IS_VERY_YUMMY_BREAD.getRef())) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			pitPlayer.heal(12);
			pitPlayer.heal(6, HealEvent.HealType.ABSORPTION, 20);
			int newCooldown = breadCooldownLength.getOrDefault(player.getUniqueId(), 0) + 10;
			breadCooldownLength.put(player.getUniqueId(), newCooldown);
			breadCooldown.put(player.getUniqueId(), newCooldown);
		}
	}

	@EventHandler
	public void onEat(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(Misc.isAirOrNull(event.getItem())) return;
		NBTItem nbtItem = new NBTItem(event.getItem());
		player.setFoodLevel(19);

		if(nbtItem.hasKey(NBTTag.IS_VERY_YUMMY_BREAD.getRef()) && breadCooldown.containsKey(player.getUniqueId())) {
			int cooldownTicks = breadCooldown.get(player.getUniqueId());
			event.setCancelled(true);
			player.setFoodLevel(20);
			Misc.sendActionBar(player, "&6Bread: &c" + breadCooldownFormat.format(cooldownTicks / 20.0) + "s cooldown!");
		}

		if(!nbtItem.hasKey(NBTTag.IS_YUMMY_BREAD.getRef())) return;
		if(breadStacks.containsKey(player)) {
			breadStacks.put(player, breadStacks.get(player) + 1);
		} else breadStacks.put(player, 1);
		Sounds.YUMMY_BREAD.play(player);
		if(event.getItem().getAmount() == 1) {
			player.getInventory().remove(event.getItem());
		} else event.getItem().setAmount(event.getItem().getAmount() - 1);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(breadStacks.containsKey(player)) {
					if(breadStacks.get(player) - 1 <= 0) breadStacks.remove(player);
					else breadStacks.put(player, breadStacks.get(player) - 1);
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 20 * 20L);
		player.updateInventory();
	}

	public static void deleteBread(Player player) {
		if(NonManager.getNon(player) != null) return;
		for(int i = 0; i < player.getInventory().getSize(); i++) {
			if(Misc.isAirOrNull(player.getInventory().getItem(i))) continue;
			NBTItem nbtItem = new NBTItem(player.getInventory().getItem(i));
			if(nbtItem.hasKey(NBTTag.IS_YUMMY_BREAD.getRef()) || nbtItem.hasKey(NBTTag.IS_VERY_YUMMY_BREAD.getRef())) {

				player.getInventory().remove(player.getInventory().getItem(i));
			}
		}
	}

	public static void giveVeryYummyBread(Player player, int amount) {
		AItemStackBuilder veryBuilder = new AItemStackBuilder(Material.BREAD, amount);
		veryBuilder.setName("&6Very yummy bread");
		ALoreBuilder veryLoreBuilder = new ALoreBuilder("&7Heals &c" + Misc.getHearts(12), "&7Grants &6" + Misc.getHearts(6));
		veryBuilder.setLore(veryLoreBuilder);

		NBTItem nbtItem = new NBTItem(veryBuilder.getItemStack());
		nbtItem.setBoolean(NBTTag.IS_VERY_YUMMY_BREAD.getRef(), true);

		AUtil.giveItemSafely(player, nbtItem.getItem(), true);
		player.updateInventory();
	}

	public static void giveYummyBread(Player player, int amount) {
		AItemStackBuilder veryBuilder = new AItemStackBuilder(Material.BREAD, amount);
		veryBuilder.setName("&6Yummy bread");
		ALoreBuilder veryLoreBuilder = new ALoreBuilder("&7Deal &c+10% &7damage to bots", "&7for 20 seconds. (Stacking)");
		veryBuilder.setLore(veryLoreBuilder);

		NBTItem nbtItem = new NBTItem(veryBuilder.getItemStack());
		nbtItem.setBoolean(NBTTag.IS_YUMMY_BREAD.getRef(), true);

		AUtil.giveItemSafely(player, nbtItem.getItem(), true);
		player.updateInventory();
	}
}
