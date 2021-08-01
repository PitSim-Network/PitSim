package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BossBarManager {

    private @Nullable
    BossBar activeBar;

    final Component name = Component.text("Awesome BossBar");
    public final BossBar defaultBar = BossBar.bossBar(name, 1, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_20);

    public void showMyBossBar(final @NonNull Audience target) {
        // Creates a red boss bar which has no progress and no notches
        final BossBar emptyBar = BossBar.bossBar(name, 0, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        // Creates a green boss bar which has 50% progress and 10 notches
        final BossBar halfBar = BossBar.bossBar(name, 0.5f, BossBar.Color.RED, BossBar.Overlay.NOTCHED_10);

        // etc..
        final BossBar fullBar = BossBar.bossBar(name, 1, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_20);

        // Send a bossbar to your audience
        target.showBossBar(defaultBar);

        // Store it locally to be able to hide it manually later
        this.activeBar = fullBar;
    }

    public void hideActiveBossBar(final @NonNull Audience target) {
        target.hideBossBar(this.activeBar);
        this.activeBar = null;

    }
        public void timerBar(Audience targetPlayer, String message, int startminutes, int startseconds, ChatColor numcolor) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            BossBar bossBar = defaultBar;

            new BukkitRunnable() {
                int minutes = startminutes;
                int seconds = startseconds;
                @Override
                public void run() {
                    if(seconds > 0) {
                        seconds = seconds - 1;
                    } else {
                        if(minutes > 0) {
                            minutes = minutes - 1;
                            seconds = 59;
                        } else {
                            hideActiveBossBar(targetPlayer);
                            this.cancel();
                        }

                    }

                    float first = (float) (minutes * 60) + (float) seconds;
                    float second = (float) (startminutes * 60) + (float) startseconds;
                    float decimal = first / second;
                    String finalseconds = (seconds < 10 ? "0" : "") + seconds;
                    String finalminutes = (minutes < 10 ? "0" : "") + minutes;
                    Component newComponent = Component.text(message + " " + numcolor + finalminutes + ":" + finalseconds);
                    bossBar.name(newComponent);
                    bossBar.progress(decimal);
//
//                    Bukkit.broadcastMessage(String.valueOf((minutes * 60) + seconds));
                }
            }.runTaskTimer(PitSim.INSTANCE, 0L, 20L);

        }
    }



}
