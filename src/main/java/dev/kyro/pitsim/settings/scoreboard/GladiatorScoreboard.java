package dev.kyro.pitsim.settings.scoreboard;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.perks.Gladiator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GladiatorScoreboard extends ScoreboardOption {

	@Override
	public String getDisplayName() {
		return "&9Gladiator";
	}

	@Override
	public String getRefName() {
		return "gladiator";
	}

	@Override
	public String getValue(PitPlayer pitPlayer) {
		if(!Gladiator.INSTANCE.playerHasUpgrade(pitPlayer.player)) return null;
		int reduction = Gladiator.getReduction(pitPlayer.player);
		if(reduction == 0) return null;
		return "&6Gladiator: &9-" + reduction + "%";
	}

	@Override
	public ItemStack getBaseDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.BONE)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Shows the current damage",
						"&7reduction from " + "&a" + Gladiator.INSTANCE.displayName + " &7when",
						"&7applicable"
				)).getItemStack();
		return itemStack;
	}
}
