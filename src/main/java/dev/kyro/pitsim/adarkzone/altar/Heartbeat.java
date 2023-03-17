package dev.kyro.pitsim.adarkzone.altar;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Heartbeat {
	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				Location altarLocation = AltarManager.TEXT_LOCATION.clone();
				for(Player player : Bukkit.getOnlinePlayers()) {
					EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

					if(player.getLocation().distance(altarLocation) < AltarManager.EFFECT_RADIUS) {
						nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(2, 0));
						nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(8, 1000f));
						nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, 3f));

						Misc.applyPotionEffect(player, PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 9, false, false);
						Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, 15, 0, false, false);
						Sounds.HEARTBEAT1.play(player.getLocation());

						new BukkitRunnable() {
							@Override
							public void run() {
								Sounds.HEARTBEAT2.play(player.getLocation());
								Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, 15, 0, false, false);
							}
						}.runTaskLater(PitSim.INSTANCE, 8);

//						new BukkitRunnable() {
//							int count = 0;
//
//							@Override
//							public void run() {
//								if(++count >= 14) {
//									cancel();
//								}
//								Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, 2, 0, false, false);
//							}
//						}.runTaskTimer(PitSim.INSTANCE, 23, 1);

					} else {
						nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(2, 0));
						nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(8, 1000f));
						nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, 0f));
						player.removePotionEffect(PotionEffectType.NIGHT_VISION);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 36);
	}

	public static void init() {

	}
}
