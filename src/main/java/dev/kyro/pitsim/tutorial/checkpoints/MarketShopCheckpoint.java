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
		tutorial.sendMessage("You can now access the Market Shop", 0);
		tutorial.sendMessage("This is a special area where you can get special items", 20);
		tutorial.sendMessage("You can access the Market Shop by typing &b/ms", 40);
		tutorial.sendMessage("You can also access the Market Shop by clicking the &bMarket Shop &7item in your inventory", 60);
	}

	@Override
	public void onCheckpointSatisfy(Tutorial tutorial) {
		tutorial.delayTask(() -> removeTutorialTag(tutorial.getPlayer()), getSatisfyDelay());
		tutorial.sendMessage("You have accessed the Market Shop", 0);
		tutorial.sendMessage("You can now access the Market Shop by typing &b/ms", 20);
		tutorial.sendMessage("You can also access the Market Shop by clicking the &bMarket Shop &7item in your inventory", 40);
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
