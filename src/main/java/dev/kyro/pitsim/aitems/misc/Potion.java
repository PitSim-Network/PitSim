package dev.kyro.pitsim.aitems.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.AuctionCategory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Potion extends PitItem {

	public Potion() {
		hasDropConfirm = true;
		hideExtra = true;
		unbreakable = true;
		auctionCategory = AuctionCategory.POTIONS;
	}

	@Override
	public String getNBTID() {
		return "potion";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("pot", "potion", "pots", "potions"));
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
	public void updateItem(ItemStack itemStack) {

	}

	@Override
	public ItemStack getReplacementItem(PitPlayer pitPlayer, ItemStack itemStack, NBTItem nbtItem) {
		return itemStack;
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return false;
	}
}
