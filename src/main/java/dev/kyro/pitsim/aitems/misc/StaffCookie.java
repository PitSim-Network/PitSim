package dev.kyro.pitsim.aitems.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MarketCategory;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaffCookie extends PitItem {

	public StaffCookie() {
		hasUUID = true;
		hasDropConfirm = true;
		marketCategory = MarketCategory.MISC;
	}

	@Override
	public String getNBTID() {
		return "staff-cookie";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("staffcookie", "cookie"));
	}

	public Material getMaterial() {
		return Material.COOKIE;
	}

	public String getName() {
		return "&dStaff Cookie";
	}

	public List<String> getLore(ItemStack itemStack) {
		NBTItem nbtItem = new NBTItem(itemStack);
		String giverString = nbtItem.getString(NBTTag.COOKIE_GIVER.getRef());
		String receiverString = nbtItem.getString(NBTTag.COOKIE_RECEIVER.getRef());

		return new ALoreBuilder(
				"&eSpecial item",
				"&7Given to you by a staff member",
				"&7for some reason",
				"",
				"&7From: " + giverString,
				"&7To: " + receiverString
		).getLore();
	}

	@Override
	public void updateItem(ItemStack itemStack) {
		if(!defaultUpdateItem(itemStack)) return;

		itemStack.setType(getMaterial());
		new AItemStackBuilder(itemStack)
				.setName(getName())
				.setLore(getLore(itemStack));
	}

	public ItemStack getItem(Player staff, Player receiver, int amount) {
		ItemStack itemStack = new ItemStack(getMaterial(), amount);
		itemStack = buildItem(itemStack);

		if(Misc.isKyro(staff.getUniqueId())) {
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			itemStack.setItemMeta(itemMeta);
			itemStack.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
		}

		NBTItem nbtItem = new NBTItem(itemStack);
		String giverString = PlaceholderAPI.setPlaceholders(staff,
				"&8[%luckperms_primary_group_name%&8] %luckperms_prefix%") + staff.getName();
		String receiverString = PlaceholderAPI.setPlaceholders(receiver,
				"&8[%luckperms_primary_group_name%&8] %luckperms_prefix%") + receiver.getName();
		nbtItem.setString(NBTTag.COOKIE_GIVER.getRef(), giverString);
		nbtItem.setString(NBTTag.COOKIE_RECEIVER.getRef(), receiverString);
		itemStack = nbtItem.getItem();

		return new AItemStackBuilder(itemStack)
				.setName(getName())
				.setLore(getLore(itemStack))
				.getItemStack();
	}

	@EventHandler
	public void onEat(PlayerItemConsumeEvent event) {
		ItemStack itemStack = event.getItem();
		if(!isThisItem(itemStack)) return;
		event.setCancelled(true);
	}

	@Override
	public ItemStack getReplacementItem(PitPlayer pitPlayer, ItemStack itemStack, NBTItem nbtItem) {
		return null;
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return itemStack.getType() == Material.COOKIE;
	}
}
