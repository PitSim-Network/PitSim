package dev.kyro.pitsim;

public enum RedstoneColor {
	DARK_RED(0.66406F, 0F, 0F, 1),
	RED(1F, 0.33203F, 0.33203F, 1),
	GOLD(1F, 0.66406F, 0F, 1),
	YELLOW(1F, 1F, 0.33203F, 1),
	DARK_GREEN(Float.MIN_VALUE, 0.66406F, 0F, 1),
	GREEN(0.33203F, 1F, 0.33203F, 1),
	AQUA(0.33203F, 1F, 1F, 1),
	DARK_AQUA(Float.MIN_VALUE, 0.66406F, 0.66406F, 1),
	DARK_BLUE(Float.MIN_VALUE, Float.MIN_VALUE, 0.66406F, 1),
	BLUE(0.33203F, 0.33203F, 1F, 1),
	LIGHT_PURPLE(1F, 0.33203F, 1F, 1),
	DARK_PURPLE(0.66406F, 0F, 0.66406F, 1),
	WHITE(1F, 1F, 1F, 1),
	GRAY(0.66406F, 0.66406F, 0.66406F, 1),
	DARK_GRAY(0.33203F, 0.33203F, 0.33203F, 1),
	BLACK(Float.MIN_VALUE, 0F, 0F, 1);

	public float red;
	public float green;
	public float blue;
	public float brightness;

	RedstoneColor(float red, float green, float blue, float brightness) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.brightness = brightness;
	}

	public static RedstoneColor getRedstoneColor(String refName) {
		for(RedstoneColor value : values()) {
			if(refName.equalsIgnoreCase(value.name().replaceAll("_", ""))) return value;
		}
		return null;
	}
}
