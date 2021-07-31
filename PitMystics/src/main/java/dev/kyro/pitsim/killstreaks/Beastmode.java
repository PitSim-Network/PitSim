package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Beastmode extends Megastreak {
    public Beastmode(PitPlayer pitPlayer) {
        super(pitPlayer);
    }

    public BukkitTask runnable;

    @Override
    public String getName() {
        return "&a&lBEAST";
    }

    @Override
    public String getRawName() {
        return "Beastmode";
    }

    @Override
    public String getPrefix() {
        return "&aBeastmode";
    }

    @Override
    public List<String> getRefNames() {
        return Arrays.asList("beastmode", "beast");
    }

    @Override
    public int getRequiredKills() {
        return 50;
    }

    @Override
    public int guiSlot() {
        return 13;
    }

    @Override
    public int levelReq() {
        return 20;
    }

    @Override
    public ItemStack guiItem() {


        ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Triggers on: &c50 kills"));
        lore.add("");
        lore.add(ChatColor.GRAY + "On trigger:");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Deal &c+25% &7damage to bots."));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Earn &b+100% XP &7from kills."));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Gain &b+50 max XP&7."));
        lore.add("");
        lore.add(ChatColor.GRAY + "BUT:");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &7Receive &c+1% &7damage per kill over 50."));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7(Damage tripled for bots)"));
        lore.add("");
        lore.add(ChatColor.GRAY + "On death:");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&e\u25a0 &7Earn between &b1000 &7and &b5000 XP&7."));

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onKill(KillEvent killEvent) {
        PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.killer);
        if(pitPlayer != this.pitPlayer) return;
        if(pitPlayer.megastreak.playerIsOnMega(killEvent) && pitPlayer.megastreak.getClass() == Beastmode.class) {
            killEvent.xpMultipliers.add(2.0);
        }
    }

    @EventHandler
    public void onHit(AttackEvent.Apply attackEvent) {
        PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.defender);
        if(pitPlayer != this.pitPlayer) return;
        if(pitPlayer.megastreak.isOnMega() && pitPlayer.megastreak.getClass() == Beastmode.class) {
            int ks = (int) Math.floor(pitPlayer.getKills());
//            attackEvent.increasePercent += ((ks / 5)  / 100D) * 8;
            if(NonManager.getNon(attackEvent.attacker) == null) {
                attackEvent.increasePercent += (ks - 50) / 100D;
            } else {
                attackEvent.increasePercent += ((ks - 50) * 3) / 100D;
            }
//            Bukkit.broadcastMessage(attackEvent.getFinalDamage() + "");
        }
    }

    @EventHandler
    public void kill(KillEvent killEvent) {
        PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.killer);
        if(pitPlayer != this.pitPlayer) return;
        if(pitPlayer.megastreak.isOnMega() && pitPlayer.megastreak.getClass() == Beastmode.class) {
            killEvent.xpCap += 50;
        }
    }

    @EventHandler
    public void onAttack(AttackEvent.Apply attackEvent) {
        PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.attacker);
        PitPlayer pitDefender = PitPlayer.getPitPlayer(attackEvent.defender);
        if(pitPlayer != this.pitPlayer) return;
        if(pitPlayer.megastreak.isOnMega() && pitPlayer.megastreak.getClass() == Beastmode.class) {
            if(NonManager.getNon(attackEvent.defender) != null) {
                attackEvent.increasePercent += 25 / 100D;
            }
        }
    }

    @Override
    public void proc() {

        pitPlayer.player.getWorld().playSound(pitPlayer.player.getLocation(), Sound.WITHER_SPAWN, 1000, 1);
        String message = "%luckperms_prefix%";
        if(pitPlayer.megastreak.isOnMega()) {
            pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
        } else {
            pitPlayer.prefix = "&7[&e" + pitPlayer.playerLevel + "&7] &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
        }

        pitPlayer.megastreak = this;
        for(Player player : Bukkit.getOnlinePlayers()) {
            PitPlayer pitPlayer2 = PitPlayer.getPitPlayer(player);
            if(pitPlayer2.disabledStreaks) continue;
           String streakMessage = ChatColor.translateAlternateColorCodes('&',
                    "&c&lMEGASTREAK! %luckperms_prefix%" + pitPlayer.player.getDisplayName() + "&7 activated &a&lBEASTMODE&7!");
           AOutput.send(player, PlaceholderAPI.setPlaceholders(pitPlayer.player, streakMessage));

        }

    }

    @Override
    public void reset() {

        String message = "%luckperms_prefix%";
        if(pitPlayer.megastreak.isOnMega()) {
            pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
        } else {
            pitPlayer.prefix = "&7[&e" + pitPlayer.playerLevel + "&7] &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
        }

        int randomNum = ThreadLocalRandom.current().nextInt(1000, 5000 + 1);
        if(pitPlayer.megastreak.isOnMega())  {
            AOutput.send(pitPlayer.player, "&c&lBEASTMODE! &7Earned &b" + randomNum + "&b XP &7from megastreak!");
            pitPlayer.remainingXP = Math.max(pitPlayer.remainingXP - randomNum, 0);
        }

    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }
}
