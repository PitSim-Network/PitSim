package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Arrays;
import java.util.List;

public class Highlander extends Megastreak {
    public static Highlander INSTANCE;
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
        return "&6&lHIGH";
    }

    @Override
    public List<String> getRefNames() {
        return Arrays.asList("highlander", "high");
    }

    @Override
    public int getRequiredKills() {
        return 50;
    }

        @EventHandler
        public static void onKill(KillEvent killEvent) {
            if(INSTANCE.playerIsOnMega(killEvent) && PitPlayer.getPitPlayer(killEvent.killer).megastreak == INSTANCE) {
                killEvent.goldMultipliers.add((100 / 100D) + 0.5);

            }
        }

    @EventHandler
    public void onAttack(AttackEvent.Apply attackEvent) {
        int killstreak  = pitPlayer.getKills();

        if(killstreak >= 50) {
            attackEvent.increasePercent += (killstreak / 100D);
        }
    }

    @Override
    public void proc() {

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

    }
}
