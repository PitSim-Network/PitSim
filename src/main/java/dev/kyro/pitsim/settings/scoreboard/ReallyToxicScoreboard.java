package dev.kyro.pitsim.settings.scoreboard;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.overworld.ReallyToxic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ReallyToxicScoreboard extends ScoreboardOption {

	@Override
	public String getDisplayName() {
		return "&aReally Toxic";
	}

	@Override
	public String getRefName() {
		return "reallytoxic";
	}

	@Override
	public String getValue(PitPlayer pitPlayer) {
		int attackerCharge = HitCounter.getCharge(pitPlayer.player, ReallyToxic.INSTANCE);
		if(attackerCharge == 0) return null;
		return "&6Toxic: &a-" + Math.min(attackerCharge, ReallyToxic.getMaxReduction()) + "% Healing";
	}

	@Override
	public ItemStack getBaseDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.INK_SACK, 1, 2)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Shows the current healing",
						"&7reduction from " + ReallyToxic.INSTANCE.getDisplayName(),
						"&7when applicable"
				)).getItemStack();
		return itemStack;
	}
}
