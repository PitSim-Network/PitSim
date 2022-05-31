package dev.kyro.pitsim.slayers.tainted;

import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.PantColor;
import org.bukkit.inventory.ItemStack;

public class GearConstructor {
    public static ItemStack damageLeggings(){
        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.HOT_PINK);
        try{
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("rgm"), 2, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("mirror"), 1, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("reg"), 3, false);
        }catch (Exception ignored){}

        return itemStack;
    }

    public static ItemStack damageSword(){

        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.GREEN);
        try{
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("bill"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("cd"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("kb"), 3, false);
        } catch (Exception ignored) {}

        return itemStack;
    }

    public static ItemStack glassLeggings(){
        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.BLUEBERRY_BLUES);
        try{
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("rgm"), 2, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("toxic"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("reg"), 3, false);
        }catch (Exception ignored){}

        return itemStack;
    }

    public static ItemStack glassSword(){

        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.GREEN);
        try{
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("bill"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("perun"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("kb"), 3, false);
        } catch (Exception ignored) {}

        return itemStack;
    }

    public static ItemStack tankLeggings(){
        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.BUSINESS_GRAY);
        try{
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("rgm"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("crit"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("prot"), 3,false);
        }catch (Exception ignored){}

        return itemStack;
    }

    public static ItemStack tankSword(){

        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.GREEN);
        try{
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("bill"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("pf"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ls"), 3, false);
        } catch (Exception ignored) {}

        return itemStack;
    }

    public static ItemStack mediumLeggings(){
        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.GREEN);
        try{
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("rgm"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("crit"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("prot"), 2,false);
        }catch (Exception ignored){}

        return itemStack;
    }

    public static ItemStack mediumSword(){

        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.GREEN);
        try{
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("bill"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("cd"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("kb"), 2, false);
        } catch (Exception ignored) {}

        return itemStack;
    }

    public static ItemStack shredderLeggings(){
        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.BLOOD_RED);
        try{
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("rgm"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("toxic"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("crit"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("reg"), 3,false);
        }catch (Exception ignored){}

        return itemStack;
    }

    public static ItemStack shredderSword(){

        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.GREEN);
        try{
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("bill"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("exe"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("perun"), 3, false);
            itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ds"), 3, false);
        } catch (Exception ignored) {}

        return itemStack;
    }

}
