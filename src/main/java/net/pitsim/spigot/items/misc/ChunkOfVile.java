package net.pitsim.spigot.items.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.items.MysticFactory;
import net.pitsim.spigot.items.StaticPitItem;
import net.pitsim.spigot.items.TemporaryItem;
import net.pitsim.spigot.controllers.UpgradeManager;
import net.pitsim.spigot.enums.MarketCategory;
import net.pitsim.spigot.enums.NBTTag;
import net.pitsim.spigot.inventories.VileGUI;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.Sounds;
import net.pitsim.spigot.upgrades.Withercraft;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChunkOfVile extends StaticPitItem {

	public ChunkOfVile() {
		hasDropConfirm = true;
		marketCategory = MarketCategory.PURE_RELATED;
	}

	@Override
	public String getNBTID() {
		return "vile";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("vile"));
	}

	@Override
	public Material getMaterial() {
		return Material.COAL;
	}

	@Override
	public String getName() {
		return "&5Chunk of Vile";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Right-Click to open menu.",
				"&7Can be consumed to repair",
				"&7a life on a &3Jewel &7item. Does",
				"&7not work on broken items",
				"",
				"&7Kept on death"
		).getLore();
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack vileStack = player.getItemInHand();

		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
		if(!isThisItem(vileStack)) return;

		if(!UpgradeManager.hasUpgrade(player, Withercraft.INSTANCE)) {
			AOutput.error(player, "&c&lERROR!&7 You must first unlock Withercraft from the renown shop before using this item!");
			Sounds.ERROR.play(player);
			return;
		}

		if(Misc.getItemCount(player, false, (pitItem, itemStack) -> {
			if(!MysticFactory.hasLives(itemStack)) return false;
			TemporaryItem temporaryItem = (TemporaryItem) pitItem;
			return !temporaryItem.isAtMaxLives(itemStack) && temporaryItem.getLives(itemStack) != 0;
		}) == 0) {
			AOutput.error(player, "&c&lERROR!&7 There are no items to repair in your inventory (armor excluded)");
			Sounds.ERROR.play(player);
			return;
		}

		VileGUI vileGUI = new VileGUI(player);
		vileGUI.open();
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return nbtItem.hasKey(NBTTag.IS_VILE.getRef());
	}
}
