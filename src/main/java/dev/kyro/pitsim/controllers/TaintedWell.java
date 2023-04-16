package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.DarkzoneBalancing;
import dev.kyro.pitsim.adarkzone.progression.ProgressionManager;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
import dev.kyro.pitsim.adarkzone.progression.skillbranches.SoulBranch;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.aitems.mystics.TaintedChestplate;
import dev.kyro.pitsim.aitems.mystics.TaintedScythe;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.HypixelSound;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.World;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TaintedWell implements Listener {
	public static Location wellLocation = new Location(Bukkit.getWorld("darkzone"), 186.0, 92.0, -106.0);
	public static ArmorStand wellStand;
	public static ArmorStand[] textStands = new ArmorStand[4];
	public static Map<Player, ArmorStand> removeStands;
	public static Map<Player, ArmorStand> enchantStands;
	public static Map<UUID, Integer> enchantCostStands;
	public static List<Player> enchantingPlayers;
	private static Map<Player, ItemStack> playerItems;
	public static Map<UUID, Integer> yawMap = new HashMap<>();
	public static Map<UUID, Double> velocityMap = new HashMap<>();

	public static final double MINIMUM_VELOCITY = 10;
	public static final double MAXIMUM_VELOCITY = 60;
	public static final double ACCELERATION = 4;
	public static final double DECELERATION = 2;

	static {
		removeStands = new HashMap<>();
		enchantStands = new HashMap<>();
		enchantCostStands = new HashMap<>();
		enchantingPlayers = new ArrayList<>();
		playerItems = new HashMap<>();


		new BukkitRunnable() {
			public void run() {
				if(!PitSim.getStatus().isDarkzone()) return;

				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if(onlinePlayer.getWorld() != wellLocation.getWorld()) continue;
					if(playerItems.containsKey(onlinePlayer)) continue;

					setText(onlinePlayer, "&5&lTainted Well&1", "&7Enchant Mystic Items found&1", "&7in the Darkzone here&1", "&eRight-Click with an Item!&1");
				}


				if(wellStand != null) {
					for(Entity entity : wellStand.getNearbyEntities(25.0, 25.0, 25.0)) {
						if(!(entity instanceof Player)) {
							continue;
						}
						Player player = (Player)entity;

						int yaw = yawMap.getOrDefault(player.getUniqueId(), 0);

						PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(wellStand), (byte)0, (byte)0, (byte)0, (byte) yaw, (byte)0, false);
						EntityPlayer nmsPlayer = ((CraftPlayer)entity).getHandle();
						nmsPlayer.playerConnection.sendPacket(packet);
						for(Map.Entry<Player, ArmorStand> entry : enchantStands.entrySet()) {
							if(player == entry.getKey()) {
								continue;
							}
							PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(entry.getValue()));
							nmsPlayer.playerConnection.sendPacket(destroyPacket);
						}
						for(Map.Entry<Player, ArmorStand> entry : removeStands.entrySet()) {
							if(player == entry.getKey()) {
								continue;
							}
							PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(entry.getValue()));
							nmsPlayer.playerConnection.sendPacket(destroyPacket);
						}

						double velocity = velocityMap.getOrDefault(player.getUniqueId(), MINIMUM_VELOCITY);

						if(enchantingPlayers.contains(player)) {
							velocity = Math.min(velocity + ACCELERATION, MAXIMUM_VELOCITY);
							player.playEffect(wellLocation.clone().add(0.0, 1.0, 0.0), Effect.ENDER_SIGNAL, 0);
						} else {
							velocity = Math.max(velocity - DECELERATION, MINIMUM_VELOCITY);
						}

						yaw += velocity;
						velocityMap.put(player.getUniqueId(), velocity);

						if(yaw >= 256) yaw = 0;
						yawMap.put(player.getUniqueId(), yaw);
					}
				}

			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 2L);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!PitSim.getStatus().isDarkzone()) return;
				for(Entity entity : wellStand.getNearbyEntities(25.0, 25.0, 25.0)) {
					if(!(entity instanceof Player)) {
						continue;
					}
					Player player = (Player) entity;
					if(!enchantingPlayers.contains(player) && !removeStands.containsKey(player))
						setText(player, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Tainted Well", ChatColor.GRAY + "Enchant Mystic Items found", ChatColor.GRAY + "in the Darkzone here", ChatColor.YELLOW + "Right-Click with an Item!");
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 100, 100);
	}

	public static void onStart() {
		if(wellLocation == null) return;
		if(wellLocation.getChunk() == null) return;
		wellLocation.getChunk().load();
		
		wellStand = wellLocation.getWorld().spawn(wellLocation.clone().add(0.5, 0.5, 0.5), ArmorStand.class);
		wellStand.setArms(true);
		wellStand.setVisible(false);
		wellStand.setGravity(false);

		for(int i = 0; i < 4; i++) {
			textStands[i] = wellLocation.getWorld().spawn(wellLocation.clone().add(0.5, 3.0 - (i * 0.3), 0.5), ArmorStand.class);
			textStands[i].setArms(true);
			textStands[i].setVisible(false);
			textStands[i].setCustomName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Tainted Well");
			textStands[i].setCustomNameVisible(true);
			textStands[i].setGravity(false);
			textStands[i].setMarker(true);
		}

		wellLocation.getBlock().setType(Material.ENCHANTMENT_TABLE);
	}

	public static void onStop() {
		wellStand.remove();
		for(ArmorStand textStand : textStands) {
			textStand.remove();
		}

		for(ArmorStand value : removeStands.values()) {
			value.remove();
		}

		for(ArmorStand value : enchantStands.values()) {
			value.remove();
		}

		for(Map.Entry<Player, ItemStack> entry : playerItems.entrySet()) {
			AUtil.giveItemSafely(entry.getKey(), entry.getValue());
		}
	}

	public static void placeItemInWell(Player player, ItemStack itemStack) {
		playerItems.put(player, itemStack);
		player.getInventory().remove(itemStack);
		
		PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(getStandID(wellStand), 0, CraftItemStack.asNMSCopy(itemStack));
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
		showButtons(player, getEnchantCost(getTier(itemStack), player));
	}

	public static void showButtons(Player player, int cost) {
		Location spawnLoc = wellLocation.clone().add(0.5, 0.0, 0.5);

		ArmorStand removeStand = wellLocation.getWorld().spawn(spawnLoc, ArmorStand.class);
		removeStand.setGravity(false);
		removeStand.setArms(true);
		removeStand.setVisible(false);
		removeStand.setCustomName(ChatColor.RED + "Remove Item");
		removeStand.setCustomNameVisible(true);
		removeStands.put(player, removeStand);
		
		ArmorStand enchantStand = wellLocation.getWorld().spawn(spawnLoc, ArmorStand.class);
		enchantStand.setGravity(false);
		enchantStand.setArms(true);
		enchantStand.setVisible(false);
		enchantStand.setCustomName(ChatColor.GREEN + "Enchant Item");
		enchantStand.setCustomNameVisible(true);
		enchantStands.put(player, enchantStand);

		PacketPlayOutEntityEquipment removePacket = new PacketPlayOutEntityEquipment(getStandID(removeStand), 4, CraftItemStack.asNMSCopy(new ItemStack(Material.REDSTONE_BLOCK)));
		PacketPlayOutEntityEquipment enchantPacket = new PacketPlayOutEntityEquipment(getStandID(enchantStand), 4, CraftItemStack.asNMSCopy(new ItemStack(new ItemStack(Material.EMERALD_BLOCK))));
		new BukkitRunnable() {
			public void run() {
				((CraftPlayer)player).getHandle().playerConnection.sendPacket(removePacket);
				((CraftPlayer)player).getHandle().playerConnection.sendPacket(enchantPacket);
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
		
		PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving((EntityLiving) ((CraftEntity)removeStand).getHandle());
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawn);
		
		PacketPlayOutSpawnEntityLiving enchantSpawn = new PacketPlayOutSpawnEntityLiving((EntityLiving)((CraftEntity)enchantStand).getHandle());
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(enchantSpawn);
		
		removeStand.teleport(removeStand.getLocation().clone().subtract(2.0, 0.0, 0.0));
		
		PacketPlayOutEntityTeleport tpPacket = new PacketPlayOutEntityTeleport(((CraftEntity)removeStand).getHandle());
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(tpPacket);
		
		enchantStand.teleport(enchantStand.getLocation().clone().add(2.0, 0.0, 0.0));
		
		PacketPlayOutEntityTeleport tpRemovePacket = new PacketPlayOutEntityTeleport(((CraftEntity)enchantStand).getHandle());
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(tpRemovePacket);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(cost == -1) return;
				World world = ((CraftWorld) (spawnLoc.getWorld())).getHandle();
				EntityArmorStand costStand = new EntityArmorStand(world, spawnLoc.getX() + 2, spawnLoc.getY() + 0.3, spawnLoc.getZ());
				costStand.setInvisible(true);
				costStand.setGravity(false);
				costStand.setCustomNameVisible(true);
				costStand.setBasePlate(true);

				if(enchantCostStands.containsKey(player.getUniqueId())) {
					PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(enchantCostStands.get(player.getUniqueId()));
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroy);
				}

				enchantCostStands.put(player.getUniqueId(), costStand.getId());

				PacketPlayOutSpawnEntityLiving costSpawn = new PacketPlayOutSpawnEntityLiving(costStand);
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(costSpawn);

				DataWatcher dataWatcher = costStand.getDataWatcher();
				String text = "&f" + cost + " Souls";
				dataWatcher.watch(2, (Object) ChatColor.translateAlternateColorCodes('&', text));
				PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(costStand.getId(), dataWatcher, true);
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(meta);
			}
		}.runTaskLater(PitSim.INSTANCE, 5);

		setText(player, "\u00A77", "\u00A77", "\u00A77", "\u00A77");
		setItemText(player);
	}

	public static void removeButtons(Player player) {
		ArmorStand removeStand = removeStands.get(player);
		ArmorStand enchantStand = enchantStands.get(player);

		if(enchantStand == null || removeStand == null) return;

		PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook tpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(removeStand), (byte)64, (byte)0, (byte)0, (byte)0, (byte)0, false);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(tpPacket);

		if(enchantCostStands.containsKey(player.getUniqueId())) {
			PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(enchantCostStands.get(player.getUniqueId()));
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroy);
		}
		enchantCostStands.remove(player.getUniqueId());

		new BukkitRunnable() {
			public void run() {
				removeStands.remove(player);
				removeStand.remove();
			}
		}.runTaskLater(PitSim.INSTANCE, 2L);

		PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook tpRemovePacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(enchantStand), (byte)(-64), (byte)0, (byte)0, (byte)0, (byte)0, false);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(tpRemovePacket);

		new BukkitRunnable() {
			public void run() {
				enchantStands.remove(player);
				enchantStand.remove();
			}
		}.runTaskLater(PitSim.INSTANCE, 2L);
	}

	public static void onButtonPush(Player player, boolean enchant) {
		int previousLives;
		Map<PitEnchant, Integer> previousEnchants;

		if(enchantingPlayers.contains(player)) return;
		
		if(!enchant) {
			setText(player, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Tainted Well", ChatColor.GRAY + "Enchant Mystic Items found", ChatColor.GRAY + "in the Darkzone here", ChatColor.YELLOW + "Right-Click with an Item!");
			ItemStack item = playerItems.get(player);
			
			AUtil.giveItemSafely(player, item, true);
			
			playerItems.remove(player);
			enchantingPlayers.remove(player);
			
			PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(getStandID(wellStand), 0, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
			removeButtons(player);
		} else {
			NBTItem nbtFreshItem = new NBTItem(playerItems.get(player));
			int freshTier = nbtFreshItem.getInteger(NBTTag.TAINTED_TIER.getRef());

			if(freshTier >= getMaxTier(player)) {
				String maxTier = getMaxTier(player) == 4 ?  "\u00A77" : ChatColor.GRAY + "(Max Tier is Upgradable)";
				setText(player, ChatColor.RED + "You cannot enchant", ChatColor.RED + "this item any further!", maxTier, "\u00A77");
				new BukkitRunnable() {
					public void run() {
						setItemText(player);
//						setText(player, "\u00A77", "\u00A77", "\u00A77", "\u00A77");
//						showButtons(player, getEnchantCost(getTier(playerItems.get(player)), player));
					}
				}.runTaskLater(PitSim.INSTANCE, 40L);
				return;
			}

			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			int cost = getEnchantCost(getTier(playerItems.get(player)), player);

			if(cost > pitPlayer.taintedSouls) {
				setText(player, "\u00A77", ChatColor.RED + "Not enough Souls!", ChatColor.WHITE + String.valueOf(cost) + " Souls" + ChatColor.RED + " Required", "\u00A77");
				new BukkitRunnable() {
					public void run() {
						setItemText(player);
//						showButtons(player, getEnchantCost(getTier(playerItems.get(player)), player));
					}
				}.runTaskLater(PitSim.INSTANCE, 40L);
				return;
			}

			removeButtons(player);

			pitPlayer.taintedSouls -= cost;
			ItemStack freshItem = playerItems.get(player);

			PitItem pitItem = ItemFactory.getItem(freshItem);
			if(pitItem instanceof TaintedScythe || pitItem instanceof TaintedChestplate) previousLives =
					((TemporaryItem) pitItem).getMaxLives(freshItem);
			else previousLives = -1;

			previousEnchants = EnchantManager.getEnchantsOnItem(freshItem);

			try {
				ItemStack newItem;
				newItem = TaintedEnchanting.enchantItem(freshItem, player);

				if(newItem == null) return;

				NBTItem nbtItem = new NBTItem(newItem);
				int newTier = nbtItem.getInteger(NBTTag.TAINTED_TIER.getRef());
				if(newTier == 3) pitPlayer.stats.itemsTier3++;
				else if(newTier == 4) pitPlayer.stats.itemsTier4++;

				ItemMeta meta = newItem.getItemMeta();
				meta.setDisplayName(EnchantManager.getMysticName(newItem));
				newItem.setItemMeta(meta);
				EnchantManager.setItemLore(newItem, player);

				playerItems.put(player, newItem);
				int rares = 0;
				for(PitEnchant pitEnchant : EnchantManager.getEnchantsOnItem(newItem).keySet()) {
					if(pitEnchant.isRare) rares++;
				}


				HypixelSound.Sound sound = HypixelSound.Sound.getTier(freshTier + 1);
				assert sound != null;

				assert pitItem != null;
				String title = TaintedEnchanting.getTitle(previousEnchants, EnchantManager.getEnchantsOnItem(newItem), previousLives,
						((TemporaryItem) pitItem).getMaxLives(newItem));
				HypixelSound.play(player, player.getLocation(), sound, title != null);

				enchantingPlayers.add(player);
				setText(player, "\u00A77", "\u00A77", "\u00A77", ChatColor.YELLOW + "Its rolling...");

				new BukkitRunnable() {
					public void run() {
						TaintedWell.enchantingPlayers.remove(player);
						TaintedWell.showButtons(player, getEnchantCost(getTier(playerItems.get(player)), player));
						String title = TaintedEnchanting.getTitle(previousEnchants, EnchantManager.getEnchantsOnItem(newItem), previousLives,
								((TemporaryItem) pitItem).getMaxLives(newItem));
						if(title == null) return;

						TaintedEnchanting.broadcastMessage(playerItems.get(player), title, player);

						player.playEffect(TaintedWell.wellLocation.clone().add(0.0, 1.0, 0.0), Effect.EXPLOSION_HUGE, 0);
						Sounds.EXPLOSIVE_3.play(player);

					}
				}.runTaskLater(PitSim.INSTANCE, sound.length);
			} catch(Exception e) {
				e.printStackTrace();
			}

		}
	}

	@EventHandler
	public static void onEnchantingTableClick(PlayerInteractEvent event) {
		if(event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(!event.getClickedBlock().getLocation().equals(wellLocation)) return;
		
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		
		if(block.getType() != Material.ENCHANTMENT_TABLE || player.getWorld() != Bukkit.getWorld("darkzone")) return;
		event.setCancelled(true);
		if(playerItems.containsKey(event.getPlayer()) || Misc.isAirOrNull(player.getItemInHand())) return;

		PitItem pitItem = ItemFactory.getItem(player.getItemInHand());
		if((!(pitItem instanceof TaintedScythe) && !(pitItem instanceof TaintedChestplate))) {
			setText(player, "\u00A77", ChatColor.RED + "Invalid Item!", ChatColor.RED + "Please try again!", "\u00A77");

			new BukkitRunnable() {
				public void run() {
					if(!playerItems.containsKey(player)) {
						setText(player, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Tainted Well", ChatColor.GRAY + "Enchant Mystic Items found", ChatColor.GRAY + "in the Darkzone here", ChatColor.YELLOW + "Right-Click with an Item!");
					}
				}
			}.runTaskLater(PitSim.INSTANCE, 40L);

			return;
		}

		placeItemInWell(player, player.getItemInHand());
		Sounds.MYSTIC_WELL_OPEN_1.play(player);
		Sounds.MYSTIC_WELL_OPEN_2.play(player);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if(playerItems.containsKey(event.getPlayer())) {
			AUtil.giveItemSafely(event.getPlayer(), playerItems.get(event.getPlayer()));
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if(!playerItems.containsKey(event.getPlayer())) return;

		if(event.getPlayer().getWorld() != Bukkit.getWorld("darkzone")) {
			onButtonPush(event.getPlayer(), false);
		}

		if(event.getPlayer().getLocation().distance(wellLocation) > 10.0) {
			onButtonPush(event.getPlayer(), false);
		}
	}

	@EventHandler
	public void onStandClick(PlayerInteractAtEntityEvent event) {
		if(!playerItems.containsKey(event.getPlayer())) return;

		for(ArmorStand value : removeStands.values()) {
			if(value.getUniqueId().equals(event.getRightClicked().getUniqueId())) {
				onButtonPush(event.getPlayer(), false);
			}
		}

		for(ArmorStand value : enchantStands.values()) {
			if(value.getUniqueId().equals(event.getRightClicked().getUniqueId())) {
				onButtonPush(event.getPlayer(), true);
			}
		}
	}

	@EventHandler
	public void onHit(AttackEvent.Pre event) {
		if(event.getDefender().getUniqueId().equals(wellStand.getUniqueId())) event.setCancelled(true);

		for(ArmorStand textStand : textStands) {
			if(event.getDefender().getUniqueId().equals(textStand.getUniqueId())) event.setCancelled(true);
		}

		for(ArmorStand value : enchantStands.values()) {
			if(value.getUniqueId().equals(event.getDefender().getUniqueId())) event.setCancelled(true);
		}

		for(ArmorStand value : removeStands.values()) {
			if(value.getUniqueId().equals(event.getDefender().getUniqueId())) event.setCancelled(true);
		}
	}

	public static int getStandID(ArmorStand stand) {
		for(Entity entity : Bukkit.getWorld("darkzone").getNearbyEntities(wellLocation, 5.0, 5.0, 5.0)) {
			if(!(entity instanceof ArmorStand)) {
				continue;
			}
			if(entity.getUniqueId().equals(stand.getUniqueId())) {
				return entity.getEntityId();
			}
		}
		return 0;
	}

	public static void setText(Player player, String... lines) {
		assert lines.length == 4;

		for(int i = 0; i < 4; i++) {
			if(lines[i] == null) continue;

			DataWatcher dw = ((CraftEntity)textStands[i]).getHandle().getDataWatcher();
			dw.watch(2, (Object)ChatColor.translateAlternateColorCodes('&', lines[i]));
			PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(getStandID(textStands[i]), dw, false);
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(metaPacket);
		}
	}

	public static void setItemText(Player player) {
		ItemStack item = playerItems.get(player);
		if(item == null) return;
		Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnItem(item);
		List<PitEnchant> enchants = new ArrayList<PitEnchant>(enchantMap.keySet());
		if(enchants.size() == 0) {
			setText(player, item.getItemMeta().getDisplayName(), "\u00A77", "\u00A77", "\u00A77");
			return;
		}
		String enchant1 = "\u00A77";
		String enchant2 = "\u00A77";
		String enchant3 = "\u00A77";
		enchant1 = enchants.get(0).getDisplayName() + " " + AUtil.toRoman(enchantMap.get(enchants.get(0)));
		if(enchants.size() > 1) {
			enchant2 = enchants.get(1).getDisplayName() + " " + AUtil.toRoman(enchantMap.get(enchants.get(1)));
		}
		if(enchants.size() > 2) {
			enchant3 = enchants.get(2).getDisplayName() + " " + AUtil.toRoman(enchantMap.get(enchants.get(2)));
		}
		setText(player, item.getItemMeta().getDisplayName(), enchant1, enchant2, enchant3);
	}

	public static int getEnchantCost(int tier, Player player) {
		int cost;

		if(tier >= getMaxTier(player)) return -1;

		switch(tier + 1) {
		case 1:
			cost = DarkzoneBalancing.TIER_1_ENCHANT_COST;
			break;
		case 2:
			cost = DarkzoneBalancing.TIER_2_ENCHANT_COST;
			break;
		case 3:
			cost = DarkzoneBalancing.TIER_3_ENCHANT_COST;
			break;
		case 4:
			cost = DarkzoneBalancing.TIER_4_ENCHANT_COST;
			break;
		default:
			cost = -1;
		}

		if(cost != -1 && ProgressionManager.isUnlocked(PitPlayer.getPitPlayer(player), SoulBranch.INSTANCE, SkillBranch.MajorUnlockPosition.SECOND_PATH)) cost *= 0.7;
		return cost;
	}

	public static int getTier(ItemStack itemStack) {
		NBTItem nbtFreshItem = new NBTItem(itemStack);
		return nbtFreshItem.getInteger(NBTTag.TAINTED_TIER.getRef());
	}

	public static int getMaxTier(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!ProgressionManager.isUnlocked(pitPlayer, SoulBranch.INSTANCE, SkillBranch.MajorUnlockPosition.FIRST)) return 2;
		return ProgressionManager.isUnlocked(pitPlayer, SoulBranch.INSTANCE, SkillBranch.MajorUnlockPosition.LAST) ? 4 : 3;
	}

	@EventHandler
	public void onArmorStandEquip(PlayerArmorStandManipulateEvent event) {
		event.setCancelled(true);
	}
}
