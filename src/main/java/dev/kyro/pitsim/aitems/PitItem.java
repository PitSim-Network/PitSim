package dev.kyro.pitsim.aitems;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.AuctionCategory;
import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class PitItem implements Listener {
	public short itemData = 0;
	//	This is forced true if the item has drop confirm
	public boolean hasUUID;
	public boolean hasDropConfirm;
	public boolean destroyIfDroppedInSpawn;

	public boolean hideExtra;
	public boolean unbreakable;
	public boolean hasEnchantGlint;
	public Map<Enchantment, Integer> itemEnchants = new HashMap<>();

	public boolean isProt;
	public boolean isMystic;

	public AuctionCategory auctionCategory;

	public PitItem() {
		Bukkit.getPluginManager().registerEvents(this, PitSim.INSTANCE);
	}

	public abstract String getNBTID();
	public abstract List<String> getRefNames();
	public abstract void updateItem(ItemStack itemStack);

	public ItemStack getReplacementItem(PitPlayer pitPlayer, ItemStack itemStack, NBTItem nbtItem) {return null;};
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {return false;};

	public ItemStack buildItem(ItemStack itemStack) {
		ItemMeta itemMeta = itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType());
		if(hideExtra) {
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
		}
		if(unbreakable) {
			itemMeta.spigot().setUnbreakable(true);
			itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}
		if(hasEnchantGlint) {
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}

		itemStack.setItemMeta(itemMeta);

		if(!itemEnchants.isEmpty()) {
			itemStack.addUnsafeEnchantments(itemEnchants);
		}
		if(hasEnchantGlint) {
			itemStack.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
		}

		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.setString(NBTTag.CUSTOM_ITEM.getRef(), getNBTID());
		if(hasUUID) nbtItem.setString(NBTTag.ITEM_UUID.getRef(), UUID.randomUUID().toString());
		return nbtItem.getItem();
	}

	public ItemStack randomizeUUID(ItemStack itemStack) {
		if(!isThisItem(itemStack) || !hasUUID) throw new RuntimeException();

		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.setString(NBTTag.ITEM_UUID.getRef(), UUID.randomUUID().toString());
		itemStack = nbtItem.getItem();

		updateItem(itemStack);
		return itemStack;
	}

	public boolean isThisItem(ItemStack itemStack) {
		return ItemFactory.getItem(itemStack) == this;
	}
}
