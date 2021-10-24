package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.CombatManager;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.SpawnManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OofCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if(SpawnManager.isInSpawn(player.getLocation())) {

            AOutput.send(player, "&c&lNOPE! &7Cant /oof in spawn!");
            Sounds.ERROR.play(player);
        } else {

            if(!CombatManager.taggedPlayers.containsKey(player.getUniqueId())) {
                DamageManager.death(player);
                OofEvent oofEvent = new OofEvent(player);
                Bukkit.getPluginManager().callEvent(oofEvent);
                return false;
            }

            PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
            UUID attackerUUID = pitPlayer.lastHitUUID;
            for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if(onlinePlayer.getUniqueId().equals(attackerUUID)) {

                    Map<PitEnchant, Integer> attackerEnchant = new HashMap<>();
                    Map<PitEnchant, Integer> defenderEnchant = new HashMap<>();
                    EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(onlinePlayer, player, EntityDamageEvent.DamageCause.CUSTOM, 0);
                    AttackEvent attackEvent = new AttackEvent(ev, attackerEnchant, defenderEnchant, false);


                    DamageManager.kill(attackEvent, onlinePlayer, player, false);
                    return false;
                }
            }
            DamageManager.death(player);
            OofEvent oofEvent = new OofEvent(player);
            Bukkit.getPluginManager().callEvent(oofEvent);
        }
        return false;
    }
}
