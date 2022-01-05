package dev.kyro.pitsim.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorReduction {

	public static double getReductionMultiplier(Player player) {
		int actualPoints = getArmorPoints(player);
		int missingPoints = 0;

		ItemStack leggings = player.getEquipment().getLeggings();
		if(!Misc.isAirOrNull(leggings) && leggings.getType() == Material.LEATHER_LEGGINGS) {
			NBTItem pants = new NBTItem(leggings);
			if(pants.hasKey(NBTTag.ITEM_UUID.getRef())) missingPoints += 3;
		}

		ItemStack helmet = player.getEquipment().getHelmet();
		if(!Misc.isAirOrNull(helmet) && helmet.getType() == Material.GOLD_HELMET) {
			missingPoints += 1;
		}

		double actualReduction = actualPoints * 4;
		double missingReduction = missingPoints * 4;

		return (100 - actualReduction - missingReduction) / (100 - actualReduction);
	}

	public static int getArmorPoints(Player player) {
		int points = 0;
		points += getArmorPoints(player.getEquipment().getHelmet());
		points += getArmorPoints(player.getEquipment().getChestplate());
		points += getArmorPoints(player.getEquipment().getLeggings());
		points += getArmorPoints(player.getEquipment().getBoots());
		return points;
	}

	public static int getArmorPoints(ItemStack itemStack) {
		if(Misc.isAirOrNull(itemStack)) return 0;
		Material material = itemStack.getType();
		switch(material) {
			case LEATHER_HELMET:
			case LEATHER_BOOTS:
			case GOLD_BOOTS:
				return 1;
			case LEATHER_LEGGINGS:
			case GOLD_HELMET:
			case IRON_HELMET:
			case IRON_BOOTS:
				return 2;
			case LEATHER_CHESTPLATE:
			case DIAMOND_HELMET:
			case DIAMOND_BOOTS:
				return 3;
			case IRON_LEGGINGS:
				return 5;
			case DIAMOND_LEGGINGS:
				return 6;
			case DIAMOND_CHESTPLATE:
				return 8;
		}
		return 0;
	}
}
