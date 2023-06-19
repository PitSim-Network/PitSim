package net.pitsim.spigot.controllers;

import de.tr7zw.nbtapi.NBTItem;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.items.PitItem;
import net.pitsim.spigot.items.TemporaryItem;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.controllers.objects.PluginMessage;
import net.pitsim.spigot.enchants.tainted.uncommon.Durable;
import net.pitsim.spigot.enums.MysticType;
import net.pitsim.spigot.enums.NBTTag;
import net.pitsim.spigot.misc.CustomSerializer;
import net.pitsim.spigot.misc.Misc;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigInteger;
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

	public static ItemStack enchantItem(ItemStack itemStack, Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		long seed = pitPlayer.darkzoneTutorial.isActive() ? toSeededLong(player.getUniqueId()) : -1;

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

			LinkedHashMap<Integer, Double> tokenChance = new LinkedHashMap<>();
			tokenChance.put(1, TIER_1_TOKENS_1);
			tokenChance.put(2, TIER_1_TOKENS_2);
			tokens = Misc.weightedRandom(tokenChance, seed);

			for(int i = 0; i < tokens; i++) {
				Map<PitEnchant, Integer> enchantsOnItem = EnchantManager.getEnchantsOnItem(itemStack);

				LinkedHashMap<PitEnchant, Double> randomEnchantMap = new LinkedHashMap<>();
				for(Map.Entry<PitEnchant, Integer> entry : enchantsOnItem.entrySet()) {
					randomEnchantMap.put(entry.getKey(), 1.0);
				}

				PitEnchant randomEnchant = getRandomEnchant(type, new ArrayList<>(randomEnchantMap.keySet()), 0, seed);
				randomEnchantMap.put(randomEnchant, (double) 1);

				PitEnchant selectedEnchant = Misc.weightedRandom(randomEnchantMap, seed);
				int newTier = enchantsOnItem.getOrDefault(selectedEnchant, 0);
				try {
					itemStack = EnchantManager.addEnchant(itemStack, selectedEnchant, newTier + 1, false);
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}

		} else {
			int newTokens;
			LinkedHashMap<Integer, Double> enchantRandom = new LinkedHashMap<>();

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
			newTokens = Misc.weightedRandom(enchantRandom, seed);

			for(int i = 0; i < newTokens; i++) {
				LinkedHashMap<PitEnchant, Integer> enchantsOnItem = EnchantManager.getEnchantsOnItem(itemStack);
				LinkedHashMap<PitEnchant, Double> randomEnchantMap = new LinkedHashMap<>();
				for(Map.Entry<PitEnchant, Integer> entry : enchantsOnItem.entrySet()) {
					if(entry.getValue() < (previousTier == 3 ? 4 : 3)) {
						if(previousTier >= 2 || (previousTier == 1 && !entry.getKey().isRare)) randomEnchantMap.put(entry.getKey(), 1.0);
					}
				}

				if(randomEnchantMap.size() < 3) {
					LinkedHashMap<Integer, Double> randomRarityMap = new LinkedHashMap<>();

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

					PitEnchant randomEnchant = getRandomEnchant(type, new ArrayList<>(usedEnchants), Misc.weightedRandom(randomRarityMap, seed), seed);
					randomEnchantMap.put(randomEnchant, (double) (3 - enchantsOnItem.size()));
				}

				PitEnchant selectedEnchant = Misc.weightedRandom(randomEnchantMap, seed);
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

			addedLives += (newDurableLives - previousDurableLives);
		}

		itemStack = nbtItem.getItem();
		temporaryItem.addMaxLives(itemStack, addedLives);

		return itemStack;
	}

	public static PitEnchant getRandomEnchant(MysticType type, List<PitEnchant> existingEnchants, int enchantRarity, long seed) {
		List<PitEnchant> enchantPool = new ArrayList<>(EnchantManager.getEnchants(type));
		LinkedHashMap<PitEnchant, Double> rarityPool = new LinkedHashMap<>();
		for(PitEnchant enchant : enchantPool) {
			int rarity = 0;
			if(enchant.isUncommonEnchant) rarity = 1;
			if(enchant.isRare) rarity = 2;

			if(rarity == enchantRarity && !existingEnchants.contains(enchant)) rarityPool.put(enchant, 1D);
		}

		return Misc.weightedRandom(rarityPool, seed);
	}

	public static String getTitle(Map<PitEnchant, Integer> existingEnchants, Map<PitEnchant, Integer> newEnchants, int previousLives, int currentLives) {
		int existingRares = 0;
		for(PitEnchant pitEnchant : existingEnchants.keySet()) {
			if(pitEnchant.isRare) existingRares++;
		}

		int newRares = 0;
		for(PitEnchant pitEnchant : newEnchants.keySet()) {
			if(pitEnchant.isRare && !existingEnchants.containsKey(pitEnchant)) newRares++;
		}

		if(existingRares > 0 && newRares > 0) return "Extraordinary";
		else if(newRares > 0) return "";

		if(currentLives == EnchantManager.TAINTED_ARTIFACT_LIVES && previousLives < EnchantManager.TAINTED_ARTIFACT_LIVES) return "Artifact";
		else if(currentLives == EnchantManager.TAINTED_DEMONIC_LIVES && previousLives < EnchantManager.TAINTED_DEMONIC_LIVES) return "Demonic";

		return null;
	}

	public static void broadcastMessage(ItemStack itemStack, String title, Player player) {
		if(title == null) return;

		ItemMeta itemMeta = itemStack.getItemMeta();
		String itemName = EnchantManager.getMysticName(itemStack);
		itemName = "&5" + title + (title.isEmpty() ? "" : " ") + itemName;
		itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
		itemStack.setItemMeta(itemMeta);

		PitItem pitItem = ItemFactory.getItem(itemStack);
		if(pitItem != null) pitItem.updateItem(itemStack);

		EnchantManager.setItemLore(itemStack, null, false, true);

		sendTaintedEnchantMessage(Misc.getDisplayName(player), itemStack);

		new PluginMessage()
				.writeString("TAINTEDENCHANT")
				.writeString(PitSim.serverName)
				.writeString(Misc.getDisplayName(player))
				.writeString(CustomSerializer.serialize(itemStack))
				.send();
	}

	public static void sendTaintedEnchantMessage(String displayName, ItemStack itemStack) {
		TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&d&lTAINTED! " + displayName));
		message.addExtra(Misc.createItemHover(itemStack, " &7created "));

		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
			if(!pitPlayer.playerChatDisabled) onlinePlayer.sendMessage(message);
		}
	}

	public static long toSeededLong(UUID uuid) {
		byte[] bytes = uuid.toString().getBytes();
		BigInteger bigInteger = new BigInteger(bytes);
		return bigInteger.longValue();
	}
}
