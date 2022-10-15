package dev.kyro.pitsim.acosmetics;

import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public abstract class PitCosmetic {
	private String displayName;
	public String refName;
	public CosmeticType cosmeticType;

//	This is only null if implemented in subclass; just adding because its going to be super common
	public BukkitTask runnable;

	public PitCosmetic(String displayName, String refName, CosmeticType cosmeticType) {
		this.displayName = displayName;
		this.refName = refName;
		this.cosmeticType = cosmeticType;
	}

	public abstract void onEnable(PitPlayer pitPlayer);
	public abstract void onDisable(PitPlayer pitPlayer);
	public abstract ItemStack getRawDisplayItem();

	public ItemStack getDisplayItem(boolean equipped) {
		ItemStack itemStack = getRawDisplayItem();
		if(equipped) Misc.addEnchantGlint(itemStack);
		return itemStack;
	}

	//	TODO: Implement
	public boolean isUnlocked(PitPlayer pitPlayer) {
		return true;
	}

//	TODO: Implement
	public boolean isEnabled(PitPlayer pitPlayer) {
		return true;
	}

	public String getDisplayName() {
		return ChatColor.translateAlternateColorCodes('&', displayName);
	}
}
