package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.events.armor.AChangeEquipmentEvent;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class WolfPack extends PitEnchant {

	public static Map<UUID, List<Wolf>> wolfMap = new HashMap<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Map.Entry<UUID, List<Wolf>> entry : wolfMap.entrySet()) {
					for(Wolf wolf : entry.getValue()) {
						Player target = null;
						double distance = 0;
						for(Entity entity : wolf.getNearbyEntities(5, 5, 5)) {

							if(!(entity instanceof Player)) continue;
							Player player = (Player) entity;
							if(target == null || target.getUniqueId() == entry.getKey() ||
									player.getLocation().distance(wolf.getLocation()) < distance) target = player;

//							Non non = NonManager.getNon((Player) entity);
//							if(non == null || DamageManager.hitCooldownList.contains(non.non)) continue;
//
//							EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(entry.getValue(), non.non,
//									EntityDamageEvent.DamageCause.CUSTOM, damage);
//							Bukkit.getServer().getPluginManager().callEvent(event);
//							if(!event.isCancelled()) {
//								non.non.damage(damage);
//							}
						}
						wolf.setTarget(target != null ? target : Bukkit.getPlayer(entry.getKey()));
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	public WolfPack() {
		super("Wolf Pack", true, ApplyType.PANTS,
				"wolfpack", "wolf", "pack", "wolf-pack", "wp");
	}

	public static boolean isInPack(Wolf wolf) {

		for(Map.Entry<UUID, List<Wolf>> entry : wolfMap.entrySet()) {

			if(entry.getValue().contains(wolf)) return true;
		}
		return false;
	}

	public static Player getOwner(Wolf wolf) {

		for(Map.Entry<UUID, List<Wolf>> entry : wolfMap.entrySet()) {
			if(!entry.getValue().contains(wolf)) continue;
			return Bukkit.getPlayer(entry.getKey());
		}
		return null;
	}

	@EventHandler(ignoreCancelled = true)
	public void onAttack(EntityDamageEvent event) {

		if(!(event.getEntity() instanceof Wolf) || !isInPack((Wolf) event.getEntity())) return;
		Wolf wolf = (Wolf) event.getEntity();

		if(getOwner(wolf) == event.getEntity()) {

			event.setCancelled(true);
			return;
		}

		if(event.getFinalDamage() < wolf.getHealth()) return;
		for(Map.Entry<UUID, List<Wolf>> entry : wolfMap.entrySet()) {
			if(entry.getValue() != wolf) continue;
			wolfMap.remove(entry.getKey());
			return;
		}
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {

		int enchantLvl = killEvent.getKillerEnchantLevel(this);
		if(enchantLvl == 0) return;

		HitCounter.incrementCounter(killEvent.killer, this);
		if(!HitCounter.hasReachedThreshold(killEvent.killer, this, getKills(enchantLvl))) return;

		List<Wolf> pack = wolfMap.get(killEvent.killer.getUniqueId());
		if(pack == null) pack = new ArrayList<>();
		if(pack.size() >= getMaxWolves(enchantLvl)) return;

		Wolf wolf = (Wolf) killEvent.killer.getWorld().spawnEntity(killEvent.killer.getLocation(), EntityType.WOLF);
		wolf.setAngry(true);

		pack.add(wolf);
		wolfMap.put(killEvent.killer.getUniqueId(), pack);
	}

	@EventHandler
	public void onArmorEquip(AChangeEquipmentEvent event) {

		Player player = event.getPlayer();

		Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnPlayer(player);
		int enchantLvl = enchantMap.getOrDefault(this, 0);

		Map<PitEnchant, Integer> oldEnchantMap = EnchantManager.getEnchantsOnPlayer(event.getPreviousArmor());
		int oldEnchantLvl = oldEnchantMap.getOrDefault(this, 0);

		if(oldEnchantLvl == 0 && enchantLvl != 0) {
//			Don't remove this
		} else if(enchantLvl == 0 && oldEnchantLvl != 0) {
			List<Wolf> pack = wolfMap.get(player.getUniqueId());
			if(pack == null) return;
			for(Wolf wolf : pack) wolf.remove();
			wolfMap.remove(player.getUniqueId());
		} else if(wolfMap.containsKey(player.getUniqueId())) {

			List<Wolf> pack = wolfMap.get(player.getUniqueId());
			if(pack == null) return;
			int count = 1;
			for(Wolf wolf : pack) {

				if(count++ > getMaxWolves(enchantLvl)) {
					pack.remove(wolf);
					wolf.remove();
				}
			}
		}
	}

	public static int getMaxWolves(int enchantLvl) {

		return enchantLvl * 2 + 3;
	}

	public static int getKills(int enchantLvl) {

		return Math.max(Misc.linearEnchant(enchantLvl, -0.5, 4.5), 1);
	}

	@Override
	public void onDisable() {

		for(Map.Entry<UUID, List<Wolf>> entry : wolfMap.entrySet()) for(Wolf wolf : entry.getValue()) wolf.remove();
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Spawn a &cWolf &7every " + getKills(enchantLvl) + " kills.",
				"&7Can have up to &c" + getMaxWolves(enchantLvl) + " &7wolves at", "&7once").getLore();
	}
}
