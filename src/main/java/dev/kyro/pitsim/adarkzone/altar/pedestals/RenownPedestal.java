package dev.kyro.pitsim.adarkzone.altar.pedestals;

import dev.kyro.pitsim.adarkzone.altar.AltarPedestal;
import org.bukkit.Location;

public class RenownPedestal extends AltarPedestal {
	public RenownPedestal(Location location) {
		super(location);
	}

	@Override
	public String getDisplayName() {
		return "&e&lRENOWN";
	}
}
