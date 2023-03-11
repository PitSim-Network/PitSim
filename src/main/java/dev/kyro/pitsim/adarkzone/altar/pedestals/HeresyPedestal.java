package dev.kyro.pitsim.adarkzone.altar.pedestals;

import dev.kyro.pitsim.adarkzone.altar.AltarPedestal;
import org.bukkit.Location;

public class HeresyPedestal extends AltarPedestal {
	public HeresyPedestal(Location location) {
		super(location);
	}

	@Override
	public String getDisplayName() {
		return "&5&lHERESY";
	}
}
