package dev.kyro.pitsim.controllers;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.NonTrait;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.IncrementKillsEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.megastreaks.Highlander;
import dev.kyro.pitsim.megastreaks.NoMegastreak;
import dev.kyro.pitsim.misc.DeathCrys;
import dev.kyro.pitsim.misc.KillEffects;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

//import net.kyori.adventure.audience.Audience;

public class PlayerManager implements Listener {
//	public static Map<Player, BossBarManager> bossBars = new HashMap<>();
	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Non non : NonManager.nons) if(non.non != null) ((CraftPlayer) non.non).getHandle().getDataWatcher().watch(9, (byte) 0);
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) ((CraftPlayer) onlinePlayer).getHandle().getDataWatcher().watch(9, (byte) 0);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if(event.getPlayer().isOp()) return;
//		TODO: Pay needs to be moved to its own command because essentials pay autocompletes name so its not feasible to block command if receiver level < 100
		if(ChatColor.stripColor(event.getMessage()).toLowerCase().startsWith("/trade") ||
				ChatColor.stripColor(event.getMessage()).toLowerCase().startsWith("/pay")) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
			if(pitPlayer.level < 100) {
				event.setCancelled(true);
				AOutput.error(event.getPlayer(), "&c&lNOPE! &7You cannot trade until level 100");
			}
		}
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if(AFKManager.AFKPlayers.contains(onlinePlayer) || Math.random() > (1.0 / 3.0)) continue;
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					pitPlayer.renown++;
					FileConfiguration playerData = APlayerData.getPlayerData(onlinePlayer);
					playerData.set("renown", pitPlayer.renown);
					APlayerData.savePlayerData(onlinePlayer);
					AOutput.send(onlinePlayer, "&7You have been given &e1 renown &7for being active");
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 20 * 60, 20 * 60 * 10);

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(!player.hasPermission("group.eternal") || player.getWorld() != MapManager.getMid().getWorld()) continue;
					if(SpawnManager.isInSpawn(player.getLocation())) continue;
					List<Player> nearbyNons = new ArrayList<>();
					for(Entity nearbyEntity : player.getNearbyEntities(4, 4, 4)) {
						if(!(nearbyEntity instanceof Player)) continue;
						Player nearby = (Player) nearbyEntity;
						if(NonManager.getNon(nearby) == null || SpawnManager.isInSpawn(nearby.getLocation())) continue;
						if(nearby.getLocation().distance(player.getLocation()) > 4) continue;
						nearbyNons.add(nearby);
					}
					if(!nearbyNons.isEmpty()) {
						Collections.shuffle(nearbyNons);
						Player target = nearbyNons.remove(0);

						double damage;
						if(Misc.isAirOrNull(player.getItemInHand())) {
							damage = 1;
						} else if(player.getItemInHand().getType() == Material.GOLD_SWORD) {
							damage = 7.5;
						} else {
							damage = 1;
						}
						if(Misc.isCritical(player)) damage *= 1.5;

						target.setNoDamageTicks(0);
						target.damage(damage, player);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 10L);
	}

	@EventHandler
	public void onKillForRank(KillEvent killEvent) {
		double multiplier = 1;
		if(killEvent.killer.hasPermission("group.nitro")) {
			multiplier += 0.1;
		}

		if(killEvent.killer.hasPermission("group.eternal")) {
			multiplier += 0.35;
		} else if(killEvent.killer.hasPermission("group.unthinkable")) {
			multiplier += 0.25;
		} else if(killEvent.killer.hasPermission("group.miraculous")) {
			multiplier += 0.20;
		} else if(killEvent.killer.hasPermission("group.extraordinary")) {
			multiplier += 0.15;
		} else if(killEvent.killer.hasPermission("group.overpowered")) {
			multiplier += 0.1;
		} else if(killEvent.killer.hasPermission("group.legendary")) {
			multiplier += 0.05;
		}
		killEvent.xpMultipliers.add(multiplier);
		killEvent.goldMultipliers.add(multiplier);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onKill(KillEvent killEvent) {

		PitPlayer pitKiller = PitPlayer.getPitPlayer(killEvent.killer);
		PitPlayer pitDead = PitPlayer.getPitPlayer(killEvent.dead);
		Non killingNon = NonManager.getNon(killEvent.killer);
		Non deadNon = NonManager.getNon(killEvent.dead);

		if(pitKiller.killEffect != null && killEvent.killer.hasPermission("pitsim.killeffect")) {
			KillEffects.trigger(killEvent.killer, pitKiller.killEffect, killEvent.dead.getLocation());
		}

		if(pitDead.deathCry != null && killEvent.dead.hasPermission("pitsim.deathcry")) {
			DeathCrys.trigger(killEvent.dead, pitDead.deathCry, killEvent.dead.getLocation());
		}

		if(pitDead.bounty != 0 && killingNon == null && pitKiller != pitDead) {
			if(killEvent.isLuckyKill) pitDead.bounty = pitDead.bounty * 3;
			DecimalFormat formatter = new DecimalFormat("#,###.#");

			for(Player player : Bukkit.getOnlinePlayers()) {
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
				if(pitPlayer.disabledBounties) continue;

				String bounty1 = ChatColor.translateAlternateColorCodes('&',
						"&6&lBOUNTY CLAIMED!&7 %luckperms_prefix%" + killEvent.killer.getDisplayName() + "&7 killed ");
				String bounty2 = ChatColor.translateAlternateColorCodes('&', "%luckperms_prefix%" + killEvent.dead.getDisplayName()
						+ "&7 for &6&l" + formatter.format(pitDead.bounty)) + "g";
				String bounty3 = PlaceholderAPI.setPlaceholders(killEvent.killer, bounty1);
				String bounty4 = PlaceholderAPI.setPlaceholders(killEvent.dead, bounty2);
				player.sendMessage(bounty3 + bounty4);
			}
			LevelManager.addGold(killEvent.killer, pitDead.bounty);
			if(pitDead.megastreak.getClass() != Highlander.class) pitDead.bounty = 0;

			if(pitKiller.stats != null) pitKiller.stats.bountiesClaimed++;
		}

		int maxBounty = 20_000;
		if(Math.random() < 0.1 && killingNon == null && pitKiller.bounty < maxBounty) {

			int amount = (int) Math.floor(Math.random() * 5 + 1) * 200;
			if(pitKiller.bounty + amount > maxBounty) {
				amount = maxBounty - pitKiller.bounty;
				pitKiller.bounty = maxBounty;
			}  else {
				pitKiller.bounty += amount;
			}
			String message = "&6&lBOUNTY!&7 bump &6&l" + amount + "g&7 on %luckperms_prefix%" + killEvent.killer.getDisplayName() +
					"&7 for high streak";
			if(!pitKiller.disabledBounties) AOutput.send(killEvent.killer, PlaceholderAPI.setPlaceholders(killEvent.killer, message));
			Sounds.BOUNTY.play(killEvent.killer);
		}
	}

	@EventHandler
	public void onIncrement(IncrementKillsEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.player);
		if(event.currentAmount < pitPlayer.megastreak.getRequiredKills() && event.newAmount >= pitPlayer.megastreak.getRequiredKills() && pitPlayer.megastreak.getClass() != NoMegastreak.class) pitPlayer.megastreak.proc();
		pitPlayer.megastreak.kill();
	}

	public static List<UUID> pantsSwapCooldown = new ArrayList<>();
	@EventHandler
	public static void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(Misc.isAirOrNull(player.getItemInHand())) return;

		int firstArrow = -1; boolean multipleStacks = false; boolean hasSpace = false;
		if(player.getItemInHand().getType() == Material.BOW) {

			NBTItem nbtItem = new NBTItem(player.getItemInHand());
			if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && !player.getItemInHand().getItemMeta().hasEnchant(Enchantment.WATER_WORKER)) {
				ItemStack modified = player.getItemInHand();
				modified.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
				ItemMeta itemMeta = modified.getItemMeta();
				itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				modified.setItemMeta(itemMeta);
				player.setItemInHand(modified);
			}

			for(int i = 0; i < 36; i++) {
				ItemStack itemStack = player.getInventory().getItem(i);
				if(Misc.isAirOrNull(itemStack)) {
					hasSpace = true;
					continue;
				}
				if(itemStack.getType() != Material.ARROW) continue;
				if(firstArrow == -1) firstArrow = i; else {
					multipleStacks = true;
					break;
				}
			}
			if(!multipleStacks) {
				if(firstArrow == -1) {
					if(hasSpace) {
						player.getInventory().addItem(new ItemStack(Material.ARROW, 32));
					} else {
						AOutput.error(player, "Please make room in your inventory for arrows");
					}
				} else {
					player.getInventory().setItem(firstArrow, new ItemStack(Material.ARROW, 32));
				}
			}
		}

		if(player.getItemInHand().getType().toString().contains("LEGGINGS")){
			if(Misc.isAirOrNull(player.getInventory().getLeggings())) return;

			if(pantsSwapCooldown.contains(player.getUniqueId())) {

				Sounds.NO.play(player);
				return;
			}

			ItemStack held = player.getItemInHand();
			player.setItemInHand(player.getInventory().getLeggings());
			player.getInventory().setLeggings(held);

			pantsSwapCooldown.add(player.getUniqueId());
			new BukkitRunnable() {
				@Override
				public void run() {
					pantsSwapCooldown.remove(player.getUniqueId());
				}
			}.runTaskLater(PitSim.INSTANCE, 40L);
			Sounds.ARMOR_SWAP.play(player);
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				event.getPlayer().teleport(MapManager.getPlayerSpawn());
			}
		}.runTaskLater(PitSim.INSTANCE, 10L);

	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if(event.getPlayer().getLocation().getY() < 20 && event.getPlayer().getLocation().getWorld().getName().equals("pitsim"))  {
			DamageManager.death(event.getPlayer());
		}
	}

	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {

		Non defendingNon = NonManager.getNon(attackEvent.defender);
//		Arch chest
		if(defendingNon == null) {
			attackEvent.multiplier.add(0.85);
		} else {
			if(defendingNon.traits.contains(NonTrait.IRON_STREAKER)) attackEvent.multiplier.add(0.6);
		}

//		ItemStack itemStack = attackEvent.attacker.getItemInHand();
//		if(itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasEnchant(Enchantment.DAMAGE_ALL)
//				&& itemStack.getItemMeta().getEnchantLevel(Enchantment.DAMAGE_ALL) == 1) {
//			itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
//			attackEvent.attacker.setItemInHand(itemStack);
//		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		if(PitEventManager.majorEvent) FeatherBoardAPI.showScoreboard(event.getPlayer(), "event");
		else {
			FeatherBoardAPI.resetDefaultScoreboard(event.getPlayer());
			FeatherBoardAPI.showScoreboard(event.getPlayer(), "default");
		}

//		if(!bossBars.containsKey(event.getPlayer())) {
//			BossBarManager bm = new BossBarManager();
//			Audience audiences = PitSim.INSTANCE.adventure().player(event.getPlayer());
//			bossBars.put(event.getPlayer(), bm);
//		}

//		if(!player.isOp()) {
//			BypassManager.bypassAll.add(player);
//			Misc.sendTitle(player, ChatColor.translateAlternateColorCodes('&', "&c&lSYNCING WORLD"), 200);
//			new BukkitRunnable() {
//				int count = 0;
//				@Override
//				public void run() {
//					if((count != 0 && !player.isOnline()) || count++ >= 80) {
//						cancel();
//						BypassManager.bypassAll.remove(player);
//						return;
//					}
//
//					Location spawnLoc = new Location(Bukkit.getWorld("pit"), -108.5, 86, 194.5, 45, 0);
//					player.teleport(spawnLoc);
//				}
//			}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
//		}

		new BukkitRunnable() {
			@Override
			public void run() {

				if(!player.isOp() && !player.getName().equals("Fishduper")) {

					int itemsRemoved = 0;
					for(int i = 0; i < 36; i++) {

						ItemStack itemStack = player.getInventory().getItem(i);
						if(EnchantManager.isIllegalItem(itemStack)) {
							player.getInventory().setItem(i, new ItemStack(Material.AIR));
							itemsRemoved++;
						}
					}
					if(EnchantManager.isIllegalItem(player.getEquipment().getLeggings())) {
						player.getEquipment().setLeggings(new ItemStack(Material.AIR));
						itemsRemoved++;
					}
					if(itemsRemoved != 0) AOutput.error(player, "&c" + itemsRemoved + " &7illegal item" +
							(itemsRemoved == 1 ? " was" : "s were") + " removed from your inventory");
				}

				pitPlayer.updateMaxHealth();
				player.setHealth(player.getMaxHealth());
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}

	@EventHandler
	public void onJoin(PlayerSpawnLocationEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		Location spawnLoc = MapManager.getPlayerSpawn();
		player.teleport(spawnLoc);

		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getServer().dispatchCommand(player, "spawn");

				String message = "%luckperms_prefix%";
				if(pitPlayer.megastreak.isOnMega()) {
					pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(player, message);
				} else {
					pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(player, message);
				}
			}
		}.runTaskLater(PitSim.INSTANCE,  10L);
	}

	public static void removeIllegalItems(Player player) {
			int itemsRemoved = 0;
			for(int i = 0; i < 36; i++) {

				ItemStack itemStack = player.getInventory().getItem(i);
				if(EnchantManager.isIllegalItem(itemStack)) {
					player.getInventory().setItem(i, new ItemStack(Material.AIR));
					itemsRemoved++;
				}
			}
			if(EnchantManager.isIllegalItem(player.getEquipment().getLeggings())) {
				player.getEquipment().setLeggings(new ItemStack(Material.AIR));
				itemsRemoved++;
			}
//			if(itemsRemoved != 0) AOutput.error(player, "&c" + itemsRemoved + " &7illegal item" +
//					(itemsRemoved == 1 ? " was" : "s were") + " removed from your inventory");

	}

	@EventHandler
	public void onCraft(InventoryClickEvent event) {
		if(event.getSlot() == 80 || event.getSlot() == 81 || event.getSlot() == 82 || event.getSlot() == 83) event.setCancelled(true);
	}

	@EventHandler
	public void onJoin(AsyncPlayerPreLoginEvent event) {
		Player player = Bukkit.getServer().getPlayerExact(event.getName());
		if(player == null) return;
		if(player.isOnline()) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You are already online! \nIf you believe this is an error, try re-logging in a few seconds.");
		}
	}

	@EventHandler
	public static void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if(!player.isOp()) return;
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.dispatchCommand(player, "buzz exempt");
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}

	public static List<Player> toggledPlayers = new ArrayList<>();

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if(!event.getPlayer().isOp()) return;
		if(toggledPlayers.contains(event.getPlayer())) return;
		event.setCancelled(true);
		AOutput.error(event.getPlayer(), "&CBlock breaking disabled, run /pitsim bypass to toggle");
	}

	@EventHandler
	public void onBreak(BlockPlaceEvent event) {
		if(!event.getPlayer().isOp()) return;
		if(toggledPlayers.contains(event.getPlayer())) return;
		event.setCancelled(true);
		AOutput.error(event.getPlayer(), "&CBlock placing disabled, run /pitsim bypass to toggle");
	}
}
