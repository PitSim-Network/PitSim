package dev.kyro.pitsim.slayers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.BossManager;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enchants.Lifesteal;
import dev.kyro.pitsim.enums.*;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.BossSkin;
import dev.kyro.pitsim.slayers.tainted.SimpleBoss;
import dev.kyro.pitsim.slayers.tainted.SimpleSkin;
import me.confuser.barapi.BarAPI;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.AttackStrategy;
import net.citizensnpcs.api.ai.TargetType;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.npc.ai.CitizensNavigator;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.fusesource.jansi.Ansi;

import java.util.List;
import java.util.Map;

public class ZombieBoss extends PitBoss {
    public NPC npc;
    public Player entity;
    public Player target;
    public String name = "&c&lZombie Boss";
    public SubLevel subLevel = SubLevel.ZOMBIE_CAVE;
    public BossBar activeBar;
    public SimpleBoss boss;

    public ZombieBoss(Player target) throws Exception {
        super(target, SubLevel.ZOMBIE_CAVE);
        this.target = target;

        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

        this.boss = new SimpleBoss(npc, target, subLevel, 1, SimpleSkin.ZOMBIE, this);
        this.entity = (Player) npc.getEntity();
        this.target = target;

        boss.run();


    }

    public void spawn() throws Exception {
        PitBoss.spawn(this.npc, this.target, this.subLevel,
                new BossSkin(this.npc, "zombie"),
                getBillionaire(),
                new ItemStack(Material.DIAMOND_HELMET),
                new ItemStack(Material.DIAMOND_CHESTPLATE),
                getSolitude(),
                new ItemStack(Material.DIAMOND_BOOTS));
    }


    public void onAttack() throws Exception {
        Equipment equipment = npc.getTrait(Equipment.class);
        double health = ((LivingEntity) npc.getEntity()).getHealth();
        double maxHealth = ((LivingEntity) npc.getEntity()).getMaxHealth();
        Map<PitEnchant, Integer> enchants = EnchantManager.getEnchantsOnItem(equipment.get(Equipment.EquipmentSlot.HAND));
        if(equipment.get(Equipment.EquipmentSlot.HAND).getType() == Material.BOW) {
            equipment.set(Equipment.EquipmentSlot.HAND, getLifesteal());
        }
        else if(health < (maxHealth / 2) && !enchants.containsValue(EnchantManager.getEnchant("ls"))) {
            equipment.set(Equipment.EquipmentSlot.HAND, getExplosive());
            LivingEntity shooter = ((LivingEntity) npc.getEntity());
            shooter.launchProjectile(Arrow.class);
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        equipment.set(Equipment.EquipmentSlot.HAND, getLifesteal());
                    } catch (Exception ignored) { }
                }
            }.runTaskLater(PitSim.INSTANCE, 10);
        }
        else equipment.set(Equipment.EquipmentSlot.HAND, getBillionaire());

        new BukkitRunnable() {
            @Override
            public void run() {
                if(npc.getEntity() == null) {return;}

                List<Entity> entities = npc.getEntity().getNearbyEntities(4, 4, 4);
                if(!entities.contains(target)) {
                    try {
                        equipment.set(Equipment.EquipmentSlot.HAND, getPullbow());

                    } catch (Exception ignored) { }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try {
                                LivingEntity shooter = ((LivingEntity) npc.getEntity());
                                shooter.launchProjectile(Arrow.class);
                                equipment.set(Equipment.EquipmentSlot.HAND, getBillionaire());
                                //TODO: Just put pull code here fuck this lol

                                Vector dirVector = npc.getEntity().getLocation().toVector().subtract(target.getLocation().toVector()).setY(0);
                                Vector pullVector = dirVector.clone().normalize().setY(0.2).multiply(0.5).add(dirVector.clone().multiply(0.03));
                                target.setVelocity(pullVector.multiply((0.5 * 0.2) + 1.15));

                            } catch (Exception ignored) { }
                        }
                    }.runTaskLater(PitSim.INSTANCE, 20);
                }
            }
        }.runTaskLater(PitSim.INSTANCE, 10);
    }

    @Override
    public void onDefend() {
        double health = ((LivingEntity) npc.getEntity()).getHealth();
        double maxHealth = ((LivingEntity) npc.getEntity()).getMaxHealth();
        float progress = (float) health / (float) maxHealth;
        activeBar.progress(progress);

        npc.getNavigator().setTarget(target, true);
    }

    @Override
    public void onDeath() {
        hideActiveBossBar(PitSim.adventure.player(target));
        NoteBlockAPI.stopPlaying(target);
    }

    @Override
    public Player getEntity() {
        return entity;
    }

    public static ItemStack getBillionaire() throws Exception {
        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.GREEN);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("bill"), 3, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("cd"), 3, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("kb"), 2, false);
        return itemStack;
    }

    public static ItemStack getLifesteal() throws Exception {
        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.GREEN);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ls"), 3, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("pf"), 1, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("cheal"), 2, false);
        return itemStack;
    }

    public static ItemStack getSolitude() throws Exception {
        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.GREEN);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("rgm"), 3, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("mirror"), 3, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("pero"), 2, false);
        return itemStack;
    }

    public static ItemStack getExplosive() throws Exception {
        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.BOW, PantColor.GREEN);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("explo"), 3, false);
        return itemStack;
    }

    public static ItemStack getPullbow() throws Exception {
        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.BOW, PantColor.GREEN);
//        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("pull"), 3, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("robin"), 3, false);
        return itemStack;
    }

    /*
    public void skin(String name) {
        npc.data().set(NPC.PLAYER_SKIN_UUID_METADATA, name);
        npc.data().set(NPC.PLAYER_SKIN_USE_LATEST, false);
        if(npc.isSpawned()) {
            SkinnableEntity skinnable = (SkinnableEntity) npc.getEntity();
            if(skinnable != null) {
                skinnable.setSkinName(name);
            }
        }
    }

     */

    @Override
    public void setNPC(NPC npc) {
        this.npc = npc;
    }

    public void showMyBossBar(final @NonNull Audience player) {
        final Component name = Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Zombie Boss");
        final BossBar fullBar = BossBar.bossBar(name, 1F, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);

        player.showBossBar(fullBar);
        this.activeBar = fullBar;
    }

    public void hideActiveBossBar(final @NonNull Audience player) {
        player.hideBossBar(this.activeBar);
        this.activeBar = null;
    }
}
