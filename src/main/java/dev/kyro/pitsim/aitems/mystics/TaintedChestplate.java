package dev.kyro.pitsim.aitems.mystics;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TaintedChestplate extends PitItem {

	public TaintedChestplate() {
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

	public Material getMaterial() {
		return Material.LEATHER_CHESTPLATE;
	}

	public String getName() {
		return "&5Fresh Tainted Chestplate";
	}

	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Used in the tainted well",
				"",
				"&7Kept on death"
		).getLore();
	}

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
		nbtItem.setString(NBTTag.ITEM_UUID.getRef(), UUID.randomUUID().toString());
		nbtItem.addCompound(NBTTag.PIT_ENCHANTS.getRef());
		return nbtItem.getItem();
	}

	@Override
	public void updateItem(ItemStack itemStack) {
		NBTItem nbtItem = new NBTItem(itemStack);
		Integer enchantNum = nbtItem.getInteger(NBTTag.ITEM_ENCHANTS.getRef());
		if(enchantNum == 0) {
			new AItemStackBuilder(itemStack)
					.setName(getName())
					.setLore(getLore());
			return;
		}
		EnchantManager.setItemLore(itemStack, null);
	}
}
