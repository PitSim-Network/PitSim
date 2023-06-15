package net.pitsim.spigot.cosmetics.particles;

import net.pitsim.spigot.cosmetics.PitParticle;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.material.MaterialData;

public class BlockCrackParticle extends PitParticle {
	public MaterialData materialData;

	public BlockCrackParticle(MaterialData materialData) {
		this(false, false, materialData);
		this.materialData = materialData;
	}

	public BlockCrackParticle(boolean accountForPitch, boolean accountForYaw, MaterialData materialData) {
		super(accountForPitch, accountForYaw);
		this.materialData = materialData;
	}

	@Override
	public void display(EntityPlayer entityPlayer, Location location, ParticleColor particleColor) {
		entityPlayer.playerConnection.sendPacket(new PacketPlayOutWorldParticles(
				EnumParticle.BLOCK_CRACK, true, (float) location.getX(), (float) location.getY(), (float) location.getZ(),
				0, 0, 0, 0, 0, (materialData.getData() << 12 | materialData.getItemTypeId() & 4095)
		));
	}
}
