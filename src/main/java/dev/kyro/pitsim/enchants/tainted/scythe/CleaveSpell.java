package dev.kyro.pitsim.enchants.tainted.scythe;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.DarkzoneBalancing;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.objects.PitEnchantSpell;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.events.SpellUseEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class CleaveSpell extends PitEnchantSpell {
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

						for(Entity nearbyEntity : cleaveEntity.armorStand.getNearbyEntities(40, 40, 40)) {
							if(!(nearbyEntity instanceof Player)) continue;
							Player player = (Player) nearbyEntity;

							PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook identityTpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(
									cleaveEntity.armorStand.getEntityId(), (byte) 0, (byte) 0, (byte) 0, (byte) (cleaveEntity.rotation % 256), (byte) 0, false);
							((CraftPlayer) player).getHandle().playerConnection.sendPacket(identityTpPacket);
						}

						Location checkLocation = cleaveEntity.armorStand.getLocation();
						for(Entity entity : cleaveEntity.armorStand.getWorld().getNearbyEntities(checkLocation, 1, 1, 1)) {
							if(!(entity instanceof LivingEntity) || entity == entry.getKey() || entity == cleaveEntity.armorStand) continue;
							LivingEntity livingEntity = (LivingEntity) entity;
							if(!Misc.isEntity(livingEntity, PitEntityType.REAL_PLAYER, PitEntityType.PIT_BOSS, PitEntityType.PIT_MOB)) continue;

							DamageManager.createDirectAttack(cleaveEntity.attacker, livingEntity, DarkzoneBalancing.SCYTHE_DAMAGE, attackEvent -> {
								if(!attackEvent.isCancelled()) Sounds.CLEAVE3.play(cleaveEntity.attacker);
							});
						}
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 2, 2);
	}

	public CleaveSpell() {
		super("Cleave", "cleave", "cleaver");
		isTainted = true;
	}

	@EventHandler(ignoreCancelled = true)
	public void onUse(SpellUseEvent event) {
		if(!isThisSpell(event.getSpell())) return;
		Player player = event.getPlayer();

		Location standLocation = player.getLocation().add(0, player.getEyeHeight() - 1.1, 0).subtract(player.getLocation().getDirection());
		Vector velocity = player.getTargetBlock((Set<Material>) null, 30).getLocation().add(0.5, 0.5, 0.5).toVector()
				.subtract(standLocation.toVector()).normalize().multiply(1.0);

		ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(standLocation, EntityType.ARMOR_STAND);
		stand.setGravity(true);
		stand.setArms(true);
		stand.setVisible(false);
		stand.setSmall(true);
		stand.setMarker(true);
		stand.setItemInHand(player.getItemInHand().clone());
		stand.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(345), Math.toRadians(250)));
		stand.setVelocity(velocity);

		standMap.putIfAbsent(player, new ArrayList<>());
		List<CleaveEntity> cleaveEntities = standMap.get(player);

		CleaveEntity cleaveEntity = new CleaveEntity(player, stand, standLocation, velocity);
		cleaveEntities.add(cleaveEntity);

		Sounds.CLEAVE1.play(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!standMap.containsKey(player)) return;
				cleaveEntities.remove(cleaveEntity);
				cleaveEntity.remove();
			}
		}.runTaskLater(PitSim.INSTANCE, 60);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Right-Clicking casts this spell for &b" + getManaCost(enchantLvl) + " mana&7, " +
						"&7throwing your scythe and dealing damage to all entities it hits"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"throws your scythe when used, dealing damage to all the enemies it hits";
	}

	@Override
	public int getManaCost(int enchantLvl) {
		return Math.max(20 - enchantLvl * 4, 0);
	}

	@Override
	public int getCooldownTicks(int enchantLvl) {
		return 4;
	}

	public static class CleaveEntity {
		public Player attacker;
		public ArmorStand armorStand;
		public Location spawnLocation;
		public Vector velocity;
		public int rotation = 0;

		public CleaveEntity(Player attacker, ArmorStand armorStand, Location spawnLocation, Vector velocity) {
			this.attacker = attacker;
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
