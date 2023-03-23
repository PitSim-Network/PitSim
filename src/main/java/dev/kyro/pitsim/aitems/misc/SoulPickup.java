package dev.kyro.pitsim.aitems.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SoulPickup extends PitItem {

	public SoulPickup() {
		hasUUID = true;
	}

	@Override
	public String getNBTID() {
		return "soul-pickup";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("gemshard", "shard"));
	}

	@Override
	public ItemStack getReplacementItem(PitPlayer pitPlayer, ItemStack itemStack, NBTItem nbtItem) {
		return null;
	}

	public Material getMaterial() {
		return Material.GHAST_TEAR;
	}

	public String getName(int amount) {
		return "&f" + amount + "x Soul";
	}

	public List<String> getLore() {
		return new ALoreBuilder(
				"&7You shouldn't be able to read this lol"
		).getLore();
	}

	public ItemStack getItem(int amount) {
		ItemStack itemStack = new ItemStack(getMaterial(), Math.min(amount, 64));
		itemStack = buildItem(itemStack);

		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.setInteger(NBTTag.SOUL_AMOUNT.getRef(), amount);
		itemStack = nbtItem.getItem();

		return new AItemStackBuilder(itemStack)
				.setName(getName(amount))
				.setLore(getLore())
				.getItemStack();
	}

	public int getSouls(ItemStack itemStack) {
		if(!isThisItem(itemStack)) return 0;
		NBTItem nbtItem = new NBTItem(itemStack);
		return nbtItem.getInteger(NBTTag.SOUL_AMOUNT.getRef());
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return false;
	}
}
