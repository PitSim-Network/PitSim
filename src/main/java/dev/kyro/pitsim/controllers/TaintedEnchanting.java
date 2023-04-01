package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enchants.tainted.uncommon.Durable;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TaintedEnchanting {

	public static final double TIER_1_TOKENS_1 = 1.0;
	public static final double TIER_1_TOKENS_2 = 1.0;

	public static final double TIER_2_TOKENS_1 = 0.35;
	public static final double TIER_2_TOKENS_2 = 0.65;

	public static final double TIER_3_TOKENS_1 = 0.1;
	public static final double TIER_3_TOKENS_2 = 0.4;
	public static final double TIER_3_TOKENS_3 = 0.5;
	public static final double TIER_3_TOKENS_4 = 0.2;

	public static final double TIER_4_TOKENS_1 = 0.1;
	public static final double TIER_4_TOKENS_2 = 0.4;
	public static final double TIER_4_TOKENS_3 = 0.5;
	public static final double TIER_4_TOKENS_4 = 0.2;

	public static final double TIER_2_COMMON = 0.49;
	public static final double TIER_2_UNCOMMON = 0.49;
	public static final double TIER_2_RARE = 0.02;

	public static final double TIER_3_COMMON = 0.45;
	public static final double TIER_3_UNCOMMON = 0.45;
	public static final double TIER_3_RARE = 0.1;

	public static final double TIER_4_COMMON = 0.45;
	public static final double TIER_4_UNCOMMON = 0.45;
	public static final double TIER_4_RARE = 0.1;

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

		int previousDurableTier = -1;
		int durableTier = 0;

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

			double tokens1 = 0;
			double tokens2 = 0;
			double tokens3 = 0;
			double tokens4 = 0;

			switch(previousTier) {
				case 1:
					tokens1 = TIER_2_TOKENS_1;
					tokens2 = TIER_2_TOKENS_2;
					break;
				case 2:
					tokens1 = TIER_3_TOKENS_1;
					tokens2 = TIER_3_TOKENS_2;
					tokens3 = TIER_3_TOKENS_3;
					tokens4 = TIER_3_TOKENS_4;
					break;
				case 3:
					tokens1 = TIER_4_TOKENS_1;
					tokens2 = TIER_4_TOKENS_2;
					tokens3 = TIER_4_TOKENS_3;
					tokens4 = TIER_4_TOKENS_4;
					break;
			}

			enchantRandom.put(1, tokens1);
			enchantRandom.put(2, tokens2);
			enchantRandom.put(3, tokens3);
			enchantRandom.put(4, tokens4);
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

					double common = 0;
					double uncommon = 0;
					double rare = 0;

					switch(previousTier) {
						case 1:
							common = TIER_2_COMMON;
							uncommon = TIER_2_UNCOMMON;
							rare = TIER_2_RARE;
							break;
						case 2:
							common = TIER_3_COMMON;
							uncommon = TIER_3_UNCOMMON;
							rare = TIER_3_RARE;
							break;
						case 3:
							common = TIER_4_COMMON;
							uncommon = TIER_4_UNCOMMON;
							rare = TIER_4_RARE;
							break;
					}

					randomRarityMap.put(2, rare);
					randomRarityMap.put(1, uncommon);
					randomRarityMap.put(0, common);

					List<PitEnchant> usedEnchants = new ArrayList<>(enchantsOnItem.keySet());
					for(PitEnchant enchant : randomEnchantMap.keySet()) {
						if(!usedEnchants.contains(enchant)) usedEnchants.add(enchant);
					}

					PitEnchant randomEnchant = getRandomEnchant(type, new ArrayList<>(usedEnchants), Misc.weightedRandom(randomRarityMap));
					randomEnchantMap.put(randomEnchant, (double) (3 - enchantsOnItem.size()));
				}

				PitEnchant selectedEnchant = Misc.weightedRandom(randomEnchantMap);
				if(selectedEnchant instanceof Durable) {
					if(previousDurableTier == -1) previousDurableTier = enchantsOnItem.getOrDefault(selectedEnchant, 0);
					durableTier++;
				}

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

		if(durableTier > 0) {
			int previousDurableLives = Durable.getExtraLives(previousDurableTier);
			int newDurableLives = Durable.getExtraLives(durableTier);

			addedLives += newDurableLives - previousDurableLives;
		}

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
