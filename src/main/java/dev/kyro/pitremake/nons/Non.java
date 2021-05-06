package dev.kyro.pitremake.nons;

import dev.kyro.pitremake.PitRemake;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.npc.ai.CitizensNavigator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Non {

	public NPC npc;
	public Player non;
	public Player target;

	public NonState nonState = NonState.RESPAWNING;
	public int count = 0;

	public Non(String name) {

		this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
		spawn();
		this.non = (Player) npc.getEntity();

		npc.setProtected(false);

		NonManager.nons.add(this);

		respawn();
	}

	public void tick() {

		non = (Player) npc.getEntity();

		pickTarget();
		npc.getNavigator().setTarget(target, true);

		if(target == null || !target.getWorld().equals(non.getWorld())) return;

		if(count % 3 == 0) {

			Block underneath = non.getLocation().clone().subtract(0, 0.2, 0).getBlock();
			if(underneath.getType() != Material.AIR) {

				int rand = (int) (Math.random() * 2);
				Location rotLoc = non.getLocation().clone();
				rotLoc.setYaw(non.getLocation().getYaw() + (rand == 0 ? -90 : 90));

				double distance = target.getLocation().toVector().distance(non.getLocation().toVector());
				Vector sprintVelo = target.getLocation().toVector().subtract(non.getLocation().toVector())
						.normalize();
				if(distance < Math.random() * 1.5 + 1.5) sprintVelo.multiply(-0.16).setY(0.4); else sprintVelo.multiply(0.5).setY(0.4);

				Vector velo = sprintVelo.add(rotLoc.getDirection().normalize().setY(0).multiply(0.1));

				non.setVelocity(sprintVelo);
			}
		}

		count++;
	}

	public void pickTarget() {

		Player closest = null;
		double closestDistance = 100;
		for(Entity nearbyEntity : non.getNearbyEntities(10, 10, 10)) {

			if(!(nearbyEntity instanceof Player) || nearbyEntity.getUniqueId().equals(non.getUniqueId())) continue;

			double distance = nearbyEntity.getLocation().toVector().distance(non.getLocation().toVector());
			if(distance >= closestDistance) continue;

			closest = (Player) nearbyEntity;
			closestDistance = distance;
		}
		target = closest;
	}

	public void spawn() {
		Location spawnLoc = new Location(Bukkit.getWorld("world"), 0.5, 100, 0.5, -180, 60);
		npc.spawn(spawnLoc);
	}

	public void respawn() {

		Location spawnLoc = new Location(Bukkit.getWorld("world"), 0.5, 100, 0.5, -180, 60);
		npc.teleport(spawnLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);

		CitizensNavigator navigator = (CitizensNavigator) npc.getNavigator();
		navigator.getDefaultParameters()
				.attackDelayTicks((int) (Math.random() * 4 + 3))
				.attackRange(4);

		non.setHealth(20);

		Equipment equipment = npc.getTrait(Equipment.class);
		equipment.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.IRON_SWORD));
		equipment.set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.CHAINMAIL_CHESTPLATE));
		equipment.set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Material.CHAINMAIL_LEGGINGS));
		equipment.set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.CHAINMAIL_BOOTS));

		int rand = (int) (Math.random() * 3);
		switch(rand) {
			case 0:
				equipment.set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.IRON_CHESTPLATE));
				break;
			case 1:
				equipment.set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Material.IRON_LEGGINGS));
				break;
			case 2:
				equipment.set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.IRON_BOOTS));
				break;
		}

		new BukkitRunnable() {
			@Override
			public void run() {

				Vector velo = non.getLocation().getDirection().normalize().multiply(0.7);
				velo.setY(0.35);
				non.setVelocity(velo);

				new BukkitRunnable() {
					@Override
					public void run() {
						nonState = NonState.FIGHTING;
					}
				}.runTaskLater(PitRemake.INSTANCE, 40L);
			}
		}.runTaskLater(PitRemake.INSTANCE, (long) (Math.random() * 20 + 20));
	}
}
