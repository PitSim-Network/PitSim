package dev.kyro.pitsim.cosmetics;

import dev.kyro.pitsim.cosmetics.particles.ParticleColor;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;

public abstract class PitParticle {
	public boolean accountForYaw;
	public boolean accountForPitch;

	public PitParticle() {

	}

	public PitParticle(boolean accountForYaw, boolean accountForPitch) {
		this.accountForYaw = accountForYaw;
		this.accountForPitch = accountForPitch;
	}

	public abstract void display(EntityPlayer entityPlayer, Location location, ParticleColor particleColor);

//	public void display(List<EntityPlayer> entityPlayers, Location location) {
//		for(EntityPlayer entityPlayer : entityPlayers) display(entityPlayer, location);
//	}

	public void display(EntityPlayer entityPlayer, Location location, ParticleOffset particleOffset, ParticleColor particleColor) {
		display(entityPlayer, particleOffset.applyOffset(location, accountForYaw, accountForPitch), particleColor);
	}
}
