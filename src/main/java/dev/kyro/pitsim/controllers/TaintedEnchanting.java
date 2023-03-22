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

	public static final double TIER_1_TOKENS_1 = 1.0;
	public static final double TIER_1_TOKENS_2 = 1.0;

	public static final double TIER_2_TOKENS_1 = 0.3;
	public static final double TIER_2_TOKENS_2 = 0.6;
	public static final double TIER_2_TOKENS_3 = 0.1;

	public static final double OTHER_TIERS_TOKENS_1 = 0.1;
	public static final double OTHER_TIERS_TOKENS_2 = 0.4;
	public static final double OTHER_TIERS_TOKENS_3 = 0.5;

	public static final double TIER_2_COMMON = 0.49;
	public static final double TIER_2_UNCOMMON = 0.49;
	public static final double TIER_2_RARE = 0.02;

	public static final double OTHER_TIERS_COMMON = 0.45;
	public static final double OTHER_TIERS_UNCOMMON = 0.45;
	public static final double OTHER_TIERS_RARE = 0.1;

	public static ItemStack enchantItem(ItemStack itemStack) {
		PitItem pitItem = ItemFactory.getItem(itemStack);
		if(pitItem == null || !pitItem.isMystic ||
				pitItem.getTemporaryType(itemStack) != TemporaryItem.TemporaryType.LOOSES_LIVES_ON_DEATH) return null;
		TemporaryItem temporaryItem = pitItem.getAsTemporaryItem();

		MysticType type = MysticType.getMysticType(itemStack);
//		if(type != MysticType.TAINTED_CHESTPLATE && type != MysticType.TAINTED_SCYTHE) return null;
		NBTItem nbtItem = new NBTItem(itemStack, true);
		int previousTier = nbtItem.getInteger(NBTTag.TAINTED_TIER.getRef());
		if(previousTier == 4) return null;

		if(previousTier == 0) {
			int tokens;

			Map<Integer, Double> tokenChance = new HashMap<>();
			tokenChance.put(1, TIER_1_TOKENS_1);
			tokenChance.put(2, TIER_1_TOKENS_2);
			tokens = Misc.weightedRandom(tokenChance);

			for(int i = 0; i < tokens; i++) {
				Map<PitEnchant, Integer> enchantsOnItem = EnchantManager.getEnchantsOnItem(itemStack);

				Map<PitEnchant, Double> randomEnchantMap = new HashMap<>();
				for(Map.Entry<PitEnchant, Integer> entry : enchantsOnItem.entrySet()) {
					randomEnchantMap.put(entry.getKey(), 1.0);
				}

				PitEnchant randomEnchant = getRandomEnchant(type, new ArrayList<>(randomEnchantMap.keySet()), 0);
				randomEnchantMap.put(randomEnchant, (double) 1);

				PitEnchant selectedEnchant = Misc.weightedRandom(randomEnchantMap);
				int newTier = enchantsOnItem.getOrDefault(selectedEnchant, 0);
				try {
					itemStack = EnchantManager.addEnchant(itemStack, selectedEnchant, newTier + 1, false);
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}

		} else {
			int newTokens;
			Map<Integer, Double> enchantRandom = new HashMap<>();
			enchantRandom.put(1, previousTier == 1 ? TIER_2_TOKENS_1 : OTHER_TIERS_TOKENS_1);
			enchantRandom.put(2, previousTier == 1 ? TIER_2_TOKENS_2 : OTHER_TIERS_TOKENS_2);
			enchantRandom.put(3, previousTier == 1 ? TIER_2_TOKENS_3 : OTHER_TIERS_TOKENS_3);
			newTokens = Misc.weightedRandom(enchantRandom);

			for(int i = 0; i < newTokens; i++) {
				Map<PitEnchant, Integer> enchantsOnItem = EnchantManager.getEnchantsOnItem(itemStack);

				Map<PitEnchant, Double> randomEnchantMap = new HashMap<>();
				for(Map.Entry<PitEnchant, Integer> entry : enchantsOnItem.entrySet()) {
					if(entry.getValue() < (previousTier == 3 ? 4 : 3)) {
						if(previousTier >= 2 || (previousTier == 1 && !entry.getKey().isRare)) randomEnchantMap.put(entry.getKey(), 1.0);
					}
				}

				if(randomEnchantMap.size() < 3) {
					Map<Integer, Double> randomRarityMap = new HashMap<>();
					randomRarityMap.put(2, previousTier == 1 ? TIER_2_RARE : OTHER_TIERS_RARE);
					randomRarityMap.put(1, previousTier == 1 ? TIER_2_UNCOMMON : OTHER_TIERS_UNCOMMON);
					randomRarityMap.put(0, previousTier == 1 ? TIER_2_COMMON : OTHER_TIERS_COMMON);

					List<PitEnchant> usedEnchants = new ArrayList<>(enchantsOnItem.keySet());
					for(PitEnchant enchant : randomEnchantMap.keySet()) {
						if(!usedEnchants.contains(enchant)) usedEnchants.add(enchant);
					}

					PitEnchant randomEnchant = getRandomEnchant(type, new ArrayList<>(usedEnchants), Misc.weightedRandom(randomRarityMap));
					randomEnchantMap.put(randomEnchant, (double) (3 - enchantsOnItem.size()));
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

		nbtItem = new NBTItem(itemStack, true);
		nbtItem.setInteger(NBTTag.TAINTED_TIER.getRef(), previousTier + 1);
		int addedLives = EnchantManager.getTaintedMaxLifeIncrease(previousTier + 1, temporaryItem.getMaxLives(itemStack));
		itemStack = nbtItem.getItem();
		temporaryItem.addMaxLives(itemStack, addedLives);
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
