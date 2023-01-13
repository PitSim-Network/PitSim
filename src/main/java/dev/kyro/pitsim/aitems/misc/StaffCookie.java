package dev.kyro.pitsim.aitems.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaffCookie extends PitItem {

	public StaffCookie() {
		hasDropConfirm = true;
	}

	@Override
	public String getNBTID() {
		return "staff-cookie";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("staffcookie", "cookie"));
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.COOKIE;
	}

	@Override
	public String getName(Player player) {
		return "&dStaff Cookie";
	}

	@Override
	public List<String> getLore(Player player) {
		return new ALoreBuilder(
				"&7Given to you by a staff member",
				"&7for some reason"
		).getLore();
	}

	@Override
	public void giveItem(Player player, int amount) {
		throw new RuntimeException();
	}

	public static ItemStack setCookieInformation(ItemStack itemStack, Player staff, Player receiver) {
		if(Misc.isAirOrNull(itemStack)) return null;
		NBTItem nbtItem = new NBTItem(itemStack);
		if(nbtItem.hasKey(NBTTag.COOKIE_GIVER.getRef())) return null;

		String giverString = "&7From: " + PlaceholderAPI.setPlaceholders(staff,
				"%luckperms_prefix%[%luckperms_groups%] ") + Misc.getDisplayName(staff);
		String receiverString = "&7To: " + PlaceholderAPI.setPlaceholders(receiver,
				"%luckperms_prefix%[%luckperms_groups%] ") + Misc.getDisplayName(receiver);

		ALoreBuilder loreBuilder = new ALoreBuilder(itemStack.getItemMeta().getLore());
		loreBuilder.addLore("", giverString, receiverString);

		return new AItemStackBuilder(itemStack)
				.setLore(loreBuilder)
				.getItemStack();
	}

	@EventHandler
	public void onEat(PlayerItemConsumeEvent event) {
		ItemStack itemStack = event.getItem();
		if(!isThisItem(itemStack)) return;
		event.setCancelled(true);
	}
}
