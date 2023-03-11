package dev.kyro.pitsim.adarkzone.altar.pedestals;

import dev.kyro.pitsim.adarkzone.altar.AltarPedestal;
import org.bukkit.Location;

public class KnowledgePedestal extends AltarPedestal {
	public KnowledgePedestal(Location location) {
		super(location);
	}

	@Override
	public String getDisplayName() {
		return "&3&lKNOWLEDGE";
	}
}
