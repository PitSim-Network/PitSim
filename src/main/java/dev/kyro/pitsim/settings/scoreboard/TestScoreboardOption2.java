package dev.kyro.pitsim.settings.scoreboard;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TestScoreboardOption2 extends ScoreboardOption {

	public TestScoreboardOption2() {
		super("Test2", "test2");
	}

	@Override
	public String getValue(PitPlayer pitPlayer) {
		return "&6Test: &e" + pitPlayer.prestige * 2;
	}

	@Override
	public ItemStack getBaseDisplayItem() {
		return new AItemStackBuilder(Material.PORK)
				.setName("&fPrestige2")
				.setLore(new ALoreBuilder(
						"&7Shows the current prestige of the",
						"&7player when applicable"
				)).getItemStack();
	}
}
