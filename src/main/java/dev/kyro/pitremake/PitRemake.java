package dev.kyro.pitremake;

import dev.kyro.arcticapi.ArcticAPI;
import dev.kyro.pitremake.commands.ATestCommand;
import dev.kyro.pitremake.commands.EnchantCommand;
import dev.kyro.pitremake.commands.FreshCommand;
import dev.kyro.pitremake.controllers.DamageManager;
import dev.kyro.pitremake.controllers.EnchantManager;
import dev.kyro.pitremake.enchants.*;
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
		EnchantManager.registerEnchant(new Gamble());
		EnchantManager.registerEnchant(new Executioner());
		EnchantManager.registerEnchant(new ComboPerun());
		EnchantManager.registerEnchant(new ComboDamage());
		EnchantManager.registerEnchant(new ComboHeal());

		EnchantManager.registerEnchant(new Solitude());
		EnchantManager.registerEnchant(new DiamondAllergy());
	}

	private void registerCommands() {

		getCommand("atest").setExecutor(new ATestCommand());
		getCommand("enchant").setExecutor(new EnchantCommand());
		getCommand("fresh").setExecutor(new FreshCommand());
	}

	private void registerListeners() {

        getServer().getPluginManager().registerEvents(new DamageManager(), this);
	}

	private void loadConfig() {

		getConfig().options().copyDefaults(true);
		saveConfig();
	}
}
