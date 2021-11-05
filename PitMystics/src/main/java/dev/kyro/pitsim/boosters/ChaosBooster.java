package dev.kyro.pitsim.boosters;

import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ChaosBooster extends Booster {
	public ChaosBooster() {
		super("Chaos Booster", "chaos");
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!isActive()) return;
		killEvent.goldMultipliers.add(100D);
	}

	@Override
	public List<String> getDescription() {
		return null;
	}

	@Override
	public ItemStack getDisplayItem() {
		return null;
	}
}
