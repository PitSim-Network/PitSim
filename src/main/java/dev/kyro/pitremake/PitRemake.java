package dev.kyro.pitremake;

import dev.kyro.arcticapi.ArcticAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class PitRemake extends JavaPlugin {

	public static PitRemake INSTANCE;

	@Override
	public void onEnable() {

		INSTANCE = this;

		loadConfig();

		ArcticAPI.configInit(this, "prefix", "error-prefix");

		registerCommands();
		registerListeners();
	}

	@Override
	public void onDisable() {


	}

	private void loadConfig() {

		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	private void registerCommands() {

//        getCommand("printer").setExecutor(new PrinterCommand());
	}

	private void registerListeners() {

//        getServer().getPluginManager().registerEvents(new PrinterEvents(), this);
	}
}
