package net.pitsim.spigot.items.mystics;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.items.MysticFactory;
import net.pitsim.spigot.items.StaticPitItem;
import net.pitsim.spigot.items.TemporaryItem;
import net.pitsim.spigot.controllers.EnchantManager;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enums.MarketCategory;
import net.pitsim.spigot.enums.NBTTag;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MysticSword extends StaticPitItem implements TemporaryItem {

	public MysticSword() {
		hasUUID = true;
		hasLastServer = true;
		hasDropConfirm = true;
		destroyIfDroppedInSpawn = true;
		hideExtra = true;
		unbreakable = true;
		hasEnchantGlint = true;
		isMystic = true;
		marketCategory = MarketCategory.OVERWORLD_GEAR;

		itemEnchants.put(Enchantment.DAMAGE_ALL, 2);
	}

	@Override
	public String getNBTID() {
		return "mystic-sword";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("sword", "mysticsword"));
	}

	@Override
	public Material getMaterial() {
		return Material.GOLD_SWORD;
	}

	@Override
	public String getName() {
		return "&eMystic Sword";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Used in the mystic well",
				"",
				"&7Kept on death"
		).getLore();
	}

	@Override
	public ItemStack getItem() {
		ItemStack itemStack = new AItemStackBuilder(getMaterial())
				.setName(getName())
				.setLore(getLore())
				.getItemStack();
		itemStack = buildItem(itemStack);

		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.addCompound(NBTTag.MYSTIC_ENCHANTS.getRef());
		return nbtItem.getItem();
	}

	@Override
	public void updateItem(ItemStack itemStack) {
		if(!defaultUpdateItem(itemStack)) return;
		boolean isJewel = MysticFactory.isJewel(itemStack, false);
		boolean isJewelComplete = MysticFactory.isJewel(itemStack, true);

		NBTItem nbtItem = new NBTItem(itemStack);
		Integer enchantNum = nbtItem.getInteger(NBTTag.ITEM_ENCHANT_NUM.getRef());
		if(enchantNum == 0 && !isJewel) {
			new AItemStackBuilder(itemStack)
					.setName(getName())
					.setLore(getLore());
			return;
		}

		if(getLives(itemStack) == 0 && isJewelComplete) {
			itemStack.setType(Material.STONE_SWORD);
			itemStack.removeEnchantment(Enchantment.DAMAGE_ALL);
			Misc.removeEnchantGlint(itemStack);
		} else {
			itemStack.setType(Material.GOLD_SWORD);
			itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
			Misc.addEnchantGlint(itemStack);
		}

		EnchantManager.setItemLore(itemStack, null);
	}

	@Override
	public ItemStack getReplacementItem(PitPlayer pitPlayer, ItemStack itemStack, NBTItem nbtItem) {
		ItemStack newItemStack = new ItemStack(getMaterial(), 1);
		newItemStack = buildItem(newItemStack);
		NBTItem newNBTItem = new NBTItem(newItemStack);

		newNBTItem.addCompound(NBTTag.MYSTIC_ENCHANTS.getRef());
		NBTCompound newItemEnchants = newNBTItem.getCompound(NBTTag.MYSTIC_ENCHANTS.getRef());
		NBTCompound itemEnchants = nbtItem.getCompound(NBTTag.MYSTIC_ENCHANTS.getRef());
		for(String enchantKey : itemEnchants.getKeys()) {
			int enchantLvl = itemEnchants.getInteger(enchantKey);
			if(enchantKey.equals("comoswift")) enchantKey = "comboswift";
			if(enchantKey.equals("gotta-go-fast")) enchantKey = "gottagofast";
			if(enchantKey.equals("boo-boo")) enchantKey = "booboo";
			if(enchantLvl == 0) continue;
			newItemEnchants.setInteger(enchantKey, enchantLvl);
		}

		if(nbtItem.hasKey(NBTTag.ITEM_ENCHANT_NUM.getRef()))
			newNBTItem.setInteger(NBTTag.ITEM_ENCHANT_NUM.getRef(), nbtItem.getInteger(NBTTag.ITEM_ENCHANT_NUM.getRef()));
		if(nbtItem.hasKey(NBTTag.ITEM_TOKENS.getRef()))
			newNBTItem.setInteger(NBTTag.ITEM_TOKENS.getRef(), nbtItem.getInteger(NBTTag.ITEM_TOKENS.getRef()));
		if(nbtItem.hasKey(NBTTag.ITEM_RTOKENS.getRef()))
			newNBTItem.setInteger(NBTTag.ITEM_RTOKENS.getRef(), nbtItem.getInteger(NBTTag.ITEM_RTOKENS.getRef()));
		if(nbtItem.hasKey(NBTTag.CURRENT_LIVES.getRef()))
			newNBTItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()));
		if(nbtItem.hasKey(NBTTag.MAX_LIVES.getRef()))
			newNBTItem.setInteger(NBTTag.MAX_LIVES.getRef(), nbtItem.getInteger(NBTTag.MAX_LIVES.getRef()));
		if(nbtItem.hasKey(NBTTag.ITEM_JEWEL_KILLS.getRef()))
			newNBTItem.setInteger(NBTTag.ITEM_JEWEL_KILLS.getRef(), nbtItem.getInteger(NBTTag.ITEM_JEWEL_KILLS.getRef()));

		if(nbtItem.hasKey(NBTTag.MYSTIC_ENCHANT_ORDER.getRef())) {
			List<String> enchantOrder = nbtItem.getStringList(NBTTag.MYSTIC_ENCHANT_ORDER.getRef());
			for(String refName : enchantOrder) {
				if(refName.equals("comoswift")) refName = "comboswift";
				if(refName.equals("gotta-go-fast")) refName = "gottagofast";
				if(refName.equals("boo-boo")) refName = "booboo";
				newNBTItem.getStringList(NBTTag.MYSTIC_ENCHANT_ORDER.getRef()).add(refName);
			}
		}

		if(nbtItem.hasKey(NBTTag.ITEM_JEWEL_ENCHANT.getRef()))
			newNBTItem.setString(NBTTag.ITEM_JEWEL_ENCHANT.getRef(), nbtItem.getString(NBTTag.ITEM_JEWEL_ENCHANT.getRef()));

		if(nbtItem.hasKey(NBTTag.IS_GEMMED.getRef()))
			newNBTItem.setBoolean(NBTTag.IS_GEMMED.getRef(), nbtItem.getBoolean(NBTTag.IS_GEMMED.getRef()));
		if(nbtItem.hasKey(NBTTag.IS_VENOM.getRef()))
			newNBTItem.setBoolean(NBTTag.IS_VENOM.getRef(), nbtItem.getBoolean(NBTTag.IS_VENOM.getRef()));
		if(nbtItem.hasKey(NBTTag.IS_JEWEL.getRef()))
			newNBTItem.setBoolean(NBTTag.IS_JEWEL.getRef(), nbtItem.getBoolean(NBTTag.IS_JEWEL.getRef()));
		newItemStack = newNBTItem.getItem();

		String name = itemStack.getItemMeta().getDisplayName();
		new AItemStackBuilder(newItemStack)
				.setName(name == null ? getName() : name);

		return newItemStack;
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) &&
				(itemStack.getType() == Material.GOLD_SWORD || itemStack.getType() == Material.STONE_SWORD);
	}

	@Override
	public TemporaryType getTemporaryType() {
		return TemporaryType.LOOSES_LIVES_ON_DEATH;
	}
}
