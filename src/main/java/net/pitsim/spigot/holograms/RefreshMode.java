package net.pitsim.spigot.holograms;

public enum RefreshMode {
	AUTOMATIC_SLOW(20),
	AUTOMATIC_MEDIUM(5),
	AUTOMATIC_FAST(1),
	MANUAL(Integer.MAX_VALUE),
	;

	public final int iterations;

	RefreshMode(int iterations) {
		this.iterations = iterations;
	}
}
