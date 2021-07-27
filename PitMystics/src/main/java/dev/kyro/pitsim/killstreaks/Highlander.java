package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Arrays;
import java.util.List;

public class Highlander extends Megastreak {
    public Highlander(PitPlayer pitPlayer) {
        super(pitPlayer);
        INSTANCE = this;
    }

    @Override
    public String getName() {
        return "&6&lHIGH";
    }

    @Override
    public String getPrefix() {
        return "&6Highlander";
    }

    @Override
    public List<String> getRefNames() {
        return Arrays.asList("highlander", "high");
    }

    @Override
    public int getRequiredKills() {
        return 1;
    }

    @EventHandler
    public void onKill(KillEvent killEvent) {
        PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.killer);
        if(pitPlayer != this.pitPlayer) return;
        if(pitPlayer.megastreak.playerIsOnMega(killEvent) && pitPlayer.megastreak.getClass() == Highlander.class) {
            killEvent.goldMultipliers.add(1.5);
            Bukkit.broadcastMessage(killEvent.goldMultipliers.toString());
            Bukkit.broadcastMessage(String.valueOf(killEvent.goldReward));
            Bukkit.broadcastMessage(killEvent.getFinalGold() + "");
        }
    }

//    @EventHandler
//    public void onAttack(AttackEvent.Apply attackEvent) {
//        int killstreak  = pitPlayer.getKills();
//
//        if(killstreak >= 50 && pitPlayer.megastreak.getClass() == Highlander.class) {
//            attackEvent.increasePercent += (killstreak / 100D);
//        }
//    }

    @Override
    public void proc() {
        String message = "%luckperms_prefix%";
        if(pitPlayer.megastreak.isOnMega()) {
            pitPlayer.prefix = pitPlayer.megastreak.getName() + " " + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
        } else {
            pitPlayer.prefix = "&7[&e" + pitPlayer.playerLevel + "&7] &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
        }

        ASound.play(pitPlayer.player, Sound.WITHER_SPAWN, 2, 0.5f);
        pitPlayer.megastreak = this;
        for(Player player : Bukkit.getOnlinePlayers()) {
            PitPlayer pitPlayer2 = PitPlayer.getPitPlayer(player);
            if(pitPlayer2.disabledStreaks) continue;
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c&lMEGASTREAK!&7 " + pitPlayer.player.getDisplayName() + "&7 activated &6&lHIGHLANDER&7!"));
        }

    }

    @Override
    public void reset() {

        String message = "%luckperms_prefix%";
        if(pitPlayer.megastreak.isOnMega()) {
            pitPlayer.prefix = pitPlayer.megastreak.getName() + " " + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
        } else {
            pitPlayer.prefix = "&7[&e" + pitPlayer.playerLevel + "&7] &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
        }
    }

}
