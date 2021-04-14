package dev.kyro.pitremake;

import dev.kyro.arcticapi.ArcticAPI;
import dev.kyro.pitremake.commands.ATestCommand;
import dev.kyro.pitremake.controllers.DamageManager;
import dev.kyro.pitremake.controllers.EnchantManager;
import dev.kyro.pitremake.enchants.Billionaire;
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
		registerEnchants();
	}

	@Override
	public void onDisable() {}

	private void registerEnchants() {

		EnchantManager.registerEnchant(new Billionaire());
	}

	private void registerCommands() {

		getCommand("atest").setExecutor(new ATestCommand());
	}

	private void registerListeners() {

        getServer().getPluginManager().registerEvents(new DamageManager(), this);
	}

	private void loadConfig() {

		getConfig().options().copyDefaults(true);
		saveConfig();
	}
}
