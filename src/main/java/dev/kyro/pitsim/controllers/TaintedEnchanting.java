package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TaintedEnchanting {
	public static ItemStack enchantItem(ItemStack itemStack) {
		PitItem pitItem = ItemFactory.getItem(itemStack);
		if(pitItem == null || !pitItem.isMystic ||
				pitItem.getTemporaryType(itemStack) != TemporaryItem.TemporaryType.LOOSES_LIVES_ON_DEATH) return null;
		TemporaryItem temporaryItem = pitItem.getAsTemporaryItem();

		MysticType type = MysticType.getMysticType(itemStack);
//		if(type != MysticType.TAINTED_CHESTPLATE && type != MysticType.TAINTED_SCYTHE) return null;
		NBTItem nbtItem = new NBTItem(itemStack);
		int tier = nbtItem.getInteger(NBTTag.TAINTED_TIER.getRef());
		if(tier == 3) return null;

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

					List<PitEnchant> enchantsOnItem = new ArrayList<>(EnchantManager.getEnchantsOnItem(itemStack).keySet());
					itemStack = EnchantManager.addEnchant(itemStack, getRandomEnchant(type, enchantsOnItem, 0), newEnchants, false);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			int newTokens;
			Map<Integer, Double> enchantRandom = new HashMap<>();
			enchantRandom.put(1, tier == 1 ? 0.3 : 0.1);
			enchantRandom.put(2, tier == 1 ? 0.6 : 0.4);
			enchantRandom.put(3, tier == 1 ? 0.1 : 0.5);
			newTokens = Misc.weightedRandom(enchantRandom);

			for(int i = 0; i < newTokens; i++) {
				Map<PitEnchant, Integer> enchantsOnItem = EnchantManager.getEnchantsOnItem(itemStack);
				List<PitEnchant> randomEnchantList = new ArrayList<>();

				Map<PitEnchant, Double> randomEnchantMap = new HashMap<>();
				for(Map.Entry<PitEnchant, Integer> entry : enchantsOnItem.entrySet()) {
					if(entry.getValue() < 3) {
						if(tier == 2 || (tier == 1 && !entry.getKey().isRare)) randomEnchantList.add(entry.getKey());
					}
				}

				if(enchantsOnItem.size() < 3) {
					Map<Integer, Double> randomRarityMap = new HashMap<>();
					randomRarityMap.put(2, tier == 1 ? 0.02 : 0.1);
					randomRarityMap.put(1, tier == 1 ? 0.49 : 0.45);
					randomRarityMap.put(0, tier == 1 ? 0.49 : 0.45);
					randomEnchantList.add(getRandomEnchant(type, new ArrayList<>(enchantsOnItem.keySet()), Misc.weightedRandom(randomRarityMap)));
				}

				for(PitEnchant enchant : randomEnchantList) {
					randomEnchantMap.put(enchant, (double) (1 / randomEnchantList.size()));
				}

				PitEnchant selectedEnchant = Misc.weightedRandom(randomEnchantMap);
				int newTier = enchantsOnItem.getOrDefault(selectedEnchant, 0);
				try {
					itemStack = EnchantManager.addEnchant(itemStack, selectedEnchant, newTier + 1, false);
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		}

		int currentMaxLives = temporaryItem.getMaxLives(itemStack);
		int addedLives = EnchantManager.getTaintedMaxLifeIncrease(tier + 1, currentMaxLives);
		itemStack = temporaryItem.addMaxLives(itemStack, addedLives);
		return itemStack;
	}

	public static PitEnchant getRandomEnchant(MysticType type, List<PitEnchant> existingEnchants, int enchantRarity) {
		List<PitEnchant> enchantPool = new ArrayList<>(EnchantManager.getEnchants(type));
		List<PitEnchant> rarityPool = new ArrayList<>();
		for(PitEnchant enchant : enchantPool) {
			int rarity = 0;
			if(enchant.isUncommonEnchant) rarity = 1;
			if(enchant.isRare) rarity = 2;

			if(rarity == enchantRarity && !existingEnchants.contains(enchant)) rarityPool.add(enchant);
		}

		Random random = new Random();
		return rarityPool.get(random.nextInt(rarityPool.size()));
	}
}
