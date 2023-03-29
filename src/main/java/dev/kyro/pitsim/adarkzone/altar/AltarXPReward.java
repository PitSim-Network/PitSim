package dev.kyro.pitsim.adarkzone.altar;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.DarkzoneLeveling;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import net.minecraft.server.v1_8_R3.EntityExperienceOrb;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityExperienceOrb;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class AltarXPReward {
	public Player player;
	public double xp;
	public int orbCount = new Random().nextInt(5 - 3) + 3;

	public AltarXPReward(Player player, double xp) {
		this.player = player;
		this.xp = xp;
	}

	public void spawn(Location location) {
		for(int i = 0; i < orbCount; i++) {
			World world = ((CraftWorld) location.getWorld()).getHandle();
			EntityExperienceOrb orb = new EntityExperienceOrb(world, location.getX(), location.getY(), location.getZ(), (int) xp);
			PacketPlayOutSpawnEntityExperienceOrb spawn = new PacketPlayOutSpawnEntityExperienceOrb(orb);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawn);

			new BukkitRunnable() {
				@Override
				public void run() {
					despawn(orb);
					reward();
				}
			}.runTaskLater(PitSim.INSTANCE, 20 + (3L * i));
		}
	}

	public void despawn(EntityExperienceOrb orb) {
		PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(orb.getId());
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroy);
	}

	public void reward() {
		DarkzoneLeveling.giveXP(PitPlayer.getPitPlayer(player), xp / orbCount);
	}
}
