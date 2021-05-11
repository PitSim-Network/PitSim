package dev.kyro.pitdiscord;

import org.bukkit.plugin.java.JavaPlugin;

public class DiscordPlugin extends JavaPlugin {

	@Override
	public void onEnable() {

		new PitDiscord();
	}
}
