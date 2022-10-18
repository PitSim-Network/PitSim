package dev.kyro.pitsim.acosmetics;

import dev.kyro.pitsim.RedstoneColor;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public abstract class PitCosmetic {
	private String displayName;
	public String refName;
	public CosmeticType cosmeticType;
	public boolean accountForYaw = true;
	public boolean accountForPitch = true;
	public boolean isColorCosmetic = false;
	public boolean isPermissionRequired = false;

	public Map<UUID, BukkitTask> runnableMap = new HashMap<>();

	public PitCosmetic(String displayName, String refName, CosmeticType cosmeticType) {
		this.displayName = displayName;
		this.refName = refName;
		this.cosmeticType = cosmeticType;
	}

	public abstract void onEnable(PitPlayer pitPlayer);
	public abstract void onDisable(PitPlayer pitPlayer);
	public abstract ItemStack getRawDisplayItem();

	public void loadColor(PitPlayer pitPlayer) {
		if(!(this instanceof ColorableCosmetic)) return;
		ColorableCosmetic colorableCosmetic = (ColorableCosmetic) this;
		colorableCosmetic.setRedstoneColor(pitPlayer.player, getColor(pitPlayer));
	}

	public ItemStack getDisplayItem(boolean equipped) {
		ItemStack itemStack = getRawDisplayItem();
		if(equipped) Misc.addEnchantGlint(itemStack);
		return itemStack;
	}

	private boolean hasPermission(PitPlayer pitPlayer, RedstoneColor redstoneColor) {
		String permission = "pitsim.cosmetic." + cosmeticType.refName + "." + refName;
		if(redstoneColor == null) {
			for(RedstoneColor testColor : RedstoneColor.values()) {
				if(pitPlayer.player.hasPermission(permission + "." +
						testColor.name().toLowerCase().replaceAll("_", ""))) return true;
			}
			return false;
		}
		permission += "." + redstoneColor.name().toLowerCase().replaceAll("_", "");
		return pitPlayer.player.hasPermission(permission);
	}

	public boolean isUnlocked(PitPlayer pitPlayer) {
		return isUnlocked(pitPlayer, null);
	}

	public boolean isUnlocked(PitPlayer pitPlayer, RedstoneColor redstoneColor) {
		if(isPermissionRequired) return hasPermission(pitPlayer, redstoneColor);
		PitPlayer.UnlockedCosmeticData unlockedCosmeticData = pitPlayer.unlockedCosmeticsMap.get(refName);
		if(unlockedCosmeticData == null) return false;
		if(isColorCosmetic && redstoneColor != null) {
			return unlockedCosmeticData.unlockedColors.contains(redstoneColor);
		}
		return true;
	}

	public List<RedstoneColor> getUnlockedColors(PitPlayer pitPlayer) {
		List<RedstoneColor> redstoneColors = new ArrayList<>();
		if(!isUnlocked(pitPlayer)) return redstoneColors;

		if(isPermissionRequired) {
			for(RedstoneColor redstoneColor : RedstoneColor.values()) {
				if(hasPermission(pitPlayer, redstoneColor)) redstoneColors.add(redstoneColor);
			}
			return redstoneColors;
		}

		List<RedstoneColor> unorderedColors = pitPlayer.unlockedCosmeticsMap.get(refName).unlockedColors;
		for(RedstoneColor redstoneColor : RedstoneColor.values()) {
			if(unorderedColors.contains(redstoneColor)) redstoneColors.add(redstoneColor);
		}
		return redstoneColors;
	}

//	Returns true if color cosmetic if any color is equipped
	public boolean isEquipped(PitPlayer pitPlayer) {
		if(!isUnlocked(pitPlayer)) return false;
		if(!pitPlayer.equippedCosmeticMap.containsKey(cosmeticType.name())) return false;
		PitPlayer.EquippedCosmeticData cosmeticData = pitPlayer.equippedCosmeticMap.get(cosmeticType.name());
		if(cosmeticData == null) return false;
		return cosmeticData.refName.equals(refName);
	}

	public boolean isEquipped(PitPlayer pitPlayer, RedstoneColor redstoneColor) {
		if(!isEquipped(pitPlayer) || !isUnlocked(pitPlayer, redstoneColor)) return false;
		PitPlayer.EquippedCosmeticData cosmeticData = pitPlayer.equippedCosmeticMap.get(cosmeticType.name());
		return cosmeticData.redstoneColor == redstoneColor;
	}

	public RedstoneColor getColor(PitPlayer pitPlayer) {
		if(!isEquipped(pitPlayer)) return null;
		PitPlayer.EquippedCosmeticData cosmeticData = pitPlayer.equippedCosmeticMap.get(cosmeticType.name());
		if(cosmeticData == null) return null;
		return cosmeticData.redstoneColor;
	}

	public String getDisplayName() {
		return ChatColor.translateAlternateColorCodes('&', displayName);
	}

	public double random(double variance) {
		return Math.random() * variance - variance / 2;
	}
}
