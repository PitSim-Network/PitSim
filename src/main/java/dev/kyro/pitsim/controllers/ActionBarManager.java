package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.megastreaks.RNGesus;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActionBarManager implements Listener {
	public static Map<Player, Integer> lockedTimeMap = new HashMap<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Map.Entry<Player, Integer> entry : new ArrayList<>(lockedTimeMap.entrySet())) {
					if(entry.getValue() - 1 == 0) {
						lockedTimeMap.remove(entry.getKey());
					} else {
						lockedTimeMap.put(entry.getKey(), entry.getValue() - 1);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	public static void sendActionBar(Player player, String message) {
		sendActionBar(player, LockTime.FULL_OPACITY_TIME, message);
	}

	public static void sendActionBar(Player player, LockTime lockTime, String message) {
		if(lockTime == null) {
			if(lockedTimeMap.containsKey(player)) return;
		} else {
			lockedTimeMap.put(player, lockTime.lockedTicks);
		}

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.megastreak instanceof RNGesus && pitPlayer.getKills() < RNGesus.INSTABILITY_THRESHOLD && pitPlayer.megastreak.isOnMega())
			return;

		PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" +
				ChatColor.translateAlternateColorCodes('&', message) + "\"}"), (byte) 2);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

//	controls the amount of ticks that the action bar is locked from displaying low priority messages
	public enum LockTime {
	SHORT_TIME(30), // time that action bar message is opaque
	FULL_OPACITY_TIME(2 * 20), // time that action bar message is opaque
	FULL_TIME(3 * 20), // time it takes until action bar message has fully faded out
	EXTRA_TIME(4 * 20), // fade + 1 second
	EXTRA_EXTRA_TIME(5 * 20); // fade + 2 seconds

		public int lockedTicks;

		LockTime(int ticks) {
			this.lockedTicks = ticks;
		}
	}
}
