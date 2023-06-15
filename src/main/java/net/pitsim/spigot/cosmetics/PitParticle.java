package net.pitsim.spigot.cosmetics;

import net.pitsim.spigot.cosmetics.particles.ParticleColor;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

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

	public void display(Player player, Location location) {
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		display(entityPlayer, location, (ParticleColor) null);
	}

	public void display(Player player, Location location, ParticleColor particleColor) {
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		display(entityPlayer, location, particleColor);
	}

	public void display(Player player, Location location, ParticleOffset particleOffset, ParticleColor particleColor) {
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		display(entityPlayer, particleOffset.applyOffset(location, accountForYaw, accountForPitch), particleColor);
	}

	public void display(Player player, Location location, ParticleOffset particleOffset) {
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		display(entityPlayer, location, particleOffset, null);
	}

	public void display(List<Player> players, Location location) {
		for(Player player : players) display(player, location);
	}

	public void display(List<Player> players, Location location, ParticleColor particleColor) {
		for(Player player : players) display(player, location, particleColor);
	}

	public void display(List<Player> players, Location location, ParticleOffset particleOffset) {
		for(Player player : players) display(player, location, particleOffset);
	}

	public void display(List<Player> players, Location location, ParticleOffset particleOffset, ParticleColor particleColor) {
		for(Player player : players) display(player, location, particleOffset, particleColor);
	}
}
