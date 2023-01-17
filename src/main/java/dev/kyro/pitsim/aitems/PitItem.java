package dev.kyro.pitsim.aitems;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.ItemFactory;
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

public abstract class PitItem implements Listener {
	public short itemData = 0;
	//	This is forced true if the item has drop confirm
	public boolean hasDropConfirm;
	public boolean destroyIfDroppedInSpawn;

	public boolean hideExtra;
	public boolean unbreakable;
	public Map<Enchantment, Integer> itemEnchants = new HashMap<>();

	public boolean isProt;
	public AuctionCategory auctionCategory;

	public PitItem() {
		Bukkit.getPluginManager().registerEvents(this, PitSim.INSTANCE);
	}

	public abstract String getNBTID();
	public abstract List<String> getRefNames();
	public abstract void updateItem(ItemStack itemStack);

	public ItemStack buildItem(ItemStack itemStack) {
		ItemMeta itemMeta = itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType());
		if(hideExtra) {
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
		}
		if(unbreakable) {
			itemMeta.spigot().setUnbreakable(true);
			itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}
		if(!itemEnchants.isEmpty()) {
			itemStack.addUnsafeEnchantments(itemEnchants);
		}
		itemStack.setItemMeta(itemMeta);
		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.setString(NBTTag.CUSTOM_ITEM.getRef(), getNBTID());
		return nbtItem.getItem();
	}

	public boolean isThisItem(ItemStack itemStack) {
		return ItemFactory.getItem(itemStack) == this;
	}
}
