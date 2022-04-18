package dev.kyro.pitsim.controllers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.KillEffect;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.slayers.ZombieBoss;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BossManager implements Listener {

    public static List<Player> activePlayers = new ArrayList<>();
    public static Map<NPC, PitBoss> bosses = new HashMap<>();
    public static List<Hologram> holograms = new ArrayList<>();
    public static Map<SubLevel, Villager> clickables = new HashMap<>();
    public static Map<SubLevel, Map<Player, Integer>> bossItems = new HashMap<>();
    public static void onStart() {
        for (Hologram hologram : HologramsAPI.getHolograms(PitSim.INSTANCE)) {
            hologram.delete();
        }

        for (SubLevel level : SubLevel.values()) {
            for (Entity nearbyEntity : level.middle.getWorld().getNearbyEntities(level.middle, 5, 5, 5)) {
                if(nearbyEntity instanceof Villager) nearbyEntity.remove();
            }

            bossItems.put(level, new HashMap<>());

            Hologram holo = HologramsAPI.createHologram(PitSim.INSTANCE, level.middle.add(0, 2, 0));
            holo.setAllowPlaceholders(true);
            holo.appendTextLine("&cPlace " + level.itemName);
            holo.appendTextLine("{fast}" + level.placeholder + " ");
            holograms.add(holo);

            Villager villager = Bukkit.getWorld("darkzone").spawn(level.middle.subtract(0, 1, 0), Villager.class);
            System.out.println("Stand!");
            noAI(villager);
            villager.setAdult();
            villager.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            clickables.put(level, villager);
            System.out.println(villager.getLocation());
        }
    }

    @EventHandler
    public void onTrade(PlayerInteractEntityEvent event) {
        if(event.getRightClicked() instanceof Villager) {
            for (Villager value : clickables.values()) {
                if(!value.getUniqueId().equals(event.getRightClicked().getUniqueId())) return;
            }
            event.setCancelled(true);

            SubLevel level = null;
            for (Map.Entry<SubLevel, Villager> entry : clickables.entrySet()) {
                if(entry.getValue().getUniqueId().equals(event.getRightClicked().getUniqueId())) level = entry.getKey();
            }
            if(activePlayers.contains(event.getPlayer())) return;

            assert level != null;
            if(useItem(event.getPlayer(), level.bossItem)) {
                Map<Player, Integer> players = bossItems.get(level);
                if(players.containsKey(event.getPlayer())) players.put(event.getPlayer(), players.get(event.getPlayer()) + 1);
                else players.put(event.getPlayer(), 1);

                if(players.get(event.getPlayer()) == 10) {
                    players.remove(event.getPlayer());
                    level.middle.getWorld().playEffect(level.middle, Effect.EXPLOSION_HUGE, 100);
                    Sounds.PRESTIGE.play(level.middle);
                    try {
                        new ZombieBoss(event.getPlayer());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEquip(PlayerArmorStandManipulateEvent event) {
//        Bukkit.broadcastMessage("1");
//        if(event.getRightClicked() == null) return;
//        Bukkit.broadcastMessage(event.getRightClicked() + "");
//        Bukkit.broadcastMessage(clickables + "");
//        if(!clickables.containsValue(event.getRightClicked())) return;
//        Bukkit.broadcastMessage("3");
//
//        SubLevel level = null;
//        for (Map.Entry<SubLevel, ArmorStand> entry : clickables.entrySet()) {
//            if(entry.getValue() == event.getRightClicked()) level = entry.getKey();
//        }
//        Bukkit.broadcastMessage("4");
//        Bukkit.broadcastMessage(level + "");
//
//        assert level != null;
//        if(useItem(event.getPlayer(), level.bossItem)) {
//            Map<Player, Integer> players = bossItems.get(level);
//            if(players.containsKey(event.getPlayer())) players.put(event.getPlayer(), players.get(event.getPlayer()) + 1);
//            else players.put(event.getPlayer(), 1);
//
//            if(players.get(event.getPlayer()) == 10) {
//                players.remove(event.getPlayer());
//                try {
//                    new ZombieBoss((Player) event.getPlayer());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }


    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<NPC, PitBoss> entry : bosses.entrySet()) {
                   if(entry.getValue().target.getWorld() == entry.getValue().getEntity().getWorld() && entry.getValue().subLevel.middle.distance(entry.getValue().target.getLocation()) < 40) continue;
                    entry.getValue().onDeath();
                    NPC npc;
                    if(entry.getKey() == null) npc = CitizensAPI.getNPCRegistry().getNPC(entry.getValue().getEntity());
                    else npc = entry.getKey();
                    npc.destroy();
                    AOutput.send(entry.getValue().target, "&c&lDESPAWN! &7Your boss has despawned because you went to far away.");
                    bosses.remove(npc);
                    activePlayers.remove(entry.getValue().target);
                }
            }
        }.runTaskTimer(PitSim.INSTANCE, 200, 200);
    }

    @EventHandler
    public void onAttack(EntityTargetLivingEntityEvent event) {
        if(event.getTarget() instanceof Villager) event.setCancelled(true);
        if(bosses.containsKey(CitizensAPI.getNPCRegistry().getNPC(event.getTarget()))) event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION || event.getCause() == EntityDamageEvent.DamageCause.FALL) event.setCancelled(true);
    }

    @EventHandler
    public void onKill(KillEvent event) {
        List<NPC> toRemove = new ArrayList<>();

        for (Map.Entry<NPC, PitBoss> entry : bosses.entrySet()) {
            if(entry.getKey().getEntity() == event.dead) {
                entry.getKey().destroy();
                entry.getValue().onDeath();
                toRemove.add(entry.getKey());
                activePlayers.remove(entry.getValue().target);
            } else if(entry.getValue().target == event.dead) {
                entry.getKey().destroy();
                toRemove.add(entry.getKey());
                activePlayers.remove(event.deadPlayer);
            }
        }

        for (NPC npc : toRemove) {
            bosses.remove(npc);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        List<NPC> toRemove = new ArrayList<>();

        for (Map.Entry<NPC, PitBoss> entry : bosses.entrySet()) {
            if(entry.getValue().target == player) {
                activePlayers.remove(event.getPlayer());
                toRemove.add(entry.getKey());
                entry.getKey().destroy();
            }
        }

        for (NPC npc : toRemove) {
            bosses.remove(npc);
        }
    }

    @EventHandler
    public void onHit(AttackEvent.Apply event) throws Exception {
        if(bosses.containsKey(CitizensAPI.getNPCRegistry().getNPC(event.attacker)))
            bosses.get(CitizensAPI.getNPCRegistry().getNPC(event.attacker)).onAttack();
        if(bosses.containsKey(CitizensAPI.getNPCRegistry().getNPC(event.defender)))
            bosses.get(CitizensAPI.getNPCRegistry().getNPC(event.defender)).onDefend();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHit(AttackEvent.Pre event) {
        if(event.defender instanceof Wither) event.setCancelled(true);
    }

    public static boolean useItem(Player player, NBTTag nbtTag) {

        for(int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if(Misc.isAirOrNull(itemStack)) continue;
            NBTItem nbtItem = new NBTItem(itemStack);
            if(nbtItem.hasKey(nbtTag.getRef())) {
                Sounds.BOOSTER_REMIND.play(player);
                if(itemStack.getAmount() > 1) itemStack.setAmount(itemStack.getAmount() - 1);
                else player.getInventory().setItem(i, null);
                return true;
            }
        }
        return false;
    }

    public static void noAI(Entity bukkitEntity) {
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) bukkitEntity).getHandle();
        NBTTagCompound tag = nmsEntity.getNBTTag();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        nmsEntity.c(tag);
        tag.setInt("NoAI", 1);
        nmsEntity.f(tag);
    }


}
