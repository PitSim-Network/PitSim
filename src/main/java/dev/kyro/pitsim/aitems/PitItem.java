package dev.kyro.pitsim.aitems;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class PitItem implements Listener {
	public short itemData = 0;
	public boolean hideExtra;
	public boolean hasDropConfirm;
//	This is forced true if the item has drop confirm
	public boolean destroyIfDroppedInSpawn;

	public PitItem() {
		Bukkit.getPluginManager().registerEvents(this, PitSim.INSTANCE);
	}

	public abstract String getNBTID();
	public abstract List<String> getRefNames();
	public abstract Material getMaterial(Player player);
	public abstract String getName(Player player);
	public abstract List<String> getLore(Player player);

	public ItemStack getItem(int amount) {
		return getItem(null, amount);
	}

	public ItemStack getItem(Player player, int amount) {
		ItemStack itemStack = new AItemStackBuilder(getMaterial(player), amount, itemData)
				.setName(getName(player))
				.setLore(getLore(player))
				.getItemStack();
		hideExtra(itemStack);
		return setTag(itemStack);
	}

	public void giveItem(Player player, int amount) {
		AUtil.giveItemSafely(player, getItem(player, amount), true);
	}

	public ItemStack setTag(ItemStack itemStack) {
		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.setString(NBTTag.CUSTOM_ITEM.getRef(), getNBTID());
		return nbtItem.getItem();
	}

	public void hideExtra(ItemStack itemStack) {
		if(!hideExtra) return;
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
		itemStack.setItemMeta(itemMeta);
	}

	public boolean isThisItem(ItemStack itemStack) {
		return ItemFactory.getItem(itemStack) == this;
	}
}
