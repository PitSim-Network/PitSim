package dev.kyro.pitsim.controllers.objects;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.FPSCommand;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.SkinManager;
import dev.kyro.pitsim.controllers.SpawnManager;
import dev.kyro.pitsim.enums.NonState;
import dev.kyro.pitsim.enums.NonTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.npc.ai.CitizensNavigator;
import net.citizensnpcs.util.PlayerAnimation;
import net.citizensnpcs.util.Util;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Non {

	public NPC npc;
	public Player non;
	public Player target;
	public String name;
	public String displayName;

	public List<NonTrait> traits = new ArrayList<>();
	public double persistence;
	public NonState nonState = NonState.RESPAWNING;
	public int count = (int) (Math.random() * 20);

	public Non(String name) {
		this.name = name;

		displayName = "&7" + name;
		this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, displayName);

		spawn();
		SkinManager.skinNPC(npc, name);

		this.non = (Player) npc.getEntity();
		FPSCommand.hideNewNon(this);
		NonManager.nons.add(this);

		CitizensNavigator navigator = (CitizensNavigator) npc.getNavigator();
		navigator.getDefaultParameters()
				.attackDelayTicks((int) (Math.random() * 4 + 4))
				.attackRange(4)
				.updatePathRate(40);
		npc.setProtected(false);

		pickTraits();

		persistence = (Math.random() * 3 + 94) / 100D;
		if(traits.contains(NonTrait.IRON_STREAKER)) persistence -= 100 - persistence;

		respawn(false);
	}

	public void tick() {
		count++;
		if(npc.getEntity() != null && non != npc.getEntity()) FPSCommand.hideNewNon(this);
		non = (Player) npc.getEntity();
		if(!npc.isSpawned()) respawn(false);
		if(npc.isSpawned() && non.getLocation().getY() <= MapManager.currentMap.getY() - 0.1) {
			Location teleportLoc = non.getLocation().clone();
			teleportLoc.setY(MapManager.currentMap.getY() + 1.2);
			non.teleport(teleportLoc);
			return;
		}

		if(nonState != NonState.FIGHTING) {
			if(!npc.isSpawned()) respawn(false);
			if(npc.isSpawned()) {
				npc.getNavigator().setTarget(null, true);
			}
			return;
		}

		if(npc.isSpawned()) {
			if(count % 5 == 0) {
				pickTarget();
//				npc.getNavigator().setTarget(target, true);
				if(target != null && Math.abs(non.getLocation().getY() - MapManager.currentMap.getY()) < 5) {
					target.damage(7, non);
					if(Math.random() < 0.5) PlayerAnimation.ARM_SWING.play(non);
				}
			}
			if(count % 2 == 0 && target != null) Util.faceLocation(non, target.getLocation());
			if(count % 20 == 0) {
				Location midLoc = MapManager.currentMap.getMid();
				double distanceFromMid = Math.sqrt(Math.pow(midLoc.getX() - non.getLocation().getX(), 2) + Math.pow(midLoc.getZ() - non.getLocation().getZ(), 2));
				if(distanceFromMid >= NonManager.MAX_DISTANCE_FROM_MID) {
					non.setHealth(non.getMaxHealth());
					non.teleport(MapManager.currentMap.getNonSpawn());
				}
			}
		} else respawn(false);

		if(target == null || !npc.isSpawned()) return;

		if(count % 7 == 0) {

			if(npc.isSpawned()) {
				Block underneath = non.getLocation().clone().subtract(0, 0.1, 0).getBlock();
				if(underneath.getType() != Material.AIR && underneath.getType() != Material.CARPET) {

					int rand = (int) (Math.random() * 2);
					Location rotLoc = non.getLocation().clone();
					rotLoc.setYaw(non.getLocation().getYaw() + (rand == 0 ? -90 : 90));

					try {
						double distance = target.getLocation().distance(non.getLocation());
						Vector sprintVelo = target.getLocation().toVector().subtract(non.getLocation().toVector())
								.normalize();

						double yVelo = 0.1;
						double xMultiplier = 1;
						if(traits.contains(NonTrait.NO_JUMP)) {
							if(Math.random() < 0.1) {
								yVelo = 0.4;
							} else xMultiplier = 1.3;
						} else yVelo = 0.4;
						if(distance < Math.random() * 1.5 + 1.5) sprintVelo.multiply(0.16 * xMultiplier).setY(yVelo);
						else sprintVelo.multiply(0.4 * xMultiplier).setY(yVelo);
						non.setVelocity(sprintVelo);
					} catch(Exception ignored) {
						AOutput.log("error with non targets (im assuming)");
					}
				}
			}
		}
	}

	public void pickTarget() {

		Player closest = null;
		double closestDistance = 100;
		Location midLoc = MapManager.currentMap.getMid();

		List<Player> nearbyPlayers = new ArrayList<>();
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(onlinePlayer.getWorld() != non.getWorld()) continue;
			if(SpawnManager.isInSpawn(onlinePlayer.getLocation())) continue;
			nearbyPlayers.add(onlinePlayer);
		}
		for(Entity nearbyEntity : non.getWorld().getNearbyEntities(midLoc, 3.5, 3, 3.5)) {
			if(!(nearbyEntity instanceof Player)) continue;
			if(!nearbyPlayers.contains(nearbyEntity)) nearbyPlayers.add((Player) nearbyEntity);
		}

		for(Entity nearbyEntity : nearbyPlayers) {

			if(!(nearbyEntity instanceof Player) || nearbyEntity.getUniqueId().equals(non.getUniqueId())) continue;
			if(nearbyEntity.getWorld() != non.getWorld()) continue;

			double targetDistanceFromMid = Math.sqrt(Math.pow(nearbyEntity.getLocation().getX() - midLoc.getX(), 2) +
					Math.pow(nearbyEntity.getLocation().getZ() - midLoc.getZ(), 2));
			if(targetDistanceFromMid > 8) continue;

			double distance = nearbyEntity.getLocation().distance(non.getLocation());
			if(distance >= closestDistance) continue;

			closest = (Player) nearbyEntity;
			closestDistance = distance;
		}
		target = closest;
	}

	public void setDisabled(Boolean toggled) {
		Location spawnLoc = MapManager.currentMap.getNonSpawn();
		if(toggled) npc.despawn();
		else npc.spawn(spawnLoc);
	}

	public void spawn() {
		Location spawnLoc = MapManager.currentMap.getNonSpawn();
		npc.spawn(spawnLoc);
	}

	public void respawn(boolean fakeKill) {
		if(!fakeKill) nonState = NonState.RESPAWNING;
		Location spawnLoc = MapManager.currentMap.getNonSpawn();

		if(!npc.isSpawned() || non == null) spawn();
		try {

			if(npc.isSpawned() && !fakeKill) {
				non.teleport(spawnLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
			}

		} catch(Exception ignored) {

			ignored.printStackTrace();
			npc.despawn();
			npc.spawn(spawnLoc);
			AOutput.log("non teleportation respawn errored");
		}

		if(npc.isSpawned()) {
			if(!fakeKill) non.setHealth(non.getMaxHealth());

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
		}
		if(!fakeKill) {
			new BukkitRunnable() {
				@Override
				public void run() {
					nonState = NonState.FIGHTING;
				}
			}.runTaskLater(PitSim.INSTANCE, 20L);
		}
	}

	public void pickTraits() {

		if(Math.random() < 0.8) {

			traits.add(NonTrait.NO_JUMP);
		}
		if(Math.random() < 0.08) {

			traits.add(NonTrait.IRON_STREAKER);
		}
	}

	public void rewardKill() {

		non.setHealth(Math.min(non.getHealth() + 3, non.getMaxHealth()));
		EntityPlayer nmsPlayer = ((CraftPlayer) non).getHandle();
		if(nmsPlayer.getAbsorptionHearts() < 8) {
			nmsPlayer.setAbsorptionHearts(Math.min(nmsPlayer.getAbsorptionHearts() + 3, 5));
		}
	}

	public void remove() {

		NonManager.nons.remove(this);
		npc.destroy();
	}
}
