package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class SavingGraceSpell extends PitEnchant {
    public SavingGraceSpell() {
        super("Saving Grace", true, ApplyType.SCYTHES, "savinggrace", "save", "saving", "grace");
        tainted = true;
    }

    @EventHandler
    public void onUse(PitPlayerAttemptAbilityEvent event) {
        int enchantLvl = event.getEnchantLevel(this);
        if(enchantLvl == 0) return;
        if(!event.getPlayer().isSneaking()) return;

        Cooldown cooldown = getCooldown(event.getPlayer(), 10);
        if(cooldown.isOnCooldown()) return;

        PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
        if(!pitPlayer.useMana(getManaCost(enchantLvl))) {
            Sounds.NO.play(event.getPlayer());
            return;
        }

        pitPlayer.updateMaxHealth();
        if(event.getPlayer().getMaxHealth() <= 8) {
            AOutput.send(event.getPlayer(), "&c&lNOPE! &7Not enough health!");
            Sounds.NO.play(event.getPlayer());
            return;
        }

        cooldown.restart();

        Player player = event.getPlayer();

        pitPlayer.heal(player.getMaxHealth(), HealEvent.HealType.ABSORPTION, (int) (player.getMaxHealth()));
        pitPlayer.graceTiers += 1;
        pitPlayer.updateMaxHealth();
        player.getWorld().spigot().playEffect(player.getLocation().add(0, 1, 0),
                Effect.HAPPY_VILLAGER, 0, 0, (float) 1, (float) 1, (float) 1, (float) 0.5, 25, 50);

        Sounds.SoundMoment soundMoment = new Sounds.SoundMoment(1);
        soundMoment.add(Sound.ORB_PICKUP, 2, 2);
        Sounds.SoundMoment soundMoment2 = new Sounds.SoundMoment(5);
        soundMoment2.add(Sound.ORB_PICKUP, 2, 1.9);
        Sounds.SoundMoment soundMoment3 = new Sounds.SoundMoment(7);
        soundMoment3.add(Sound.ORB_PICKUP, 2, 1.85);
        Sounds.SoundMoment soundMoment4 = new Sounds.SoundMoment(10);
        soundMoment4.add(Sound.ORB_PICKUP, 2, 1.9);
        Sounds.SoundMoment soundMoment5 = new Sounds.SoundMoment(13);
        soundMoment5.add(Sound.ORB_PICKUP, 2, 2);

        Sounds.SURVIVOR_HEAL.play(player);

        soundMoment.play(player);
        soundMoment2.play(player);
        soundMoment3.play(player);
        soundMoment4.play(player);
        soundMoment5.play(player);
    }

    @EventHandler
    public void onKill(KillEvent killEvent) {
        if(!killEvent.isDeadPlayer()) return;
        killEvent.getDeadPitPlayer().graceTiers = 0;

        new BukkitRunnable() {
            @Override
            public void run() {
                killEvent.getDeadPitPlayer().heal(killEvent.getDeadPlayer().getMaxHealth());
            }
        }.runTaskLater(PitSim.INSTANCE, 10);
    }

    @EventHandler
    public void onOof(OofEvent event) {
        PitPlayer.getPitPlayer(event.getPlayer()).graceTiers = 0;
        PitPlayer.getPitPlayer(event.getPlayer()).heal(event.getPlayer().getMaxHealth());

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!event.getPlayer().isOnline()) return;
                PitPlayer.getPitPlayer(event.getPlayer()).heal(event.getPlayer().getMaxHealth());
            }
        }.runTaskLater(PitSim.INSTANCE, 2);
    }

    @Override
    public List<String> getDescription(int enchantLvl) {
        return new ALoreBuilder("&7Heal your max health in &6\u2764", "&7but lose &c2\u2764 &7until you die", "&7(Shift Right-Click)", "&d&o-" + getManaCost(enchantLvl) + " Mana").getLore();
    }

    public static int getManaCost(int enchantLvl) {
        return 30 * (4 - enchantLvl);
    }
}
