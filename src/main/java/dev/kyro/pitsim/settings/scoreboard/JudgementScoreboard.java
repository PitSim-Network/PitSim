package dev.kyro.pitsim.settings.scoreboard;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.HelmetAbility;
import dev.kyro.pitsim.controllers.objects.HelmetManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.helmetabilities.JudgementAbility;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class JudgementScoreboard extends ScoreboardOption {

	@Override
	public String getDisplayName() {
		return "&9Judgement";
	}

	@Override
	public String getRefName() {
		return "judgement";
	}

	@Override
	public String getValue(PitPlayer pitPlayer) {
		ItemStack helmet = HelmetManager.getHelmet(pitPlayer.player);
		HelmetAbility deadAbility = HelmetManager.getAbility(helmet);
		HelmetAbility liveAbility = HelmetManager.abilities.get(pitPlayer.player);
		if(!(deadAbility instanceof JudgementAbility)) return null;

		if(liveAbility != null && liveAbility.isActive) {
			int remainingSeconds = (int) Math.ceil(JudgementAbility.maxActivationMap.getOrDefault(pitPlayer.player, 0) / 20.0);
			return "&6Judgement: &e(" + remainingSeconds + ")";
		} else {
			if(!JudgementAbility.cooldownMap.containsKey(pitPlayer.player.getUniqueId())) return "&6Judgement: &aReady";
			int remainingSeconds = (int) Math.ceil(JudgementAbility.cooldownMap.getOrDefault(pitPlayer.player.getUniqueId(), 0) / 20.0);
			return "&6Judgement: &7(" + remainingSeconds + ")";
		}
	}

	@Override
	public ItemStack getBaseDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.BEACON)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Shows the remaining time",
						"&9" + JudgementAbility.INSTANCE.name + " &7is active/on cooldown",
						"&7for when applicable"
				)).getItemStack();
		return itemStack;
	}
}
