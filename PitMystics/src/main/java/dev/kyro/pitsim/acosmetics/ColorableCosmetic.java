package dev.kyro.pitsim.acosmetics;

import dev.kyro.pitsim.RedstoneColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class ColorableCosmetic extends PitCosmetic {
	private Map<UUID, RedstoneColor> colorMap = new HashMap<>();

	public ColorableCosmetic(String displayName, String refName, CosmeticType cosmeticType) {
		super(displayName, refName, cosmeticType);
	}

	public RedstoneColor getRedstoneColor(Player player) {
		return colorMap.getOrDefault(player.getUniqueId(), RedstoneColor.RED);
	}

	public void setRedstoneColor(Player player, RedstoneColor redstoneColor) {
		colorMap.put(player.getUniqueId(), redstoneColor);
	}
}
