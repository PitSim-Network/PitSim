package net.pitsim.pitsim.cosmetics;

import net.pitsim.pitsim.cosmetics.particles.ParticleColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class ColorableCosmetic extends PitCosmetic {
	private Map<UUID, ParticleColor> colorMap = new HashMap<>();

	public ColorableCosmetic(String displayName, String refName, CosmeticType cosmeticType) {
		super(displayName, refName, cosmeticType);
	}

	public ParticleColor getParticleColor(Player player) {
		return colorMap.get(player.getUniqueId());
	}

	public void setParticleColor(Player player, ParticleColor particleColor) {
		colorMap.put(player.getUniqueId(), particleColor);
	}
}
