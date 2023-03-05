package dev.kyro.pitsim.aitems.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.enums.AuctionCategory;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.inventories.VileGUI;
import dev.kyro.pitsim.misc.Sounds;
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
		auctionCategory = AuctionCategory.PURE_RELATED;
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
				"&7Kept on death",
				"",
				"&cHeretic artifact"
		).getLore();
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack itemStack = player.getItemInHand();

		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
		if(!isThisItem(itemStack)) return;

		if(!UpgradeManager.hasUpgrade(player, "WITHERCRAFT")) {
			AOutput.error(player, "&c&lERROR!&7 You must first unlock Withercraft from the renown shop before using this item!");
			Sounds.ERROR.play(player);
			return;
		}

		if(MapManager.inDarkzone(player)) {
			AOutput.error(player, "&c&lERROR!&7 You cannot repair items while in the darkzone!");
			Sounds.ERROR.play(player);
			return;
		}

		boolean foundAnyItems = false;
		for(int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack inventoryStack = player.getInventory().getItem(i);
			PitItem pitItem = ItemFactory.getItem(itemStack);
			if(pitItem == null || !MysticFactory.isJewel(itemStack, true)) continue;

			TemporaryItem temporaryItem = pitItem.getAsTemporaryItem();
			if(temporaryItem.isAtMaxLives(inventoryStack)) continue;

			foundAnyItems = true;
			break;
		}

		if(!foundAnyItems) {
			AOutput.error(player, "&c&lERROR!&7 You have no items to repair!");
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
