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

public class MysticSword extends PitItem {

	public MysticSword() {
		hideExtra = true;
		unbreakable = true;

		itemEnchants.put(Enchantment.DAMAGE_ALL, 2);
	}

	@Override
	public String getNBTID() {
		return "mystic-sword";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("sword", "mysticsword"));
	}

	public Material getMaterial() {
		return Material.GOLD_SWORD;
	}

	public String getName() {
		return "&eMystic Sword";
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
		if(!isThisItem(itemStack)) throw new RuntimeException();

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
