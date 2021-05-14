package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.exceptions.InvalidEnchantLevelException;
import dev.kyro.pitsim.exceptions.MaxEnchantsExceededException;
import dev.kyro.pitsim.exceptions.MaxTokensExceededException;
import dev.kyro.pitsim.exceptions.MismatchedEnchantException;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EnchantManager {

	public static List<PitEnchant> pitEnchants = new ArrayList<>();

	public static void registerEnchant(PitEnchant pitEnchant) {

		pitEnchants.add(pitEnchant);
		PitSim.INSTANCE.getServer().getPluginManager().registerEvents(pitEnchant, PitSim.INSTANCE);
	}

	public static boolean canTypeApply(ItemStack itemStack, PitEnchant pitEnchant) {

		if(pitEnchant.applyType == ApplyType.ALL) return true;

		if(itemStack.getType() == Material.GOLD_SWORD) {
			return pitEnchant.applyType == ApplyType.WEAPONS || pitEnchant.applyType == ApplyType.SWORDS;
		} else if(itemStack.getType() == Material.BOW) {
			return pitEnchant.applyType == ApplyType.WEAPONS || pitEnchant.applyType == ApplyType.BOWS;
		} else if(itemStack.getType() == Material.LEATHER_LEGGINGS) {
			return pitEnchant.applyType == ApplyType.PANTS;
		}

		return false;
	}

	public static String getMysticType(ItemStack itemStack) {

		switch(itemStack.getType()) {
			case GOLD_SWORD:
				return "Sword";
			case BOW:
				return "Bow";
			case LEATHER_LEGGINGS:
				return "Pants";
		}
		return null;
	}

	public static ItemStack addEnchant(ItemStack itemStack, PitEnchant applyEnchant, int applyLvl, boolean safe) throws Exception {

		NBTItem nbtItem = new NBTItem(itemStack);

		NBTCompound itemEnchants = nbtItem.getCompound(NBTTag.PIT_ENCHANTS.getRef());
		Integer currentLvl = itemEnchants.getInteger(applyEnchant.refNames.get(0));
		Integer enchantNum = nbtItem.getInteger(NBTTag.ITEM_ENCHANTS.getRef());
		Integer tokenNum = nbtItem.getInteger(NBTTag.ITEM_TOKENS.getRef());
		Integer rTokenNum = nbtItem.getInteger(NBTTag.ITEM_RTOKENS.getRef());

		if(safe && !EnchantManager.canTypeApply(itemStack, applyEnchant)) {
			throw new MismatchedEnchantException();
		} else if(safe && applyLvl > 3) {
			throw new InvalidEnchantLevelException(true);
		} else if(currentLvl == applyLvl) {
			throw new InvalidEnchantLevelException(false);
		} else if(safe && applyLvl + tokenNum > 8) {
			throw new MaxTokensExceededException(false);
		} else if(safe && applyEnchant.isRare && applyLvl + rTokenNum > 5) {
			throw new MaxTokensExceededException(true);
		} else if(safe && enchantNum >= 3) {
			throw new MaxEnchantsExceededException();
		}

		if(currentLvl == 0) enchantNum++;
		tokenNum += applyLvl - currentLvl;
		if(applyEnchant.isRare) rTokenNum += applyLvl - currentLvl;
		currentLvl = applyLvl;
		itemEnchants.setInteger(applyEnchant.refNames.get(0), currentLvl);
		nbtItem.setInteger(NBTTag.ITEM_ENCHANTS.getRef(), enchantNum);
		nbtItem.setInteger(NBTTag.ITEM_TOKENS.getRef(), tokenNum);
		nbtItem.setInteger(NBTTag.ITEM_RTOKENS.getRef(), rTokenNum);

		itemEnchants.setInteger(applyEnchant.refNames.get(0), currentLvl);

		AItemStackBuilder itemStackBuilder = new AItemStackBuilder(nbtItem.getItem());
		itemStackBuilder.setName("&cTier " + AUtil.toRoman(enchantNum) + " " + getMysticType(itemStack));

		ALoreBuilder loreBuilder = new ALoreBuilder();
		loreBuilder.addLore("&7Lives: &a0&7/0");
		loreBuilder.addLore("&f");
		Map<PitEnchant, Integer> itemEnchantMap = getEnchantsOnItem(itemStackBuilder.getItemStack());
		for(Map.Entry<PitEnchant, Integer> entry : itemEnchantMap.entrySet()) {

			loreBuilder.addLore(entry.getKey().getDisplayName() + enchantLevelToRoman(entry.getValue()));
			loreBuilder.addLore(entry.getKey().getDescription(entry.getValue()));
			loreBuilder.addLore("&f");
		}
		itemStackBuilder.setLore(loreBuilder.getLore());

		return itemStackBuilder.getItemStack();
	}

	public static PitEnchant getEnchant(String refName) {

		for(PitEnchant enchant : pitEnchants) {

			if(!enchant.refNames.contains(refName)) continue;
			return enchant;
		}
		return null;
	}

	public static String enchantLevelToRoman(int enchantLvl) {

		return enchantLvl <= 1 ? "" : " " + AUtil.toRoman(enchantLvl);
	}
	
	public static int getEnchantLevel(Player player, PitEnchant pitEnchant) {

		List<ItemStack> inUse = new ArrayList<>(Arrays.asList(player.getInventory().getArmorContents()));
		inUse.add(player.getItemInHand());

		int finalLevel = 0;
		for(ItemStack itemStack : inUse) {
			int enchantLvl = getEnchantLevel(itemStack, pitEnchant);
			if(pitEnchant.levelStacks) {
				finalLevel += enchantLvl;
			} else {
				if(enchantLvl > finalLevel) finalLevel = enchantLvl;
			}
		}

		return finalLevel;
	}

	public static int getEnchantLevel(ItemStack itemStack, PitEnchant pitEnchant) {

		if(itemStack == null || itemStack.getType() == Material.AIR) return 0;
		NBTItem nbtItem = new NBTItem(itemStack);
		if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) return 0;

		Map<PitEnchant, Integer> itemEnchantMap = getEnchantsOnItem(itemStack);
		return getEnchantLevel(itemEnchantMap, pitEnchant);
	}

	public static int getEnchantLevel(Map<PitEnchant, Integer> enchantMap, PitEnchant pitEnchant) {

		for(Map.Entry<PitEnchant, Integer> entry : enchantMap.entrySet()) {

			if(entry.getKey() != pitEnchant) continue;
			return entry.getValue();
		}

		return 0;
	}

	public static Map<PitEnchant, Integer> getEnchantsOnPlayer(Player player) {

		List<ItemStack> inUse = new ArrayList<>(Arrays.asList(player.getInventory().getArmorContents()));
		inUse.add(player.getItemInHand());

		return getEnchantsOnPlayer(inUse.toArray(new ItemStack[5]));
	}

	public static Map<PitEnchant, Integer> getEnchantsOnPlayer(ItemStack[] inUseArr) {

		Map<PitEnchant, Integer> itemEnchantMap = new HashMap<>();
		for(ItemStack itemStack : inUseArr) {
			if(itemStack == null || itemStack.getType() == Material.AIR) continue;
			itemEnchantMap.putAll(getEnchantsOnItem(itemStack, itemEnchantMap));
		}

		return itemEnchantMap;
	}

	public static Map<PitEnchant, Integer> getEnchantsOnItem(ItemStack itemStack) {
		return getEnchantsOnItem(itemStack, new HashMap<>());
	}

	@org.jetbrains.annotations.NotNull
	public static Map<PitEnchant, Integer> getEnchantsOnItem(ItemStack itemStack, @NotNull Map<PitEnchant, Integer> currentEnchantMap) {

		Map<PitEnchant, Integer> itemEnchantMap = new HashMap<>();
		if(itemStack == null || itemStack.getType() == Material.AIR) return itemEnchantMap;
		NBTItem nbtItem = new NBTItem(itemStack);
		if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) return itemEnchantMap;

		NBTCompound itemEnchants = nbtItem.getCompound(NBTTag.PIT_ENCHANTS.getRef());
		Set<String> keys = itemEnchants.getKeys();
		for(String key : keys) {

			PitEnchant pitEnchant = getEnchant(key);
			Integer enchantLvl = itemEnchants.getInteger(key);
			if(pitEnchant == null || enchantLvl == 0) continue;

			if(currentEnchantMap.containsKey(pitEnchant) && currentEnchantMap.get(pitEnchant) >= enchantLvl) continue;

			itemEnchantMap.put(pitEnchant, enchantLvl);
		}

		return itemEnchantMap;
	}
}
