package dev.kyro.pitsim.aitems;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.ItemFactory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class StaticPitItem extends PitItem {
	public abstract Material getMaterial();
	public abstract String getName();
	public abstract List<String> getLore();

	@Override
	public void updateItem(ItemStack itemStack) {
		itemStack.setType(getMaterial());
		new AItemStackBuilder(itemStack)
				.setName(getName())
				.setLore(getLore());
	}

	public ItemStack getItem() {
		return getItem(1);
	}

	public ItemStack getItem(int amount) {
		ItemStack itemStack = new AItemStackBuilder(getMaterial(), amount, itemData)
				.setName(getName())
				.setLore(getLore())
				.getItemStack();
		return buildItem(itemStack);
	}

	public void giveItem(Player player, int amount) {
		AUtil.giveItemSafely(player, getItem(amount), true);
	}

	public boolean isThisItem(ItemStack itemStack) {
		return ItemFactory.getItem(itemStack) == this;
	}
}
