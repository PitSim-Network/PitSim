package dev.kyro.pitsim.tutorial.checkpoints;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.tutorial.NPCCheckpoint;
import dev.kyro.pitsim.tutorial.Tutorial;
import dev.kyro.pitsim.tutorial.TutorialObjective;
import org.bukkit.Location;

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
		return true;
	}
}
