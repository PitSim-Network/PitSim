package dev.kyro.pitsim.misc;

import dev.kyro.pitsim.PitSim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.lang.reflect.Method;

public class ReloadManager {

	public static void init() {
		new BukkitRunnable() {
			long lastModified = 0;
			boolean startedUpload = false;

			@Override
			public void run() {
				try {
					Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
					getFileMethod.setAccessible(true);
					File file = (File) getFileMethod.invoke(PitSim.INSTANCE);

					if(lastModified == 0) {
						lastModified = file.lastModified();
						return;
					}

					if(file.lastModified() == lastModified && startedUpload) {
						cancel();
						System.out.println("Jar upload finished. Restarting plugin");
						if(PitSim.getStatus() != PitSim.ServerStatus.ALL)
							for(Player onlinePlayer : Bukkit.getOnlinePlayers())
								onlinePlayer.kickPlayer("reloading plugin");
						new BukkitRunnable() {
							@Override
							public void run() {
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "plugman reload pitremake");
							}
						}.runTaskLater(PitSim.INSTANCE, 1L);

						return;
					}

					if(file.lastModified() != lastModified && !startedUpload) {
						startedUpload = true;
						System.out.println("Detected server jar upload. Waiting for completion");
					}
					lastModified = file.lastModified();
				} catch(Exception exception) {
					throw new RuntimeException(exception);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 10L);
	}
}