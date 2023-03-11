package dev.kyro.pitsim.adarkzone.altar.pedestals;

import dev.kyro.pitsim.adarkzone.altar.AltarPedestal;
import org.bukkit.Location;

public class WealthPedestal extends AltarPedestal {
	public WealthPedestal(Location location) {
		super(location);
	}

	@Override
	public String getDisplayName() {
		return "&6&lWEALTH";
	}
}
