package dev.kyro.pitsim.controllers;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.codingforcookies.armorequip.ArmorType;
import de.myzelyam.api.vanish.VanishAPI;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.data.APlayer;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.arcticguilds.controllers.BuffManager;
import dev.kyro.arcticguilds.controllers.GuildManager;
import dev.kyro.arcticguilds.controllers.objects.Guild;
import dev.kyro.arcticguilds.controllers.objects.GuildBuff;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.brewing.ingredients.MagmaCream;
import dev.kyro.pitsim.brewing.objects.PotionEffect;
import dev.kyro.pitsim.commands.FPSCommand;
import dev.kyro.pitsim.controllers.objects.*;
import dev.kyro.pitsim.enums.*;
import dev.kyro.pitsim.events.*;
import dev.kyro.pitsim.megastreaks.Highlander;
import dev.kyro.pitsim.megastreaks.NoMegastreak;
import dev.kyro.pitsim.megastreaks.RNGesus;
import dev.kyro.pitsim.misc.DeathCrys;
import dev.kyro.pitsim.misc.KillEffects;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
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
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.text.DecimalFormat;
import java.util.*;

//import net.kyori.adventure.audience.Audience;

public class PlayerManager implements Listener {
	//	public static Map<Player, BossBarManager> bossBars = new HashMap<>();

