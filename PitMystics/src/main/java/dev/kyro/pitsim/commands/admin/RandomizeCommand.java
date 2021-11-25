package dev.kyro.pitsim.commands.admin;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.commands.ASubCommand;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class RandomizeCommand extends ASubCommand {
    public OfflinePlayer offlinePlayer = null;
    public String uuidString = null;
    public RandomizeCommand(String executor) {
        super(executor);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;

        if(Misc.isAirOrNull(player.getItemInHand())) {
            AOutput.error(player, "&cInvalid item!");
            return;
        }

        NBTItem nbtItem = new NBTItem(player.getItemInHand());

        if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) {
            AOutput.error(player, "&cInvalid item!");
            return;
        }

        EnchantManager.setItemLore(nbtItem.getItem());

        nbtItem.setString(NBTTag.ITEM_UUID.getRef(), UUID.randomUUID().toString());
        player.getInventory().setItemInHand(nbtItem.getItem());

    }
}
