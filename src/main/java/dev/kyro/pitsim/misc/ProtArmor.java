package dev.kyro.pitsim.misc;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class ProtArmor {

	public static void getArmor(Player player, String type) {
		ItemStack armorPiece = getArmor(type);

		new BukkitRunnable() {
			@Override
			public void run() {
				AUtil.giveItemSafely(player, armorPiece, true);
			}
		}.runTaskLater(PitSim.INSTANCE, 10L);
	}

	public static ItemStack getArmor(String type) {
		ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
		ItemMeta helmetMeta = helmet.getItemMeta();
		helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
		helmetMeta.spigot().setUnbreakable(true);
		helmet.setItemMeta(helmetMeta);

		ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemMeta chestMeta = chestplate.getItemMeta();
		chestMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
		chestMeta.spigot().setUnbreakable(true);
		chestplate.setItemMeta(chestMeta);

		ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
		ItemMeta legsMeta = leggings.getItemMeta();
		legsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
		legsMeta.spigot().setUnbreakable(true);
		leggings.setItemMeta(legsMeta);

		ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
		ItemMeta bootsmeta = boots.getItemMeta();
		bootsmeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
		bootsmeta.spigot().setUnbreakable(true);
		boots.setItemMeta(bootsmeta);

		if(type.equalsIgnoreCase("helmet")) {
			return helmet;
		} else if(type.equalsIgnoreCase("chestplate")) {
			return chestplate;
		} else if(type.equalsIgnoreCase("leggings")) {
			return leggings;
		} else if(type.equalsIgnoreCase("boots")) {
			return boots;
		}
		return null;
	}

	public static void deleteArmor(Player player) {

//        ItemStack helmet = player.getInventory().getHelmet();
//        if(!Misc.isAirOrNull(helmet) && helmet.getItemMeta().hasEnchant(Enchantment.PROTECTION_ENVIRONMENTAL)) {
//            player.getInventory().setChestplate(new ItemStack(Material.AIR));
//        }

		if(!Misc.isAirOrNull(player.getInventory().getHelmet()) && player.getInventory().getHelmet().getItemMeta().hasEnchant(Enchantment.PROTECTION_ENVIRONMENTAL))
			player.getInventory().setHelmet(new ItemStack(Material.AIR));
		if(!Misc.isAirOrNull(player.getInventory().getChestplate()) && player.getInventory().getChestplate().getItemMeta().hasEnchant(Enchantment.PROTECTION_ENVIRONMENTAL))
			player.getInventory().setChestplate(new ItemStack(Material.AIR));
		if(!Misc.isAirOrNull(player.getInventory().getLeggings()) && player.getInventory().getLeggings().getItemMeta().hasEnchant(Enchantment.PROTECTION_ENVIRONMENTAL))
			player.getInventory().setLeggings(new ItemStack(Material.AIR));
		if(!Misc.isAirOrNull(player.getInventory().getBoots()) && player.getInventory().getBoots().getItemMeta().hasEnchant(Enchantment.PROTECTION_ENVIRONMENTAL))
			player.getInventory().setBoots(new ItemStack(Material.AIR));

		for(ItemStack itemStack : player.getInventory()) {
			if(Misc.isAirOrNull(itemStack)) continue;
			if(itemStack.getType().equals(Material.DIAMOND_HELMET) || itemStack.getType().equals(Material.DIAMOND_CHESTPLATE) || itemStack.getType().equals
					(Material.DIAMOND_LEGGINGS) || itemStack.getType().equals(Material.DIAMOND_BOOTS)) {
				if(itemStack.getItemMeta().hasEnchant(Enchantment.PROTECTION_ENVIRONMENTAL)) {
					player.getInventory().remove(itemStack);
				}

			}
		}

	}

}