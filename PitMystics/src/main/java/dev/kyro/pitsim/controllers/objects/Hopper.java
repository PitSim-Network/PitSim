package dev.kyro.pitsim.controllers.objects;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.HopperManager;
import dev.kyro.pitsim.controllers.SpawnManager;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.SpawnNPCs;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.util.Util;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Hopper {
	public NPC npc;
	public Player hopper;
	public String name;
	public Type type;
	public Player target;
	public List<UUID> team = new ArrayList<>();
	public int count = 0;

	public boolean lockedToTarget = false;
	public boolean canHitOtherHoppers = false;
	public long lastHitTarget;
	public double speed = Math.random() < 0.1 ? 0.12 : 5 * (Math.random() / 100D) + 0.04;

	public double switchBias = 4 * (Math.random() / 100D) + 0.02;
	public boolean dirClockwise = true;

	public Hopper(String name, Type type) {
		this.name = name;
		this.type = type;
		start();
	}

	public Hopper(String name, Type type, Player target) {
		this.name = name;
		this.type = type;
		this.target = target;
		lockedToTarget = true;
		lastHitTarget = System.currentTimeMillis();
		start();
	}

	public void start() {
		npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, type.colorCode + name);

		npc.spawn(target.getLocation());
		SpawnNPCs.skin(npc, name);
		hopper = (Player) npc.getEntity();

		npc.setProtected(false);

		Equipment equipment = npc.getTrait(Equipment.class);
		for(Map.Entry<Equipment.EquipmentSlot, ItemStack> entry : type.getEquipment().entrySet()) equipment.set(entry.getKey(), entry.getValue());
		if(type == Type.GSET) {
			hopper.setMaxHealth(28);
			hopper.setHealth(hopper.getMaxHealth());
		} else if(type == Type.VENOM) {
			hopper.setMaxHealth(24);
			hopper.setHealth(hopper.getMaxHealth());
		}
	}

	public void tick() {
		hopper = (Player) npc.getEntity();
		if(count++ == 0 || !npc.isSpawned()) return;

		if(lastHitTarget + 5000 < System.currentTimeMillis()) {
			hopper.teleport(target);
			lastHitTarget = System.currentTimeMillis();
		}

		if(count % 5 == 0) {
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				if(!onlinePlayer.getWorld().equals(hopper.getWorld()));
				PacketPlayOutAnimation attackPacket = new PacketPlayOutAnimation(((CraftEntity)hopper).getHandle(), 0);
				((CraftPlayer) onlinePlayer).getHandle().playerConnection.sendPacket(attackPacket);
			}
		}
		if(count % 5 == 4) {
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				if(!onlinePlayer.getWorld().equals(hopper.getWorld()));

				PacketContainer packet = PitSim.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ENTITY_METADATA);
				packet.getIntegers().write(0, hopper.getEntityId());
				WrappedDataWatcher watcher = new WrappedDataWatcher();
				watcher.setEntity(hopper);
				watcher.setObject(0, (byte) (0x0));
				packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

				try {
					PitSim.PROTOCOL_MANAGER.sendServerPacket(onlinePlayer, packet);
				} catch(InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		} else {
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				PacketContainer packet = PitSim.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ENTITY_METADATA);
				packet.getIntegers().write(0, hopper.getEntityId());
				WrappedDataWatcher watcher = new WrappedDataWatcher();
				watcher.setEntity(hopper);
				watcher.setObject(0, (byte) (0x10));
				packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

				try {
					PitSim.PROTOCOL_MANAGER.sendServerPacket(onlinePlayer, packet);
				} catch(InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

		if(count % 5 == 0) {
			Block underneath = hopper.getLocation().clone().subtract(0, 0.2, 0).getBlock();
			if(underneath.getType() != Material.AIR) {
				hopper.setVelocity(new Vector(0, 0.42, 0));
			}
		}

		if(target != null) Util.faceEntity(hopper, target);

		if(Math.random() < switchBias) dirClockwise = !dirClockwise;

		Entity entity = hopper;
		Vector npcVelo = entity.getVelocity();
		Vector dir = entity.getLocation().getDirection();
		double distanceFromOptimal = hopper.getLocation().distance(target.getLocation()) - 2.7;

		if(target != null) {
			if(target.getLocation().distance(hopper.getLocation()) > 6) {

				entity.setVelocity(npcVelo.add(dir.setY(0).normalize().multiply(speed + 0.02)));
			} else {

				Location rotLoc = entity.getLocation().clone();
				rotLoc.setYaw(entity.getLocation().getYaw() + (dirClockwise ? - 70 : 70));
				entity.setVelocity(npcVelo.add(rotLoc.getDirection().setY(0).normalize().multiply(speed))
						.add(dir.setY(0).normalize().multiply(distanceFromOptimal / 20D)));
			}
		}

		boolean isCritical = Misc.isCritical(hopper);
		if(count > 40) {
			for(Entity nearbyEntity : hopper.getNearbyEntities(5, 5, 5)) {
				if(!(nearbyEntity instanceof Player) || team.contains(nearbyEntity.getUniqueId())) continue;
				Player hitTarget = (Player) nearbyEntity;
				if(SpawnManager.isInSpawn(hitTarget.getLocation())) continue;

				double range = 3.7;
				if(!Misc.isAirOrNull(hopper.getEquipment().getLeggings()) &&
						EnchantManager.getEnchantLevel(hopper.getEquipment().getLeggings(), EnchantManager.getEnchant("regularity")) != 0) range -= 0.7;

				double damage = 7.5;
				if(isCritical) damage *= 1.5;
				if(target != null && target != hitTarget) {
					damage /= 2;
					range -= 0.5;
				}

				if(hopper.getLocation().distance(hitTarget.getLocation()) > range) continue;
				if(!canHitOtherHoppers && HopperManager.isHopper(hitTarget)) continue;
				hitTarget.damage(damage, hopper);
				if(hitTarget == target) lastHitTarget = System.currentTimeMillis();
			}
		}
	}

	public void remove() {

		HopperManager.toRemove.add(this);
		npc.destroy();
		hopper.remove();
	}

	public double getDamage(ItemStack weapon) {
		if(weapon == null) return 1;
		switch(weapon.getType()) {
			case IRON_SWORD:
				return 7;
			case DIAMOND_SWORD:
				return 9.6;
			case GOLD_SWORD:
				return 7.5;
		}
		return 1;
	}

	public enum Type {
		CHAIN("&7Chain Hopper", "chain", "&7", 0.5),
		DIAMOND("&9Diamond Hopper", "diamond", "&9", 0.5),
		MYSTIC("&eMystic Hopper", "mystic", "&e", 0.6),
		VENOM("&2Venom Hopper", "venom", "&2", 0.6),
		GSET("&6GSet Hopper", "gset", "&6", 0.6);

		public String name;
		public String refName;
		public String colorCode;
		public double damageMultiplier;

		Type(String name, String refName, String colorCode, double damageMultiplier) {
			this.name = name;
			this.refName = refName;
			this.colorCode = colorCode;
			this.damageMultiplier = damageMultiplier;
		}

		public static Type getType(String refName) {
			for(Type value : values()) if(value.refName.equalsIgnoreCase(refName)) return value;
			return null;
		}

		public Map<Equipment.EquipmentSlot, ItemStack> getEquipment() {
			Map<Equipment.EquipmentSlot, ItemStack> equipmentMap = new HashMap<>();
			switch(this) {
				case CHAIN:
					equipmentMap.put(Equipment.EquipmentSlot.HAND, new ItemStack(Material.IRON_SWORD));
					equipmentMap.put(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.CHAINMAIL_CHESTPLATE));
					equipmentMap.put(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Material.CHAINMAIL_LEGGINGS));
					equipmentMap.put(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.CHAINMAIL_BOOTS));
					int rand = (int) (Math.random() * 3);
					switch(rand) {
						case 0:
							equipmentMap.put(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.IRON_CHESTPLATE));
							break;
						case 1:
							equipmentMap.put(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Material.IRON_LEGGINGS));
							break;
						case 2:
							equipmentMap.put(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.IRON_BOOTS));
							break;
					}
					break;
				case DIAMOND:
					equipmentMap.put(Equipment.EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_SWORD));
					equipmentMap.put(Equipment.EquipmentSlot.HELMET, new ItemStack(Math.random() < 0.25 ? Material.DIAMOND_HELMET : Material.IRON_HELMET));
					equipmentMap.put(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.DIAMOND_CHESTPLATE));
					equipmentMap.put(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Math.random() < 0.5 ? Material.DIAMOND_LEGGINGS : Material.IRON_LEGGINGS));
					equipmentMap.put(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.DIAMOND_BOOTS));
					break;
				case MYSTIC:
					ItemStack mysticSword = FreshCommand.getFreshItem(MysticType.SWORD, null);
					try {
						mysticSword = EnchantManager.addEnchant(mysticSword, EnchantManager.getEnchant("sharpness"), (int) (Math.random() * 6), false);
						mysticSword = EnchantManager.addEnchant(mysticSword, EnchantManager.getEnchant("lifesteal"), 3, false);
						if(Math.random() < 0.25) mysticSword = EnchantManager.addEnchant(mysticSword, EnchantManager.getEnchant("perun"), 1, false);
					} catch(Exception ignored) { }
					ItemStack mysticPants = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.getNormalRandom());
					try {
						mysticPants = EnchantManager.addEnchant(mysticPants, EnchantManager.getEnchant("mirror"), 2, false);
						mysticPants = EnchantManager.addEnchant(mysticPants, EnchantManager.getEnchant("protection"), 3, false);
						if(Math.random() < 0.25) {
							mysticPants = EnchantManager.addEnchant(mysticPants, EnchantManager.getEnchant("rgm"), 1, false);
						} else if(Math.random() < 0.25) {
							mysticPants = EnchantManager.addEnchant(mysticPants, EnchantManager.getEnchant("regularity"), 1, false);
						}
					} catch(Exception ignored) { }

					equipmentMap.put(Equipment.EquipmentSlot.HAND, mysticSword);
					equipmentMap.put(Equipment.EquipmentSlot.HELMET, new ItemStack(Math.random() < 0.25 ? Material.DIAMOND_HELMET : Material.IRON_HELMET));
					equipmentMap.put(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.DIAMOND_CHESTPLATE));
					equipmentMap.put(Equipment.EquipmentSlot.LEGGINGS, mysticPants);
					equipmentMap.put(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.DIAMOND_BOOTS));
					break;
				case VENOM:
					ItemStack venoms = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.DARK);
					try {
						venoms = EnchantManager.addEnchant(venoms, EnchantManager.getEnchant("venom"), 1, false);
					} catch(Exception ignored) { }

					equipmentMap.put(Equipment.EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_SWORD));
					equipmentMap.put(Equipment.EquipmentSlot.HELMET, new ItemStack(Math.random() < 0.25 ? Material.DIAMOND_HELMET : Material.IRON_HELMET));
					equipmentMap.put(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.DIAMOND_CHESTPLATE));
					equipmentMap.put(Equipment.EquipmentSlot.LEGGINGS, venoms);
					equipmentMap.put(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.DIAMOND_BOOTS));
					break;
				case GSET:
					ItemStack gsetSword = FreshCommand.getFreshItem(MysticType.SWORD, null);
					double random = Math.random();
					try {
						if(random < 0.33) {
							gsetSword = EnchantManager.addEnchant(gsetSword, EnchantManager.getEnchant("bill"), 1, false);
							gsetSword = EnchantManager.addEnchant(gsetSword, EnchantManager.getEnchant("perun"), 3, false);
							gsetSword = EnchantManager.addEnchant(gsetSword, EnchantManager.getEnchant("comboheal"), 3, false);
						} else if (random < 0.67) {
							gsetSword = EnchantManager.addEnchant(gsetSword, EnchantManager.getEnchant("lifesteal"), 3, false);
							gsetSword = EnchantManager.addEnchant(gsetSword, EnchantManager.getEnchant("bill"), 3, false);
						} else {
							gsetSword = EnchantManager.addEnchant(gsetSword, EnchantManager.getEnchant("lifesteal"), 2, false);
//							gsetSword = EnchantManager.addEnchant(gsetSword, EnchantManager.getEnchant("bill"), 1, false);
							gsetSword = EnchantManager.addEnchant(gsetSword, EnchantManager.getEnchant("painfocus"), 10, false);
						}
					} catch(Exception ignored) { }
					ItemStack gsetPants = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.getNormalRandom());
					try {

						gsetPants = EnchantManager.addEnchant(gsetPants, EnchantManager.getEnchant("soli"), 3, false);
						gsetPants = EnchantManager.addEnchant(gsetPants, EnchantManager.getEnchant("cf"), 3, false);
						if(random < 0.33) {
							gsetPants = EnchantManager.addEnchant(gsetPants, EnchantManager.getEnchant("mirror"), 3, false);
							gsetPants = EnchantManager.addEnchant(gsetPants, EnchantManager.getEnchant("regularity"), 3, false);
						} else if(random < 0.66){
							gsetPants = EnchantManager.addEnchant(gsetPants, EnchantManager.getEnchant("mirror"), 3, false);
							gsetPants = EnchantManager.addEnchant(gsetPants, EnchantManager.getEnchant("rgm"), 3, false);
						} else {
							gsetPants = EnchantManager.addEnchant(gsetPants, EnchantManager.getEnchant("soli"), 3, false);
							gsetPants = EnchantManager.addEnchant(gsetPants, EnchantManager.getEnchant("cf"), 3, false);
						}
					} catch(Exception ignored) { }

					equipmentMap.put(Equipment.EquipmentSlot.HAND, gsetSword);
					equipmentMap.put(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.GOLD_HELMET));
					equipmentMap.put(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.DIAMOND_CHESTPLATE));
					equipmentMap.put(Equipment.EquipmentSlot.LEGGINGS, gsetPants);
					equipmentMap.put(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.DIAMOND_BOOTS));
					break;
			}
			return equipmentMap;
		}
	}
}
