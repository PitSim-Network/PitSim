package dev.kyro.pitsim.commands.admin;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.commands.ASubCommand;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class UUIDCommand extends ASubCommand {
    public UUIDCommand(String executor) {
        super(executor);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;

        ItemStack itemStack = player.getItemInHand();
        if(Misc.isAirOrNull(itemStack)) return;

        NBTItem nbtItem = new NBTItem(itemStack);
        if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) return;
        AOutput.broadcast(nbtItem.getString(NBTTag.ITEM_UUID.getRef()) + "");
    }
}
