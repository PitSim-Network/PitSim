package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Regenerative extends PitPerk {

    public static Regenerative INSTANCE;

    public Regenerative() {
        super("Regenerative", "regen", new ItemStack(Material.RED_ROSE), 21, false, "", INSTANCE, true);
        INSTANCE = this;
    }

    @EventHandler
    public void onKill(KillEvent event) {
        if(!playerHasUpgrade(event.killerPlayer)) return;

        double maxHealth = event.killerPlayer.getMaxHealth();
        double extraHealth = maxHealth - 24;
        if(maxHealth <= 0) return;

        double regenLevel = Math.floor(extraHealth / 2);
        Misc.applyPotionEffect(event.killerPlayer, PotionEffectType.REGENERATION, 100, (int) regenLevel, true, false);

    }

    @Override
    public List<String> getDescription() {
        return new ALoreBuilder("&7On kill, gain &f1 &7level of &cRegeneration", "&7per &c\u2764 &7over &f12 &7(5s)").getLore();
    }
}
