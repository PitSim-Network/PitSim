package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.events.armor.AChangeEquipmentEvent;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PitBlob extends PitEnchant {

	public static Map<UUID, Slime> blobMap = new HashMap<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Map.Entry<UUID, Slime> entry : blobMap.entrySet()) {
					double damage = (entry.getValue().getSize() - 1) * 1.5;
					for(Entity entity : entry.getValue().getNearbyEntities(0, 0, 0)) {

						if(!(entity instanceof Player)) continue;
						Non non = NonManager.getNon((Player) entity);
						if(non == null || DamageManager.hitCooldownList.contains(non.non)) continue;

						EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(entry.getValue(), non.non,
								EntityDamageEvent.DamageCause.CUSTOM, damage);
						Bukkit.getServer().getPluginManager().callEvent(event);
						if(!event.isCancelled()) {
							non.non.damage(damage);
						}
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	public PitBlob() {
		super("Slime Boss Mid", true, ApplyType.PANTS,
				"pitblob", "slimeboss", "pit-blob", "slime-boss", "blob", "slime", "boss");
	}

	public static Player getOwner(Slime slime) {

		for(Map.Entry<UUID, Slime> entry : PitBlob.blobMap.entrySet()) {
			if(!entry.getValue().equals(slime)) continue;
			return Bukkit.getPlayer(entry.getKey());
		}
		return null;
	}

	@EventHandler(ignoreCancelled = true)
	public void onAttack(EntityDamageEvent event) {

		if(!(event.getEntity() instanceof Slime) || !blobMap.containsValue((Slime) event.getEntity())) return;
		Slime slime = (Slime) event.getEntity();

		if(getOwner(slime) == event.getEntity()) {

			event.setCancelled(true);
			return;
		}

		if(event.getFinalDamage() < slime.getHealth()) return;
		for(Map.Entry<UUID, Slime> entry : blobMap.entrySet()) {
			if(entry.getValue() != slime) continue;
			blobMap.remove(entry.getKey());
			return;
		}
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {

		int enchantLvl = killEvent.getKillerEnchantLevel(this);
		if(enchantLvl == 0) return;

		Slime slime = blobMap.get(killEvent.killer.getUniqueId());
		if(slime != null) {

			boolean isMaxSize = slime.getSize() >= getMaxSlimeSize(enchantLvl);
			if(Math.random() < 0.25 && !isMaxSize) slime.setSize(slime.getSize() + 1);
			if(!isMaxSize) slime.setHealth(slime.getMaxHealth());
			return;
		}

		slime = (Slime) killEvent.killer.getWorld().spawnEntity(killEvent.killer.getLocation(), EntityType.SLIME);
		slime.setSize(1);
		blobMap.put(killEvent.killer.getUniqueId(), slime);
	}

	@EventHandler
	public void onArmorEquip(AChangeEquipmentEvent event) {

		Player player = event.getPlayer();

		Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnPlayer(player);
		int enchantLvl = enchantMap.getOrDefault(this, 0);

		Map<PitEnchant, Integer> oldEnchantMap = EnchantManager.getEnchantsOnPlayer(event.getPreviousArmor());
		int oldEnchantLvl = oldEnchantMap.getOrDefault(this, 0);

		if(oldEnchantLvl == 0 && enchantLvl != 0) {

//			if(blobMap.containsKey(player.getUniqueId())) return;

		} else if(enchantLvl == 0 && oldEnchantLvl != 0) {
			Slime slime = blobMap.get(player.getUniqueId());
			if(slime == null) return;
			slime.remove();
			blobMap.remove(player.getUniqueId());
		} else if(blobMap.containsKey(player.getUniqueId())) {

			if(blobMap.get(player.getUniqueId()) == null) return;
			blobMap.get(player.getUniqueId()).setSize(Math.min(enchantLvl * 2, blobMap.get(player.getUniqueId()).getSize()));
		}
	}

	@Override
	public void onDisable() {

		for(Map.Entry<UUID, Slime> entry : blobMap.entrySet()) {

			entry.getValue().remove();
		}
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Kills respawn &aThe Blob&7. This", "&7slimy pet will follow you around",
				"&7and kill your enemies. &aThe Blob", "&7grows and gains health for every", "&7enemy you kill.").getLore();
	}

	public static int getMaxSlimeSize(int enchantLvl) {

		return enchantLvl * 2;
	}
}
