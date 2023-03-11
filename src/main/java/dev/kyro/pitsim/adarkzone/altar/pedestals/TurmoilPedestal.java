package dev.kyro.pitsim.adarkzone.altar.pedestals;

import dev.kyro.pitsim.adarkzone.altar.AltarPedestal;
import org.bukkit.Location;

public class TurmoilPedestal extends AltarPedestal {
	public TurmoilPedestal(Location location) {
		super(location);
	}

	@Override
	public String getDisplayName() {
		return "&2&lTURMOIL";
	}
}
