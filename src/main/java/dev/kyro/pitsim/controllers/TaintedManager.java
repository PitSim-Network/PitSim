package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.mystics.TaintedScythe;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TaintedManager implements Listener {
	public static List<Player> players = new ArrayList<>();

	@EventHandler
	public void onHeal(HealEvent event) {
		if(!event.isPlayer) return;
		if(!MapManager.inDarkzone(event.player)) return;
		event.multipliers.add(0.5);
	}

	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Arrow) return;
		if(event.getEntity() instanceof CraftArrow) return;
		if(event.getEntity() instanceof Fireball) return;
		if(!(event.getEntity() instanceof LivingEntity)) return;

		try {
			if(!MapManager.inDarkzone((LivingEntity) event.getDamager()) || !MapManager.inDarkzone((LivingEntity) event.getEntity()))
				return;
		} catch(ClassCastException ignored) {}

		if(event.getDamager() instanceof Player) {
			ItemStack held = ((Player) event.getDamager()).getItemInHand();
			if(!Misc.isAirOrNull(held) && held.getType() == Material.GOLD_HOE) {
				double multiplier = Misc.isCritical((Player) event.getDamager()) ? 1.5 : 1;
				event.setDamage(TaintedScythe.BASE_DAMAGE * multiplier);
			}
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if(players.contains(event.getPlayer())) return;
		if(!Bukkit.getOnlinePlayers().contains(event.getPlayer())) return;
		players.add(event.getPlayer());

		new BukkitRunnable() {
			@Override
			public void run() {
				players.remove(event.getPlayer());
			}
		}.runTaskLater(PitSim.INSTANCE, 40);
	}

//	@EventHandler
//	public void onAttack(AttackEvent.Pre event) {
//		Player player = event.getAttackerPlayer();
//		if(player == null) return;
//
//		if(player.getWorld() == MapManager.getDarkzone()) {
//			if(!Misc.isAirOrNull(player.getItemInHand())) {
//				NBTItem nbtItem = new NBTItem(player.getItemInHand());
//				if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && MysticType.getMysticType(player.getItemInHand()) == MysticType.SWORD) {
//					for(PitEnchant pitEnchant : EnchantManager.getEnchantsOnItem(player.getItemInHand()).keySet()) {
//						event.getAttackerEnchantMap().remove(pitEnchant);
//					}
//				}
//			}
//
//			if(!Misc.isAirOrNull(player.getInventory().getLeggings())) {
//				NBTItem nbtItem = new NBTItem(player.getInventory().getLeggings());
//				if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && MysticType.getMysticType(player.getInventory().getLeggings()) == MysticType.PANTS) {
//					for(PitEnchant pitEnchant : EnchantManager.getEnchantsOnItem(player.getInventory().getLeggings()).keySet()) {
//						event.getAttackerEnchantMap().remove(pitEnchant);
//					}
//				}
//			}
//
//		} else {
//
//			if(!Misc.isAirOrNull(player.getItemInHand())) {
//				NBTItem nbtItem = new NBTItem(player.getItemInHand());
//				if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && MysticType.getMysticType(player.getItemInHand()) == MysticType.TAINTED_SCYTHE) {
//					for(PitEnchant pitEnchant : EnchantManager.getEnchantsOnItem(player.getItemInHand()).keySet()) {
//						event.getAttackerEnchantMap().remove(pitEnchant);
//					}
//				}
//			}
//
//			if(!Misc.isAirOrNull(player.getInventory().getChestplate())) {
//				NBTItem nbtItem = new NBTItem(player.getInventory().getChestplate());
//				if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && MysticType.getMysticType(player.getInventory().getChestplate()) == MysticType.TAINTED_CHESTPLATE) {
//					for(PitEnchant pitEnchant : EnchantManager.getEnchantsOnItem(player.getInventory().getChestplate()).keySet()) {
//						event.getAttackerEnchantMap().remove(pitEnchant);
//					}
//				}
//			}
//		}
//	}

//	@EventHandler
//	public void onDefend(AttackEvent.Pre event) {
//		Player player = event.getDefenderPlayer();
//		if(player == null) return;
//
//		if(player.getWorld() == MapManager.getDarkzone()) {
//			if(!Misc.isAirOrNull(player.getItemInHand())) {
//				NBTItem nbtItem = new NBTItem(player.getItemInHand());
//				if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && MysticType.getMysticType(player.getItemInHand()) == MysticType.SWORD) {
//					for(PitEnchant pitEnchant : EnchantManager.getEnchantsOnItem(player.getItemInHand()).keySet()) {
//						event.getDefenderEnchantMap().remove(pitEnchant);
//					}
//				}
//			}
//
//			if(!Misc.isAirOrNull(player.getInventory().getLeggings())) {
//				NBTItem nbtItem = new NBTItem(player.getInventory().getLeggings());
//				if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && MysticType.getMysticType(player.getInventory().getLeggings()) == MysticType.PANTS) {
//					for(PitEnchant pitEnchant : EnchantManager.getEnchantsOnItem(player.getInventory().getLeggings()).keySet()) {
//						event.getDefenderEnchantMap().remove(pitEnchant);
//					}
//				}
//			}
//
//		} else {
//
//			if(player.getWorld() == MapManager.getDarkzone()) {
//				if(!Misc.isAirOrNull(player.getItemInHand())) {
//					NBTItem nbtItem = new NBTItem(player.getItemInHand());
//					if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && MysticType.getMysticType(player.getItemInHand()) == MysticType.TAINTED_SCYTHE) {
//						for(PitEnchant pitEnchant : EnchantManager.getEnchantsOnItem(player.getItemInHand()).keySet()) {
//							event.getDefenderEnchantMap().remove(pitEnchant);
//						}
//					}
//				}
//
//				if(!Misc.isAirOrNull(player.getInventory().getChestplate())) {
//					NBTItem nbtItem = new NBTItem(player.getInventory().getChestplate());
//					if(nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && MysticType.getMysticType(player.getInventory().getChestplate()) == MysticType.TAINTED_CHESTPLATE) {
//						for(PitEnchant pitEnchant : EnchantManager.getEnchantsOnItem(player.getInventory().getChestplate()).keySet()) {
//							event.getDefenderEnchantMap().remove(pitEnchant);
//						}
//					}
//				}
//			}
//		}
//	}

	@EventHandler
	public void onOpen(InventoryOpenEvent event) {
		if(players.contains((Player) event.getPlayer())) event.setCancelled(true);
	}
}
