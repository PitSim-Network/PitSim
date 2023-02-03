package dev.kyro.pitsim.aitems.mystics;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaintedChestplate extends StaticPitItem {

	public TaintedChestplate() {
		hasUUID = true;
		hideExtra = true;
		unbreakable = true;
	}

	@Override
	public String getNBTID() {
		return "tainted-chestplate";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("chestplate", "taintedchestplate"));
	}

	@Override
	public Material getMaterial() {
		return Material.LEATHER_CHESTPLATE;
	}

	@Override
	public String getName() {
		return "&5Fresh Tainted Chestplate";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Used in the tainted well",
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

		LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
		meta.setColor(Color.fromRGB(PantColor.TAINTED.hexColor));
		itemStack.setItemMeta(meta);

		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.addCompound(NBTTag.MYSTIC_ENCHANTS.getRef());
		return nbtItem.getItem();
	}

	@Override
	public ItemStack getItem(int amount) {
		if(amount == 1) return getItem();
		throw new RuntimeException();
	}

	@Override
	public void updateItem(ItemStack itemStack) {
		if(!isThisItem(itemStack)) throw new RuntimeException();

		NBTItem nbtItem = new NBTItem(itemStack);
		Integer enchantNum = nbtItem.getInteger(NBTTag.ITEM_ENCHANT_NUM.getRef());
		if(enchantNum == 0) {
			new AItemStackBuilder(itemStack)
					.setName(getName())
					.setLore(getLore());
			return;
		}
		EnchantManager.setItemLore(itemStack, null);
	}

	@Override
	public ItemStack getReplacementItem(PitPlayer pitPlayer, ItemStack itemStack, NBTItem nbtItem) {
//		TODO: Refund
		return null;
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return nbtItem.hasKey(NBTTag.TAINTED_TIER.getRef()) && itemStack.getType() == Material.LEATHER_CHESTPLATE;
	}
}
