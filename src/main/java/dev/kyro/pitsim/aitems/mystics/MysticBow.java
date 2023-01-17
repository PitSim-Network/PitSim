package dev.kyro.pitsim.aitems.mystics;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.enums.NBTTag;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MysticBow extends PitItem {

	public MysticBow() {
		hideExtra = true;
		unbreakable = true;

		itemEnchants.put(Enchantment.WATER_WORKER, 1);
	}

	@Override
	public String getNBTID() {
		return "mystic-bow";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("bow", "mysticbow"));
	}

	public Material getMaterial() {
		return Material.BOW;
	}

	public String getName() {
		return "&bMystic Bow";
	}

	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Used in the mystic well",
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
