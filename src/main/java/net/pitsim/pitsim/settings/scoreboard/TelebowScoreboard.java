package net.pitsim.pitsim.settings.scoreboard;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.controllers.Cooldown;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enchants.overworld.Telebow;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TelebowScoreboard extends ScoreboardOption {

	@Override
	public String getDisplayName() {
		return "&9Telebow";
	}

	@Override
	public String getRefName() {
		return "telebow";
	}

	@Override
	public String getValue(PitPlayer pitPlayer) {
		if(!Telebow.INSTANCE.cooldowns.containsKey(pitPlayer.player.getUniqueId())) return null;
		Cooldown cooldown = Telebow.INSTANCE.cooldowns.get(pitPlayer.player.getUniqueId());
		if(!cooldown.isOnCooldown()) return null;
		int seconds = (int) Math.ceil(cooldown.getTicksLeft() / 20.0);
		return "&6Telebow: &e" + seconds + "s";
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		ItemStack itemStack = new AItemStackBuilder(Material.ENDER_PEARL)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Shows the current cooldown",
						"&7left on " + Telebow.INSTANCE.getDisplayName(),
						"&7when applicable"
				)).getItemStack();
		return itemStack;
	}
}
