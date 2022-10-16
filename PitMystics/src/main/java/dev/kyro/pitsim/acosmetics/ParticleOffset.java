package dev.kyro.pitsim.acosmetics;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class ParticleOffset {
	public Vector offset;

	public ParticleOffset(double offsetX, double offsetY, double offsetZ) {
		this.offset = new Vector(offsetX, offsetY, offsetZ);
	}

	public Location applyOffset(Location location, boolean accountForYaw, boolean accountForPitch) {
		location = location.clone();
		Vector newOffset = offset.clone();

		double yaw = 0;
		if(accountForYaw) {
			yaw = location.getYaw();
			if(yaw < 0) yaw += 360;
			yaw = (yaw + 180) % 360;
		}
		double pitch = 0;
		if(accountForPitch) pitch = location.getPitch();
		if(accountForYaw | accountForPitch) RotationTools.rotate(newOffset, yaw, pitch, 0);

		return location.add(newOffset);
	}
}
