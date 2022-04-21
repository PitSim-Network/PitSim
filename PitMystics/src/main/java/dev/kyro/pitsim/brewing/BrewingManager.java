package dev.kyro.pitsim.brewing;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.objects.BrewingAnimation;
import dev.kyro.pitsim.brewing.objects.BrewingSession;
import dev.kyro.pitsim.controllers.TaintedWell;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

public class BrewingManager implements Listener {

    public static List<BrewingAnimation> brewingAnimations = new ArrayList<>();
    public static List<ArmorStand> brewingStands = new ArrayList<>();
    public static List<BrewingSession> brewingSessions = new ArrayList<>();
    public static List<Player> pausePlayers = new ArrayList<>();
    public static ArmorStand spinStand;
    public static int i = 0;

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                BrewingAnimation anim = brewingAnimations.get(0);

                for (Entity nearbyEntity : anim.location.getWorld().getNearbyEntities(anim.location, 25, 25, 25)) {
                    List<ArmorStand> destroyStands = new ArrayList<>(anim.personalStands);
                    if(!(nearbyEntity instanceof Player)) continue;
                    Player player = (Player) nearbyEntity;

                    if(anim.players.contains(player)) {
                        destroyStands.remove(anim.cancelStands.get(player));
                        destroyStands.remove(anim.confirmStands.get(player));
                        destroyStands.remove(anim.identityStands.get(player));
                        destroyStands.remove(anim.potencyStands.get(player));
                        destroyStands.remove(anim.durationStands.get(player));
                        destroyStands.remove(anim.brewingTimeStands.get(player));
                    }

                    for (ArmorStand destroyStand : destroyStands) {
                        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(getStandID(destroyStand, anim.location));
                        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(destroyPacket);
                    }

                    if(anim.players.contains(player)) continue;
                    if(pausePlayers.contains(player)) continue;
                    PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
                    String[] text = new String[5];
                    text[0] = "&e&lActively Brewing Potions";
                    for (int i = 0; i < pitPlayer.brewingSessions.length; i++) {
                        BrewingSession session = getBrewingSession(player, i + 1);
                        if(session != null) {
                            int addTicks = (105 - session.reduction.getBrewingReductionMinutes()) * 60 * 20;
                            int timeLeft = (int) ((int) (((session.startTime / 1000) * 20) + addTicks) - (((System.currentTimeMillis() / 1000) * 20)));
                            if(timeLeft < 0) text[i + 1] = "&a&lREADY!";
                            else text[i + 1] = session.identifier.color + session.identifier.name + " &f" + Misc.ticksToTime(timeLeft) + "";
                        } else if(UpgradeManager.getTier(player, "CHEMIST") >= i) text[i + 1] = "&cSlot Empty!";
                        else text[i + 1] = "&cSlot Locked!";
                    }

                    if(hasReadyPotions(player)) text[4] = "&aRight-Click to collect!";
                    else if(getBrewingSlot(player) >=0) text[4] = "&eRight Click to Brew!";
                    else text[4] = "&cAll slots full!";

                    anim.setText(player, text);
                }
            }
        }.runTaskTimer(PitSim.INSTANCE, 20, 20);


        new BukkitRunnable() {
            @Override
            public void run() {
                BrewingAnimation anim = brewingAnimations.get(0);

                for (Entity nearbyEntity : anim.location.getWorld().getNearbyEntities(anim.location, 10, 10, 10)) {
                    if(!(nearbyEntity instanceof Player)) continue;
                    Player player = (Player) nearbyEntity;
                    if(!hasPotions(player)) continue;

                    PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook identityTpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(spinStand, anim.location), (byte) 0, (byte) 0, (byte) 0, (byte) i, (byte) 0, false);
                    ((CraftPlayer)player).getHandle().playerConnection.sendPacket(identityTpPacket);
                    player.playEffect(anim.location.clone().add(0.5, 1.0, 0.5), Effect.POTION_SWIRL, 0);

                    i += 4;
                    if(i >= 256) i = 0;
                }
            }
        }.runTaskTimer(PitSim.INSTANCE, 2, 2);

        new BukkitRunnable() {
            @Override
            public void run() {
                BrewingAnimation anim = brewingAnimations.get(0);

                for (Entity nearbyEntity : anim.location.getWorld().getNearbyEntities(anim.location, 10, 10, 10)) {
                    if(!(nearbyEntity instanceof Player)) continue;
                    Player player = (Player) nearbyEntity;
                    if(hasPotions(player)) Sounds.POTION_BUBBLE.play(player);
                }
            }
        }.runTaskTimer(PitSim.INSTANCE, 100, 100);

    }

    public static void onStart() {
        brewingAnimations.add(new BrewingAnimation(new Location(Bukkit.getWorld("darkzone"), 10, 71, -80)));
        spinStand = (ArmorStand) brewingAnimations.get(0).location.getWorld().spawnEntity(brewingAnimations.get(0).location.clone().add(0.5, 0, 0.5), EntityType.ARMOR_STAND);
        spinStand.setItemInHand(new ItemStack(Material.STICK));
        spinStand.setArms(true);
        spinStand.setRightArmPose(new EulerAngle(Math.toRadians(330), Math.toRadians(345), Math.toRadians(0)));
        spinStand.setVisible(false);
        spinStand.setGravity(false);
        brewingStands.add(spinStand);
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(event.getClickedBlock().getType() != Material.CAULDRON) return;
        if(event.getPlayer().getWorld() != Bukkit.getWorld("darkzone")) return;
        if(hasReadyPotions(event.getPlayer())) {
            for (int i = 0; i < 3; i++) {
                BrewingSession session = getBrewingSession(event.getPlayer(), i + 1);
                if(session == null) continue;

                int addTicks = (105 - session.reduction.getBrewingReductionMinutes()) * 60 * 20;
                int timeLeft = (int) ((int) (((session.startTime / 1000) * 20) + addTicks) - (((System.currentTimeMillis() / 1000) * 20)));
                if(timeLeft < 0) session.givePotion();
            }
            return;
        }
        if(getBrewingSlot(event.getPlayer()) < 0) {
            pausePlayers.add(event.getPlayer());
            brewingAnimations.get(0).setText(event.getPlayer(), new String[]{"&cAll of your Brewing Slots", "&care full!", "&c", "&cYou may unlock up to 3", "&cin the &eRenown Shop&c."});
            new BukkitRunnable() {
                @Override
                public void run() {
                    pausePlayers.remove(event.getPlayer());
                }
            }.runTaskLater(PitSim.INSTANCE, 40);
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        for (BrewingAnimation brewingAnimation : brewingAnimations) {
            if(brewingAnimation.location.equals(event.getClickedBlock().getLocation())) {
                if(!brewingAnimation.players.contains(event.getPlayer())) brewingAnimation.addPlayer(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onStandClick(PlayerInteractAtEntityEvent event) {
        for (BrewingAnimation brewingAnimation : brewingAnimations) {
            brewingAnimation.onButtonPush(event);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        for (BrewingAnimation brewingAnimation : brewingAnimations) {
            brewingAnimation.onMove(event);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (BrewingAnimation brewingAnimation : brewingAnimations) {
            brewingAnimation.onQuit(event);
        }
    }

    @EventHandler
    public void onHit(AttackEvent.Pre pre) {
        if(!(pre.defender instanceof ArmorStand)) return;
        if(pre.defender.getUniqueId().equals(spinStand.getUniqueId())) pre.setCancelled(true);
        for (ArmorStand brewingStand : brewingStands) {
            if(brewingStand.getUniqueId().equals(pre.defender.getUniqueId())) pre.setCancelled(true);
        }
    }

    public static int getBrewingSlot(Player player) {
        PitPlayer pitPlayer = PitPlayer.getPitPlayer(player.getPlayer());
        for (int i = 0; i < pitPlayer.brewingSessions.length; i++) {
            if(pitPlayer.brewingSessions[i] == null && UpgradeManager.getTier(player, "CHEMIST") >= i) return i + 1;
        }
        return -1;
    }

    public static int getStandID(final ArmorStand stand, Location location) {
        for (final Entity entity : Bukkit.getWorld("darkzone").getNearbyEntities(location, 7.0, 7.0, 7.0)) {
            if (!(entity instanceof ArmorStand)) {
                continue;
            }
            if (entity.getUniqueId().equals(stand.getUniqueId())) {
                return entity.getEntityId();
            }
        }
        return 0;
    }

    public static boolean hasReadyPotions(Player player) {
        for (int i = 0; i < 3; i++) {
            BrewingSession session = getBrewingSession(player, i + 1);
            if(session == null) continue;

            int addTicks = (105 - session.reduction.getBrewingReductionMinutes()) * 60 * 20;
            int timeLeft = (int) ((int) (((session.startTime / 1000) * 20) + addTicks) - (((System.currentTimeMillis() / 1000) * 20)));
            if(timeLeft < 0) return true;
        }
        return false;
    }

    public static boolean hasPotions(Player player) {
        for (int i = 0; i < 3; i++) {
            if(getBrewingSession(player, i + 1) != null) return true;
        }
        return false;
    }

    public static BrewingSession getBrewingSession(Player player, int slot) {
        for (BrewingSession brewingSession : brewingSessions) {
            if(brewingSession.player == player && brewingSession.brewingSlot == slot) return brewingSession;
        }
        return null;
    }
}
