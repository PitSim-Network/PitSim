package dev.kyro.pitsim.controllers.objects;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.controllers.KitManager;
import dev.kyro.pitsim.enums.KitItem;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.exceptions.PitException;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Kit {
	public List<KitItem> items = new ArrayList<>();

	public int slot;
	public static List<Integer> slots = new ArrayList<>(Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25));

	public Kit() {
		this.slot = slots.remove(0);
		addItems();
	}

	public abstract void addItems();
	public abstract ItemStack getDisplayItem();

	public void giveKit(Player player) throws PitException {
		int space = 0;
		for(int i = 0; i < 36; i++) {
			ItemStack testStack = player.getInventory().getItem(i);
			if(Misc.isAirOrNull(testStack)) space++;
		}
		if(space < items.size()) throw new PitException();

		for(KitItem item : items) {
			ItemStack itemStack = KitManager.kitItemMap.get(item);

			NBTItem nbtItem = new NBTItem(itemStack);
			nbtItem.setBoolean(NBTTag.IS_PREMADE.getRef(), true);
			nbtItem.setBoolean(NBTTag.DROP_CONFIRM.getRef(), true);

			player.getInventory().addItem(nbtItem.getItem());
		}
	}
}
