package dev.kyro.pitsim.acosmetics.particles;

import dev.kyro.pitsim.RedstoneColor;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import dev.kyro.pitsim.acosmetics.PitParticle;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;

public class SmokeParticle extends PitParticle {
	public SmokeParticle(PitCosmetic pitCosmetic) {
		super(pitCosmetic);
	}

	@Override
	public void display(EntityPlayer entityPlayer, Location location, RedstoneColor redstoneColor) {
		entityPlayer.playerConnection.sendPacket(new PacketPlayOutWorldParticles(
				EnumParticle.SMOKE_LARGE, true, (float) location.getX(), (float) location.getY(), (float) location.getZ(),
				0, 0, 0, 0, 0
		));
	}
}
