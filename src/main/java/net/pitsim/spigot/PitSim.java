package net.pitsim.spigot;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.pitsim.spigot.bots.LoadSkinCommand;
import net.pitsim.spigot.bots.SkinManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PitSim extends JavaPlugin {

	public static PitSim INSTANCE;

	@Override
	public void onEnable() {
		INSTANCE = this;

		loadConfig();
		new BukkitRunnable() {
			@Override
			public void run() {
				List<NPC> toRemove = new ArrayList<>();
				for(NPC npc : CitizensAPI.getNPCRegistry()) {
					toRemove.add(npc);
				}
				while(!toRemove.isEmpty()) {
					toRemove.get(0).destroy();
					toRemove.remove(0);
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 10);

		registerCommands();
		registerListeners();
	}

	@Override
	public void onDisable() {
		File file = new File("plugins/Citizens/saves.yml");
		if(file.exists()) file.deleteOnExit();
	}



	private void registerCommands() {
		getCommand("loadskin").setExecutor(new LoadSkinCommand());
	}

	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new SkinManager(), this);
	}

	private void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	@Override
	public void onLoad() {
		File file = new File("plugins/Citizens/save.yml");
		if(file.exists()) file.delete();
	}
}
