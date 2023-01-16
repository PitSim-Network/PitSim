package dev.kyro.pitsim.aitems.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.enums.AuctionCategory;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.inventories.VileGUI;
import dev.kyro.pitsim.misc.Misc;
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

public class ChunkOfVile extends PitItem {

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
	public Material getMaterial(Player player) {
		return Material.COAL;
	}

	@Override
	public String getName(Player player) {
		return "&5Chunk of Vile";
	}

	@Override
	public List<String> getLore(Player player) {
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

		int items = 0;
		for(int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack item = player.getInventory().getItem(i);

			if(Misc.isAirOrNull(item)) continue;

			NBTItem nbtItem = new NBTItem(item);
			if(nbtItem.hasKey(NBTTag.ITEM_JEWEL_ENCHANT.getRef())) {
				if(nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()).equals(nbtItem.getInteger(NBTTag.MAX_LIVES.getRef()))) continue;
				items++;
			}
		}

		if(items == 0) {
			AOutput.error(player, "&c&lERROR!&7 You have no items to repair!");
			Sounds.ERROR.play(player);
			return;
		}

		VileGUI vileGUI = new VileGUI(player);
		vileGUI.open();
	}
}
