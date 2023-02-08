package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaintedEnchanting {
	public static ItemStack enchantItem(ItemStack itemStack) {
		MysticType type = MysticType.getMysticType(itemStack);
		if(type != MysticType.TAINTED_CHESTPLATE && type != MysticType.TAINTED_SCYTHE) return null;
		NBTItem nbtItem = new NBTItem(itemStack);
		int tier = nbtItem.getInteger(NBTTag.TAINTED_TIER.getRef());
		if(tier == 3) return null;

		ItemStack returnStack;


		double rand = Math.random();

		if(tier == 0) {
			int enchants;
			if(rand <= 0.9) enchants = 1;
			else enchants = 12;

			for(int i = 0; i < enchants; i++) {
				try {
					rand = Math.random();
					int newEnchants = enchants == 1 ? 1 : (rand <= 0.7) ? 1 : 2;

					EnchantManager.addEnchant(itemStack, getRandomEnchant(type, 0), newEnchants, false);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}


		return null;
	}

	public static PitEnchant getRandomEnchant(MysticType type, int enchantRarity) {
		List<PitEnchant> enchantPool = new ArrayList<>(EnchantManager.getEnchants(type));
		List<PitEnchant> rarityPool = new ArrayList<>();
		for(PitEnchant enchant : enchantPool) {
			int rarity = 0;
			if(enchant.isUncommonEnchant) rarity = 1;
			if(enchant.isRare) rarity = 2;

			if(rarity == enchantRarity) rarityPool.add(enchant);
		}

		Random random = new Random();
		return rarityPool.get(random.nextInt(rarityPool.size()));
	}
}
