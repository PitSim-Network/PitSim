package dev.kyro.pitsim.cosmetics.particles;

import dev.kyro.pitsim.ParticleColor;
import dev.kyro.pitsim.cosmetics.PitCosmetic;
import dev.kyro.pitsim.cosmetics.PitParticle;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;

public class ExplosionHugeParticle extends PitParticle {
	public ExplosionHugeParticle(PitCosmetic pitCosmetic) {
		super(pitCosmetic);
	}

	@Override
	public void display(EntityPlayer entityPlayer, Location location, ParticleColor particleColor) {
		entityPlayer.playerConnection.sendPacket(new PacketPlayOutWorldParticles(
				EnumParticle.EXPLOSION_HUGE, true, (float) location.getX(), (float) location.getY(), (float) location.getZ(),
				0, 0, 0, 0, 0
		));
	}
}
