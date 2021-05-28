package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import me.clip.placeholderapi.PlaceholderAPI;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


public class DamageIndicator implements Listener {

//    @EventHandler(priority = EventPriority.MONITOR)
    public static void onAttack(AttackEvent.Apply attackEvent) {

        if(attackEvent.fakeHit) return;

        Player attacker = attackEvent.attacker;
        Player defender = attackEvent.defender;

//        double maxHealth = defender.getMaxHealth() / 2;
//        double currentHealth = defender.getHealth() / 2;
//        double damageTaken = attackEvent.event.getFinalDamage() / 2;
//
//
//        Bukkit.broadcastMessage(String.valueOf("Max Health: " + maxHealth));
//        Bukkit.broadcastMessage(String.valueOf("Current Health: " + currentHealth));
//        Bukkit.broadcastMessage(String.valueOf("Damage Taken: " + damageTaken));
//
//        StringBuilder output = new StringBuilder();
//
//
//
//        for (int i = 0; i < Math.floor(currentHealth - damageTaken); i++) {
//            output.append(ChatColor.DARK_RED).append("\u2764");
//        }
//
//        for (int i = 0; i < Math.ceil(damageTaken); i++) {
//            output.append(ChatColor.RED).append("\u2764");
//        }
//
//        for (int i = 0; i < maxHealth - (Math.floor(currentHealth - damageTaken) + Math.ceil(damageTaken)); i++) {
//            output.append(ChatColor.BLACK).append("\u2764");
//        }
//
//        Misc.sendActionBar(attacker, output.toString());

        EntityPlayer player = ((CraftPlayer) defender).getHandle();

        int roundedDamageTaken = ((int) attackEvent.event.getFinalDamage()) / 2;

        int originalHealth = ((int) defender.getHealth()) / 2;
        int maxHealth = ((int) defender.getMaxHealth()) / 2;

        int result = Math.max(originalHealth - roundedDamageTaken, 0);

        if((defender.getHealth() - attackEvent.event.getFinalDamage()) % 2 < 1 && attackEvent.event.getFinalDamage() > 1) roundedDamageTaken++;

        if (result == 0) {
            roundedDamageTaken = 0;

            for (int i = 0; i < originalHealth; i++) {
                roundedDamageTaken++;
            }
        }

        Non defendingNon = NonManager.getNon(defender);
        StringBuilder output = new StringBuilder();

        String playername = "&7%luckperms_prefix%" + (defendingNon == null ? "%player_name%" : defendingNon.displayName) + " ";
        output.append(PlaceholderAPI.setPlaceholders(attackEvent.defender, playername));

        for (int i = 0; i < Math.max(originalHealth - roundedDamageTaken, 0); i++) {
            output.append(ChatColor.DARK_RED).append("\u2764");
        }

        for (int i = 0; i < roundedDamageTaken - (int) player.getAbsorptionHearts() / 2; i++) {
            output.append(ChatColor.RED).append("\u2764");
        }

        for (int i = originalHealth; i < maxHealth; i++) {
            output.append(ChatColor.BLACK).append("\u2764");
        }

        for (int i = 0; i < (int) player.getAbsorptionHearts() / 2; i++) {
            output.append(ChatColor.YELLOW).append("\u2764");
        }

        Misc.sendActionBar(attacker, output.toString());
    }


}
