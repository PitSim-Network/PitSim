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
		tutorial.delayTask(() -> giveFreshItems(tutorial), 120);

		tutorial.sendMessage("&eHere we have the &5&lTainted Well&e.", 0);
		tutorial.sendMessage("&eThis is where you can enchant the various &aFresh Items &efound around the Darkzone.", 40);
		tutorial.sendMessage("&eHow about you give it a try?", 80);
		tutorial.sendMessage("&dEnchant &ethe &5Scythe &eand &5Chestplate &eI've given you to &fTier II&e.", 120);
		tutorial.sendMessage("&eYou can do this by right clicking the &5&lTainted Well &eand right-clicking the &aEnchant &ebutton.", 160);
		tutorial.sendMessage("&eMake sure to enchant each item twice in order to get it to &fTier II&e.", 200);
		tutorial.sendMessage("&eNext, use the &cRemove Item &ebutton to reclaim your item.", 240);
		tutorial.sendMessage("&eOnce you have done this to &aBoth Items&e, come back to me and I will give you your next task.", 280);
	}

	@Override
	public void onCheckpointSatisfy(Tutorial tutorial) {
		tutorial.delayTask(() -> removeTutorialTag(tutorial.getPlayer()), getSatisfyDelay());
		tutorial.sendMessage("&eNicely done!", 0);
		tutorial.sendMessage("&eYou can keep those items, as you'll be needing them on your &5Darkzone &eadventure.", 40);
	}

	@Override
	public int getEngageDelay() {
		return 280;
	}

	@Override
	public int getSatisfyDelay() {
		return 40;
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
