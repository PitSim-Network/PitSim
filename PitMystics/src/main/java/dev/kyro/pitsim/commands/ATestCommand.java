package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.inventories.PrestigeGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class ATestCommand implements CommandExecutor {

	public static List<String> hoppers = new ArrayList<>();

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

		hoppers.add("wiji1");
		hoppers.add("KyroKrypt");
		hoppers.add("Muruseni");
		hoppers.add("wackful");
		hoppers.add("Bobbybenny12");
		hoppers.add("Troving");
		hoppers.add("lkjv");
		hoppers.add("Xavier9346");
		hoppers.add("FreeJUSTHUNTINGU");
		hoppers.add("Zsombor_1");
		hoppers.add("AddisonDj");
		hoppers.add("Airpark");
		hoppers.add("1Ror");
		hoppers.add("Tinykloon");
		hoppers.add("_MarcusW_");
		hoppers.add("UpdateGame");
		hoppers.add("_A1Sauce");
		hoppers.add("GRIMPIT");
		hoppers.add("GANGMEMBER7PUMP");
		hoppers.add("woolens");
		hoppers.add("Qtj_ALT");
		hoppers.add("perungod");
		hoppers.add("memescientist");
		hoppers.add("souliow");
		hoppers.add("PitSim");
		hoppers.add("el24");




	}

	public Team t;

	public static Player targetPlayer = null;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		if(!player.isOp()) return false;

		PrestigeGUI prestigeGUI = new PrestigeGUI(player);
		prestigeGUI.open();

//		System.out.println(player.getItemInHand().toString());
//		NBTItem nbtItem = new NBTItem(player.getItemInHand());
//		System.out.println(nbtItem.toString());

//		EnchantingGUI enchantingGUI = new EnchantingGUI(player);
//		enchantingGUI.open();
//		AOutput.send(player, "Opening enchanting gui");

//		MainEnchantGUI mainEnchantGUI = new MainEnchantGUI(player);
//		player.openInventory(mainEnchantGUI.getInventory());
//		mainEnchantGUI.updateGUI();

//		if(true) return false;
//
//		if(args.length < 1) {
//
//			AOutput.error(player, "Usage: /atest <target>");
//			return false;
//		}
//		String target = args[0];
//		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//
//			if(!onlinePlayer.getName().equalsIgnoreCase(target)) continue;
//			targetPlayer = onlinePlayer;
//			break;
//		}
//		if(targetPlayer == null) {
//
//			AOutput.error(player, "That player is not online");
//			return false;
//		}
//
//		Scoreboard score = Bukkit.getScoreboardManager().getMainScoreboard();
//		t = score.getTeam("nhide");
//		if(t == null) {
//			t = score.registerNewTeam("nhide");
//			t.setNameTagVisibility(NameTagVisibility.NEVER);
//		}
//
//		new BukkitRunnable() {
//			int count = 0;
//			@Override
//			public void run() {
//
//				if(hoppers.get(count) == null) {
//
//					cancel();
//					return;
//				}
//
//				callHopper(targetPlayer, hoppers.get(count++));
//
//			}
//		}.runTaskTimer(PitSim.INSTANCE, 0L, 3L);
//		return false;
//	}
//
//	public static String getRandomRank() {
//
//		int rand = (int) (Math.random() * 9);
//
//		switch(rand) {
//
//			case 0:
//				return "&7";
//			case 1:
//			case 2:
//				return "&a";
//			case 3:
//			case 4:
//			case 5:
//			case 6:
//				return "&b";
//			case 7:
//			case 8:
//				return "&6";
//		}
//
//		return "&b";
//	}
//
//	public static String getRandomBracketColor() {
//
//		int rand = (int) (Math.random() * 9);
//
//		switch(rand) {
//
//			case 0:
//				return "&7";
//			case 1:
//				return "&9";
//			case 2:
//				return "&e";
//			case 3:
//				return "&6";
//			case 4:
//				return "&c";
//			case 5:
//				return "&5";
//			case 6:
//				return "&d";
//			case 7:
//				return "&f";
//			case 8:
//				return "&b";
//		}
//
//		return "&b";
//	}
//
//	public void callHopper(Player player, String name) {
//
//		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
//		if(!npc.isSpawned()) npc.spawn(player.getLocation());
//
////		String bracket = getRandomBracketColor();
////		String rank = getRandomRank();
//
//		LookClose lookClose = new LookClose();
////		lookClose.lookClose(true);
////		npc.addTrait(lookClose);
//
////		ArmorStand armorStand = (ArmorStand) npc.getEntity().getWorld().spawnEntity(npc.getEntity().getLocation(), EntityType.ARMOR_STAND);
////		armorStand.setVisible(false);
////		armorStand.setCustomNameVisible(true);
////		armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&',
////				bracket + "[&b&l120" + bracket + "] " + rank + name));
//
//		npc.setProtected(false);
//
//		Navigator navigator = npc.getNavigator();
//
//		Equipment equipment = npc.getTrait(Equipment.class);
//		equipment.set(Equipment.EquipmentSlot.HAND, new AItemStackBuilder(Material.DIAMOND_SPADE).setName("&bCombat Spade").getItemStack());
//
//		if(Math.random() < 0.5) {
//			equipment.set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.DIAMOND_HELMET));
//		} else {
//			equipment.set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.IRON_HELMET));
//		}
//		equipment.set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.DIAMOND_CHESTPLATE));
//		if(Math.random() < 0.75) {
//			equipment.set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Material.DIAMOND_LEGGINGS));
//		} else {
//			equipment.set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Material.IRON_LEGGINGS));
//		}
//		equipment.set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.DIAMOND_BOOTS));
//
//		new BukkitRunnable() {
//
//			int count = 0;
//			boolean dirClockwise = true;
//
//			@Override
//			public void run() {
//
//				if(!npc.isSpawned()) {
//
//					cancel();
//					return;
//				}
//
////				armorStand.teleport(npc.getEntity());
////				t.addEntry(hoppers.get(count));
//
//				if(count % 5 == 0) {
//					Block underneath = npc.getEntity().getLocation().clone().subtract(0, 0.2, 0).getBlock();
//					if(underneath.getType() != Material.AIR) {
//						npc.getEntity().setVelocity(new Vector(0, 0.42, 0));
//					}
//				}
//
//				((Player) npc.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999, 1, true, false));
//
//				Util.faceEntity(npc.getEntity(), player);
//
//				if(Math.random() < 0.03) dirClockwise = !dirClockwise;
//
//				Entity entity = npc.getEntity();
//				Vector npcVelo = entity.getVelocity();
//				Vector dir = entity.getLocation().getDirection();
//				if(player.getLocation().distance(npc.getEntity().getLocation()) > 4.2) {
//
//					entity.setVelocity(npcVelo.add(dir.normalize().setY(0).multiply(0.12)));
//				} else {
//
//					Location rotLoc = entity.getLocation().clone();
//					rotLoc.setYaw(entity.getLocation().getYaw() + (dirClockwise ? - 90 : 90));
//					entity.setVelocity(npcVelo.add(rotLoc.getDirection().normalize().setY(0).multiply(0.12)));
//				}
//
//				if(player.getLocation().distance(npc.getEntity().getLocation()) < 4.2) {
//
//					player.damage(9, npc.getEntity());
//				}
//			}
//		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
		return false;
	}
}