package dev.kyro.pitsim.settings.scoreboard;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.perks.StrengthChaining;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class StrengthScoreboard extends ScoreboardOption {

	@Override
	public String getDisplayName() {
		return "&cStrength-Chaining";
	}

	@Override
	public String getRefName() {
		return "strengthchaining";
	}

	@Override
	public String getValue(PitPlayer pitPlayer) {
		if(!StrengthChaining.INSTANCE.playerHasUpgrade(pitPlayer.player)) return null;
		int amplifier = StrengthChaining.amplifierMap.getOrDefault(pitPlayer.player.getUniqueId(), 0);
		if(amplifier == 0) return null;
		int duration = StrengthChaining.durationMap.getOrDefault(pitPlayer.player.getUniqueId(), 0);
		int seconds = (int) Math.ceil(duration / 20.0);
		return "&6Strength: &c" + AUtil.toRoman(amplifier) + " &7(" + seconds + ")";
	}

	@Override
	public ItemStack getBaseDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.REDSTONE)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Shows the current level and",
						"&7duration of strength from",
						"&a" + StrengthChaining.INSTANCE.displayName + " &7when applicable"
				)).getItemStack();
		return itemStack;
	}
}
