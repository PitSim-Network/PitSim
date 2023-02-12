package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.mystics.TaintedChestplate;
import dev.kyro.pitsim.aitems.mystics.TaintedScythe;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaintedWell implements Listener {
	public static Location wellLocation;
	public static ArmorStand wellStand;
	public static ArmorStand textLine1;
	public static ArmorStand textLine2;
	public static ArmorStand textLine3;
	public static ArmorStand textLine4;
	public static Map<Player, ArmorStand> removeStands;
	public static Map<Player, ArmorStand> enchantStands;
	public static List<Player> enchantingPlayers;
	private static Map<Player, ItemStack> playerItems;
	public static int i;

	static {
		wellLocation = new Location(Bukkit.getWorld("darkzone"), 199.0, 92.0, -115.0);
		removeStands = new HashMap<>();
		enchantStands = new HashMap<>();
		enchantingPlayers = new ArrayList<>();
		playerItems = new HashMap<>();
		i = 0;
		new BukkitRunnable() {
			public void run() {
				if(!PitSim.getStatus().isDarkzone()) return;
				if(wellStand != null){
					for(Entity entity : wellStand.getNearbyEntities(25.0, 25.0, 25.0)) {
						if(!(entity instanceof Player)) {
							continue;
						}
						Player player = (Player)entity;

						PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(wellStand), (byte)0, (byte)0, (byte)0, (byte)i, (byte)0, false);
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
						if(!playerItems.containsKey(player)) {}
						if(enchantingPlayers.contains(player)) {
							i += 24;
							player.playEffect(wellLocation.clone().add(0.0, 1.0, 0.0), Effect.ENDER_SIGNAL, 0);
						}
						else {
							i += 8;
						}
						if(i < 256) {
							continue;
						}
						i = 0;
					}
				}

			}
		}.runTaskTimer(PitSim.INSTANCE, 2L, 2L);

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
		
		textLine1 = wellLocation.getWorld().spawn(wellLocation.clone().add(0.5, 1.0, 0.5), ArmorStand.class);
		textLine1.setArms(true);
		textLine1.setVisible(false);
		textLine1.setCustomName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Tainted Well");
		textLine1.setCustomNameVisible(true);
		textLine1.setGravity(false);
		
		textLine2 = wellLocation.getWorld().spawn(wellLocation.clone().add(0.5, 0.7, 0.5), ArmorStand.class);
		textLine2.setArms(true);
		textLine2.setVisible(false);
		textLine2.setCustomName(ChatColor.GRAY + "Enchant Mystic Items found");
		textLine2.setCustomNameVisible(true);
		textLine2.setGravity(false);
		
		textLine3 = wellLocation.getWorld().spawn(wellLocation.clone().add(0.5, 0.4, 0.5), ArmorStand.class);
		textLine3.setArms(true);
		textLine3.setVisible(false);
		textLine3.setCustomName(ChatColor.GRAY + "in the Darkzone here");
		textLine3.setCustomNameVisible(true);
		textLine3.setGravity(false);
		
		textLine4 = wellLocation.getWorld().spawn(wellLocation.clone().add(0.5, 0.1, 0.5), ArmorStand.class);
		textLine4.setArms(true);
		textLine4.setVisible(false);
		textLine4.setCustomName(ChatColor.YELLOW + "Right-Click with an Item!");
		textLine4.setCustomNameVisible(true);
		textLine4.setGravity(false);
		
		wellLocation.getBlock().setType(Material.ENCHANTMENT_TABLE);
	}

	public void onStop() {
		wellStand.remove();
		textLine1.remove();
		textLine2.remove();
		textLine3.remove();
		textLine4.remove();

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

	public static void onEnchant(Player player, ItemStack itemStack) {
		playerItems.put(player, itemStack);
		player.getInventory().remove(itemStack);
		
		PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(getStandID(wellStand), 0, CraftItemStack.asNMSCopy(itemStack));
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
		showButtons(player);
	}

	public static void showButtons(Player player) {
		ArmorStand removeStand = wellLocation.getWorld().spawn(wellLocation.clone().add(0.5, 0.0, 0.5), ArmorStand.class);
		removeStand.setGravity(false);
		removeStand.setArms(true);
		removeStand.setVisible(false);
		removeStand.setCustomName(ChatColor.RED + "Remove Item");
		removeStand.setCustomNameVisible(true);
		removeStands.put(player, removeStand);
		
		ArmorStand enchantStand = wellLocation.getWorld().spawn(wellLocation.clone().add(0.5, 0.0, 0.5), ArmorStand.class);
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
		
		setText(player, "\u00A77", "\u00A77", "\u00A77", "\u00A77");
		setItemText(player);
	}

	public static void onButtonPush(Player player, boolean enchant) {
		ArmorStand removeStand = removeStands.get(player);
		ArmorStand enchantStand = enchantStands.get(player);

		if(enchantStand == null || removeStand == null) return;

		PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook tpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(removeStand), (byte)64, (byte)0, (byte)0, (byte)0, (byte)0, false);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(tpPacket);
		
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
		
		if(!enchant) {
			setText(player, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Tainted Well", ChatColor.GRAY + "Enchant Mystic Items found", ChatColor.GRAY + "in the Darkzone here", ChatColor.YELLOW + "Right-Click with an Item!");
			ItemStack item = playerItems.get(player);
			
			AUtil.giveItemSafely(player, item, true);
			
			playerItems.remove(player);
			enchantingPlayers.remove(player);
			
			PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(getStandID(wellStand), 0, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
			
			new BukkitRunnable() {
				public void run() {
					setText(player, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Tainted Well", ChatColor.GRAY + "Enchant Mystic Items found", ChatColor.GRAY + "in the Darkzone here", ChatColor.YELLOW + "Right-Click with an Item!");
				}
			}.runTaskLater(PitSim.INSTANCE, 3L);

		} else {
			NBTItem nbtFreshItem = new NBTItem(playerItems.get(player));
			int freshTier = nbtFreshItem.getInteger(NBTTag.TAINTED_TIER.getRef());
			
			if(freshTier == 3) {
				setText(player, "\u00A77", ChatColor.RED + "Item is Max Tier!", ChatColor.RED + "Please remove", "\u00A77");
				new BukkitRunnable() {
					public void run() {
						setText(player, "\u00A77", "\u00A77", "\u00A77", "\u00A77");
						showButtons(player);
					}
				}.runTaskLater(PitSim.INSTANCE, 40L);
				return;
			}

			ItemStack freshItem = playerItems.get(player);

			try {
				ItemStack newItem;
				newItem = TaintedEnchanting.enchantItem(freshItem);
				if(newItem == null) return;
				
				NBTItem nbtItem = new NBTItem(newItem);
				
				if(nbtItem.hasKey(NBTTag.TAINTED_TIER.getRef())) {
					int tier = nbtItem.getInteger(NBTTag.TAINTED_TIER.getRef());
					nbtItem.setInteger(NBTTag.TAINTED_TIER.getRef(), tier + 1);
				} else {
					nbtItem.setInteger(NBTTag.TAINTED_TIER.getRef(), 1);
					PitPlayer.getPitPlayer(player).stats.itemsEnchanted++;
				}
				
				playerItems.put(player, nbtItem.getItem());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			enchantingPlayers.add(player);
			setText(player, "\u00A77", "\u00A77", "\u00A77", ChatColor.YELLOW + "Its rolling...");
			
			new BukkitRunnable() {
				public void run() {
					enchantingPlayers.remove(player);
					player.playEffect(wellLocation.clone().add(0.0, 1.0, 0.0), Effect.EXPLOSION_HUGE, 0);
					Sounds.EXPLOSIVE_3.play(player);
					showButtons(player);
				}
			}.runTaskLater(PitSim.INSTANCE, 80L);
		}
	}

	@EventHandler
	public static void onEnchantingTableClick(PlayerInteractEvent event) {
		if(event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		
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

		onEnchant(player, player.getItemInHand());
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
		if(event.getDefender().getUniqueId().equals(textLine1.getUniqueId())) event.setCancelled(true);
		if(event.getDefender().getUniqueId().equals(textLine2.getUniqueId())) event.setCancelled(true);
		if(event.getDefender().getUniqueId().equals(textLine3.getUniqueId())) event.setCancelled(true);
		if(event.getDefender().getUniqueId().equals(textLine4.getUniqueId())) event.setCancelled(true);

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

	public static void setText(Player player, String line1, String line2, String line3, String line4) {
		if(line1 != null) {
			PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving((EntityLiving)((CraftEntity)textLine1).getHandle());
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawn);
			DataWatcher dw = ((CraftEntity)textLine1).getHandle().getDataWatcher();
			dw.watch(2, (Object)line1);
			PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(getStandID(textLine1), dw, false);
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(metaPacket);
		}
		else {
			PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(textLine1));
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(destroyPacket);
		}
		if(line2 != null) {
			PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving((EntityLiving)((CraftEntity)textLine2).getHandle());
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawn);
			DataWatcher dw = ((CraftEntity)textLine2).getHandle().getDataWatcher();
			dw.watch(2, (Object)line2);
			PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(getStandID(textLine2), dw, false);
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(metaPacket);
		}
		else {
			PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(textLine2));
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(destroyPacket);
		}
		if(line3 != null) {
			PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving((EntityLiving)((CraftEntity)textLine3).getHandle());
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawn);
			DataWatcher dw = ((CraftEntity)textLine3).getHandle().getDataWatcher();
			dw.watch(2, (Object)line3);
			PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(getStandID(textLine3), dw, false);
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(metaPacket);
		}
		else {
			PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(textLine3));
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(destroyPacket);
		}
		if(line4 != null) {
			PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving((EntityLiving)((CraftEntity)textLine4).getHandle());
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawn);
			DataWatcher dw = ((CraftEntity)textLine4).getHandle().getDataWatcher();
			dw.watch(2, (Object)line4);
			PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(getStandID(textLine4), dw, false);
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(metaPacket);
		}
		else {
			PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(textLine4));
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(destroyPacket);
		}
	}

	public static void setItemText(Player player) {
		ItemStack item = playerItems.get(player);
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
}
