package net.pitsim.spigot.darkzone.altar.pedestals;

import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.darkzone.altar.AltarPedestal;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PedestalSpin {

	public static final double MINIMUM_VELOCITY = 10;
	public static final double MAXIMUM_VELOCITY = 60;
	public static final double MAXIMUM_HEIGHT = 70;
	public static final double ACCELERATION = 4;
	public static final double DECELERATION = 1.5;

	public Player player;
	public AltarPedestal pedestal;

	public boolean decelerating = false;
	public int ticks = 0;

	public PedestalSpin(Player player, AltarPedestal pedestal) {
		this.player = player;
		this.pedestal = pedestal;

		rotatePedestal();
	}

	public void stop() {
		decelerating = true;
	}

	public double getLocationY() {
		return Math.min(ticks, MAXIMUM_HEIGHT) / 60D;
	}

	public void rotatePedestal() {
		ArmorStand stand = pedestal.stand;

		new BukkitRunnable() {
			int yaw = 0;
			double velocity = MINIMUM_VELOCITY;

			@Override
			public void run() {
				if(stand != null) {

					byte yVelocity = (byte) ((decelerating ? -1 : 1));
					if(!decelerating && ticks >= MAXIMUM_HEIGHT) {
						yVelocity = 0;
					}

					PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(stand.getEntityId(),
							(byte)0, yVelocity, (byte)0, (byte) yaw, (byte)0, false);
					EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
					nmsPlayer.playerConnection.sendPacket(packet);

					if(!decelerating) velocity = Math.min(velocity + ACCELERATION, MAXIMUM_VELOCITY);
					else velocity = Math.max(velocity - DECELERATION, MINIMUM_VELOCITY);

					if(velocity == MINIMUM_VELOCITY) {
						cancel();

						Location loc = pedestal.stand.getLocation();
						int x = MathHelper.floor(loc.getX() * 32.0);
						int y = MathHelper.floor(loc.getY() * 32.0);
						int z = MathHelper.floor(loc.getZ() * 32.0);
						byte yaw = (byte) (0);
						byte pitch = (byte) (0);

						new BukkitRunnable() {
							@Override
							public void run() {
								PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(stand.getEntityId(), x, y, z, yaw, pitch, false);
								nmsPlayer.playerConnection.sendPacket(teleportPacket);
							}
						}.runTaskLater(PitSim.INSTANCE, 20);

						return;
					}

					yaw += velocity;

					if(yaw >= 256) {
						yaw = 0;
					}
				}

				ticks += 2;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 2);
	}
}
