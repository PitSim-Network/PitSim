package dev.kyro.pitsim.aitems.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.enums.AuctionCategory;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TokenOfAppreciation extends PitItem {

	public TokenOfAppreciation() {
		hasDropConfirm = true;
		auctionCategory = AuctionCategory.MISC;
	}

	@Override
	public String getNBTID() {
		return "token-of-appreciation";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("token", "tokenofappreciation"));
	}

	public Material getMaterial() {
		return Material.MAGMA_CREAM;
	}

	public String getName() {
		return "&6Token of Appreciation";
	}

	public List<String> getLore(ItemStack itemStack) {
		NBTItem nbtItem = new NBTItem(itemStack);
		String giverString = nbtItem.getString(NBTTag.COOKIE_GIVER.getRef());
		String receiverString = nbtItem.getString(NBTTag.COOKIE_RECEIVER.getRef());

		return new ALoreBuilder(
				"&eSpecial item",
				"&7A token of appreciation for understanding",
				"&7why we have to reset the PitSim economy.",
				"&7Who knows, this might do something some day",
				"",
				giverString,
				receiverString
		).getLore();
	}

	@Override
	public void updateItem(ItemStack itemStack) {
		itemStack.setType(getMaterial());
		new AItemStackBuilder(itemStack)
				.setName(getName())
				.setLore(getLore(itemStack));
	}

	public ItemStack getItem(Player staff, Player receiver, int amount) {
		ItemStack itemStack = new ItemStack(getMaterial(), amount);
		itemStack = buildItem(itemStack);

		NBTItem nbtItem = new NBTItem(itemStack);
		String giverString = "&7From: " + PlaceholderAPI.setPlaceholders(staff,
				"%luckperms_prefix%[%luckperms_groups%] ") + Misc.getDisplayName(staff);
		String receiverString = "&7To: " + PlaceholderAPI.setPlaceholders(receiver,
				"%luckperms_prefix%[%luckperms_groups%] ") + Misc.getDisplayName(receiver);
		nbtItem.setString(NBTTag.COOKIE_GIVER.getRef(), giverString);
		nbtItem.setString(NBTTag.COOKIE_RECEIVER.getRef(), receiverString);
		itemStack = nbtItem.getItem();

		return new AItemStackBuilder(itemStack)
				.setName(getName())
				.setLore(getLore(itemStack))
				.getItemStack();
	}
}
