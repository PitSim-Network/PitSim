package dev.kyro.pitsim.tutorial.checkpoints;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.mystics.TaintedChestplate;
import dev.kyro.pitsim.aitems.mystics.TaintedScythe;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.TaintedWell;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.tutorial.NPCCheckpoint;
import dev.kyro.pitsim.tutorial.Tutorial;
import dev.kyro.pitsim.tutorial.TutorialObjective;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaintedWellCheckpoint extends NPCCheckpoint {
	public TaintedWellCheckpoint() {
		super(TutorialObjective.TAINTED_WELL, new Location(MapManager.getDarkzone(),
				188.5, 92, -101.5, 13, 0));
	}

	@Override
	public void onCheckpointEngage(Tutorial tutorial) {
		tutorial.delayTask(() -> giveFreshItems(tutorial), getEngageDelay());

		tutorial.sendMessage("You can now access the Tainted Well", 0);
		tutorial.sendMessage("This is a special area where you can get special items", 20);
		tutorial.sendMessage("You can access the Tainted Well by typing &b/tw", 40);
		tutorial.sendMessage("You can also access the Tainted Well by clicking the &bTainted Well &7item in your inventory", 60);
	}

	@Override
	public void onCheckpointSatisfy(Tutorial tutorial) {
		tutorial.delayTask(() -> removeTutorialTag(tutorial.getPlayer()), getSatisfyDelay());
		tutorial.sendMessage("You have accessed the Tainted Well", 0);
		tutorial.sendMessage("You can now access the Tainted Well by typing &b/tw", 20);
		tutorial.sendMessage("You can also access the Tainted Well by clicking the &bTainted Well &7item in your inventory", 40);
	}

	@Override
	public int getEngageDelay() {
		return 60;
	}

	@Override
	public int getSatisfyDelay() {
		return 60;
	}

	@Override
	public boolean canEngage(Tutorial tutorial) {
		return Misc.getEmptyInventorySlots(tutorial.getPlayer()) >= 2;
	}

	@Override
	public boolean canSatisfy(Tutorial tutorial) {
		Player player = tutorial.getPlayer();

		boolean hasChestplate = false;
		boolean hasScythe = false;

		List<ItemStack> items = new ArrayList<>(Arrays.asList(player.getInventory().getContents()));
		items.addAll(Arrays.asList(player.getInventory().getArmorContents()));

		for(ItemStack itemStack : items) {
			if(!ItemFactory.isTutorialItem(itemStack)) continue;
			PitItem pitItem = ItemFactory.getItem(itemStack);
			if(pitItem == null) continue;
			NBTItem nbtItem = new NBTItem(itemStack);

			if(pitItem instanceof TaintedChestplate) {
				if(nbtItem.getInteger(NBTTag.TAINTED_TIER.getRef()) >= 2) hasChestplate = true;
			} else if(pitItem instanceof TaintedScythe) {
				if(nbtItem.getInteger(NBTTag.TAINTED_TIER.getRef()) >= 2) hasScythe = true;
			}
		}

		return hasChestplate && hasScythe;
	}

	@Override
	public void onCheckPointDisengage(Tutorial tutorial) {
		TaintedWell.tutorialReset(tutorial.getPlayer());
	}

	public void removeTutorialTag(Player player) {
		ItemStack chestplate = player.getInventory().getChestplate();
		PitItem pitItem = ItemFactory.getItem(chestplate);
		if(pitItem != null) {
			ItemFactory.setTutorialItem(chestplate, false);
			player.getInventory().setChestplate(chestplate);
		}

		for(int i = 0; i < player.getInventory().getContents().length; i++) {
			ItemStack itemStack = player.getInventory().getContents()[i];
			PitItem item = ItemFactory.getItem(itemStack);
			if(!(item instanceof TaintedChestplate) && !(item instanceof TaintedScythe)) continue;
			ItemFactory.setTutorialItem(itemStack, false);
			player.getInventory().setItem(i, itemStack);
		}

		player.updateInventory();
	}

	public void giveFreshItems(Tutorial tutorial) {
		Player player = tutorial.getPlayer();
		if(Misc.getEmptyInventorySlots(player) < 2) {
			tutorial.sendMessage("&cYou do not have enough inventory space to continue the tutorial! Create more before talking to me again", 5);
			return;
		}

		ItemStack scythe = MysticFactory.getFreshItem(MysticType.TAINTED_SCYTHE, null);
		ItemFactory.setTutorialItem(scythe, true);
		EnchantManager.setItemLore(scythe, player);
		AUtil.giveItemSafely(player, scythe);

		ItemStack chestplate = MysticFactory.getFreshItem(MysticType.TAINTED_CHESTPLATE, null);
		ItemFactory.setTutorialItem(chestplate, true);
		EnchantManager.setItemLore(chestplate, player);
		AUtil.giveItemSafely(player, chestplate);
	}
}
