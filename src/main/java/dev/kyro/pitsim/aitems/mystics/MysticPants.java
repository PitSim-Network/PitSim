package dev.kyro.pitsim.aitems.mystics;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MysticPants extends PitItem {

	public MysticPants() {
		hideExtra = true;
		unbreakable = true;
	}

	@Override
	public String getNBTID() {
		return "mystic-pants";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("pants", "mysticpants"));
	}

	public Material getMaterial() {
		return Material.LEATHER_LEGGINGS;
	}

	public String getName(PantColor pantColor) {
		return pantColor.chatColor + "Fresh " + pantColor.refName + " Pants";
	}

	public List<String> getLore(PantColor pantColor) {
		return new ALoreBuilder(
				pantColor.chatColor + "Used in the mystic well.",
				pantColor.chatColor + "Also a fashion statement",
				"",
				"&7Kept on death"
		).getLore();
	}

	@Override
	public void updateItem(ItemStack itemStack) {
		if(!isThisItem(itemStack)) throw new RuntimeException();

		NBTItem nbtItem = new NBTItem(itemStack);
		Integer enchantNum = nbtItem.getInteger(NBTTag.ITEM_ENCHANTS.getRef());
		if(enchantNum == 0) {
			PantColor pantColor = PantColor.getPantColor(itemStack);
			assert pantColor != null;
			new AItemStackBuilder(itemStack)
					.setName(getName(pantColor))
					.setLore(getLore(pantColor));
			return;
		}
		EnchantManager.setItemLore(itemStack, null);
	}

	public ItemStack getItem(PantColor pantColor) {
		ItemStack itemStack = new AItemStackBuilder(Material.LEATHER_LEGGINGS)
				.setName(getName(pantColor))
				.setLore(new ALoreBuilder(getLore(pantColor)))
				.getItemStack();
		itemStack = buildItem(itemStack);
		itemStack = PantColor.setPantColor(itemStack, pantColor);

		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.setString(NBTTag.ITEM_UUID.getRef(), UUID.randomUUID().toString());
		nbtItem.addCompound(NBTTag.PIT_ENCHANTS.getRef());
		return nbtItem.getItem();
	}
}
