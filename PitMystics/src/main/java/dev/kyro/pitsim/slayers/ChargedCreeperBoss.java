package dev.kyro.pitsim.slayers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.BossManager;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.ThrowBlock;
import dev.kyro.pitsim.misc.BossSkin;
import dev.kyro.pitsim.misc.ThrowableBlock;
import dev.kyro.pitsim.slayers.tainted.SimpleBoss;
import dev.kyro.pitsim.slayers.tainted.SimpleSkin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.npc.ai.CitizensNavigator;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Map;



public class ChargedCreeperBoss extends PitBoss {

    /*

    This is purely experimental and will probably not be the version creeper boss on full release (Basically needs bugs sorted out)

     */


    public NPC npc;
    public Player entity;
    public Player target;
    public String name = "&c&lCreeper Boss";
    public SubLevel subLevel = SubLevel.CREEPER_CAVE;
    public SimpleBoss boss;

    public ChargedCreeperBoss(Player target) throws Exception {
        super(target, SubLevel.CREEPER_CAVE);
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

        this.boss = new SimpleBoss(npc, target, subLevel, 4, SimpleSkin.CREEPER, this) {
            @Override
            protected void attackHigh(){
                target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lNUCLEAR REACTOR! &7Ouch, you're on full blast!"));

                target.getWorld().playEffect(target.getLocation(), Effect.EXPLOSION_LARGE, 1);
                if(npc.getEntity() != null){
                    for (Entity player : npc.getEntity().getNearbyEntities(10, 10, 10)) {
                        if(player != target) continue;
                        PitPlayer.getPitPlayer((Player) player).damage(3.0, (LivingEntity) npc.getEntity());
                    }
                }
            }

            @Override
            protected void attackMedium(){
                Vector dirVector = ChargedCreeperBoss.this.target.getLocation().toVector().subtract(npc.getEntity().getLocation().toVector()).setY(0);
                Vector pullVector = dirVector.clone().normalize().setY(0.2).multiply(0.5).add(dirVector.clone().multiply(0.03));

                if(npc.getEntity() != null)
                    ThrowBlock.addThrowableBlock(new ThrowableBlock(npc.getEntity(), Material.TNT, pullVector.multiply((0.5 * 0.2) + 1.15)));
            }

            @Override
            protected void attackLow(){
                target.getWorld().createExplosion(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), 2, false, false);
            }

            @Override
            protected void defend() {

            }
        };
        this.entity = (Player) npc.getEntity();
        this.target = target;

        boss.run();
    }

    public void onAttack(AttackEvent.Apply event) throws Exception {
        boss.attackAbility(event);
    }

    @Override
    public void onDefend() {
        boss.defendAbility();
    }

    @Override
    public void onDeath() {
        boss.hideActiveBossBar();
    }

    @Override
    public void setNPC(NPC npc) {
        this.npc = npc;
    }

    @Override
    public Player getEntity() {
        return (Player) npc.getEntity();
    }

}
