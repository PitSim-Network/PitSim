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

public class OldCrateGiveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(sender instanceof Player) return false;
        Player player = null;

        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(args[1].equalsIgnoreCase(onlinePlayer.getName())) player = onlinePlayer;
        }
        assert player != null;

        if(args[0].equals("HJP")) {
            for(int i = 0; i < 2; i++) {
                ItemStack jewel = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.JEWEL);
                jewel = ItemManager.enableDropConfirm(jewel);
                NBTItem nbtItem = new NBTItem(jewel);
                nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
                EnchantManager.setItemLore(nbtItem.getItem());
                AUtil.giveItemSafely(player, nbtItem.getItem());
            }

            broadcast("&32x Hidden Jewel Pants", player);
        }
        if(args[0].equals("HJS")) {
            for(int i = 0; i < 2; i++) {
                ItemStack jewelSword = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.JEWEL);
                jewelSword = ItemManager.enableDropConfirm(jewelSword);
                NBTItem nbtItemSword = new NBTItem(jewelSword);
                nbtItemSword.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
                EnchantManager.setItemLore(nbtItemSword.getItem());
                AUtil.giveItemSafely(player, nbtItemSword.getItem());
            }
            broadcast("&e2x Hidden Jewel Sword", player);
        }
        if(args[0].equals("HJB")) {
            for(int i = 0; i < 2; i++) {
                ItemStack jewelBow = FreshCommand.getFreshItem(MysticType.BOW, PantColor.JEWEL);
                jewelBow = ItemManager.enableDropConfirm(jewelBow);
                NBTItem nbtItemBow = new NBTItem(jewelBow);
                nbtItemBow.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
                EnchantManager.setItemLore(nbtItemBow.getItem());
                AUtil.giveItemSafely(player, nbtItemBow.getItem());
            }
            broadcast("&b2x Hidden Jewel Bow", player);
        }
        if(args[0].equals("P1")) {
            ProtArmor.getArmor(player, "helmet");
            ProtArmor.getArmor(player, "chestplate");
            ProtArmor.getArmor(player, "leggings");
            ProtArmor.getArmor(player, "boots");
            broadcast("&bProtection I Diamond Set", player);
        }
        if(args[0].equals("1F")) {
            FunkyFeather.giveFeather(player, 1);
            broadcast("&31x Funky Feather", player);
        }
        if(args[0].equals("2F")) {
            FunkyFeather.giveFeather(player, 2);
            broadcast("&32x Funky Feather", player);
        }
        if(args[0].equals("3F")) {
            FunkyFeather.giveFeather(player, 3);
            broadcast("&33x Funky Feather", player);
        }
        if(args[0].equals("5F")) {
            FunkyFeather.giveFeather(player, 5);
            broadcast("&35x Funky Feather", player);
        }
        if(args[0].equals("1V")) {
            ChunkOfVile.giveVile(player, 1);
            broadcast("&51x Chunk of Vile", player);
        }
        if(args[0].equals("2V")) {
            ChunkOfVile.giveVile(player, 2);
            broadcast("&52x Chunk of Vile", player);
        }
        if(args[0].equals("3V")) {
            ChunkOfVile.giveVile(player, 3);
            broadcast("&53x Chunk of Vile", player);
        }
        if(args[0].equals("6V")) {
            ChunkOfVile.giveVile(player, 6);
            broadcast("&56x Chunk of Vile", player);
        }
        if(args[0].equals("50K")) {
            PitSim.VAULT.depositPlayer(player, 150_000);
            broadcast("&6150,000 Gold", player);
        }
        if(args[0].equals("150K")) {
            PitSim.VAULT.depositPlayer(player, 300_000);
            broadcast("&6300,000 Gold", player);
        }
        if(args[0].equals("300K")) {
            PitSim.VAULT.depositPlayer(player, 500_000);
            broadcast("&6500,000 Gold", player);
        }
        if(args[0].equals("DC")) {
            double gold = PitSim.VAULT.getBalance(player);
            if(gold * 2 > 10_000_000) PitSim.VAULT.depositPlayer(player, 10_000_000);
            else PitSim.VAULT.depositPlayer(player, gold);
            broadcast("&6Double Current Gold", player);
        }
        if(args[0].equals("JB")) {
            for(int i = 0; i < 2; i++) {
                ItemStack jbsword = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.JEWEL);
                jbsword = ItemManager.enableDropConfirm(jbsword);
                NBTItem nbtjbsword = new NBTItem(jbsword);
                nbtjbsword.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
                EnchantManager.setItemLore(nbtjbsword.getItem());
                AUtil.giveItemSafely(player, nbtjbsword.getItem());
            }

            for(int i = 0; i < 2; i++) {
                ItemStack jbbow = FreshCommand.getFreshItem(MysticType.BOW, PantColor.JEWEL);
                jbbow = ItemManager.enableDropConfirm(jbbow);
                NBTItem nbtjbbow = new NBTItem(jbbow);
                nbtjbbow.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
                EnchantManager.setItemLore(nbtjbbow.getItem());
                AUtil.giveItemSafely(player, nbtjbbow.getItem());
            }

            for(int i = 0; i < 2; i++) {
                ItemStack jb = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.JEWEL);
                jb = ItemManager.enableDropConfirm(jb);
                NBTItem nbtjb = new NBTItem(jb);
                nbtjb.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
                EnchantManager.setItemLore(nbtjb.getItem());
                AUtil.giveItemSafely(player, nbtjb.getItem());
            }
            broadcast("&bHidden Jewel Bundle", player);
        }
        if(args[0].equals("P1H")) {
            ProtArmor.getArmor(player, "Helmet");
            broadcast("&bProtection I Diamond Helmet", player);
        }
        if(args[0].equals("P1C")) {
            ProtArmor.getArmor(player, "Chestplate");
            broadcast("&bProtection I Diamond Chestplate", player);
        }
        if(args[0].equals("P1L")) {
            ProtArmor.getArmor(player, "Leggings");
            broadcast("&bProtection I Diamond Leggings", player);
        }
        if(args[0].equals("P1B")) {
            ProtArmor.getArmor(player, "Boots");
            broadcast("&bProtection I Diamond Boots", player);
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