	static {
			new BukkitRunnable() {
			@Override
			public void run() {
				for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					if(!MapManager.inDarkzone(pitPlayer.player)) continue;

					if(onlinePlayer.getLocation().getY() >= 85) onlinePlayer.spigot().playEffect(onlinePlayer.getLocation(), Effect.PORTAL, 0, 0, 10, 10, 10, 1, 128, 100);

					double reduction = 0.0;

					for (Map.Entry<PitEnchant, Integer> entry : EnchantManager.getEnchantsOnPlayer(pitPlayer.player).entrySet()) {
						if(!entry.getKey().tainted || entry.getKey().applyType != ApplyType.CHESTPLATES) continue;
						reduction += (0.8 - (0.2 * entry.getValue()));
					}

					double amount = 0.5;
					PotionEffect effect = PotionManager.getEffect(pitPlayer.player, MagmaCream.INSTANCE);
					if(effect != null) amount += (Double) effect.potionType.getPotency(effect.potency);
					amount *= (1 - reduction);

					if(pitPlayer.mana + amount > pitPlayer.getMaxMana()) {
						pitPlayer.updateXPBar();
						continue;
					}
					pitPlayer.mana += amount;
					pitPlayer.updateXPBar();
				}
			}
		}.runTaskTimerAsynchronously(PitSim.INSTANCE, 0L, 1L);

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Non non : NonManager.nons)
					if(non.non != null) ((CraftPlayer) non.non).getHandle().getDataWatcher().watch(9, (byte) 0);
				for(Player onlinePlayer : Bukkit.getOnlinePlayers())
					((CraftPlayer) onlinePlayer).getHandle().getDataWatcher().watch(9, (byte) 0);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if(AFKManager.AFKPlayers.contains(onlinePlayer) || Math.random() > (1.0 / 3.0)) continue;

					Guild guild = GuildManager.getGuild(onlinePlayer);
					GuildBuff renownBuff = BuffManager.getBuff("renown");
					double buff = 0;
					if(guild != null) {
						for(Map.Entry<GuildBuff.SubBuff, Double> entry : renownBuff.getBuffs(guild.getLevel(renownBuff)).entrySet()) {
							if(entry.getKey().refName.equals("renown")) buff = entry.getValue();
						}
					}
					if(Math.random() * 2 > 1 + buff / 100.0) continue;

					PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					pitPlayer.renown++;
					APlayer aPlayer = APlayerData.getPlayerData(onlinePlayer);
					aPlayer.playerData.set("renown", pitPlayer.renown);
					aPlayer.save();
					AOutput.send(onlinePlayer, "&7You have been given &e1 renown &7for being active");
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, Misc.getRunnableOffset(5), 20 * 60 * 5);

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(!player.hasPermission("group.eternal") || !MapManager.currentMap.lobbies.contains(player.getWorld()) || VanishAPI.isInvisible(player))
						continue;
					if(SpawnManager.isInSpawn(player.getLocation())) continue;
					List<Player> nearbyNons = new ArrayList<>();
					for(Entity nearbyEntity : player.getNearbyEntities(4, 4, 4)) {
						if(nearbyEntity.getWorld() == Bukkit.getWorld("tutorial")) continue;
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
		}.runTaskTimer(PitSim.INSTANCE, 0L, 12L);

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					removeIllegalItems(onlinePlayer);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, Misc.getRunnableOffset(1), 60 * 20);
	}

	public static boolean isRealPlayerTemp(Player player) {
		if(player == null) return false;
		return Bukkit.getOnlinePlayers().contains(player);
	}

	public static void sendItemBreakMessage(Player player, ItemStack itemStack) {
		if(Misc.isAirOrNull(itemStack)) return;

		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), 0);
		EnchantManager.setItemLore(nbtItem.getItem(), player);

		TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cRIP!&7 Your "));
		message.addExtra(Misc.createItemHover(nbtItem.getItem()));
		message.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7 broke")));

		player.sendMessage(message);
	}

	public static void sendLivesLostMessage(Player player, int livesLost) {
		if(livesLost == 0) return;
		AOutput.error(player, "&c&lRIP!&7 You lost lives on &f" + livesLost + " &7item" + (livesLost == 1 ? "" : "s"));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onClick2(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Player player = event.getPlayer();
		if(!MapManager.inDarkzone(player)) return;
		ItemStack itemStack = player.getItemInHand();
		if(Misc.isAirOrNull(itemStack)) return;
		MysticType mysticType = MysticType.getMysticType(itemStack);
		if(mysticType != MysticType.TAINTED_SCYTHE) return;
		if(SpawnManager.isInDarkzoneSpawn(player.getLocation())) return;

		PitPlayerAttemptAbilityEvent newEvent = new PitPlayerAttemptAbilityEvent(player);
		Bukkit.getPluginManager().callEvent(newEvent);
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.updateXPBar();
	}

	@EventHandler
	public void onItemCraft(CraftItemEvent event) {
		Player player = (Player) event.getWhoClicked();
		event.setCancelled(true);
		player.updateInventory();
		AOutput.error(player, "You are not allowed to craft items");
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if(player.isOp()) return;
		if(ChatColor.stripColor(event.getMessage()).toLowerCase().startsWith("/trade")) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			if(pitPlayer.level < 100) {
				event.setCancelled(true);
				AOutput.error(player, "&c&lNOPE! &7You cannot trade until you are level 100");
			}
		}
		if(ChatColor.stripColor(event.getMessage()).toLowerCase().startsWith("/invsee")) {
			event.setCancelled(true);
			AOutput.send(player, "&c&lOUTDATED! &7Please use /view <player> instead");
		}
	}

	@EventHandler
	public void onCommand2(PlayerCommandPreprocessEvent event) {
		if(event.getPlayer().isOp()) return;
	}

	@EventHandler
	public void onKillForRank(KillEvent killEvent) {
		double multiplier = 1;
		if(killEvent.killer.hasPermission("group.nitro")) {
			multiplier += 0.1;
		}

		if(killEvent.killer.hasPermission("group.eternal")) {
			multiplier += 0.30;
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
		if(!killEvent.deadIsPlayer || !killEvent.killerIsPlayer) return;

		PitPlayer pitKiller = PitPlayer.getPitPlayer(killEvent.killerPlayer);
		PitPlayer pitDead = PitPlayer.getPitPlayer(killEvent.deadPlayer);
		Non killingNon = NonManager.getNon(killEvent.killer);
		Non deadNon = NonManager.getNon(killEvent.dead);

		if(pitKiller.killEffect != null && killEvent.killer.hasPermission("pitsim.killeffect")) {
			KillEffects.trigger(killEvent.killerPlayer, pitKiller.killEffect, killEvent.dead.getLocation());
		}

		if(pitDead.deathCry != null && killEvent.dead.hasPermission("pitsim.deathcry")) {
			DeathCrys.trigger(killEvent.deadPlayer, pitDead.deathCry, killEvent.dead.getLocation());
		}

		if(pitDead.bounty != 0 && killingNon == null && pitKiller != pitDead) {
			if(killEvent.isLuckyKill) pitDead.bounty = pitDead.bounty * 3;
			DecimalFormat formatter = new DecimalFormat("#,###.#");

			for(Player player : Bukkit.getOnlinePlayers()) {
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
				if(pitPlayer.bountiesDisabled) continue;

				String bounty1 = ChatColor.translateAlternateColorCodes('&',
						"&6&lBOUNTY CLAIMED!&7 %luckperms_prefix%" + killEvent.killerPlayer.getDisplayName() + "&7 killed ");
				String bounty2 = ChatColor.translateAlternateColorCodes('&', "%luckperms_prefix%" + killEvent.deadPlayer.getDisplayName()
						+ "&7 for &6&l" + formatter.format(pitDead.bounty)) + "g";
				String bounty3 = PlaceholderAPI.setPlaceholders(killEvent.killerPlayer, bounty1);
				String bounty4 = PlaceholderAPI.setPlaceholders(killEvent.deadPlayer, bounty2);
				player.sendMessage(bounty3 + bounty4);
			}
			LevelManager.addGold(killEvent.killerPlayer, pitDead.bounty);
			if(pitDead.megastreak.getClass() != Highlander.class) pitDead.bounty = 0;

			if(pitKiller.stats != null) pitKiller.stats.bountiesClaimed++;
		}

		int maxBounty = 20_000;
		if(Math.random() < 0.1 && killingNon == null && pitKiller.bounty < maxBounty) {

			int amount = (int) Math.floor(Math.random() * 5 + 1) * 200;
			if(pitKiller.bounty + amount > maxBounty) {
				amount = maxBounty - pitKiller.bounty;
				pitKiller.bounty = maxBounty;
			} else {
				pitKiller.bounty += amount;
			}
			String message = "&6&lBOUNTY!&7 bump &6&l" + amount + "g&7 on %luckperms_prefix%" + killEvent.killerPlayer.getDisplayName() +
					"&7 for high streak";
			if(!pitKiller.bountiesDisabled)
				AOutput.send(killEvent.killer, PlaceholderAPI.setPlaceholders(killEvent.killerPlayer, message));
			Sounds.BOUNTY.play(killEvent.killer);
		}
	}

	@EventHandler
	public void onIncrement(IncrementKillsEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.player);
		int kills = event.kills;
		Megastreak megastreak = pitPlayer.megastreak;
		if(kills == megastreak.getRequiredKills() && megastreak.getClass() != NoMegastreak.class) megastreak.proc();
		pitPlayer.megastreak.kill();
	}

	public static List<UUID> pantsSwapCooldown = new ArrayList<>();
	public static List<UUID> helmetSwapCooldown = new ArrayList<>();
	public static List<UUID> chestplateSwapCooldown = new ArrayList<>();

	@EventHandler
	public static void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(Misc.isAirOrNull(player.getItemInHand())) return;

		int firstArrow = -1;
		boolean multipleStacks = false;
		boolean hasSpace = false;
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
				if(firstArrow == -1) firstArrow = i;
				else {
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

		if(player.getItemInHand().getType().toString().contains("LEGGINGS")) {
			if(Misc.isAirOrNull(player.getInventory().getLeggings())) return;

			if(pantsSwapCooldown.contains(player.getUniqueId())) {

				Sounds.NO.play(player);
				return;
			}

			ItemStack held = player.getItemInHand();
			ArmorEquipEvent equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.HOTBAR_SWAP, ArmorType.LEGGINGS, player.getInventory().getLeggings(), held);
			Bukkit.getPluginManager().callEvent(equipEvent);

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

		if(player.getItemInHand().getType().toString().contains("HELMET")) {
			if(player.isSneaking()) return;
			if(Misc.isAirOrNull(player.getInventory().getHelmet())) return;

			if(GoldenHelmet.abilities.get(event.getPlayer()) != null) {
				GoldenHelmet.deactivate(event.getPlayer());
			}
			GoldenHelmet.toggledPlayers.remove(event.getPlayer());
			GoldenHelmet.abilities.remove(event.getPlayer());

			if(helmetSwapCooldown.contains(player.getUniqueId())) {

				Sounds.NO.play(player);
				return;
			}

			ItemStack held = player.getItemInHand();
			ArmorEquipEvent equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.HOTBAR_SWAP, ArmorType.HELMET, player.getInventory().getHelmet(), held);
			Bukkit.getPluginManager().callEvent(equipEvent);

			player.setItemInHand(player.getInventory().getHelmet());
			player.getInventory().setHelmet(held);

			helmetSwapCooldown.add(player.getUniqueId());
			new BukkitRunnable() {
				@Override
				public void run() {
					helmetSwapCooldown.remove(player.getUniqueId());
				}
			}.runTaskLater(PitSim.INSTANCE, 40L);
			Sounds.ARMOR_SWAP.play(player);
		}

		if(player.getItemInHand().getType().toString().contains("CHESTPLATE")) {
			if(Misc.isAirOrNull(player.getInventory().getChestplate())) return;

			if(chestplateSwapCooldown.contains(player.getUniqueId())) {

				Sounds.NO.play(player);
				return;
			}

			ItemStack held = player.getItemInHand();
			ArmorEquipEvent equipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.HOTBAR_SWAP, ArmorType.CHESTPLATE, player.getInventory().getChestplate(), held);
			Bukkit.getPluginManager().callEvent(equipEvent);

			player.setItemInHand(player.getInventory().getChestplate());
			player.getInventory().setChestplate(held);

			chestplateSwapCooldown.add(player.getUniqueId());
			new BukkitRunnable() {
				@Override
				public void run() {
					chestplateSwapCooldown.remove(player.getUniqueId());
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
				event.getPlayer().teleport(MapManager.currentMap.getSpawn(event.getPlayer().getWorld()));
			}
		}.runTaskLater(PitSim.INSTANCE, 10L);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if(event.getPlayer().getLocation().getY() < 10 && event.getPlayer().getWorld() == Bukkit.getWorld("tutorial"))
			DamageManager.death(event.getPlayer());
		else if(event.getPlayer().getLocation().getY() < 10 && MapManager.currentMap.lobbies.contains(event.getPlayer().getWorld())) {
			DamageManager.death(event.getPlayer());
		} else if(event.getPlayer().getLocation().getY() < 10) DamageManager.death(event.getPlayer());
	}

	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {

		Non defendingNon = NonManager.getNon(attackEvent.defender);
//		Arch chest archangel chestplate
		if(defendingNon == null && attackEvent.defenderIsPlayer) {
			attackEvent.multipliers.add(0.8);
		} else if(attackEvent.defenderIsPlayer) {
//			Non defence
			if(defendingNon.traits.contains(NonTrait.IRON_STREAKER)) attackEvent.multipliers.add(0.8);
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

		FeatherBoardAPI.resetDefaultScoreboard(event.getPlayer());
		FeatherBoardAPI.showScoreboard(event.getPlayer(), "default");

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
				player.setGameMode(GameMode.SURVIVAL);

				pitPlayer.updateMaxHealth();
				player.setHealth(player.getMaxHealth());
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}

	public static void removeIllegalItems(Player player) {
		if(!player.isOp()) {
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
			if(itemsRemoved != 0) {
				AOutput.error(player, "&c" + itemsRemoved + " &7illegal item" +
						(itemsRemoved == 1 ? " was" : "s were") + " removed from your inventory");
				player.updateInventory();
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerSpawnLocationEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		Location spawnLoc = MapManager.currentMap.getSpawn(MapManager.currentMap.firstLobby);

		if(player.isOp()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Bukkit.dispatchCommand(player, "buzz exempt");
				}
			}.runTaskLater(PitSim.INSTANCE, 1L);
		}

//		Misc.applyPotionEffect(player, PotionEffectType.NIGHT_VISION, 2000000, 2, false, false);

		if(player.hasPermission("pitsim.autofps")) {
			FPSCommand.fpsPlayers.add(player);
			for(Non non : NonManager.nons) player.hidePlayer(non.non);

			new BukkitRunnable() {
				@Override
				public void run() {
					for(Non non : NonManager.nons) player.hidePlayer(non.non);
					player.teleport(MapManager.playerSnow);
				}
			}.runTaskLater(PitSim.INSTANCE, 1L);

			new BukkitRunnable() {
				@Override
				public void run() {
					player.teleport(spawnLoc);
				}
			}.runTaskLater(PitSim.INSTANCE, 60L);
			return;
		}

		player.teleport(spawnLoc);
		new BukkitRunnable() {
			@Override
			public void run() {
				player.teleport(spawnLoc);

				String message = "%luckperms_prefix%";
				if(pitPlayer.megastreak.isOnMega()) {
					pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(player, message);
				} else {
					pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(player, message);
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 5L);

		new BukkitRunnable() {
			@Override
			public void run() {
				for (AuctionItem auctionItem : AuctionManager.auctionItems) {
					if(!auctionItem.bidMap.containsKey(player.getUniqueId())) continue;

					if(auctionItem.getHighestBidder().equals(player.getUniqueId())) AOutput.send(player, "&5&lDARK AUCTION! &7You are currently holding the highest bid on " + auctionItem.item.itemName);
					else AOutput.send(player, "&5&lDARK AUCTION! &7Current bid on " + auctionItem.item.itemName + " &7is &f" + auctionItem.getHighestBid() + " Souls &7by &e" + Bukkit.getOfflinePlayer(auctionItem.getHighestBidder()).getName() + "&7.");
					Sounds.BOOSTER_REMIND.play(player);
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 10);

		APlayer aPlayer = APlayerData.getPlayerData(player);
		FileConfiguration playerData = aPlayer.playerData;

		if(playerData.contains("auctionreturn")) {
			String[] items = playerData.getString("auctionreturn").split(",");

			for (String item : items) {
				String[] data = item.split(":");

				if(Integer.parseInt(data[1]) == 0) {
					ItemStack itemStack = ItemType.getItemType(Integer.valueOf(data[0])).item;
					AUtil.giveItemSafely(player, itemStack, true);

					new BukkitRunnable() {
						@Override
						public void run() {
							AOutput.send(player, "&5&lDARK AUCTION! &7Received " + itemStack.getItemMeta().getDisplayName() + "&7.");
							Sounds.BOOSTER_REMIND.play(player);
						}
					}.runTaskLater(PitSim.INSTANCE, 10);
				} else {
					ItemStack itemStack = ItemType.getJewelItem(Integer.parseInt(data[0]), Integer.parseInt(data[1]));

					AUtil.giveItemSafely(player, itemStack, true);
					new BukkitRunnable() {
						@Override
						public void run() {
							AOutput.send(player, "&5&lDARK AUCTION! &7Received " + itemStack.getItemMeta().getDisplayName() + "&7.");
							Sounds.BOOSTER_REMIND.play(player);
						}
					}.runTaskLater(PitSim.INSTANCE, 10);
				}
			}

			pitPlayer.stats.auctionsWon++;

			playerData.set("auctionreturn", null);
			aPlayer.save();
		}

		if(playerData.contains("soulreturn")) {
			int souls = playerData.getInt("soulreturn");

			if(souls > 0) {
				PitPlayer.getPitPlayer(player).taintedSouls += souls;
				new BukkitRunnable() {
					@Override
					public void run() {
						AOutput.send(player, "&5&lDARK AUCTION! &7Received &f" + souls + " Tainted Souls&7.");
						Sounds.BOOSTER_REMIND.play(player);
					}
				}.runTaskLater(PitSim.INSTANCE, 10);
			}

			playerData.set("soulreturn", null);
			aPlayer.save();
		}

	}

	@EventHandler
	public void onCraft(InventoryClickEvent event) {
		if(event.getSlot() == 80 || event.getSlot() == 81 || event.getSlot() == 82 || event.getSlot() == 83)
			event.setCancelled(true);
	}

	@EventHandler
	public void onJoin(AsyncPlayerPreLoginEvent event) {
		Player player = Bukkit.getServer().getPlayerExact(event.getName());
		if(player == null) return;
		if(player.isOnline()) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You are already online! \nIf you believe this is an error, try re-logging in a few seconds.");
		}
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

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
		if(pitPlayer.megastreak.getClass() == RNGesus.class && RNGesus.isOnCooldown(event.getPlayer())) {
			pitPlayer.megastreak.stop();
			pitPlayer.megastreak = new NoMegastreak(pitPlayer);
		}
	}

	@EventHandler
	public void onDeath(KillEvent event) {
		if(!event.deadIsPlayer) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.deadPlayer);
		if(pitPlayer.megastreak.getClass() == RNGesus.class && RNGesus.isOnCooldown(event.deadPlayer)) {
			new BukkitRunnable() {
				@Override
				public void run() {
					pitPlayer.megastreak.stop();
					pitPlayer.megastreak = new NoMegastreak(pitPlayer);
					pitPlayer.fullSave();
				}
			}.runTaskLater(PitSim.INSTANCE, 1L);
		}
	}

	@EventHandler
	public void onOof(OofEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
		if(pitPlayer.megastreak.getClass() == RNGesus.class && RNGesus.isOnCooldown(event.getPlayer())) {
			new BukkitRunnable() {
				@Override
				public void run() {
					pitPlayer.megastreak.stop();
					pitPlayer.megastreak = new NoMegastreak(pitPlayer);
					pitPlayer.fullSave();
				}
			}.runTaskLater(PitSim.INSTANCE, 1L);
		}
	}
}
