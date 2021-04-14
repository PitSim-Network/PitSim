package dev.kyro.pitremake.controllers;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.enums.NBTTag;
import dev.kyro.pitremake.exceptions.MaxEnchantsExceededException;
import dev.kyro.pitremake.exceptions.MaxTokensExceededException;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EnchantManager {

	public static List<PitEnchant> pitEnchants = new ArrayList<>();

	public static void registerEnchant(PitEnchant pitEnchant) {

		pitEnchants.add(pitEnchant);
	}

	public static ItemStack addEnchant(ItemStack itemStack, PitEnchant pitEnchant, int applyLvl) throws Exception {

		return addEnchant(itemStack, pitEnchant, applyLvl, true);
	}

	public static ItemStack addEnchant(ItemStack itemStack, PitEnchant pitEnchant, int applyLvl, boolean safe) throws Exception {

		NBTItem nbtItem = new NBTItem(itemStack);

		NBTCompound itemEnchants = nbtItem.getCompound(NBTTag.PIT_ENCHANTS.getRef());
		Integer currentLvl = itemEnchants.getInteger(pitEnchant.refNames.get(0));
		Integer enchantNum = nbtItem.getInteger(NBTTag.ITEM_ENCHANTS.getRef());
		Integer tokenNum = nbtItem.getInteger(NBTTag.ITEM_TOKENS.getRef());
		Integer rTokenNum = nbtItem.getInteger(NBTTag.ITEM_RTOKENS.getRef());

		if(currentLvl >= applyLvl) {
//			throw new InvalidEnchantLevelException();
		} else if(safe && applyLvl + tokenNum > 8) {
			throw new MaxTokensExceededException(false);
		} else if(safe && applyLvl + rTokenNum > 5) {
			throw new MaxTokensExceededException(true);
		} else if(safe && enchantNum >= 3) {
			throw new MaxEnchantsExceededException();
		}

		itemEnchants.setInteger(pitEnchant.refNames.get(0), applyLvl);
		System.out.println(nbtItem.getCompound(NBTTag.PIT_ENCHANTS.getRef()).getInteger(pitEnchant.refNames.get(0)));

		AItemStackBuilder itemStackBuilder = new AItemStackBuilder(nbtItem.getItem());
		itemStackBuilder.setName("Tier X " + itemStack.getType());

		Set<String> keys = itemEnchants.getKeys();
		ALoreBuilder loreBuilder = new ALoreBuilder();
		for(String key : keys) {

			PitEnchant loreEnchant = getEnchant(key);
			Integer enchantLvl = itemEnchants.getInteger(key);
			if(enchantLvl == 0) continue;

			assert loreEnchant != null;
			loreBuilder.addLore(loreEnchant.name);
			loreBuilder.addLore(loreEnchant.getDisplayName());
		}
		itemStackBuilder.setLore(loreBuilder.getLore());

		return itemStackBuilder.getItemStack();
	}

	public static PitEnchant getEnchant(String refName) {

		for(PitEnchant enchant : EnchantManager.pitEnchants) {

			if(!enchant.refNames.contains(refName)) continue;
			return enchant;
		}
		return null;
	}
}
