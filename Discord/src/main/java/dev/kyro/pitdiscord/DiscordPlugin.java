package dev.kyro.pitdiscord;

import dev.kyro.arcticapi.ArcticAPI;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.pitdiscord.commands.PingCommand;
import dev.kyro.pitdiscord.controllers.DiscordManager;
import net.luckperms.api.LuckPerms;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscordPlugin extends JavaPlugin {
	public static DiscordPlugin INSTANCE;

	public static LuckPerms LUCKPERMS;

	@Override
	public void onEnable() {
		INSTANCE = this;
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);

		RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if (provider != null) {
			LUCKPERMS = provider.getProvider();
		}

		ArcticAPI.configInit(DiscordPlugin.INSTANCE, "prefix", "error-prefix");
		APlayerData.init();

		new DiscordManager();

		DiscordManager.registerCommand(new PingCommand());
//		DiscordManager.registerCommand(new VerifyCommand());
	}

	@Override
	public void onDisable() {

		DiscordManager.disable();
	}
}
