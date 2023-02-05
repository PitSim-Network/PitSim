package dev.kyro.pitsim.controllers;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.codingforcookies.armorequip.ArmorType;
import de.myzelyam.api.vanish.VanishAPI;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.arcticguilds.BuffManager;
import dev.kyro.arcticguilds.GuildBuff;
import dev.kyro.arcticguilds.GuildData;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.battlepass.quests.EarnRenownQuest;
import dev.kyro.pitsim.battlepass.quests.WinAuctionsQuest;
import dev.kyro.pitsim.controllers.objects.*;
import dev.kyro.pitsim.enums.*;
import dev.kyro.pitsim.events.*;
import dev.kyro.pitsim.inventories.view.ViewGUI;
import dev.kyro.pitsim.megastreaks.Highlander;
import dev.kyro.pitsim.megastreaks.NoMegastreak;
import dev.kyro.pitsim.megastreaks.RNGesus;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.pitmaps.XmasMap;
import dev.kyro.pitsim.upgrades.TheWay;
import dev.kyro.pitsim.upgrades.UberIncrease;
import me.clip.placeholderapi.PlaceholderAPI;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.PermissionNode;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class PlayerManager implements Listener {
	private static final List<UUID> realPlayers = new ArrayList<>();

	public static void addRealPlayer(UUID uuid) {
		realPlayers.add(uuid);
	}

	public static boolean isRealPlayer(Player player) {
		if(player == null) return false;
		return realPlayers.contains(player.getUniqueId());
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					if(!MapManager.inDarkzone(pitPlayer.player)) continue;

					if(onlinePlayer.getLocation().getY() >= 85)
						onlinePlayer.spigot().playEffect(onlinePlayer.getLocation(), Effect.PORTAL, 0, 0, 10, 10, 10, 1, 128, 100);

					double reduction = 0.0;

					for(Map.Entry<PitEnchant, Integer> entry : EnchantManager.getEnchantsOnPlayer(pitPlayer.player).entrySet()) {
						if(!entry.getKey().isTainted || entry.getKey().applyType != ApplyType.CHESTPLATES) continue;
						reduction += (0.8 - (0.2 * entry.getValue()));
					}

					double amount = 0.5;
//					PotionEffect effect = PotionManager.getEffect(pitPlayer.player, MagmaCream.INSTANCE);
//					if(effect != null) amount += (Double) effect.potionType.getPotency(effect.potency);
					amount *= (1 - reduction);

					if(pitPlayer.mana + amount > pitPlayer.getMaxMana()) continue;
					pitPlayer.mana += amount;
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

					GuildData guild = GuildData.getGuildData(onlinePlayer);
					GuildBuff renownBuff = BuffManager.getBuff("renown");
					double buff = 0;
					if(guild != null) {
						for(Map.Entry<GuildBuff.SubBuff, Double> entry : renownBuff.getBuffs(guild.getBuffLevel(renownBuff)).entrySet()) {
							if(entry.getKey().refName.equals("renown")) buff = entry.getValue();
						}
					}
					if(Math.random() * 2 > 1 + buff / 100.0) continue;

					PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					pitPlayer.renown++;
					EarnRenownQuest.INSTANCE.gainRenown(pitPlayer, 1);
					AOutput.send(onlinePlayer, "&7You have been given &e1 renown &7for being active");
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, Misc.getRunnableOffset(5), 20 * 60 * 5);

		if(PitSim.getStatus().isOverworld()) {
			new BukkitRunnable() {
				@Override
				public void run() {
						for(Player player : Bukkit.getOnlinePlayers()) {
							if(!player.hasPermission("group.eternal") || MapManager.currentMap.world != player.getWorld() || VanishAPI.isInvisible(player))
								continue;
							if(SpawnManager.isInSpawn(player.getLocation())) continue;
							List<Player> nearbyNons = new ArrayList<>();
							for(Entity nearbyEntity : player.getNearbyEntities(4, 4, 4)) {
	//						if(nearbyEntity.getWorld() == Bukkit.getWorld("tutorial")) continue;
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
			}.runTaskTimer(PitSim.INSTANCE, 0L, 18L);
		}
	}

	public static boolean isStaff(UUID uuid) {
		User user;
		try {
			user = PitSim.LUCKPERMS.getUserManager().loadUser(uuid).get();
		} catch(InterruptedException | ExecutionException exception) {
			exception.printStackTrace();
			return false;
		}
		Group group = PitSim.LUCKPERMS.getGroupManager().getGroup(user.getPrimaryGroup());
		return group.data().contains(PermissionNode.builder("pitsim.staff").build(), NodeEqualityPredicate.EXACT).asBoolean();
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		ItemStack itemStack = event.getItem().getItemStack();
		if(Misc.isAirOrNull(itemStack) || itemStack.getType() != Material.ARROW) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void giveMoonCap(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getKillerPlayer())) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.getKillerPlayer());
		killEvent.xpCap += pitPlayer.moonBonus;
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

	public Map<UUID, Long> viewShiftCooldown = new HashMap<>();

	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent event) {
		Player player = event.getPlayer();
		if(!(event.getRightClicked() instanceof Player)) return;
		Player target = (Player) event.getRightClicked();
		if(!player.isSneaking() || !SpawnManager.isInSpawn(player.getLocation()) || !SpawnManager.isInSpawn(target.getLocation()))
			return;
		if(!PlayerManager.isRealPlayer(target)) return;
		if(viewShiftCooldown.getOrDefault(player.getUniqueId(), 0L) + 500 > System.currentTimeMillis()) return;
		viewShiftCooldown.put(player.getUniqueId(), System.currentTimeMillis());
		new ViewGUI(player, target).open();
	}

	@EventHandler
	public void onAnvil(PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.ANVIL) {
			event.setCancelled(true);
		}
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
	public void onPickup(PlayerPickupItemEvent event) {
		ItemStack itemStack = event.getItem().getItemStack();
		if(Misc.isAirOrNull(itemStack)) return;
		NBTItem nbtItem = new NBTItem(itemStack);
		if(!nbtItem.hasKey(NBTTag.CANNOT_PICKUP.getRef())) return;
		event.setCancelled(true);
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
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.lastCommand = System.currentTimeMillis();

		if(player.isOp()) return;
		if(ChatColor.stripColor(event.getMessage()).toLowerCase().startsWith("/trade")) {
			int levelRequired = 100 - TheWay.INSTANCE.getLevelReduction(pitPlayer.player);
			if(pitPlayer.level < levelRequired) {
				event.setCancelled(true);
				AOutput.error(player, "&c&lERROR!&7 You cannot trade until you are level " + levelRequired);
			}
		}
		if(ChatColor.stripColor(event.getMessage()).toLowerCase().startsWith("/invsee")) {
			event.setCancelled(true);
			AOutput.send(player, "&c&lOUTDATED!&7 Please use /view <player> instead");
		}
	}

	@EventHandler
	public void onCommand2(PlayerCommandPreprocessEvent event) {
		if(event.getPlayer().isOp()) return;
	}

	@EventHandler
	public void onKillForRank(KillEvent killEvent) {

		if(killEvent.isDeadPlayer()) {
			XmasMap.removeFromRadio(killEvent.getDeadPlayer());
			new BukkitRunnable() {
				@Override
				public void run() {
					XmasMap.addToRadio(killEvent.getDeadPlayer());
				}
			}.runTaskLater(PitSim.INSTANCE, 20);
		}

		double multiplier = 1;
		if(killEvent.getKiller().hasPermission("group.nitro")) {
			multiplier += 0.1;
		}

		if(killEvent.getKiller().hasPermission("group.eternal")) {
			multiplier += 0.30;
		} else if(killEvent.getKiller().hasPermission("group.unthinkable")) {
			multiplier += 0.25;
		} else if(killEvent.getKiller().hasPermission("group.miraculous")) {
			multiplier += 0.20;
		} else if(killEvent.getKiller().hasPermission("group.extraordinary")) {
			multiplier += 0.15;
		} else if(killEvent.getKiller().hasPermission("group.overpowered")) {
			multiplier += 0.1;
		} else if(killEvent.getKiller().hasPermission("group.legendary")) {
			multiplier += 0.05;
		}
		killEvent.xpMultipliers.add(multiplier);
		killEvent.goldMultipliers.add(multiplier);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onKill(KillEvent killEvent) {
		if(!killEvent.isDeadPlayer() || !killEvent.isKillerPlayer()) return;

		PitPlayer pitKiller = killEvent.getKillerPitPlayer();
		PitPlayer pitDead = killEvent.getDeadPitPlayer();
		Non killingNon = NonManager.getNon(killEvent.getKiller());

		if(pitDead.bounty != 0 && killingNon == null && pitKiller != pitDead) {
			if(killEvent.isLuckyKill) pitDead.bounty = pitDead.bounty * 3;

			DecimalFormat formatter = new DecimalFormat("#,###.#");
			String bountyMessage = Misc.getBountyClaimedMessage(pitKiller, pitDead, "&6&l" + formatter.format(pitDead.bounty) + "g");
			for(Player player : Bukkit.getOnlinePlayers()) {
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
				if(pitPlayer.bountiesDisabled) continue;
				AOutput.send(player, bountyMessage);
			}
			LevelManager.addGold(killEvent.getKillerPlayer(), pitDead.bounty);
			if(!(pitDead.megastreak instanceof Highlander)) pitDead.bounty = 0;

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
			String message = "&6&lBOUNTY!&7 bump &6&l" + amount + "g&7 on %luckperms_prefix%" + killEvent.getKillerPlayer().getDisplayName() +
					"&7 for high streak";
			if(!pitKiller.bountiesDisabled)
				AOutput.send(killEvent.getKiller(), PlaceholderAPI.setPlaceholders(killEvent.getKillerPlayer(), message));
			Sounds.BOUNTY.play(killEvent.getKiller());
		}
	}

	@EventHandler
	public void onIncrement(IncrementKillsEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.player);
		int kills = event.kills;
		Megastreak megastreak = pitPlayer.megastreak;
		if(kills == megastreak.getRequiredKills() && !(megastreak instanceof NoMegastreak)) megastreak.proc();
		pitPlayer.megastreak.kill();
	}

	public static List<UUID> pantsSwapCooldown = new ArrayList<>();
	public static List<UUID> helmetSwapCooldown = new ArrayList<>();
	public static List<UUID> chestplateSwapCooldown = new ArrayList<>();

	@EventHandler(priority = EventPriority.HIGH)
	public static void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(Misc.isAirOrNull(player.getItemInHand())) return;

		int firstArrow = -1;
		boolean multipleStacks = false;
		boolean hasSpace = false;
		if(player.getItemInHand().getType() == Material.BOW) {

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

			if(HelmetManager.abilities.get(event.getPlayer()) != null) {
				HelmetManager.deactivate(event.getPlayer());
			}
			HelmetManager.toggledPlayers.remove(event.getPlayer());
			HelmetManager.abilities.remove(event.getPlayer());

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

			Block block = event.getClickedBlock();
			if(block != null && block.getType() == Material.ENCHANTMENT_TABLE) return;

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
				event.getPlayer().teleport(MapManager.currentMap.getSpawn());
			}
		}.runTaskLater(PitSim.INSTANCE, 10L);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if(event.getPlayer().getLocation().getY() < 10 && event.getPlayer().getWorld() == Bukkit.getWorld("tutorial"))
			DamageManager.death(event.getPlayer());
		else if(event.getPlayer().getLocation().getY() < 10 && MapManager.currentMap.world == event.getPlayer().getWorld()) {
			DamageManager.death(event.getPlayer());
		} else if(event.getPlayer().getLocation().getY() < 10) DamageManager.death(event.getPlayer());
	}

	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {

		Non defendingNon = NonManager.getNon(attackEvent.getDefender());
		if(PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer())) {
//			Arch chest archangel chestplate
			attackEvent.multipliers.add(0.8);
		} else if(defendingNon != null) {
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
		event.setJoinMessage(null);

		if(Misc.isKyro(player.getUniqueId()) && PitSim.anticheat instanceof GrimManager) {
			Bukkit.getServer().dispatchCommand(player, "grim alerts");
		}

		FeatherBoardAPI.resetDefaultScoreboard(player);
		if(MapManager.inDarkzone(player)) {
			FeatherBoardAPI.showScoreboard(player, "darkzone");
		} else {
			FeatherBoardAPI.showScoreboard(player, "default");
		}

		if((System.currentTimeMillis() / 1000L) - 60 * 60 * 20 > pitPlayer.uberReset) {
			pitPlayer.uberReset = 0;
			pitPlayer.dailyUbersLeft = 5 + UberIncrease.getUberIncrease(player);
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!player.isOnline()) return;
				player.setGameMode(GameMode.SURVIVAL);

				pitPlayer.updateMaxHealth();
				player.setHealth(player.getMaxHealth());
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}

	@EventHandler
	public void onJoin(PlayerSpawnLocationEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		Location spawnLoc = PitSim.getStatus() == PitSim.ServerStatus.DARKZONE ? MapManager.getDarkzoneSpawn() : MapManager.currentMap.getSpawn();
		if(LobbySwitchManager.joinedFromDarkzone.contains(player.getUniqueId())) spawnLoc = MapManager.currentMap.getFromDarkzoneSpawn();
		if(ProxyMessaging.joinTeleportMap.containsKey(player.getUniqueId())) {
			Player tpPlayer = Bukkit.getPlayer(ProxyMessaging.joinTeleportMap.get(player.getUniqueId()));
			if(tpPlayer.isOnline()) spawnLoc = tpPlayer.getLocation();

			new BukkitRunnable() {
				@Override
				public void run() {
					if(!tpPlayer.isOnline()) AOutput.error(player, "&cThe player you were trying to teleport to is no longer online.");
					else AOutput.send(player, "&aTeleporting to " + tpPlayer.getName() + "...");
				}
			}.runTaskLater(PitSim.INSTANCE, 10);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!player.isOnline()) return;
				if(!pitPlayer.musicDisabled && XmasMap.radio != null) {
					XmasMap.addToRadio(player);
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 20);

		player.teleport(spawnLoc);
		Location finalSpawnLoc = spawnLoc;
		new BukkitRunnable() {
			@Override
			public void run() {
				if(player.isOnline()) player.teleport(finalSpawnLoc);
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);

		Location finalSpawnLoc1 = spawnLoc;
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!player.isOnline()) return;
				player.teleport(finalSpawnLoc1);

				if(PitSim.getStatus() == PitSim.ServerStatus.DARKZONE) {
					player.setVelocity(new Vector(1.5, 1, 0).multiply(0.5));
					Misc.sendTitle(player, "&d&k||&5&lDarkzone&d&k||", 40);
					Misc.sendSubTitle(player, "", 40);
					AOutput.send(player, "&7You have been sent to the &d&k||&5&lDarkzone&d&k||&7.");

					if(!pitPlayer.darkzoneCutscene) {
//						CutsceneManager.play(player);
						return;
					}

				} else if(PitSim.getStatus() == PitSim.ServerStatus.OVERWORLD && LobbySwitchManager.joinedFromDarkzone.contains(player.getUniqueId()) &&
						!ProxyMessaging.joinTeleportMap.containsKey(player.getUniqueId())) {
					player.setVelocity(new Vector(1.5, 1, 0));
					Misc.sendTitle(player, "&a&lOverworld", 40);
					Misc.sendSubTitle(player, "", 40);
					AOutput.send(player, "&7You have been sent to the &a&lOverworld&7.");
				}

				ProxyMessaging.joinTeleportMap.remove(player.getUniqueId());

				String message = "%luckperms_prefix%";
				if(pitPlayer.megastreak.isOnMega()) {
					pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(player, message);
				} else {
					pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(player, message);
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 5L);

		if(PitSim.getStatus().isDarkzone()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for(AuctionItem auctionItem : AuctionManager.auctionItems) {
						if(!auctionItem.bidMap.containsKey(player.getUniqueId())) continue;

						if(auctionItem.getHighestBidder().equals(player.getUniqueId()))
							AOutput.send(player, "&5&lDARK AUCTION!&7 You are currently holding the highest bid on " + auctionItem.item.itemName);
						else
							AOutput.send(player, "&5&lDARK AUCTION!&7 Current bid on " + auctionItem.item.itemName + " &7is &f" + auctionItem.getHighestBid() + " Souls &7by &e" + Bukkit.getOfflinePlayer(auctionItem.getHighestBidder()).getName() + "&7.");
						Sounds.BOOSTER_REMIND.play(player);
					}
				}
			}.runTaskLater(PitSim.INSTANCE, 10);
		}

		if(pitPlayer.auctionReturn.size() > 0) {
			for(String item : pitPlayer.auctionReturn) {
				String[] data = item.split(":");

				ItemStack itemStack;
				if(Integer.parseInt(data[1]) == 0) itemStack = Objects.requireNonNull(ItemType.getItemType(Integer.parseInt(data[0]))).item;
				else itemStack = ItemType.getJewelItem(Integer.parseInt(data[0]), Integer.parseInt(data[1]));

				new BukkitRunnable() {
					@Override
					public void run() {
						AOutput.send(player, "&5&lDARK AUCTION!&7 Received " + itemStack.getItemMeta().getDisplayName() + "&7.");
						Sounds.BOOSTER_REMIND.play(player);
						AUtil.giveItemSafely(player, itemStack, true);
					}
				}.runTaskLater(PitSim.INSTANCE, 10);
			}

			pitPlayer.auctionReturn.clear();
			pitPlayer.stats.auctionsWon++;
			WinAuctionsQuest.INSTANCE.winAuction(pitPlayer);
		}

		if(pitPlayer.soulReturn > 0) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if(!player.isOnline()) return;

					int soulReturn = pitPlayer.soulReturn;
					pitPlayer.taintedSouls += soulReturn;
					pitPlayer.soulReturn = 0;

					AOutput.send(player, "&5&lDARK AUCTION! &7Received &f" + soulReturn + " Tainted Souls&7.");
					Sounds.BOOSTER_REMIND.play(player);
				}
			}.runTaskLater(PitSim.INSTANCE, 10);
		}
	}

	@EventHandler
	public void onCraft(InventoryClickEvent event) {
		if(event.getSlot() == 80 || event.getSlot() == 81 || event.getSlot() == 82 || event.getSlot() == 83)
			event.setCancelled(true);
	}

	@EventHandler
	public void onJoin(AsyncPlayerPreLoginEvent event) {
		UUID playerUUID = event.getUniqueId();
		if(!realPlayers.contains(playerUUID)) realPlayers.add(playerUUID);
		boolean success = PitPlayer.loadPitPlayer(playerUUID);
		if(FirestoreManager.FIRESTORE == null) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
					ChatColor.RED + "Server still starting up");
		} else if(!success) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
					ChatColor.RED + "Playerdata failed to load. Please open a support ticket: discord.pitsim.net");
		}

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
	public void onInteract(PlayerInteractEvent event) {
		if(!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
		if(event.getClickedBlock().getType() != Material.TRAP_DOOR) return;

		if(!event.getPlayer().isOp()) return;
		if(toggledPlayers.contains(event.getPlayer())) return;
		event.setCancelled(true);
		AOutput.error(event.getPlayer(), "&CBlock interactions disabled, run /pitsim bypass to toggle");
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		event.setQuitMessage(null);
		XmasMap.removeFromRadio(player);
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.megastreak.stop();
		if(pitPlayer.megastreak instanceof RNGesus && RNGesus.isOnCooldown(player)) {
			pitPlayer.megastreak = new NoMegastreak(pitPlayer);
		}
	}

	@EventHandler
	public void onOof(OofEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
		if(pitPlayer.megastreak instanceof RNGesus && RNGesus.isOnCooldown(event.getPlayer())) {
			new BukkitRunnable() {
				@Override
				public void run() {
					pitPlayer.megastreak.stop();
					pitPlayer.megastreak = new NoMegastreak(pitPlayer);
					pitPlayer.save(true, false);
				}
			}.runTaskLater(PitSim.INSTANCE, 1L);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ||
				event.getCause() == EntityDamageEvent.DamageCause.LAVA || event.getCause() == EntityDamageEvent.DamageCause.DROWNING ||
				event.getCause() == EntityDamageEvent.DamageCause.FALL) event.setCancelled(true);

//		TODO: Only do this if grim is running
		if(event.getCause() == EntityDamageEvent.DamageCause.WITHER) event.setCancelled(true);
	}
}
