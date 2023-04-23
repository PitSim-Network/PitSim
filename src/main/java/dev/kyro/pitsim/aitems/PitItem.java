package dev.kyro.pitsim.aitems;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MarketCategory;
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
	public boolean hasLastServer;
	public boolean hasDropConfirm;
	public boolean destroyIfDroppedInSpawn;

	public boolean hideExtra;
	public boolean unbreakable;
	public boolean hasEnchantGlint;
	public Map<Enchantment, Integer> itemEnchants = new HashMap<>();

	public boolean isProtDiamond;
	public boolean isShopDiamond;
	public boolean isMystic;

	public MarketCategory marketCategory;

	public PitItem() {
		Bukkit.getPluginManager().registerEvents(this, PitSim.INSTANCE);
	}

	public abstract String getNBTID();
	public abstract List<String> getRefNames();
	public abstract int getMaxStackSize();

	public abstract ItemStack getReplacementItem(PitPlayer pitPlayer, ItemStack itemStack, NBTItem nbtItem);
	public abstract boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem);

	public void updateItem(ItemStack itemStack) {
		defaultUpdateItem(itemStack);
	}

	public boolean defaultUpdateItem(ItemStack itemStack) {
		if(!isThisItem(itemStack)) throw new RuntimeException();
		NBTItem nbtItem = new NBTItem(itemStack, true);
		if(nbtItem.hasKey(NBTTag.IS_LOCKED.getRef())) return false;
		if(hasLastServer) nbtItem.setString(NBTTag.ITEM_LAST_SERVER.getRef(), PitSim.status.name());
		return true;
	}

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
		if(hasLastServer) nbtItem.setString(NBTTag.ITEM_LAST_SERVER.getRef(), PitSim.status.name());
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

	public PitSim.ServerStatus getLastServer(ItemStack itemStack) {
		if(!isThisItem(itemStack) || !hasLastServer) throw new RuntimeException();
		NBTItem nbtItem = new NBTItem(itemStack);
		if(!nbtItem.hasKey(NBTTag.ITEM_LAST_SERVER.getRef())) return PitSim.ServerStatus.OVERWORLD;
		return PitSim.ServerStatus.valueOf(nbtItem.getString(NBTTag.ITEM_LAST_SERVER.getRef()));
	}

	public TemporaryItem.TemporaryType getTemporaryType(ItemStack itemStack) {
		if(!isThisItem(itemStack)) throw new RuntimeException();
		if(!(this instanceof TemporaryItem)) return null;
		TemporaryItem temporaryItem = (TemporaryItem) this;
		return temporaryItem.getTemporaryType();
	}

	public TemporaryItem getAsTemporaryItem() {
		if(!(this instanceof TemporaryItem)) throw new RuntimeException();
		return (TemporaryItem) this;
	}
}
