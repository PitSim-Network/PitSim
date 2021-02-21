package dev.arcticdevelopment.arcticfishing;


import dev.kyro.arcticapi.commands.ABaseCommand;
import dev.kyro.arcticapi.ArcticAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class ArcticFishing extends JavaPlugin {

    public static ArcticFishing INSTANCE;

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
        ABaseCommand apiCommand = new BaseCommand("darkzone");

//        getCommand("printer").setExecutor(new PrinterCommand());
    }

    private void registerListeners() {

//        getServer().getPluginManager().registerEvents(new PrinterEvents(), this);
    }
}
