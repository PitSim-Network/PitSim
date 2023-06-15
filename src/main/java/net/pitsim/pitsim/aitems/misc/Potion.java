package net.pitsim.pitsim.aitems.misc;

import de.tr7zw.nbtapi.NBTItem;
import net.pitsim.pitsim.aitems.PitItem;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.MarketCategory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Potion extends PitItem {

	public Potion() {
		hasDropConfirm = true;
		hideExtra = true;
		unbreakable = true;
		marketCategory = MarketCategory.POTIONS;
	}

	@Override
	public String getNBTID() {
		return "potion";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("pot", "potion", "pots", "potions"));
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	public Material getMaterial() {
		return Material.POTION;
	}

	public String getName(ItemStack itemStack) {
		return itemStack.getItemMeta().getDisplayName();
	}

	public List<String> getLore(ItemStack itemStack) {
		return itemStack.getItemMeta().getLore();
	}

	public ItemStack getItem(ItemStack itemStack) {
		return buildItem(itemStack);
	}

	@Override
	public void updateItem(ItemStack itemStack) {}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		if(!itemStack.hasItemMeta()) return false;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(!itemMeta.hasLore()) return false;
		for(String line : itemMeta.getLore()) if(line.contains("Tainted Potion")) return true;
		return false;
	}

	@Override
	public ItemStack getReplacementItem(PitPlayer pitPlayer, ItemStack itemStack, NBTItem nbtItem) {
		return null;
	}
}
