package net.pitsim.pitsim.controllers.objects;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.aitems.MysticFactory;
import net.pitsim.pitsim.controllers.*;
import net.pitsim.pitsim.enchants.overworld.Hearts;
import net.pitsim.pitsim.enchants.overworld.RetroGravityMicrocosm;
import net.pitsim.pitsim.enums.MysticType;
import net.pitsim.pitsim.enums.PantColor;
import net.pitsim.pitsim.misc.MinecraftSkin;
import net.pitsim.pitsim.misc.Misc;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.util.Util;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class Hopper {
	public NPC npc;
	public Player hopper;
	public String name;
	public Type type;
	public Player target;
	public Player judgementPlayer;
	public List<UUID> team = new ArrayList<>();
	public int count = 0;

	public boolean lockedToTarget = false;
	public boolean canHitOtherHoppers = false;
	public long lastHitTarget;
	public double speed = Math.random() * 0.05 + 0.04;

	public double switchBias = Math.random() * 0.04 + 0.02;
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

		MinecraftSkin skin = MinecraftSkin.getSkin(name);
		if(skin == null) throw new RuntimeException("Skin not found: " + name);
		npc.spawn(target.getLocation());
		SkinManager.skinNPC(npc, skin);
		hopper = (Player) npc.getEntity();

		npc.setProtected(false);

		Equipment equipment = npc.getTrait(Equipment.class);
		for(Map.Entry<Equipment.EquipmentSlot, ItemStack> entry : type.getEquipment().entrySet())
			equipment.set(entry.getKey(), entry.getValue());
		if(type.isGSet) {
			int maxHealth = 28;
			int heartsLvl = EnchantManager.getEnchantLevel(hopper.getEquipment().getLeggings(), Hearts.INSTANCE);
			if(heartsLvl != 0) maxHealth += Hearts.getExtraHealth(heartsLvl);

			hopper.setMaxHealth(maxHealth);
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

		if(target != null && target.getWorld() == hopper.getWorld() && target.getLocation().distance(hopper.getLocation()) < 6) {
			if(type == Type.VENOM) {
				if(count % 4 == 0) {
					for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						if(!onlinePlayer.getWorld().equals(hopper.getWorld())) continue;
						PacketPlayOutEntityEquipment equipment = new PacketPlayOutEntityEquipment(hopper.getEntityId(), 0,
								CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND_SWORD)));
						((CraftPlayer) onlinePlayer).getHandle().playerConnection.sendPacket(equipment);
					}
					setBlocking(true);
				} else if(count % 4 == 2) {
					for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						if(!onlinePlayer.getWorld().equals(hopper.getWorld())) continue;
						PacketPlayOutEntityEquipment equipment = new PacketPlayOutEntityEquipment(hopper.getEntityId(), 0,
								CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND_SPADE)));
						((CraftPlayer) onlinePlayer).getHandle().playerConnection.sendPacket(equipment);
					}
				}
			}

			if(count % 5 == 0) {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if(!onlinePlayer.getWorld().equals(hopper.getWorld())) continue;
					PacketPlayOutAnimation attackPacket = new PacketPlayOutAnimation(((CraftEntity) hopper).getHandle(), 0);
					((CraftPlayer) onlinePlayer).getHandle().playerConnection.sendPacket(attackPacket);
				}
			}
			if(count % 5 == 4) {
				setBlocking(false);
			} else {
				setBlocking(true);
			}
		} else if(count % 5 == 4) {
			setBlocking(false);
		}

		if(count % 5 == 0 && shouldJump()) {
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
				Vector velocity = npcVelo.add(dir.setY(0).normalize().multiply(speed + 0.02));
				if(!shouldJump()) velocity.multiply(1.2);
				entity.setVelocity(velocity);
			} else {
				Location rotLoc = entity.getLocation().clone();
				rotLoc.setYaw(entity.getLocation().getYaw() + (dirClockwise ? -70 : 70));
				Vector velocity = npcVelo.add(rotLoc.getDirection().setY(0).normalize().multiply(speed))
						.add(dir.setY(0).normalize().multiply(distanceFromOptimal / 20D));
				if(!shouldJump()) velocity.multiply(1.2);
				entity.setVelocity(velocity);
			}
		}

		boolean isCritical = Misc.isCritical(hopper);
		if(count > 40) {
			for(Entity nearbyEntity : hopper.getNearbyEntities(5, 5, 5)) {
				if(!(nearbyEntity instanceof Player) || team.contains(nearbyEntity.getUniqueId())) continue;
				Player hitTarget = (Player) nearbyEntity;
				if(SpawnManager.isInSpawn(hitTarget)) continue;

				double range = 3.7;
//				if(!Misc.isAirOrNull(hopper.getEquipment().getLeggings()) &&
//						EnchantManager.getEnchantLevel(hopper.getEquipment().getLeggings(), EnchantManager.getEnchant("regularity")) != 0)
//					range -= 0.7;

				double damage = getDamage(hopper.getItemInHand());
				if(isCritical) damage *= 1.5;
				if(target != null && target != hitTarget) {
					damage /= 2;
					range -= 0.5;
				}

				if(hopper.getWorld() != hitTarget.getWorld()) continue;
				if(hopper.getLocation().distance(hitTarget.getLocation()) > range) continue;
				if(!canHitOtherHoppers && HopperManager.isHopper(hitTarget)) continue;
				if(NonManager.getNon(hitTarget) != null && Math.random() > 0.05) continue;
				if(type == Type.VENOM && !DamageManager.hitCooldownList.contains(hitTarget) && Math.random() < 0.05) {
					hitTarget.setNoDamageTicks(0);
					DamageManager.createIndirectAttack(hopper, hitTarget, damage * 0.5);
				} else {
					DamageManager.createDirectAttack(hopper, hitTarget, damage);
//					hitTarget.damage(damage, hopper);
				}
				if(hitTarget == target) lastHitTarget = System.currentTimeMillis();
			}
		}
	}

	public void setBlocking(boolean blocking) {
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(!onlinePlayer.getWorld().equals(hopper.getWorld())) continue;

			PacketContainer packet = PitSim.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ENTITY_METADATA);
			packet.getIntegers().write(0, hopper.getEntityId());
			WrappedDataWatcher watcher = new WrappedDataWatcher();
			watcher.setEntity(hopper);
			watcher.setObject(0, (byte) (blocking ? 0x10 : 0x0));
			packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

			try {
				PitSim.PROTOCOL_MANAGER.sendServerPacket(onlinePlayer, packet);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void remove() {
		HopperManager.toRemove.add(this);
		npc.destroy();
		hopper.remove();
	}

	public double getDamage(ItemStack weapon) {
		double damage = 1;
		if(weapon != null) {
			switch(weapon.getType()) {
				case IRON_SWORD:
					damage = 7;
					break;
				case DIAMOND_SWORD:
					damage = 9.6;
					break;
				case DIAMOND_SPADE:
					damage = 9;
					break;
				case GOLD_SWORD:
					damage = 7.5;
					break;
			}
		}
		return damage;
	}

	public boolean shouldJump() {
		if(target == null || !type.isGSet) return true;
		int rgmLevel = EnchantManager.getEnchantLevel(target, RetroGravityMicrocosm.INSTANCE);
		return rgmLevel == 0;
	}

	public enum Type {
		CHAIN("&7Chain Hopper", "chain", "&7", 0.5, false),
		DIAMOND("&9Diamond Hopper", "diamond", "&9", 0.5, false),
		MYSTIC("&eMystic Hopper", "mystic", "&e", 0.5, false),
		VENOM("&2Venom Hopper", "venom", "&2", 0.5, false),
		REG("&6GSet Hopper", "reg", "&6", 0.5, true),
		RGM("&6GSet Hopper", "rgm", "&6", 0.5, true);

		public String name;
		public String refName;
		public String colorCode;
		public double damageMultiplier;
		public boolean isGSet;

		Type(String name, String refName, String colorCode, double damageMultiplier, boolean isGSet) {
			this.name = name;
			this.refName = refName;
			this.colorCode = colorCode;
			this.damageMultiplier = damageMultiplier;
			this.isGSet = isGSet;
		}

		public static Type getRandomGSet() {
			List<Type> gSetTypes = new ArrayList<>();
			for(Type type : values()) if(type.isGSet) gSetTypes.add(type);
			return gSetTypes.get(new Random().nextInt(gSetTypes.size()));
		}

		public static Type getType(String refName) {
			for(Type value : values()) if(value.refName.equalsIgnoreCase(refName)) return value;
			return null;
		}

		public Map<Equipment.EquipmentSlot, ItemStack> getEquipment() {
			Map<Equipment.EquipmentSlot, ItemStack> equipmentMap = new HashMap<>();
			ItemStack sword = MysticFactory.getFreshItem(MysticType.SWORD, null);
			ItemStack pants = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.getNormalRandom());
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
					try {
						sword = EnchantManager.addEnchant(sword, EnchantManager.getEnchant("sharpness"), (int) (Math.random() * 11), false);
						sword = EnchantManager.addEnchant(sword, EnchantManager.getEnchant("lifesteal"), 2, false);
						if(Math.random() < 0.25) sword = EnchantManager.addEnchant(sword, EnchantManager.getEnchant("perun"), 3, false);

						pants = EnchantManager.addEnchant(pants, EnchantManager.getEnchant("mirror"), 2, false);
						pants = EnchantManager.addEnchant(pants, EnchantManager.getEnchant("protection"), 3, false);
						if(Math.random() < 0.25) {
							pants = EnchantManager.addEnchant(pants, EnchantManager.getEnchant("rgm"), 1, false);
						} else if(Math.random() < 0.25) {
							pants = EnchantManager.addEnchant(pants, EnchantManager.getEnchant("regularity"), 1, false);
						}
					} catch(Exception ignored) {}

					equipmentMap.put(Equipment.EquipmentSlot.HAND, sword);
					equipmentMap.put(Equipment.EquipmentSlot.HELMET, new ItemStack(Math.random() < 0.25 ? Material.DIAMOND_HELMET : Material.IRON_HELMET));
					equipmentMap.put(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.DIAMOND_CHESTPLATE));
					equipmentMap.put(Equipment.EquipmentSlot.LEGGINGS, pants);
					equipmentMap.put(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.DIAMOND_BOOTS));
					break;
				case VENOM:
					ItemStack venoms = MysticFactory.getFreshItem(MysticType.PANTS, PantColor.DARK);
					try {
						venoms = EnchantManager.addEnchant(venoms, EnchantManager.getEnchant("venom"), 1, false);
					} catch(Exception ignored) {}

					equipmentMap.put(Equipment.EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_SWORD));
					equipmentMap.put(Equipment.EquipmentSlot.HELMET, new ItemStack(Math.random() < 0.25 ? Material.DIAMOND_HELMET : Material.IRON_HELMET));
					equipmentMap.put(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.DIAMOND_CHESTPLATE));
					equipmentMap.put(Equipment.EquipmentSlot.LEGGINGS, venoms);
					equipmentMap.put(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.DIAMOND_BOOTS));
					break;
				case REG:
					try {
//						sword = EnchantManager.addEnchant(sword, EnchantManager.getEnchant("sharp"), 13, false);
						sword = EnchantManager.addEnchant(sword, EnchantManager.getEnchant("perun"), 3, false);
						sword = EnchantManager.addEnchant(sword, EnchantManager.getEnchant("comboheal"), 3, false);

						pants = EnchantManager.addEnchant(pants, EnchantManager.getEnchant("cf"), 3, false);
						pants = EnchantManager.addEnchant(pants, EnchantManager.getEnchant("toxic"), 40, false);
						pants = EnchantManager.addEnchant(pants, EnchantManager.getEnchant("mirror"), 3, false);
						pants = EnchantManager.addEnchant(pants, EnchantManager.getEnchant("regularity"), 5, false);
					} catch(Exception ignored) {}

					equipmentMap.put(Equipment.EquipmentSlot.HAND, sword);
					equipmentMap.put(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.GOLD_HELMET));
					equipmentMap.put(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.DIAMOND_CHESTPLATE));
					equipmentMap.put(Equipment.EquipmentSlot.LEGGINGS, pants);
					equipmentMap.put(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.DIAMOND_BOOTS));
					break;
				case RGM:
					try {
						sword = EnchantManager.addEnchant(sword, EnchantManager.getEnchant("lifesteal"), 2, false);
						sword = EnchantManager.addEnchant(sword, EnchantManager.getEnchant("bill"), 3, false);

						pants = EnchantManager.addEnchant(pants, EnchantManager.getEnchant("cf"), 3, false);
						pants = EnchantManager.addEnchant(pants, EnchantManager.getEnchant("toxic"), 40, false);
						pants = EnchantManager.addEnchant(pants, EnchantManager.getEnchant("mirror"), 3, false);
						pants = EnchantManager.addEnchant(pants, EnchantManager.getEnchant("rgm"), 5, false);
					} catch(Exception ignored) {}

					equipmentMap.put(Equipment.EquipmentSlot.HAND, sword);
					equipmentMap.put(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.GOLD_HELMET));
					equipmentMap.put(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.DIAMOND_CHESTPLATE));
					equipmentMap.put(Equipment.EquipmentSlot.LEGGINGS, pants);
					equipmentMap.put(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.DIAMOND_BOOTS));
					break;
			}
			return equipmentMap;
		}
	}
}
