package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.misc.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.HelmetManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.enchants.overworld.ComboVenom;
import dev.kyro.pitsim.enchants.overworld.SelfCheckout;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.exceptions.*;
import dev.kyro.pitsim.inventories.EnchantingGUI;
import dev.kyro.pitsim.logging.LogManager;
import dev.kyro.pitsim.misc.Constant;
import dev.kyro.pitsim.misc.CustomSerializer;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

import static dev.kyro.pitsim.enums.ApplyType.CHESTPLATES;

public class EnchantManager implements Listener {
	public static List<PitEnchant> pitEnchants = new ArrayList<>();
	public static Map<Player, Map<PitEnchant, Integer>> enchantMap = new HashMap<>();

	public static void readPlayerEnchants() {
		enchantMap.clear();
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) enchantMap.put(onlinePlayer, readEnchantsOnPlayer(onlinePlayer));
	}

	@EventHandler
	public static void onJewelAttack(AttackEvent.Pre attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!attackEvent.getAttacker().hasPermission("group.eternal")) return;
		ItemStack hand = attackEvent.getAttackerPlayer().getItemInHand();
		if(Misc.isAirOrNull(hand)) return;

		if(isJewel(hand) && !isJewelComplete(hand)) {
			attackEvent.getAttackerEnchantMap().put(getEnchant("executioner"), 3);
			attackEvent.getAttackerEnchantMap().put(getEnchant("shark"), 2);
		}
	}

	@EventHandler
	public static void onEnchantingTableClick(PlayerInteractEvent event) {
		if(event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		if(block.getType() != Material.ENCHANTMENT_TABLE) return;
		if(player.getWorld() == Bukkit.getWorld("darkzone")) return;

		event.setCancelled(true);

		EnchantingGUI enchantingGUI = new EnchantingGUI(player);
		enchantingGUI.open();
		Sounds.MYSTIC_WELL_OPEN_1.play(player);
		Sounds.MYSTIC_WELL_OPEN_2.play(player);
	}

	public static void registerEnchant(PitEnchant pitEnchant) {
		pitEnchants.add(pitEnchant);
		if(pitEnchant.isEnabled()) PitSim.INSTANCE.getServer().getPluginManager().registerEvents(pitEnchant, PitSim.INSTANCE);
	}

	public static boolean canTypeApply(ItemStack itemStack, PitEnchant pitEnchant) {

		if(pitEnchant.applyType == ApplyType.ALL) return true;

		if(itemStack.getType() == Material.GOLD_SWORD) {
			return pitEnchant.applyType == ApplyType.WEAPONS || pitEnchant.applyType == ApplyType.SWORDS || pitEnchant.applyType == ApplyType.MELEE;
		} else if(itemStack.getType() == Material.BOW) {
			return pitEnchant.applyType == ApplyType.WEAPONS || pitEnchant.applyType == ApplyType.BOWS;
		} else if(itemStack.getType() == Material.LEATHER_LEGGINGS) {
			return pitEnchant.applyType == ApplyType.PANTS;
		}

		return false;
	}

	public static ItemStack addEnchant(ItemStack itemStack, PitEnchant applyEnchant, int applyLvl, boolean safe) throws Exception {
		return addEnchant(itemStack, applyEnchant, applyLvl, safe, false, -1);
	}

	public static ItemStack addEnchant(ItemStack itemStack, PitEnchant applyEnchant, int applyLvl, boolean safe, boolean jewel, int insert) throws Exception {
		String enchantRefName = applyEnchant.refNames.get(0);
		NBTItem nbtItem = new NBTItem(itemStack);

		NBTList<String> enchantOrder = nbtItem.getStringList(NBTTag.MYSTIC_ENCHANT_ORDER.getRef());
		NBTCompound itemEnchants = nbtItem.getCompound(NBTTag.MYSTIC_ENCHANTS.getRef());
		Integer currentLvl = itemEnchants.getInteger(enchantRefName);
		Integer enchantNum = nbtItem.getInteger(NBTTag.ITEM_ENCHANT_NUM.getRef());
		Integer tokenNum = nbtItem.getInteger(NBTTag.ITEM_TOKENS.getRef());
		Integer rTokenNum = nbtItem.getInteger(NBTTag.ITEM_RTOKENS.getRef());

		if(!jewel && safe) {
			if(!EnchantManager.canTypeApply(itemStack, applyEnchant)) {
				throw new MismatchedEnchantException();
			} else if(isJewel(itemStack) && !isJewelComplete(itemStack)) {
				throw new IsJewelException();
			} else if(applyLvl > 3) {
				throw new InvalidEnchantLevelException(true);
			} else if(applyLvl < 0) {
				throw new InvalidEnchantLevelException(false);
			} else if(currentLvl == applyLvl) {
//			throw new InvalidEnchantLevelException(false);
			} else if(applyLvl - currentLvl + tokenNum > 8) {
				throw new MaxTokensExceededException(false);
			} else if(applyEnchant.isRare && applyLvl - currentLvl + rTokenNum > 4) {
				throw new MaxTokensExceededException(true);
			} else if(enchantNum >= 3 && applyLvl != 0 && currentLvl == 0) {
				throw new MaxEnchantsExceededException();
			}
		}
		if(nbtItem.getString(NBTTag.ITEM_JEWEL_ENCHANT.getRef()).equals(enchantRefName)) jewel = true;
		if(jewel && (safe || applyLvl == 0)) {
			throw new IsJewelException();
		}
		if(enchantNum == 2 && safe && !isJewel(itemStack)) {
			boolean hasCommonEnchant = false;
			for(String enchantString : enchantOrder) {
				PitEnchant pitEnchant = EnchantManager.getEnchant(enchantString);
				if(pitEnchant == null) continue;
				if(pitEnchant.isUncommonEnchant) continue;
				hasCommonEnchant = true;
				break;
			}
			if(!hasCommonEnchant && applyEnchant.isUncommonEnchant) throw new NoCommonEnchantException();
		}

		if(currentLvl == 0) {
			enchantNum++;
			if(insert == -1) {
				enchantOrder.add(enchantRefName);
			} else {
				List<String> tempList = new ArrayList<>(enchantOrder);
				enchantOrder.clear();
				for(int i = 0; i <= tempList.size(); i++) {
					if(i == insert) enchantOrder.add(enchantRefName);
					if(i < tempList.size()) enchantOrder.add(tempList.get(i));
				}
			}
		}
		if(applyLvl == 0) {
			enchantNum--;
			enchantOrder.remove(enchantRefName);
			itemEnchants.removeKey(enchantRefName);
		} else {
			itemEnchants.setInteger(enchantRefName, applyLvl);
		}

		tokenNum += applyLvl - currentLvl;
		if(applyEnchant.isRare && !jewel) rTokenNum += applyLvl - currentLvl;
		if(jewel) nbtItem.setString(NBTTag.ITEM_JEWEL_ENCHANT.getRef(), enchantRefName);
		nbtItem.setInteger(NBTTag.ITEM_ENCHANT_NUM.getRef(), enchantNum);
		nbtItem.setInteger(NBTTag.ITEM_TOKENS.getRef(), tokenNum);
		nbtItem.setInteger(NBTTag.ITEM_RTOKENS.getRef(), rTokenNum);
		if(applyEnchant == ComboVenom.INSTANCE) nbtItem.setBoolean(NBTTag.IS_VENOM.getRef(), true);

		AItemStackBuilder itemStackBuilder = new AItemStackBuilder(nbtItem.getItem());

		itemStackBuilder.setName(getMysticName(itemStack));

		setItemLore(itemStackBuilder.getItemStack(), null);
		return itemStackBuilder.getItemStack();
	}

	public static String getMysticName(ItemStack itemStack) {
		NBTItem nbtItem = new NBTItem(itemStack);
		int enchantNum = nbtItem.getInteger(NBTTag.ITEM_ENCHANT_NUM.getRef());
		MysticType mysticType = MysticType.getMysticType(itemStack);
		if(mysticType == null) return "";

		ChatColor chatColor = ChatColor.RED;
		if(mysticType.isTainted()) chatColor = PantColor.TAINTED.chatColor;
		if(mysticType == MysticType.PANTS) chatColor = PantColor.getPantColor(itemStack).chatColor;

		int taintedTier = nbtItem.getInteger(NBTTag.TAINTED_TIER.getRef());
		int tier = taintedTier != 0 ? taintedTier : enchantNum;
		return chatColor + "Tier " + (tier != 0 ? AUtil.toRoman(tier) : 0) + " " + mysticType.displayName;
	}

	public static boolean isIllegalItem(ItemStack itemStack) {
		if(Misc.isAirOrNull(itemStack)) return false;
		if(itemStack.getType() == Material.FISHING_ROD) return true;

		if(itemStack.getType() == Material.DIAMOND_HELMET || itemStack.getType() == Material.DIAMOND_CHESTPLATE ||
				itemStack.getType() == Material.DIAMOND_LEGGINGS || itemStack.getType() == Material.DIAMOND_BOOTS ||
				itemStack.getType() == Material.DIAMOND_SWORD) {

			if(itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL) > 1 || itemStack.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) > 1)
				return true;
		}

		PitItem pitItem = ItemFactory.getItem(itemStack);
		if(pitItem == null) return false;

		if(pitItem instanceof GoldenHelmet) {
			long gold = HelmetManager.getHelmetGold(itemStack);
			if(gold < 0) return true;
		}

		if(!pitItem.isMystic) return false;

		NBTItem nbtItem = new NBTItem(itemStack);
		if(nbtItem.hasKey(NBTTag.TAINTED_TIER.getRef())) {
			if(nbtItem.hasKey(NBTTag.IS_GEMMED.getRef())) return true;
			Map<PitEnchant, Integer> enchants = EnchantManager.getEnchantsOnItem(itemStack);
			int tainted = 0;
			for(PitEnchant pitEnchant : enchants.keySet()) {
				if(pitEnchant.isTainted) tainted++;
			}
			return tainted > 1;
		} else {

			NBTList<String> enchantOrder = nbtItem.getStringList(NBTTag.MYSTIC_ENCHANT_ORDER.getRef());
			NBTCompound itemEnchants = nbtItem.getCompound(NBTTag.MYSTIC_ENCHANTS.getRef());
			Integer enchantNum = nbtItem.getInteger(NBTTag.ITEM_ENCHANT_NUM.getRef());
			Integer tokenNum = nbtItem.getInteger(NBTTag.ITEM_TOKENS.getRef());
			Integer rTokenNum = nbtItem.getInteger(NBTTag.ITEM_RTOKENS.getRef());
			boolean isGemmed = nbtItem.getBoolean(NBTTag.IS_GEMMED.getRef());
			boolean isJewel = nbtItem.hasKey(NBTTag.IS_JEWEL.getRef());

			int maxTokens = isGemmed && isJewel ? 9 : 8;
			if(enchantNum > 3 || tokenNum > maxTokens || rTokenNum > 4) return true;
			for(PitEnchant pitEnchant : EnchantManager.pitEnchants) {
				if(itemEnchants.getInteger(pitEnchant.refNames.get(0)) > 3) return true;
			}
			boolean hasCommonEnchant = false;
			for(String enchantString : enchantOrder) {
				PitEnchant pitEnchant = EnchantManager.getEnchant(enchantString);
//				if(pitEnchant == EnchantManager.getEnchant("theking")) return true;
				if(pitEnchant == SelfCheckout.INSTANCE && !isJewel) return true;
				if(pitEnchant == null) continue;
				if(pitEnchant.isUncommonEnchant) continue;
				hasCommonEnchant = true;
				break;
			}
			return !hasCommonEnchant && enchantNum == 3 && !isJewel(itemStack);
		}
	}

	public static void setItemLore(ItemStack itemStack, Player player) {
		setItemLore(itemStack, player, false);
	}

	public static void setItemLore(ItemStack itemStack, Player player, boolean displayUncommon) {
		if(!PlayerManager.isRealPlayer(player)) player = null;

		PitItem pitItem = ItemFactory.getItem(itemStack);
		if(pitItem == null || !pitItem.isMystic) return;
		NBTItem nbtItem = new NBTItem(itemStack);

		NBTList<String> enchantOrder = nbtItem.getStringList(NBTTag.MYSTIC_ENCHANT_ORDER.getRef());
		NBTCompound itemEnchants = nbtItem.getCompound(NBTTag.MYSTIC_ENCHANTS.getRef());
		int currentLives = nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef());
		int maxLives = nbtItem.getInteger(NBTTag.MAX_LIVES.getRef());
		int jewelKills = nbtItem.getInteger(NBTTag.JEWEL_KILLS.getRef());
		boolean isJewel = isJewel(itemStack);
		char c;

		if(player != null && !player.isOp()) {
			ItemMeta itemMeta = itemStack.getItemMeta();
			if(itemMeta.hasDisplayName()) {
				String displayName = itemMeta.getDisplayName();
				String newDisplayName = displayName
						.replaceAll("\u00A7k", "")
						.replaceAll("\u00A7l", "")
						.replaceAll("\u00A7m", "")
						.replaceAll("\u00A7n", "")
						.replaceAll("\u00A7o", "");
				if(!player.isOp()) {
					String strippedName = ChatColor.stripColor(newDisplayName);
					if(strippedName.length() > 40) newDisplayName = "copium";
				}
				if(!displayName.equals(newDisplayName)) {
					itemMeta.setDisplayName(newDisplayName);
					itemStack.setItemMeta(itemMeta);
				}
			}
		}

		ALoreBuilder loreBuilder = new ALoreBuilder();

		if(nbtItem.hasKey(NBTTag.MAX_LIVES.getRef())) {
			if(currentLives <= 3) c = 'c';
			else c = 'a';
			String lives = "&7Lives: &" + c + currentLives + "&7/" + maxLives;
			if(nbtItem.hasKey(NBTTag.IS_GEMMED.getRef())) lives += " &a\u2666";
			loreBuilder.addLore(lives);
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(isJewel && !isJewelComplete(itemStack)) {

			MysticType mysticType = MysticType.getMysticType(nbtItem.getItem());
			if(mysticType == MysticType.PANTS) {
				itemMeta.setDisplayName(ChatColor.DARK_AQUA + "Hidden Jewel Pants");
				loreBuilder.addLore("&7");
				loreBuilder.addLore("&7Kill &c" + Constant.JEWEL_KILLS + " &7players to recycle");
				loreBuilder.addLore("&7into Tier I pants with a Tier III");
				loreBuilder.addLore("&7enchant");
				loreBuilder.addLore("&7Kills: &3" + jewelKills);
			} else if(mysticType == MysticType.SWORD) {
				itemMeta.setDisplayName(ChatColor.YELLOW + "Hidden Jewel Sword");
				loreBuilder.addLore("&7");
				loreBuilder.addLore("&7Kill &c" + Constant.JEWEL_KILLS + " &7players to recycle");
				loreBuilder.addLore("&7into a Tier I sword with a Tier");
				loreBuilder.addLore("&7III enchant");
				loreBuilder.addLore("&7Kills: &3" + jewelKills);
			} else if(mysticType == MysticType.BOW) {
				itemMeta.setDisplayName(ChatColor.AQUA + "Hidden Jewel Bow");
				loreBuilder.addLore("&7");
				loreBuilder.addLore("&7Kill &c" + Constant.JEWEL_KILLS + " &7players to recycle");
				loreBuilder.addLore("&7into a Tier I bow with a Tier");
				loreBuilder.addLore("&7III enchant");
				loreBuilder.addLore("&7Kills: &3" + jewelKills);
			}
		} else {
			if(nbtItem.getBoolean(NBTTag.IS_VENOM.getRef())) {
				itemMeta.setDisplayName(ChatColor.DARK_PURPLE + "Tier II Evil Pants");
				loreBuilder.getLore().clear();
				loreBuilder.addLore("&7Lives: &a140&7/140", "&7", "&9Somber", "&7You are unaffected by mystical", "&7enchantments.");
				if(itemMeta instanceof LeatherArmorMeta)
					((LeatherArmorMeta) itemMeta).setColor(Color.fromRGB(PantColor.DARK.hexColor));
			}

			for(String key : enchantOrder) {
				PitEnchant enchant = EnchantManager.getEnchant(key);
				Integer enchantLvl = itemEnchants.getInteger(key);
				if(enchant == null) continue;
				loreBuilder.addLore("&f");

				loreBuilder.addLore(enchant.getDisplayName(displayUncommon) + enchantLevelToRoman(enchantLvl));
				loreBuilder.addLore(enchant.getDescription(enchantLvl));
			}
			if(isJewel) {
				PitEnchant jewelEnchant = getEnchant(nbtItem.getString(NBTTag.ITEM_JEWEL_ENCHANT.getRef()));
				String jewelEnchantName = jewelEnchant == null ? "INVALID" : jewelEnchant.getDisplayName();
				loreBuilder.addLore("&f");
				loreBuilder.addLore("&3JEWEL!&9 " + jewelEnchantName);
			}
			if(nbtItem.getBoolean(NBTTag.IS_VENOM.getRef())) {
				loreBuilder.addLore("&7", "&5Enchants require heresy", "&5As strong as leather");
			}
		}

		itemMeta.setLore(loreBuilder.getLore());
		itemStack.setItemMeta(itemMeta);
	}

	public static boolean isJewel(ItemStack itemStack) {
		return MysticFactory.isJewel(itemStack, false);
	}

	public static boolean isJewelComplete(ItemStack itemStack) {
		return MysticFactory.isJewel(itemStack, true);
	}

	public static boolean isGemmed(ItemStack itemStack) {
		return MysticFactory.isGemmed(itemStack);
	}

	public static ItemStack completeJewel(Player player, ItemStack itemStack) {
		if(Misc.isAirOrNull(itemStack) || !isJewel(itemStack) || !isJewelComplete(itemStack)) return null;
		NBTItem nbtItem = new NBTItem(itemStack);
		String jewelString = nbtItem.getString(NBTTag.ITEM_JEWEL_ENCHANT.getRef());
		int jewelKills = nbtItem.getInteger(NBTTag.JEWEL_KILLS.getRef());
		if(jewelKills < Constant.JEWEL_KILLS || !jewelString.isEmpty()) return null;

		List<PitEnchant> enchantList = EnchantManager.getEnchants(MysticType.getMysticType(itemStack));
		List<PitEnchant> weightedEnchantList = new ArrayList<>();

		for(PitEnchant pitEnchant : enchantList) {

			weightedEnchantList.add(pitEnchant);
			if(pitEnchant.isRare) continue;
			weightedEnchantList.add(pitEnchant);
			if(pitEnchant.isUncommonEnchant) continue;
			for(int i = 0; i < 8; i++) weightedEnchantList.add(pitEnchant);
		}
		Collections.shuffle(weightedEnchantList);
		PitEnchant jewelEnchant = weightedEnchantList.get(0);

		nbtItem = new NBTItem(PantColor.setPantColor(nbtItem.getItem(), PantColor.getNormalRandom()));
		int maxLives = getRandomMaxLives();
		nbtItem.setInteger(NBTTag.MAX_LIVES.getRef(), maxLives);
		nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), maxLives);

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.stats != null) pitPlayer.stats.jewelsCompleted++;

		try {
			ItemStack jewelStack = EnchantManager.addEnchant(nbtItem.getItem(), jewelEnchant, 3, false, true, -1);

			ItemStack displayStack = new AItemStackBuilder(jewelStack.clone())
					.setName(jewelEnchant.getDisplayName())
					.getItemStack();
			sendJewelFindMessage(Misc.getDisplayName(player), displayStack);
			LogManager.onJewelComplete(player, jewelEnchant, maxLives);
			Sounds.JEWEL_FIND.play(player);

			new PluginMessage()
					.writeString("FINDJEWEL")
					.writeString(PitSim.serverName)
					.writeString(Misc.getDisplayName(player))
					.writeString(CustomSerializer.serialize(displayStack))
					.send();

			return jewelStack;
		} catch(Exception ignored) {}

		return null;
	}

	public static void sendJewelFindMessage(String displayName, ItemStack displayStack) {
		TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&3&lJEWEL!&7 " + displayName + " &7found "));
		message.addExtra(Misc.createItemHover(displayStack));
		message.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7!")));
		Bukkit.broadcast(message);
	}

	public static int getRandomMaxLives() {
		if(Math.random() < 0.001) return 420;
		if(Math.random() < 0.01) return 100;
		int maxLives = 10;
		if(Math.random() < 0.75) {
			maxLives += (int) (Math.random() * 11);
		} else {
			maxLives += (int) (Math.random() * 31 + 10);
		}
		return maxLives;
	}

	public static int getTaintedMaxLifeIncrease(int tier, int currentMaxLives) {
		if(tier == 3) {
			if(Math.random() < 0.001) return 1111 - currentMaxLives;
			if(Math.random() < 0.01) return 500 - currentMaxLives;
		}

		int randomMultiplier = tier * 25;
		return Math.max((int) (Math.random() * randomMultiplier), 10);
	}

	public static ItemStack incrementJewel(Player player, ItemStack itemStack) {
		if(Misc.isAirOrNull(itemStack) || !isJewel(itemStack) || isJewelComplete(itemStack)) return null;

		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.setInteger(NBTTag.JEWEL_KILLS.getRef(), nbtItem.getInteger(NBTTag.JEWEL_KILLS.getRef()) + 1);

		ItemStack jewelStack = completeJewel(player, nbtItem.getItem());
		if(jewelStack == null) {
			EnchantManager.setItemLore(nbtItem.getItem(), null);
			return nbtItem.getItem();
		}

		return jewelStack;
	}

	public static void incrementKillsOnJewels(Player player) {
		boolean updateInventory = false;

		ItemStack heldStack = incrementJewel(player, player.getItemInHand());
		if(heldStack != null) {
			player.setItemInHand(heldStack);
			updateInventory = true;
		}

		ItemStack pantsStack = incrementJewel(player, player.getInventory().getLeggings());
		if(pantsStack != null) {
			player.getInventory().setLeggings(pantsStack);
			updateInventory = true;
		}

		for(int i = 0; i < 9; i++) {
			if(i == player.getInventory().getHeldItemSlot()) continue;
			ItemStack hotbarStack = player.getInventory().getItem(i);
			if(Misc.isAirOrNull(hotbarStack) || !hotbarStack.getType().equals(Material.BOW)) continue;

			hotbarStack = incrementJewel(player, hotbarStack);
			if(hotbarStack != null) {
				player.getInventory().setItem(i, hotbarStack);
				updateInventory = true;
			}
		}

		if(updateInventory) player.updateInventory();
	}

	public static PitEnchant getEnchant(String refName) {

		if(refName.equals("")) return null;
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
		if(player == null) return 0;
		if(!enchantMap.containsKey(player)) return 0;
		return enchantMap.get(player).getOrDefault(pitEnchant, 0);
	}

	public static int getEnchantLevel(ItemStack itemStack, PitEnchant pitEnchant) {
		PitItem pitItem = ItemFactory.getItem(itemStack);
		if(pitItem == null || !pitItem.isMystic) return 0;

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

	public static Map<PitEnchant, Integer> getEnchantsOnPlayer(LivingEntity checkPlayer) {
		if(!(checkPlayer instanceof Player)) return new HashMap<>();
		Player player = (Player) checkPlayer;
		if(!enchantMap.containsKey(player)) return new HashMap<>();
		return enchantMap.get(player);
	}

	public static Map<PitEnchant, Integer> readEnchantsOnPlayer(LivingEntity checkPlayer) {
		if(!(checkPlayer instanceof Player)) return new HashMap<>();
		Player player = (Player) checkPlayer;
		return readEnchantsOnEquipment(new PitEquipment(player));
	}

	public static Map<PitEnchant, Integer> readEnchantsOnEquipment(PitEquipment pitEquipment) {
		List<ItemStack> equipmentList = pitEquipment.getAsList();
		Map<PitEnchant, Integer> playerEnchantMap = new HashMap<>();
		for(int i = 0; i < equipmentList.size(); i++) {
			ItemStack equipmentPiece = equipmentList.get(i);
			if(Misc.isAirOrNull(equipmentPiece)) continue;
			Map<PitEnchant, Integer> itemEnchantMap = getEnchantsOnItem(equipmentPiece, playerEnchantMap);
			if(i == 0) {
				for(Map.Entry<PitEnchant, Integer> entry : itemEnchantMap.entrySet()) {
					if(entry.getKey().applyType != ApplyType.PANTS && entry.getKey().applyType != CHESTPLATES)
						playerEnchantMap.put(entry.getKey(), entry.getValue());
				}
			} else {
				playerEnchantMap.putAll(itemEnchantMap);
			}
		}
		return playerEnchantMap;
	}

	public static Map<PitEnchant, Integer> getEnchantsOnItem(ItemStack itemStack) {
		return getEnchantsOnItem(itemStack, new HashMap<>());
	}

	public static Map<PitEnchant, Integer> getEnchantsOnItem(ItemStack itemStack, Map<PitEnchant, Integer> currentEnchantMap) {
		Map<PitEnchant, Integer> itemEnchantMap = new HashMap<>();
		PitItem pitItem = ItemFactory.getItem(itemStack);
		if(pitItem == null || !pitItem.isMystic) return itemEnchantMap;
		NBTItem nbtItem = new NBTItem(itemStack);

		NBTCompound itemEnchants = nbtItem.getCompound(NBTTag.MYSTIC_ENCHANTS.getRef());
		Set<String> keys = itemEnchants.getKeys();
		for(String key : keys) {

			PitEnchant pitEnchant = getEnchant(key);
			Integer enchantLvl = itemEnchants.getInteger(key);
			if(pitEnchant == null || enchantLvl == 0) continue;

			if(currentEnchantMap.containsKey(pitEnchant) && currentEnchantMap.get(pitEnchant) >= enchantLvl && !pitEnchant.levelStacks)
				continue;
			if(currentEnchantMap.containsKey(pitEnchant) && !pitEnchant.levelStacks)
				itemEnchantMap.put(pitEnchant, enchantLvl);
			else
				itemEnchantMap.put(pitEnchant, (currentEnchantMap.get(pitEnchant) != null ? currentEnchantMap.get(pitEnchant) : 0) + enchantLvl);
		}

		return itemEnchantMap;
	}

	public static List<PitEnchant> getEnchants(ApplyType applyType) {

		List<PitEnchant> applicableEnchants = new ArrayList<>();
		if(applyType == ApplyType.ALL) return pitEnchants;

		for(PitEnchant pitEnchant : pitEnchants) {
			ApplyType enchantApplyType = pitEnchant.applyType;
			if(enchantApplyType == ApplyType.ALL) applicableEnchants.add(pitEnchant);

			switch(applyType) {
				case BOWS:
					if(enchantApplyType == ApplyType.BOWS || enchantApplyType == ApplyType.WEAPONS)
						applicableEnchants.add(pitEnchant);
					break;
				case PANTS:
					if(enchantApplyType == ApplyType.PANTS) applicableEnchants.add(pitEnchant);
					break;
				case SWORDS:
					if(enchantApplyType == ApplyType.SWORDS || enchantApplyType == ApplyType.WEAPONS || enchantApplyType == ApplyType.MELEE)
						applicableEnchants.add(pitEnchant);
					break;
				case WEAPONS:
					if(enchantApplyType == ApplyType.WEAPONS || enchantApplyType == ApplyType.BOWS
							|| enchantApplyType == ApplyType.SWORDS || enchantApplyType == ApplyType.MELEE)
						applicableEnchants.add(pitEnchant);
					break;
				case SCYTHES:
					if(enchantApplyType == ApplyType.SCYTHES || enchantApplyType == ApplyType.MELEE)
						applicableEnchants.add(pitEnchant);
					break;
				case CHESTPLATES:
					if(enchantApplyType == CHESTPLATES) applicableEnchants.add(pitEnchant);
					break;
				case MELEE:
					if(enchantApplyType == ApplyType.MELEE) applicableEnchants.add(pitEnchant);
					break;
			}

		}
		return applicableEnchants;
	}

	public static List<PitEnchant> getEnchants(MysticType mystictype) {
		List<PitEnchant> applicableEnchants = new ArrayList<>();

		for(PitEnchant pitEnchant : pitEnchants) {
			ApplyType enchantApplyType = pitEnchant.applyType;
			if(enchantApplyType == ApplyType.ALL && !mystictype.isTainted()) applicableEnchants.add(pitEnchant);

			switch(mystictype) {
				case BOW:
					if(enchantApplyType == ApplyType.BOWS || enchantApplyType == ApplyType.WEAPONS)
						applicableEnchants.add(pitEnchant);
					break;
				case PANTS:
					if(enchantApplyType == ApplyType.PANTS) applicableEnchants.add(pitEnchant);
					break;
				case SWORD:
					if(enchantApplyType == ApplyType.SWORDS || enchantApplyType == ApplyType.WEAPONS || enchantApplyType == ApplyType.MELEE)
						applicableEnchants.add(pitEnchant);
					break;
				case TAINTED_CHESTPLATE:
					if(enchantApplyType == CHESTPLATES || enchantApplyType == ApplyType.TAINTED)
						applicableEnchants.add(pitEnchant);
					break;
				case TAINTED_SCYTHE:
					if(enchantApplyType == ApplyType.SCYTHES) applicableEnchants.add(pitEnchant);
					break;
			}
		}
		return applicableEnchants;
	}

	public static List<PitEnchant> getEnchants(MysticType mysticType, int enchantTier) {
		List<PitEnchant> mysticEnchants = getEnchants(mysticType);

		List<PitEnchant> finalEnchants = new ArrayList<>();

		for(PitEnchant mysticEnchant : mysticEnchants) {
			switch(enchantTier) {
				case 0:
					if(!mysticEnchant.isUncommonEnchant && !mysticEnchant.isRare) finalEnchants.add(mysticEnchant);
				case 1:
					if(mysticEnchant.isUncommonEnchant) finalEnchants.add(mysticEnchant);
				case 2:
					if(mysticEnchant.isRare) finalEnchants.add(mysticEnchant);
			}
		}
		return finalEnchants;
	}
}
