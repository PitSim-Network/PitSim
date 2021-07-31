package dev.kyro.pitsim.controllers;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BossBarManager {

    private @Nullable
    BossBar activeBar;

    public void showMyBossBar(final @NonNull Audience target) {
        final Component name = Component.text("Awesome BossBar");
        // Creates a red boss bar which has no progress and no notches
        final BossBar emptyBar = BossBar.bossBar(name, 0, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        // Creates a green boss bar which has 50% progress and 10 notches
        final BossBar halfBar = BossBar.bossBar(name, 0.5f, BossBar.Color.RED, BossBar.Overlay.NOTCHED_10);

        // etc..
        final BossBar fullBar = BossBar.bossBar(name, 1, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_20);

        // Send a bossbar to your audience
        target.showBossBar(fullBar);

        // Store it locally to be able to hide it manually later
        this.activeBar = fullBar;
    }

    public void hideActiveBossBar(final @NonNull Audience target) {
        target.hideBossBar(this.activeBar);
        this.activeBar = null;

    }



}
