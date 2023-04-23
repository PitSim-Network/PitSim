package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemFactory {
	public static List<PitItem> pitItems = new ArrayList<>();

	public static void registerItem(PitItem pitItem) {
		pitItems.add(pitItem);
	}

	public static boolean isPitItem(ItemStack itemStack) {
		return getItem(itemStack) != null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends PitItem> T getItem(Class<T> clazz) {
		for(PitItem pitItem : pitItems) if(pitItem.getClass() == clazz) return (T) pitItem;
		throw new RuntimeException();
	}

	public static PitItem getItem(ItemStack itemStack) {
		if(Misc.isAirOrNull(itemStack)) return null;
		NBTItem nbtItem = new NBTItem(itemStack);
		if(!nbtItem.hasKey(NBTTag.CUSTOM_ITEM.getRef())) return null;
		String identifier = nbtItem.getString(NBTTag.CUSTOM_ITEM.getRef());
		return getItem(identifier);
	}

	public static PitItem getItem(String identifier) {
		for(PitItem pitItem : pitItems) if(pitItem.getNBTID().equals(identifier)) return pitItem;
		return null;
	}

	public static boolean isThisItem(ItemStack itemStack, Class<? extends PitItem> clazz) {
		PitItem pitItem = getItem(itemStack);
		if(pitItem == null) return false;
		return pitItem.getClass() == clazz;
	}
}
