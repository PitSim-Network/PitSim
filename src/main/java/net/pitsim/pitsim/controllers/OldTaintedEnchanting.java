package net.pitsim.pitsim.controllers;

import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.enchants.overworld.SelfCheckout;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.enums.MysticType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OldTaintedEnchanting {

	public static ItemStack enchantScythe(ItemStack itemStack, int tier) {
		List<PitEnchant> applicableEnchants = EnchantManager.getEnchants(MysticType.TAINTED_SCYTHE);
		List<PitEnchant> scytheEnchants = EnchantManager.getEnchants(ApplyType.MELEE);
		ItemStack mystic = itemStack;

		if(tier == 0) {

			Random random = new Random();

			PitEnchant randEnchant = applicableEnchants.get(random.nextInt(applicableEnchants.size()));

			int enchantTier;
			double tierRand = Math.random();
			if(tierRand <= 0.8) enchantTier = 1;
			else enchantTier = 2;

			try {
				mystic = EnchantManager.addEnchant(mystic, randEnchant, enchantTier, false);
			} catch(Exception ignored) {}

		} else {
			int tokensToAdd;
			double tokenRand = Math.random();
			if(tokenRand >= 0.85) tokensToAdd = 4;
			else if(tokenRand >= 0.5) tokensToAdd = 3;
			else if(tokenRand >= 0.15) tokensToAdd = 2;
			else tokensToAdd = 1;

			if(tokensToAdd > (8 - getTokens(mystic))) tokensToAdd = (8 - getTokens(mystic));

			for(int i = 0; i < tokensToAdd; i++) {
				double newEnchantRand = Math.random();

				boolean forceNew = true;
				for(Integer value : EnchantManager.getEnchantsOnItem(mystic).values()) {
					if(value != 3) {
						forceNew = false;
						break;
					}
				}

				if(forceNew || EnchantManager.getEnchantsOnItem(mystic).size() < 3 && newEnchantRand < 0.3) {
					Random random = new Random();

					scytheEnchants.removeAll(EnchantManager.getEnchantsOnItem(mystic).keySet());

					PitEnchant randEnchant = scytheEnchants.get(random.nextInt(scytheEnchants.size()));

					try {
						mystic = EnchantManager.addEnchant(mystic, randEnchant, 1, false);
					} catch(Exception ignored) {}
				} else {
					Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnItem(mystic);
					List<PitEnchant> enchantList = new ArrayList<>(enchantMap.keySet());

					Random random = new Random();
					PitEnchant randEnchant = enchantList.get(random.nextInt(enchantList.size()));

					while(enchantMap.get(randEnchant) > 2) {
						randEnchant = enchantList.get(random.nextInt(enchantList.size()));
					}

					try {
						mystic = EnchantManager.addEnchant(mystic, randEnchant, enchantMap.get(randEnchant) + 1, false);
					} catch(Exception ignored) {}

				}

			}
		}

		return mystic;
	}

	public static ItemStack enchantChestplate(ItemStack itemStack, int tier) {
		List<PitEnchant> applicableEnchants = EnchantManager.getEnchants(MysticType.TAINTED_CHESTPLATE);
		List<PitEnchant> chestEnchants = EnchantManager.getEnchants(ApplyType.PANTS);
		chestEnchants.remove(SelfCheckout.INSTANCE);
		ItemStack mystic = itemStack;

		if(tier == 0) {

			Random random = new Random();

			PitEnchant randEnchant = applicableEnchants.get(random.nextInt(applicableEnchants.size()));

			int enchantTier;
			double tierRand = Math.random();
			if(tierRand <= 0.8) enchantTier = 1;
			else enchantTier = 2;

			try {
				mystic = EnchantManager.addEnchant(mystic, randEnchant, enchantTier, false);
			} catch(Exception ignored) {}

		} else {
			int tokensToAdd;
			double tokenRand = Math.random();
			if(tokenRand >= 0.85) tokensToAdd = 4;
			else if(tokenRand >= 0.5) tokensToAdd = 3;
			else if(tokenRand >= 0.15) tokensToAdd = 2;
			else tokensToAdd = 1;

			if(tokensToAdd > (8 - getTokens(mystic))) tokensToAdd = (8 - getTokens(mystic));

			for(int i = 0; i < tokensToAdd; i++) {
				double newEnchantRand = Math.random();

				boolean forceNew = true;
				for(Integer value : EnchantManager.getEnchantsOnItem(mystic).values()) {
					if(value != 3) {
						forceNew = false;
						break;
					}
				}

				if(forceNew || EnchantManager.getEnchantsOnItem(mystic).size() < 3 && newEnchantRand < 0.3) {
					Random random = new Random();
					PitEnchant randEnchant = chestEnchants.get(random.nextInt(chestEnchants.size()));

					try {
						mystic = EnchantManager.addEnchant(mystic, randEnchant, 1, false);
					} catch(Exception ignored) {}
				} else {
					Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnItem(mystic);
					List<PitEnchant> enchantList = new ArrayList<>(enchantMap.keySet());

					Random random = new Random();
					PitEnchant randEnchant = enchantList.get(random.nextInt(enchantList.size()));

					while(enchantMap.get(randEnchant) > 2) {
						randEnchant = enchantList.get(random.nextInt(enchantList.size()));
					}

					try {
						mystic = EnchantManager.addEnchant(mystic, randEnchant, enchantMap.get(randEnchant) + 1, false);
					} catch(Exception ignored) {}

				}

			}
		}

		return mystic;
	}

	public static int getTokens(ItemStack itemStack) {
		int tokens = 0;
		for(Integer value : EnchantManager.getEnchantsOnItem(itemStack).values()) {
			tokens += value;
		}
		return tokens;
	}

}
