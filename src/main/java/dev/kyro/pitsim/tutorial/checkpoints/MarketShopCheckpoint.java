package dev.kyro.pitsim.tutorial.checkpoints;

import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.diamond.DiamondLeggings;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.tutorial.NPCCheckpoint;
import dev.kyro.pitsim.tutorial.Tutorial;
import dev.kyro.pitsim.tutorial.TutorialObjective;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarketShopCheckpoint extends NPCCheckpoint {
	public MarketShopCheckpoint() {
		super(TutorialObjective.MARKET_SHOP, new Location(MapManager.getDarkzone(),
				205.5, 91, -86.5, 13, 0));
	}

	@Override
	public void onCheckpointEngage(Tutorial tutorial) {
		tutorial.sendMessage("&eHere is the &b&lTainted Shop &eand the &3&lPlayer Market&e!", 0);
		tutorial.sendMessage("&eThese are the primary places for buying &5Darkzone Items &efor &fSouls&e.", 60);
		tutorial.sendMessage("&eYou can buy and sell items with other &aPlayers &ein the &3&lPlayer Market&e.", 120);
		tutorial.sendMessage("&eAnd at the &b&lTainted Shop &eyou can buy specific items!", 180);
		tutorial.sendMessage("&eGo ahead and &6buy &ethe &bDiamond Leggings &efrom the &b&lTainted Shop&e.", 240);
		tutorial.sendMessage("&eTalk to me again once you've done so.", 300);
	}

	@Override
	public void onCheckpointSatisfy(Tutorial tutorial) {
		tutorial.delayTask(() -> removeTutorialTag(tutorial.getPlayer()), getSatisfyDelay());
		tutorial.sendMessage("&eGreat!", 0);
		tutorial.sendMessage("&eNow use those leggings to stay protected since your &dMystic Pants &ewon't work here!", 60);
		tutorial.sendMessage("&eDon't forget to come back and check out the &3&lPlayer Market &eonce you have some more &fSouls&e.", 120);
	}

	@Override
	public int getEngageDelay() {
		return 300;
	}

	@Override
	public int getSatisfyDelay() {
		return 120;
	}

	@Override
	public boolean canEngage(Tutorial tutorial) {
		return true;
	}

	@Override
	public boolean canSatisfy(Tutorial tutorial) {
		Player player = tutorial.getPlayer();
		List<ItemStack> items = new ArrayList<>(Arrays.asList(player.getInventory().getContents()));
		items.addAll(Arrays.asList(player.getInventory().getArmorContents()));

		for(ItemStack item : items) {
			PitItem pitItem = ItemFactory.getItem(item);
			if(!(pitItem instanceof DiamondLeggings)) continue;
			if(ItemFactory.isTutorialItem(item)) return true;
		}

		return false;
	}

	public void removeTutorialTag(Player player) {
		for(int i = 0; i < player.getInventory().getContents().length; i++) {
			ItemStack item = player.getInventory().getContents()[i];
			PitItem pitItem = ItemFactory.getItem(item);
			if(!(pitItem instanceof DiamondLeggings)) continue;
			if(ItemFactory.isTutorialItem(item)) {
				ItemFactory.setTutorialItem(item, false);
				player.getInventory().setItem(i, item);
				return;
			}
		}

		ItemStack[] armor = player.getInventory().getArmorContents();
		for(int i = 0; i < armor.length; i++) {
			ItemStack item = armor[i];
			PitItem pitItem = ItemFactory.getItem(item);
			if(!(pitItem instanceof DiamondLeggings)) continue;
			if(ItemFactory.isTutorialItem(item)) {
				ItemFactory.setTutorialItem(item, false);
				armor[i] = item;
				player.getInventory().setArmorContents(armor);
				return;
			}
		}
	}

	@Override
	public void onCheckPointDisengage(Tutorial tutorial) {

	}


}
