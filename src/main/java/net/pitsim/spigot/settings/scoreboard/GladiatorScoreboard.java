package net.pitsim.spigot.settings.scoreboard;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.perks.Gladiator;
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
		if(!Gladiator.INSTANCE.hasPerk(pitPlayer.player)) return null;
		int reduction = Gladiator.getReduction(pitPlayer.player);
		if(reduction == 0) return null;
		return "&6Gladiator: &9-" + reduction + "%";
	}

	@Override
	public ItemStack getBaseDisplayStack() {
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
