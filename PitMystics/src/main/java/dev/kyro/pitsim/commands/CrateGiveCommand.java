package dev.kyro.pitsim.commands;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.ItemManager;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.ChunkOfVile;
import dev.kyro.pitsim.misc.FunkyFeather;
import dev.kyro.pitsim.misc.ProtArmor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CrateGiveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(sender instanceof Player) return false;
        Player player = null;

        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(args[1].equalsIgnoreCase(onlinePlayer.getName())) player  = onlinePlayer;
        }
        assert player != null;

        if(args[0].equals("HJP")) {
            ItemStack jewel = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.JEWEL);
            jewel = ItemManager.enableDropConfirm(jewel);
            assert jewel != null;
            NBTItem nbtItem = new NBTItem(jewel);
            nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);

            EnchantManager.setItemLore(nbtItem.getItem());

            AUtil.giveItemSafely(player, nbtItem.getItem());
            broadcast("&3Hidden Jewel Pants", player);
        }
        if(args[0].equals("HJS")) {
            ItemStack jewel = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.JEWEL);
            jewel = ItemManager.enableDropConfirm(jewel);
            assert jewel != null;
            NBTItem nbtItem = new NBTItem(jewel);
            nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);

            EnchantManager.setItemLore(nbtItem.getItem());

            AUtil.giveItemSafely(player, nbtItem.getItem());
            broadcast("&eHidden Jewel Sword", player);
        }
        if(args[0].equals("HJB")) {
            ItemStack jewel = FreshCommand.getFreshItem(MysticType.BOW, PantColor.JEWEL);
            jewel = ItemManager.enableDropConfirm(jewel);
            assert jewel != null;
            NBTItem nbtItem = new NBTItem(jewel);
            nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);

            EnchantManager.setItemLore(nbtItem.getItem());

            AUtil.giveItemSafely(player, nbtItem.getItem());
            broadcast("&bHidden Jewel Bow", player);
        }
        if(args[0].equals("P1")) {
            ProtArmor.getArmor(player, "helmet");
            ProtArmor.getArmor(player, "chestplate");
            ProtArmor.getArmor(player, "leggings");
            ProtArmor.getArmor(player, "boots");
            broadcast("&bProtection I Diamond Set", player);
        }
        if(args[0].equals("3F")) {
            FunkyFeather.giveFeather(player, 3);
            broadcast("&33x Funky Feather", player);
        }
        if(args[0].equals("5F")) {
            FunkyFeather.giveFeather(player, 5);
            broadcast("&35x Funky Feather", player);
        }
        if(args[0].equals("6V")) {
            ChunkOfVile.giveVile(player, 6);
            broadcast("&56x Chunk of Vile", player);
        }
        if(args[0].equals("3V")) {
            ChunkOfVile.giveVile(player, 3);
            broadcast("&53x Chunk of Vile", player);
        }
        if(args[0].equals("10K")) {
            PitSim.VAULT.depositPlayer(player, 10000);
            broadcast("&610,000 Gold", player);
        }
        if(args[0].equals("25K")) {
            PitSim.VAULT.depositPlayer(player, 25000);
            broadcast("&625,000 Gold", player);
        }
        if(args[0].equals("100K")) {
            PitSim.VAULT.depositPlayer(player, 100000);
            broadcast("&6100,000 Gold", player);
        }
        if(args[0].equals("500K")) {
            PitSim.VAULT.depositPlayer(player, 500000);
            broadcast("&6500,000 Gold", player);
        }

        return false;
    }

    public static void broadcast(String prize, Player player) {
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String message = ChatColor.translateAlternateColorCodes('&', "&e&lGG! %luckperms_prefix%"
                    + player.getDisplayName() + " &7has won " + prize + " &7from the &6&lPit&e&lSim &7Crate!");
            onlinePlayer.sendMessage(PlaceholderAPI.setPlaceholders(player, message));
        }
    }
}
