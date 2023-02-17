package dev.kyro.pitsim.settings.scoreboard;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TestScoreboardOption extends ScoreboardOption {

	public TestScoreboardOption() {
		super("Test", "test");
	}

	@Override
	public String getValue(PitPlayer pitPlayer) {
		return "&6Test: &e" + pitPlayer.prestige;
	}

	@Override
	public ItemStack getBaseDisplayItem() {
		return new AItemStackBuilder(Material.BEACON)
				.setName("&fPrestige")
				.setLore(new ALoreBuilder(
						"&7Shows the current prestige of the",
						"&7player when applicable"
				)).getItemStack();
	}
}
