//package dev.kyro.pitsim.controllers;
//
//import net.kyori.adventure.audience.Audience;
//import net.kyori.adventure.bossbar.BossBar;
//import net.kyori.adventure.text.Component;
//import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
//
//public class BossBarManager {
//
//    private @Nullable
//    BossBar activeBar;
//
//    final Component name = Component.text("Awesome BossBar");
//    public final BossBar defaultBar = BossBar.bossBar(name, 1, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_20);
//
//    public void showMyBossBar(final Audience target) {
//        // Creates a red boss bar which has no progress and no notches
//        final BossBar emptyBar = BossBar.bossBar(name, 0, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
//        // Creates a green boss bar which has 50% progress and 10 notches
//        final BossBar halfBar = BossBar.bossBar(name, 0.5f, BossBar.Color.RED, BossBar.Overlay.NOTCHED_10);
//
//        // etc..
//        final BossBar fullBar = BossBar.bossBar(name, 1, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_20);
//
//        // Send a bossbar to your audience
//        target.showBossBar(defaultBar);
//
//        // Store it locally to be able to hide it manually later
//        this.activeBar = defaultBar;
//    }
//
//    public void hideActiveBossBar(final Audience target) {
//        target.hideBossBar(this.activeBar);
//        this.activeBar = null;
//
//    }
//
//
//
//
//}
