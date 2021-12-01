package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.FPSCommand;
import dev.kyro.pitsim.controllers.BoosterManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.PitEventManager;
import dev.kyro.pitsim.enums.NonState;
import dev.kyro.pitsim.enums.NonTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.npc.ai.CitizensNavigator;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.minecraft.server.v1_8_R3.EntityPlayer;
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
	public int count = 0;

	public Non(String name) {
		this.name = name;

		int rand = (int) (Math.random() * 6);
		String color;
		switch(rand) {
			case 0:
				color = "&7";
				break;
			case 1:
				color = "&9";
				break;
			case 2:
				color = "&3";
				break;
			case 3:
				color = "&2";
				break;
			case 4:
				color = "&a";
				break;
			default:
				color = "&e";
				break;
		}
//		displayName = "&7[" + color + (rand * 10 + (int) (Math.random() * 10)) + "&7]&7" + " " + name;
		displayName = "&7" + name;
		this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, displayName);
		if(!PitEventManager.majorEvent) spawn();
		this.non = (Player) npc.getEntity();
		FPSCommand.hideNewNon(this);
		NonManager.nons.add(this);

		CitizensNavigator navigator = (CitizensNavigator) npc.getNavigator();
		navigator.getDefaultParameters()
				.attackDelayTicks((int) (Math.random() * 4 + 3))
				.attackRange(4);
		npc.setProtected(false);

		pickTraits();

		persistence = (Math.random() * 3 + 94) / 100D;
		if(traits.contains(NonTrait.IRON_STREAKER)) persistence -= 100 - persistence;

		if(!PitEventManager.majorEvent)respawn();
		skin(name);
	}

	public void tick() {
		count++;
		if(npc.getEntity() != null && non != npc.getEntity()) FPSCommand.hideNewNon(this);
		non = (Player) npc.getEntity();
		if(!npc.isSpawned() && !PitEventManager.majorEvent) respawn();
		if(npc.isSpawned() && non.getLocation().getY() <= MapManager.getY() - 0.1) {
			Location teleportLoc = non.getLocation().clone();
			teleportLoc.setY(MapManager.getY() + 1.2);
			non.teleport(teleportLoc);
			return;
		}

		if(nonState != NonState.FIGHTING) {
			if(!npc.isSpawned()) respawn();
			if(npc.isSpawned()) {
				npc.getNavigator().setTarget(null, true);
			}
			return;
		}

		if(npc.isSpawned()) {
			if(count % 4 == 0) {
				pickTarget();
				npc.getNavigator().setTarget(target, true);
			}
		} else respawn();

//		if(traits.contains(NonTrait.IRON_STREAKER) && npc.isSpawned())
//				Misc.applyPotionEffect((Player) non, PotionEffectType.DAMAGE_RESISTANCE, 9999, 1, true, false);
//		non.setHealth(non.getMaxHealth());

		if(target == null && !npc.isSpawned()) return;

		if(count % 3 == 0 && (!traits.contains(NonTrait.NO_JUMP)) || Math.random() < 0.05) {

			if(npc.isSpawned()) {
			Block underneath = non.getLocation().clone().subtract(0, 0.2, 0).getBlock();
				if(underneath.getType() != Material.AIR && underneath.getType() != Material.CARPET) {

					int rand = (int) (Math.random() * 2);
					Location rotLoc = non.getLocation().clone();
					rotLoc.setYaw(non.getLocation().getYaw() + (rand == 0 ? -90 : 90));

					try {
						double distance = target.getLocation().distance(non.getLocation());
						Vector sprintVelo = target.getLocation().toVector().subtract(non.getLocation().toVector())
								.normalize();

						if(distance < Math.random() * 1.5 + 1.5) sprintVelo.multiply(-0.16).setY(0.4);
						else sprintVelo.multiply(0.4).setY(0.4);
						non.setVelocity(sprintVelo);
					} catch(Exception ignored) {
						System.out.println("error with non targets (im assuming)");
					}
				}
			}
		}
	}

	public void pickTarget() {

//		if(target != null && Math.random() < persistence) return;

		Player closest = null;
		double closestDistance = 100;
		Location midLoc = MapManager.getMid();
		for(Entity nearbyEntity : non.getWorld().getNearbyEntities(midLoc, 5, 5, 5)) {

			if(!(nearbyEntity instanceof Player) || nearbyEntity.getUniqueId().equals(non.getUniqueId())) continue;
			double targetDistanceFromMid = Math.sqrt(Math.pow(nearbyEntity.getLocation().getX() - midLoc.getX(), 2) +
					Math.pow(nearbyEntity.getLocation().getZ() - midLoc.getZ(), 2));
			if(targetDistanceFromMid > 9) continue;

			double distance = nearbyEntity.getLocation().distance(non.getLocation());
			if(distance >= closestDistance) continue;

			closest = (Player) nearbyEntity;
			closestDistance = distance;
		}
		target = closest;
	}

	public void setDisabled(Boolean toggled) {
		Location spawnLoc = MapManager.getNonSpawn();
		if(toggled) npc.despawn();
		else npc.spawn(spawnLoc);
	}

	public void spawn() {
		Location spawnLoc = MapManager.getNonSpawn();
		npc.spawn(spawnLoc);
	}

	public void respawn() {

		nonState = NonState.RESPAWNING;
		Location spawnLoc = MapManager.getNonSpawn();
		Booster booster = BoosterManager.getBooster("chaos");
		if(Math.random() < 0.5 && booster.isActive()) spawnLoc.add(0, -10, 0);

		if(!npc.isSpawned() && !PitEventManager.majorEvent) spawn();
		try {

			if(npc.isSpawned()) {
				non.teleport(spawnLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
			}

		} catch(Exception ignored) {

			ignored.printStackTrace();
			npc.despawn();
			npc.spawn(spawnLoc);
			System.out.println("non teleportation respawn errored");
		}


		if(npc.isSpawned()) {
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
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				nonState = NonState.FIGHTING;
			}
		}.runTaskLater(PitSim.INSTANCE, 20L);
	}

	public void pickTraits() {


		if(Math.random() < 0.7) {

			traits.add(NonTrait.NO_JUMP);
		}
		if(Math.random() < 0.10) {

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

	public void skin(String name) {
		npc.data().set(NPC.PLAYER_SKIN_UUID_METADATA, name);
		npc.data().set(NPC.PLAYER_SKIN_USE_LATEST, false);
		if (npc.isSpawned()) {
			SkinnableEntity skinnable = (SkinnableEntity) npc.getEntity();
			if (skinnable != null) {
				skinnable.setSkinName(name);
			}
		}
	}
}
