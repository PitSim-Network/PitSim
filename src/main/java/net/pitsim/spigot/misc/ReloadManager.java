package net.pitsim.spigot.misc;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.PitSim;
import org.bukkit.Bukkit;
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
						AOutput.log("Jar upload finished. Restarting plugin");
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "plugman reload pitremake");
						return;
					}

					if(file.lastModified() != lastModified && !startedUpload) {
						startedUpload = true;
						AOutput.log("Detected server jar upload. Waiting for completion");
					}
					lastModified = file.lastModified();
				} catch(Exception exception) {
					throw new RuntimeException(exception);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 10L);
	}
}