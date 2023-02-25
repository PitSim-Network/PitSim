package dev.kyro.pitsim.enchants.tainted.awijienchants;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class CleaveSpell extends PitEnchant {
	public static Map<Player, List<CleaveEntity>> standMap = new HashMap<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Map.Entry<Player, List<CleaveEntity>> entry : standMap.entrySet()) {
					List<CleaveEntity> cleaveEntities = entry.getValue();
					for(CleaveEntity cleaveEntity : cleaveEntities) {
						cleaveEntity.armorStand.setVelocity(cleaveEntity.getNextVelocity());
						cleaveEntity.rotation += 32;

//					entry.getValue().setVelocity(entry.getValue().getVelocity().clone().setY(0));
						for(Entity nearbyEntity : cleaveEntity.armorStand.getNearbyEntities(40, 40, 40)) {
							if(!(nearbyEntity instanceof Player)) continue;
							Player player = (Player) nearbyEntity;

							PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook identityTpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(
									cleaveEntity.armorStand.getEntityId(), (byte) 0, (byte) 0, (byte) 0, (byte) (cleaveEntity.rotation % 256), (byte) 0, false);
							((CraftPlayer) player).getHandle().playerConnection.sendPacket(identityTpPacket);
						}
						for(Entity entity : cleaveEntity.armorStand.getNearbyEntities(0.5, 0.5, 0.5)) {
							if(!(entity instanceof LivingEntity) || entity.getUniqueId().equals(entity.getUniqueId())) continue;

							if(entity instanceof ArmorStand) continue;
							if(entity instanceof Villager) continue;
							if(!(entity instanceof LivingEntity)) continue;
							Player player = null;
							for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
								if(onlinePlayer == entry.getKey()) player = onlinePlayer;
							}
							if(player == null) return;
							if(entity == player) continue;

							EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(player, entity, EntityDamageEvent.DamageCause.CUSTOM, 5);
							damageEvent.setDamage(5);
							Bukkit.getServer().getPluginManager().callEvent(damageEvent);
							if(damageEvent.isCancelled()) damageEvent.setDamage(5);
							Sounds.CLEAVE3.play(player);
						}
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 2, 2);
	}

	public CleaveSpell() {
		super("Cleave", true, ApplyType.SCYTHES, "cleave", "cleaver");
		isTainted = true;
	}

	@EventHandler
	public void onUse(PitPlayerAttemptAbilityEvent event) {
		int enchantLvl = event.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(event.getPlayer(), 10);
		if(cooldown.isOnCooldown()) return;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
		if(!pitPlayer.useMana(getManaCost(enchantLvl))) {
			Sounds.NO.play(event.getPlayer());
			return;
		}

		cooldown.restart();

		Player player = event.getPlayer();
		Location standLocation = player.getLocation().add(0, 1, 0).subtract(player.getLocation().getDirection());
		Vector velocity = player.getTargetBlock((Set<Material>) null, 30).getLocation().toVector()
				.subtract(standLocation.toVector()).normalize().multiply(1.5);
		velocity = new Vector();

		ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(standLocation, EntityType.ARMOR_STAND);
		stand.setGravity(true);
		stand.setArms(true);
		stand.setVisible(false);
		stand.setSmall(true);
		stand.setMarker(true);
		stand.setItemInHand(player.getItemInHand().clone());
		stand.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(345), Math.toRadians(318)));
		stand.setVelocity(velocity);

		CleaveEntity cleaveEntity = new CleaveEntity(stand, standLocation, velocity);
		standMap.put(player, cleaveEntity);

		Sounds.CLEAVE1.play(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!standMap.containsKey(player)) return;
				standMap.remove(player).remove();
			}
		}.runTaskLater(PitSim.INSTANCE, 60);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Throw your scythe, dealing damage to all entities it hits &d&o-" + getManaCost(enchantLvl) + " Mana"
		).getLore();
	}

	public static int getManaCost(int enchantLvl) {
		return 30 * (4 - enchantLvl);
	}

	public static class CleaveEntity {
		public ArmorStand armorStand;
		public Location spawnLocation;
		public Vector velocity;
		public int rotation = 0;

		public CleaveEntity(ArmorStand armorStand, Location spawnLocation, Vector velocity) {
			this.armorStand = armorStand;
			this.spawnLocation = spawnLocation;
			this.velocity = velocity;
		}

		public Vector getNextVelocity() {
			return velocity;
		}

		public void remove() {
			if(armorStand.isValid()) armorStand.remove();
		}
	}
}
