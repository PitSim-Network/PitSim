package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.ASubCommand;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class SetPrestigeCommand extends ASubCommand {
    public SetPrestigeCommand(String executor) {
        super(executor);
    }
    public boolean isOnlinePlayer = false;
    public boolean isOfflinePlayer = false;

    @Override
    public void execute(CommandSender sender, List<String> args) {
        isOnlinePlayer = false;
        isOfflinePlayer = false;

        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;

        PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

        if(args.size() != 2) {
            AOutput.error(player, "&cCorrect usage: /ps set prestige <player> <prestige>");
        }


        File directory = new File("plugins/PitRemake/playerdata");
        File[] files = directory.listFiles();
        assert files != null;

        String targetPlayerString = args.get(0);

        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(onlinePlayer.getName().equals(targetPlayerString)) {
                isOnlinePlayer = true;
                for(File file : files) {
                    if(file.getName().equals(onlinePlayer.getUniqueId() + ".yml")) {
                        onlinePlayer.kickPlayer(ChatColor.RED + "Your player data has changed. Please re-join.");
                        resetData(args.get(1), player, file.getName());
                        return;
                    }
                }

                return;
            }
        }

        if(!isOnlinePlayer) {
            for(File file : files) {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                if(!data.contains("name")) continue;
                if(data.getString("name").equalsIgnoreCase(targetPlayerString)) {
                    resetData(args.get(1), player, file.getName());
                    isOfflinePlayer = true;
                    return;
                }
            }

        }

        if(!isOfflinePlayer && !isOnlinePlayer) AOutput.error(player, "&cUnable to find player!");




    }

    public void resetData(String prestigeArg, Player player, String uuid) {



        new BukkitRunnable() {
            @Override
            public void run() {
                int prestige = 0;

                try {
                    prestige = Integer.parseInt(prestigeArg);
                } catch(Exception e) {
                    AOutput.error(player, "&cInvalid number!");
                    return;
                }

                if(prestige > 50 || prestige < 0) {
                    AOutput.error(player, "&cInvalid number!");
                    return;
                }

                UUID targetUUID = UUID.fromString(uuid.substring(0, uuid.length() - 4));
                FileConfiguration playerData = APlayerData.getPlayerData(targetUUID);

                PrestigeValues.PrestigeInfo prestigeInfo = PrestigeValues.getPrestigeInfo(prestige);

                int oldPrestige = 0;
                oldPrestige = playerData.getInt("prestige");

                playerData.set("prestige", prestige);
                playerData.set("moonbonus", 0);
                playerData.set("goldstack", 0);
                playerData.set("goldgrinded", 0);
                playerData.set("level", 1);
                playerData.set("playerkills", 0);
                playerData.set("xp", (int) (PrestigeValues.getXPForLevel(1) * prestigeInfo.xpMultiplier));
                playerData.set("goldstack", 0);
                playerData.set("megastreak", "Overdrive");
                playerData.set("killstreak-0", "NoKillstreak");
                playerData.set("killstreak-1", "NoKillstreak");
                playerData.set("killstreak-2", "NoKillstreak");

                int renown = 0;
                renown += playerData.getInt("renown");

                for(RenownUpgrade upgrade : UpgradeManager.upgrades) {
                    if(UpgradeManager.hasUpgrade(player, upgrade) && upgrade.prestigeReq < playerData.getInt("prestige")) {
                        playerData.set(upgrade.refName, null);
                        renown += upgrade.renownCost;
                    }
                }

                if(oldPrestige < prestige) {
                    for(int i = oldPrestige; i < prestige - 1; i++) {
                        PrestigeValues.PrestigeInfo info = PrestigeValues.getPrestigeInfo(i);
                        renown += info.renownReward;
                    }
                }
                if(oldPrestige > prestige) {
                    for(int i = oldPrestige - 1; i > prestige; i--) {
                        PrestigeValues.PrestigeInfo info = PrestigeValues.getPrestigeInfo(i);
                        renown -= info.renownReward;
                    }
                }

                playerData.set("renown", Math.max(renown, 0));




                APlayerData.savePlayerData(targetUUID);
                AOutput.send(player, "&aSuccess!");
            }
        }.runTaskLater(PitSim.INSTANCE, 10L);


    }
}
