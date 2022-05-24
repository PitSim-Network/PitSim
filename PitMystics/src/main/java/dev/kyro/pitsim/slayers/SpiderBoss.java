package dev.kyro.pitsim.slayers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.BossManager;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.enums.SubLevel;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.npc.ai.CitizensNavigator;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Map;

public class SpiderBoss extends PitBoss {
    public NPC npc;
    public Player entity;
    public Player target;
    public String name = "&c&lSpider Boss";
    public SubLevel subLevel = SubLevel.ZOMBIE_CAVE;
    public BossBar activeBar;

    public SpiderBoss(Player target) throws Exception {
        super(target, SubLevel.SPIDER_CAVE);
        this.target = target;

        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

        CitizensNavigator navigator = (CitizensNavigator) npc.getNavigator();
        navigator.getDefaultParameters()
                .attackDelayTicks(8)
                .attackRange(10)
                .updatePathRate(5)
                .speed(2);


        npc.setProtected(false);
        spawn();
        entity = (Player) npc.getEntity();
        BossManager.bosses.put(npc, this);

        entity.setMaxHealth(250);
        entity.setHealth(250);
        showMyBossBar(PitSim.adventure.player(target));
        BossManager.activePlayers.add(target);


    }

    public void spawn() throws Exception {
        Equipment equipment = npc.getTrait(Equipment.class);


        equipment.set(Equipment.EquipmentSlot.HAND, getBillionaire());
        equipment.set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.DIAMOND_HELMET));
        equipment.set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.DIAMOND_CHESTPLATE));
        equipment.set(Equipment.EquipmentSlot.LEGGINGS, getSolitude());
        equipment.set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.DIAMOND_BOOTS));

        npc.spawn(subLevel.middle);
        npc.teleport(subLevel.middle, PlayerTeleportEvent.TeleportCause.COMMAND);
        npc.getNavigator().setTarget(target, true);
        BossManager.playMusic(target, subLevel.level);
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

            if(target.getLocation().getBlock().getType() == null && target.getLocation().getBlock().getType() == Material.AIR){
                target.getLocation().getBlock().setType(Material.WEB);
            }

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
                                //shooter.launchProjectile(WitherSkull.class);
                                //equipment.set(Equipment.EquipmentSlot.HAND, getBillionaire());
                                //TODO: Just put pull code here fuck this lol

                                Vector dirVector = target.getLocation().toVector().subtract(npc.getEntity().getLocation().toVector()).setY(0);
                                Vector pullVector = dirVector.clone().normalize().setY(0.2).multiply(0.5).add(dirVector.clone().multiply(0.03));
                                npc.getEntity().setVelocity(pullVector.multiply((0.5 * 0.2) + 1.15));

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

    public void showMyBossBar(final @NonNull Audience player) {
        final Component name = Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Spider Boss");
        final BossBar fullBar = BossBar.bossBar(name, 1F, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);

        player.showBossBar(fullBar);
        this.activeBar = fullBar;
    }

    public void hideActiveBossBar(final @NonNull Audience player) {
        player.hideBossBar(this.activeBar);
        this.activeBar = null;
    }

}
