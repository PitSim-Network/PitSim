package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.events.armor.AChangeEquipmentEvent;
import dev.kyro.pitsim.PitRemake;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.nons.Non;
import dev.kyro.pitsim.nons.NonManager;
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
					double damage = entry.getValue().getSize() - 1;
					for(Entity entity : entry.getValue().getNearbyEntities(0, 0, 0)) {

						if(!(entity instanceof Player)) continue;
						Non non = NonManager.getNon((Player) entity);
						if(non == null || DamageManager.hitCooldownList.contains(non.non)) return;

						EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(entry.getValue(), non.non,
								EntityDamageEvent.DamageCause.CUSTOM, damage);
						Bukkit.getServer().getPluginManager().callEvent(event);
						if(!event.isCancelled()) {
							non.non.damage(damage);
						}
					}
				}
			}
		}.runTaskTimer(PitRemake.INSTANCE, 0L, 1L);
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

	@EventHandler
	public void onDamage(EntityDamageEvent event) {

		if(!(event.getEntity() instanceof Slime) || !blobMap.containsValue((Slime) event.getEntity())) return;
		event.setDamage(0);
	}

	@EventHandler
	public void onArmorEquip(AChangeEquipmentEvent event) {

		Player player = event.getPlayer();

		Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnPlayer(player);
		int enchantLvl = enchantMap.getOrDefault(this, 0);

		Map<PitEnchant, Integer> oldEnchantMap = EnchantManager.getEnchantsOnPlayer(event.getPreviousArmor());
		int oldEnchantLvl = oldEnchantMap.getOrDefault(this, 0);

		if(oldEnchantLvl == 0 && enchantLvl != 0) {

			if(blobMap.containsKey(player.getUniqueId())) return;

			Slime slime = (Slime) player.getWorld().spawnEntity(player.getLocation(), EntityType.SLIME);
			slime.setSize(enchantLvl);
			blobMap.put(player.getUniqueId(), slime);
		} else if(enchantLvl == 0 && oldEnchantLvl != 0) {
			Slime slime = blobMap.get(player.getUniqueId());
			if(slime == null) return;
			slime.remove();
			blobMap.remove(player.getUniqueId());
		} else if(blobMap.containsKey(player.getUniqueId())) {

			blobMap.get(player.getUniqueId()).setSize(enchantLvl);
		}
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		return damageEvent;
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

//	public double getDamageReduction(int enchantLvl) {
//
//		return (int) Math.floor(Math.pow(enchantLvl, 1.3) * 2) + 2;
//	}
}
