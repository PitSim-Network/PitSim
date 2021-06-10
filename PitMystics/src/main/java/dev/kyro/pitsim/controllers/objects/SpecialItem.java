package dev.kyro.pitsim.controllers.objects;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnusedReturnValue")
public class SpecialItem {

	public ItemStack itemStack;

	public SpecialItem(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public SpecialItem enableUndroppable() {

		if(Misc.isAirOrNull(itemStack)) return this;
		NBTItem nbtItem = new NBTItem(itemStack);

		nbtItem.setBoolean(NBTTag.UNDROPPABLE.getRef(), true);

		return this;
	}

	public SpecialItem enableDropConfirm() {

		if(Misc.isAirOrNull(itemStack)) return this;
		NBTItem nbtItem = new NBTItem(itemStack);

		nbtItem.setBoolean(NBTTag.DROP_CONFIRM.getRef(), true);

		return this;
	}
}
