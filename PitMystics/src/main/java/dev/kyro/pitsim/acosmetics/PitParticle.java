package dev.kyro.pitsim.acosmetics;

import dev.kyro.pitsim.RedstoneColor;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;

public abstract class PitParticle {
	public boolean accountForYaw;
	public boolean accountForPitch;

	public PitParticle(PitCosmetic pitCosmetic) {
		this.accountForYaw = pitCosmetic.accountForYaw;
		this.accountForPitch = pitCosmetic.accountForPitch;
	}

	public abstract void display(EntityPlayer entityPlayer, Location location, RedstoneColor redstoneColor);

//	public void display(List<EntityPlayer> entityPlayers, Location location) {
//		for(EntityPlayer entityPlayer : entityPlayers) display(entityPlayer, location);
//	}

	public void display(EntityPlayer entityPlayer, Location location, ParticleOffset particleOffset, RedstoneColor redstoneColor) {
		display(entityPlayer, particleOffset.applyOffset(location, accountForYaw, accountForPitch), redstoneColor);
	}
}
