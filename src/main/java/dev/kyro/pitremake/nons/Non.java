package dev.kyro.pitremake.nons;

import dev.kyro.pitremake.PitRemake;
import dev.kyro.pitremake.misc.Misc;
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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Non {

	public NPC npc;
	public Player non;
	public Player target;

	public List<NonTrait> traits = new ArrayList<>();
	public NonState nonState = NonState.RESPAWNING;
	public int count = 0;

	public Non(String name) {

		this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
		spawn();
		this.non = (Player) npc.getEntity();

		npc.setProtected(false);

		NonManager.nons.add(this);

		pickTraits();

		respawn();
	}

	public void tick() {

		if(nonState != NonState.FIGHTING) return;

		non = (Player) npc.getEntity();
		pickTarget();
		npc.getNavigator().setTarget(target, true);

		if(traits.contains(NonTrait.IRON_STREAKER))
				Misc.applyPotionEffect(non, PotionEffectType.DAMAGE_RESISTANCE, 9999, 2, true, false);

		if(count % 3 == 0 && (!traits.contains(NonTrait.NO_JUMP)) || Math.random() < 0.05) {

			Block underneath = non.getLocation().clone().subtract(0, 0.2, 0).getBlock();
			if(underneath.getType() != Material.AIR) {

				int rand = (int) (Math.random() * 2);
				Location rotLoc = non.getLocation().clone();
				rotLoc.setYaw(non.getLocation().getYaw() + (rand == 0 ? -90 : 90));

				double distance = target.getLocation().distance(non.getLocation());
				Vector sprintVelo = target.getLocation().toVector().subtract(non.getLocation().toVector())
						.normalize();

				if(distance < Math.random() * 1.5 + 1.5) sprintVelo.multiply(-0.16).setY(0.4); else sprintVelo.multiply(0.5).setY(0.4);
				non.setVelocity(sprintVelo);
			}
		}

		count++;
	}

	public void pickTarget() {

		Player closest = null;
		double closestDistance = 100;
		for(Entity nearbyEntity : non.getWorld().getNearbyEntities(new Location(Bukkit.getWorld("world"), 0.5, 94, 0.5), 10, 10, 10)) {

			if(!(nearbyEntity instanceof Player) || nearbyEntity.getUniqueId().equals(non.getUniqueId())) continue;
			double targetDistanceFromMid = Math.sqrt(Math.pow(nearbyEntity.getLocation().getX(), 2) +
					Math.pow(nearbyEntity.getLocation().getZ(), 2));
			if(targetDistanceFromMid > 9) continue;

			double distance = nearbyEntity.getLocation().distance(non.getLocation());
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

		non.setHealth(non.getMaxHealth());

		Equipment equipment = npc.getTrait(Equipment.class);
		if(traits.contains(NonTrait.IRON_STREAKER)) {

			equipment.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_SWORD));
			equipment.set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.IRON_HELMET));
			equipment.set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.IRON_CHESTPLATE));
			equipment.set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Material.IRON_LEGGINGS));
			equipment.set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.IRON_BOOTS));
		} else {
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
				}.runTaskLater(PitRemake.INSTANCE, 10L);
			}
		}.runTaskLater(PitRemake.INSTANCE, (long) (Math.random() * 20 + 20));
	}

	public void pickTraits() {


		if(Math.random() < 0.5) {

			traits.add(NonTrait.NO_JUMP);
		}
		if(Math.random() < 0.2) {

			traits.add(NonTrait.IRON_STREAKER);
		}
	}
}
