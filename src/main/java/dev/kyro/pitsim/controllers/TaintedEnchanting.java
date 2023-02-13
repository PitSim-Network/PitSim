package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TaintedEnchanting {
	public static ItemStack enchantItem(ItemStack itemStack) {
		MysticType type = MysticType.getMysticType(itemStack);
		if(type != MysticType.TAINTED_CHESTPLATE && type != MysticType.TAINTED_SCYTHE) return null;
		NBTItem nbtItem = new NBTItem(itemStack);
		int tier = nbtItem.getInteger(NBTTag.TAINTED_TIER.getRef());
		if(tier == 3) return null;

		ItemStack returnStack = itemStack;

		if(tier == 0) {
			int enchants;

			Map<Integer, Double> enchantChance = new HashMap<>();
			enchantChance.put(1, 0.9);
			enchantChance.put(2, 0.1);
			enchants = Misc.weightedRandom(enchantChance);

			for(int i = 0; i < enchants; i++) {
				try {

					Map<Integer, Double> newEnchantsRandom = new HashMap<>();
					newEnchantsRandom.put(1, 0.7);
					newEnchantsRandom.put(2, 0.3);
					int newEnchants = enchants == 1 ? 1 : Misc.weightedRandom(newEnchantsRandom);

					returnStack = EnchantManager.addEnchant(returnStack, getRandomEnchant(type, 0), newEnchants, false);
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
