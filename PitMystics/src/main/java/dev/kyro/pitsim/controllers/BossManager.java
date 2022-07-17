package dev.kyro.pitsim.controllers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.slayers.*;
import dev.kyro.pitsim.slayers.tainted.Loot.BossDrop;
import dev.kyro.pitsim.slayers.tainted.Loot.LootTable;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BossManager implements Listener {

    public static List<Player> activePlayers = new ArrayList<>();
    public static Map<NPC, PitBoss> bosses = new HashMap<>();
    public static List<Hologram> holograms = new ArrayList<>();
    public static Map<SubLevel, NPC> clickables = new HashMap<>();
    public static Map<SubLevel, Map<Player, Integer>> bossItems = new HashMap<>();

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<SubLevel, NPC> entry : clickables.entrySet()) {
                    entry.getValue().teleport(entry.getKey().middle, PlayerTeleportEvent.TeleportCause.UNKNOWN);
                    if(entry.getValue().isSpawned()) ((LivingEntity) entry.getValue().getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                }
            }
        }.runTaskTimer(PitSim.INSTANCE, 20, 20 * 5);
    }

    public static void onStart() {
        for (Hologram hologram : HologramsAPI.getHolograms(PitSim.INSTANCE)) {
            hologram.delete();
        }

        for (SubLevel level : SubLevel.values()) {

            bossItems.put(level, new HashMap<>());

            Hologram holo = HologramsAPI.createHologram(PitSim.INSTANCE, level.middle.add(0.5, 1, 0.5));
            holo.setAllowPlaceholders(true);
            holo.appendTextLine(ChatColor.RED + "Place " + ChatColor.translateAlternateColorCodes('&',level.itemName));
            holo.appendTextLine("{fast}" + level.placeholder + " ");
            holograms.add(holo);

            NPCRegistry registry = CitizensAPI.getNPCRegistry();
            NPC npc = registry.createNPC(EntityType.MAGMA_CUBE, "");
            npc.spawn(level.middle.add(0, -1, 0));
            clickables.put(level, npc);
        }
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        SubLevel level = null;
        for (Map.Entry<SubLevel, NPC> entry : clickables.entrySet()) {
            if(entry.getValue().getId() == event.getNPC().getId()) level = entry.getKey();
        }
        if(activePlayers.contains(event.getClicker())) return;

        if(level != null && level.bossItem != null && useItem(event.getClicker(), level.bossItem)) {
            Map<Player, Integer> players = bossItems.get(level);
            if(players.containsKey(event.getClicker())) players.put(event.getClicker(), players.get(event.getClicker()) + 1);
            else players.put(event.getClicker(), 1);

            if(players.get(event.getClicker()) == 10) {
                players.remove(event.getClicker());
                level.middle.getWorld().playEffect(level.middle, Effect.EXPLOSION_HUGE, 100);
                Sounds.PRESTIGE.play(level.middle);
                try {

                    // Hard coding for now
                    getBossType(level, event.getClicker());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getBossType(SubLevel level, Player player) throws Exception {

        switch (level.bossItem) {
            case ZOMBIE_FLESH:
                new ZombieBoss(player);
                break;
            case SKELETON_BONE:
                new SkeletonBoss(player);
                break;
            case SPIDER_EYE:
                new SpiderBoss(player);
                break;
            case CREEPER_POWDER:
                new ChargedCreeperBoss(player);
                break;
            case CAVESPIDER_EYE:
                new CaveSpiderBoss(player);
                break;
            case PIGMAN_PORK:
                new ZombiePigmanBoss(player);
                break;
            case MAGMACUBE_CREAM:
                new MagmaCubeBoss(player);
                break;
            case WITHER_SKELETON_COAL:
                new WitherSkeletonBoss(player);
                break;
            case GOLEM_INGOT:
                new IronGolemBoss(player);
                break;
            case ENDERMAN_PEARL:
                new EndermanSlayer(player);
                break;
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
                giveSouls(entry.getValue().target, entry.getValue().subLevel.level * 5);

                BossDrop bossDrop = new BossDrop(entry.getValue().target.getPlayer(), LootTable.valueOf(entry.getValue().subLevel.toString()));
//
                bossDrop.run();

                PitPlayer.getPitPlayer(entry.getValue().target).stats.bossesKilled++;
                toRemove.add(entry.getKey());
                activePlayers.remove(entry.getValue().target);
            } else if(entry.getValue().target == event.dead) {
                entry.getKey().destroy();
                toRemove.add(entry.getKey());
                NoteBlockAPI.stopPlaying(event.deadPlayer);
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
    public void onOof(OofEvent event) {
        List<NPC> toRemove = new ArrayList<>();
        for (Map.Entry<NPC, PitBoss> entry : bosses.entrySet()) {

            if(entry.getValue().target == event.getPlayer()) {
                entry.getKey().destroy();
                toRemove.add(entry.getKey());
                NoteBlockAPI.stopPlaying(event.getPlayer());
                activePlayers.remove(event.getPlayer());
            }
        }

        for (NPC npc : toRemove) {
            bosses.remove(npc);
        }
    }

    @EventHandler
    public void onHit(AttackEvent.Apply event) throws Exception {
        if(bosses.containsKey(CitizensAPI.getNPCRegistry().getNPC(event.attacker)))
            bosses.get(CitizensAPI.getNPCRegistry().getNPC(event.attacker)).onAttack(event);
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
                player.updateInventory();
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

    public static void playMusic(Player player, int level) {
        if(PitPlayer.getPitPlayer(player).musicDisabled) return;
        MusicManager.stopPlaying(player);
        File file = new File("plugins/NoteBlockAPI/Effects/boss" + level + ".nbs");
        Song song = NBSDecoder.parse(file);
        EntitySongPlayer esp = new EntitySongPlayer(song);
        esp.setEntity(player);
        esp.setDistance(16);
        esp.setRepeatMode(RepeatMode.ONE);
        esp.addPlayer(player);
        esp.setPlaying(true);
    }

    public static void giveSouls(Player player, int amount) {
        PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

        pitPlayer.taintedSouls += amount;
        pitPlayer.soulsGathered += amount;
        pitPlayer.stats.lifetimeSouls += amount;
        AOutput.send(player, "&d&lTAINTED &7You have gained &f" + amount + " Tainted Souls&7.");
    }


}
