package dev.kyro.pitsim.cosmetics;

import dev.kyro.pitsim.cosmetics.particles.ParticleColor;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;

public abstract class PitParticle {
	public boolean accountForYaw;
	public boolean accountForPitch;

	public PitParticle() {

	}

	public PitParticle(boolean accountForPitch, boolean accountForYaw) {
		this.accountForYaw = accountForYaw;
		this.accountForPitch = accountForPitch;
	}

	public abstract void display(EntityPlayer entityPlayer, Location location, ParticleColor particleColor);

	public void display(EntityPlayer entityPlayer, Location location) {
		display(entityPlayer, location, (ParticleColor) null);
	}

	public void display(EntityPlayer entityPlayer, Location location, ParticleOffset particleOffset, ParticleColor particleColor) {
		display(entityPlayer, particleOffset.applyOffset(location, accountForYaw, accountForPitch), particleColor);
	}

	public void display(EntityPlayer entityPlayer, Location location, ParticleOffset particleOffset) {
		display(entityPlayer, location, particleOffset, null);
	}
}
