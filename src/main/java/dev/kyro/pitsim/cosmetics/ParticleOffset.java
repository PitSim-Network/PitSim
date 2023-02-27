package dev.kyro.pitsim.cosmetics;

import dev.kyro.pitsim.misc.math.RotationUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class ParticleOffset {
	public Vector offset;

	public double randomX = 0;
	public double randomY = 0;
	public double randomZ = 0;

	public ParticleOffset() {
	}

	public ParticleOffset(double offsetX, double offsetY, double offsetZ) {
		this.offset = new Vector(offsetX, offsetY, offsetZ);
	}

	public ParticleOffset(double offsetX, double offsetY, double offsetZ, double randomX, double randomY, double randomZ) {
		this(offsetX, offsetY, offsetZ);
		this.randomX = randomX;
		this.randomY = randomY;
		this.randomZ = randomZ;
	}

	public ParticleOffset(Vector offset) {
		this.offset = offset.clone();
	}

	public ParticleOffset(Vector offset, double randomX, double randomY, double randomZ) {
		this.offset = offset.clone();
		this.randomX = randomX;
		this.randomY = randomY;
		this.randomZ = randomZ;
	}

	public Location applyOffset(Location location, boolean accountForYaw, boolean accountForPitch) {
		location = location.clone();
		Vector newOffset = offset.clone();

		double yaw = 0;
		if(accountForYaw) {
			yaw = location.getYaw();
			if(yaw < 0) yaw += 360;
		}
		double pitch = 0;
		if(accountForPitch) pitch = -location.getPitch();
		if(accountForYaw | accountForPitch) RotationUtils.rotate(newOffset, yaw, pitch, 0);

		location.add(random(randomX), random(randomY), random(randomZ));
		return location.add(newOffset);
	}

	public double random(double variance) {
		return Math.random() * variance - variance / 2;
	}
}
