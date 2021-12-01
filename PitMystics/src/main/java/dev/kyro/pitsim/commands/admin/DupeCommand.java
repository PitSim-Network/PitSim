package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.commands.ASubCommand;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.DupeManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DupeCommand extends ASubCommand {
    public DupeCommand(String executor) {
        super(executor);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;

        if(args.size() < 1) {
            AOutput.error(player, "Usage: /dupe <player>");
            return;
        }

        UUID targetUUID = null;
        for(Map.Entry<UUID, FileConfiguration> entry : APlayerData.getAllData().entrySet()) {
            String testName = entry.getValue().getString("name");
            if(testName == null || !testName.equalsIgnoreCase(args.get(0))) continue;
            targetUUID = entry.getKey();
            break;
        }
        if(targetUUID == null) {
            AOutput.error(player, "That player could not be found");
            return;
        }
        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetUUID);

        List<UUID> sharedWith = new ArrayList<>();
        int timesDuped = 0;
        for(DupeManager.TrackedItem trackedItem : DupeManager.dupedItems) {
            if(!trackedItem.playerUUID.equals(targetUUID)) continue;
            timesDuped++;

            AItemStackBuilder itemStackBuilder = new AItemStackBuilder(trackedItem.nbtItem.getItem().clone());
            ALoreBuilder loreBuilder = new ALoreBuilder(itemStackBuilder.getItemStack().getItemMeta().getLore());
            if(!trackedItem.sharedWith.isEmpty()) {
                loreBuilder.addLore("", "&7Shared Players:");
                List<UUID> trackedItemSharedWith = new ArrayList<>(trackedItem.sharedWith);
                if(trackedItemSharedWith.contains(targetUUID)) {
                    loreBuilder.addLore("&7 * &4Shared Internally");
                    trackedItemSharedWith.remove(targetUUID);
                }
                for(UUID offlineUUID : trackedItemSharedWith) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(offlineUUID);
                    String color = offlinePlayer.isOp() ? "&c" : "&7";
                    loreBuilder.addLore("&7 * " + color + offlinePlayer.getName());
                }
                for(UUID uuid : trackedItem.sharedWith) if(!sharedWith.contains(uuid)) sharedWith.add(uuid);
            }
            itemStackBuilder.setLore(loreBuilder);
            AUtil.giveItemSafely(player, itemStackBuilder.getItemStack());
        }

        AOutput.send(player, "");
        AOutput.send(player, "&6" + offlineTarget.getName() + " &7Shared Players (" + sharedWith.size() + "):");
        if(sharedWith.contains(targetUUID)) {
            AOutput.send(player, "&4Shared Internally");
            sharedWith.remove(targetUUID);
        }
        for(UUID offlineUUID : sharedWith) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(offlineUUID);
            String color = offlinePlayer.isOp() ? "&c" : "&7";
            AOutput.send(player, color + offlinePlayer.getName());
        }
        AOutput.send(player, "&4&lDUPEDUPEDUPEDUPE! &c" + timesDuped + " duped item" + (timesDuped == 1 ? "" : "s"));
        AOutput.send(player, "");
    }
}
