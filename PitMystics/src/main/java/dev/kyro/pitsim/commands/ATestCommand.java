package dev.kyro.pitsim.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.PantColor;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.util.Util;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ATestCommand implements CommandExecutor {
	public static List<NPC> hoppers = new ArrayList<>();

	static {

//		hoppers.add("MajorEvent");
//		hoppers.add("MinorEvent");
//		hoppers.add("Yhellow");
//		hoppers.add("GodTierPvper");
//		hoppers.add("enterusername");
//		hoppers.add("OnlySpooky");
//		hoppers.add("Cpl_Horatius");
//		hoppers.add("Tyler_P13");
//		hoppers.add("Tuumba");
//		hoppers.add("HESTRUE");
//		hoppers.add("hazelis");
//		hoppers.add("ImJustAFish");
//		hoppers.add("OnlySkelett");
//		hoppers.add("NilsZ_ZT");
//		hoppers.add("MasterDiets");
//		hoppers.add("Saito");
//		hoppers.add("KindEinesTeufels");
//		hoppers.add("OTDX");
//		hoppers.add("DomoZz");
//		hoppers.add("iStarkOMG");
//		hoppers.add("e_pot");
//		hoppers.add("RipPlay");
//		hoppers.add("Nightloot");
//		hoppers.add("t_H4nKzz_M1n0R47");
//		hoppers.add("Kymp");
//		hoppers.add("Ferutii");
//		hoppers.add("Tyska33");
//		hoppers.add("perkperk");
//		hoppers.add("SkyblocksGuild");
//		hoppers.add("princelink");
//		hoppers.add("KyroKrypt");
//		hoppers.add("ZexorPVP");
//		hoppers.add("TSM_Dauquen");
//		hoppers.add("xStateofmind");
//		hoppers.add("Skunker");
//		hoppers.add("Arti_Creep");
//		hoppers.add("M0HAMM3D17");
//		hoppers.add("bubulS");
//		hoppers.add("o6am");
//		hoppers.add("qre");
//		hoppers.add("Dark4ever");

//		hoppers.add("wiji1");
//		hoppers.add("KyroKrypt");
//		hoppers.add("Muruseni");
//		hoppers.add("wackful");
//		hoppers.add("Bobbybenny12");
//		hoppers.add("Troving");
//		hoppers.add("lkjv");
//		hoppers.add("Xavier9346");
//		hoppers.add("FreeJUSTHUNTINGU");
//		hoppers.add("Zsombor_1");
//		hoppers.add("AddisonDj");
//		hoppers.add("Airpark");
//		hoppers.add("1Ror");
//		hoppers.add("Tinykloon");
//		hoppers.add("_MarcusW_");
//		hoppers.add("UpdateGame");
//		hoppers.add("_A1Sauce");
//		hoppers.add("GRIMPIT");
//		hoppers.add("GANGMEMBER7PUMP");
//		hoppers.add("woolens");
//		hoppers.add("Qtj_ALT");
//		hoppers.add("perungod");
//		hoppers.add("memescientist");
//		hoppers.add("souliow");
//		hoppers.add("PitSim");
//		hoppers.add("el24");
	}

	public static Player targetPlayer = null;

	public static boolean isHopper(Player player) {
		for(NPC hopper : hoppers) if(hopper.getEntity() == player) return true;
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		if(args.length < 1) {

			AOutput.error(player, "Usage: /atest <target>");
			return false;
		}
		String target = args[0];
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {

			if(!onlinePlayer.getName().equalsIgnoreCase(target)) continue;
			targetPlayer = onlinePlayer;
			break;
		}
		if(targetPlayer == null) {

			AOutput.error(player, "That player is not online");
			return false;
		}

		callHopper(targetPlayer, "PayForTruce");
//		new BukkitRunnable() {
//			int count = 0;
//			@Override
//			public void run() {
//				if(hoppers.get(count) == null) {
//
//					cancel();
//					return;
//				}
//				callHopper(targetPlayer, hoppers.get(count++));
//			}
//		}.runTaskTimer(PitSim.INSTANCE, 0L, 3L);
		return false;
	}

	public void callHopper(Player player, String name) {

		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
		hoppers.add(npc);
		if(!npc.isSpawned()) npc.spawn(player.getLocation());

		LookClose lookClose = new LookClose();
//		lookClose.lookClose(true);
//		npc.addTrait(lookClose);

		npc.setProtected(false);

		Equipment equipment = npc.getTrait(Equipment.class);
		ItemStack sword = FreshCommand.getFreshItem(MysticType.SWORD, null);
		try {
			sword = EnchantManager.addEnchant(sword, EnchantManager.getEnchant("bill"), 1, false);
			sword = EnchantManager.addEnchant(sword, EnchantManager.getEnchant("perun"), 3, false);
			sword = EnchantManager.addEnchant(sword, EnchantManager.getEnchant("lifesteal"), 3, false);
		} catch(Exception ignored) { }
		ItemStack pants = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.getNormalRandom());
		try {
			pants = EnchantManager.addEnchant(pants, EnchantManager.getEnchant("mirror"), 3, false);
			pants = EnchantManager.addEnchant(pants, EnchantManager.getEnchant("protection"), 8, false);
			pants = EnchantManager.addEnchant(pants, EnchantManager.getEnchant("peroxide"), 3, false);
		} catch(Exception ignored) { }

		equipment.set(Equipment.EquipmentSlot.HAND, sword);

		if(Math.random() < 0.5) {
			equipment.set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.DIAMOND_HELMET));
		} else {
			equipment.set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.IRON_HELMET));
		}
		equipment.set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.DIAMOND_CHESTPLATE));
		equipment.set(Equipment.EquipmentSlot.LEGGINGS, pants);
		equipment.set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.DIAMOND_BOOTS));

		new BukkitRunnable() {
			int count = 0;
			boolean dirClockwise = true;
			@Override
			public void run() {
				count++;

				if(!npc.isSpawned()) {

					cancel();
					hoppers.remove(npc);
					return;
				}

				if(count % 5 == 0) {
					for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						if(!onlinePlayer.getWorld().equals(npc.getEntity().getWorld()));
						PacketPlayOutAnimation attackPacket = new PacketPlayOutAnimation(((CraftEntity)npc.getEntity()).getHandle(), 0);
						((CraftPlayer) onlinePlayer).getHandle().playerConnection.sendPacket(attackPacket);
					}
				}
				if(count % 5 == 4) {
					for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						if(!onlinePlayer.getWorld().equals(npc.getEntity().getWorld()));

						PacketContainer packet = PitSim.PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.ENTITY_METADATA);
						packet.getIntegers().write(0, npc.getEntity().getEntityId());
						WrappedDataWatcher watcher = new WrappedDataWatcher();
						watcher.setEntity(npc.getEntity());
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
						packet.getIntegers().write(0, npc.getEntity().getEntityId());
						WrappedDataWatcher watcher = new WrappedDataWatcher();
						watcher.setEntity(npc.getEntity());
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
					Block underneath = npc.getEntity().getLocation().clone().subtract(0, 0.2, 0).getBlock();
					if(underneath.getType() != Material.AIR) {
						npc.getEntity().setVelocity(new Vector(0, 0.42, 0));
					}
				}

				Util.faceEntity(npc.getEntity(), player);

				if(Math.random() < 0.03) dirClockwise = !dirClockwise;

				Entity entity = npc.getEntity();
				Vector npcVelo = entity.getVelocity();
				Vector dir = entity.getLocation().getDirection();
				if(player.getLocation().distance(npc.getEntity().getLocation()) > 3.5) {

					entity.setVelocity(npcVelo.add(dir.normalize().setY(0).multiply(0.10)));
//					entity.setVelocity(npcVelo.add(dir.normalize().setY(0).multiply(0.12)));
				} else {

					Location rotLoc = entity.getLocation().clone();
					rotLoc.setYaw(entity.getLocation().getYaw() + (dirClockwise ? - 90 : 90));
					entity.setVelocity(npcVelo.add(rotLoc.getDirection().normalize().setY(0).multiply(0.10)));
//					entity.setVelocity(npcVelo.add(rotLoc.getDirection().normalize().setY(0).multiply(0.12)));
				}

				for(Entity nearbyEntity : npc.getEntity().getNearbyEntities(5, 5, 5)) {
					if(!(nearbyEntity instanceof Player)) continue;
					Player target = (Player) nearbyEntity;
					if(npc.getEntity().getLocation().distance(target.getLocation()) > 4.2) continue;
					player.damage(12.75, npc.getEntity());
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}
}