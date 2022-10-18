package dev.kyro.pitsim;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ParticleColor {
	DARK_RED("&4Dark Red", 1,  0.66406F, 0F, 0F, 1),
	RED("&cRed", 1,  1F, 0.33203F, 0.33203F, 1),
	GOLD("&6Orange", 14,  1F, 0.66406F, 0F, 1),
	YELLOW("&eYellow", 11,  1F, 1F, 0.33203F, 1),
	DARK_GREEN("&2Dark Green", 2,  Float.MIN_VALUE, 0.66406F, 0F, 1),
	GREEN("&aLime", 10,  0.33203F, 1F, 0.33203F, 1),
	AQUA("&bLight Blue", 12,  0.33203F, 1F, 1F, 1),
	DARK_AQUA("&3Cyan", 6,  Float.MIN_VALUE, 0.66406F, 0.66406F, 1),
	BLUE("&9Blue", 4,  0.33203F, 0.33203F, 1F, 1),
	DARK_BLUE("&1Dark Blue", 4,  Float.MIN_VALUE, Float.MIN_VALUE, 0.66406F, 1),
	LIGHT_PURPLE("&dPink", 13,  1F, 0.33203F, 1F, 1),
	DARK_PURPLE("&5Purple", 5,  0.66406F, 0F, 0.66406F, 1),
	WHITE("&fWhite", 15,  1F, 1F, 1F, 1),
	GRAY("&7Gray", 7,  0.66406F, 0.66406F, 0.66406F, 1),
	DARK_GRAY("&8Dark Gray", 8,  0.33203F, 0.33203F, 0.33203F, 1),
	BLACK("&0Black", 0,  Float.MIN_VALUE, 0F, 0F, 1);

	public String displayName;
	public int data;
	public float red;
	public float green;
	public float blue;
	public float brightness;

	ParticleColor(String displayName, int data, float red, float green, float blue, float brightness) {
		this.displayName = displayName;
		this.data = data;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.brightness = brightness;
	}

	public ItemStack getDisplayItem(boolean equipped) {
		ItemStack itemStack = new AItemStackBuilder(Material.INK_SACK, 1, data)
				.setName(displayName)
				.setLore(new ALoreBuilder(
						"&7Click to select the color " + displayName
				))
				.getItemStack();
		if(equipped) Misc.addEnchantGlint(itemStack);
		return itemStack;
	}

	public static ParticleColor getParticleColor(String refName) {
		for(ParticleColor value : values()) {
			if(refName.equalsIgnoreCase(value.name().replaceAll("_", ""))) return value;
		}
		return null;
	}
}
