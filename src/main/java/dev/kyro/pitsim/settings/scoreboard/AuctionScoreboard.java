package dev.kyro.pitsim.settings.scoreboard;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.AuctionManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AuctionScoreboard extends ScoreboardOption {

	@Override
	public String getDisplayName() {
		return "&fDark Auctions";
	}

	@Override
	public String getRefName() {
		return "darkauctions";
	}

	@Override
	public String getValue(PitPlayer pitPlayer) {
		if(AuctionManager.haveAuctionsEnded()) return "&6Auctions: &eEnded";
		return "&6Auctions: &e" + AuctionManager.getRemainingTime();
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		ItemStack itemStack = new AItemStackBuilder(Material.GHAST_TEAR)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Shows the remaining time left",
						"&7on the darkzone auctions"
				)).getItemStack();
		return itemStack;
	}
}
