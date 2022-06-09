package dev.kyro.pitsim.slayers.tainted;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.BossManager;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.BossSkin;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.npc.ai.CitizensNavigator;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class SimpleBoss {

    NPC npc;
    Player target;
    SubLevel subLevel;
    BossBar activeBar;
    SimpleSkin skin;
    PitBoss pitBoss;

    // There is no cap to difficulty level
    int difficulty;

    // These following variables are handled by difficulty algorithm (But also will have setters and getters)
    int health;

    // Attacks per second (Maximum = 20)
    int aps;

    int speed;

    int reach;

    int damageIncrease;

    ItemStack helmet;
    ItemStack chestplate;
    ItemStack leggings;
    ItemStack boots;
    ItemStack sword;

    public SimpleBoss(NPC npc, Player target, SubLevel sublevel, int difficulty, SimpleSkin skin, PitBoss pitBoss){

        this.npc = npc;
        this.subLevel = sublevel;
        this.target = target;
        this.difficulty = difficulty;
        this.skin = skin;
        this.pitBoss = pitBoss;

        modifiers();

    }

    public void attackAbility(AttackEvent.Apply event){
        double bound = new Random().nextDouble();

        if(bound < .25){
            attackHigh();
        }else if(bound < .35){
            attackMedium();
        }else if (bound < .50){
            attackLow();
        }else{
            try{
                attackDefault(event);
            }catch (Exception ignored){}

        }

    }

    public void defendAbility(){

        double health = ((LivingEntity) npc.getEntity()).getHealth();
        double maxHealth = ((LivingEntity) npc.getEntity()).getMaxHealth();
        float progress = (float) health / (float) maxHealth;

        this.getActiveBar().progress(progress);

        npc.getNavigator().setTarget(target, true);

        double bound = new Random().nextDouble();

        if(bound < .05){try{defend();}catch (Exception ignored){}}
    }

    public void run(){

        CitizensNavigator navigator = (CitizensNavigator) npc.getNavigator();
        navigator.getDefaultParameters()
                .attackDelayTicks(this.aps)
                .attackRange(this.reach)
                .updatePathRate(5)
                .speedModifier(this.speed);

        spawn();

        if(npc.isSpawned()){
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(npc.getEntity() != null) {
                        Player entity = (Player) npc.getEntity();

                        entity.setMaxHealth(health);
                        entity.setHealth(health);

                        npc.setProtected(false);

                        bossBar(PitSim.adventure.player(target), entity.getDisplayName());
                        BossManager.activePlayers.add(target);

                        pitBoss.setNPC(npc);

                        BossManager.bosses.put(npc, pitBoss);
                    }

                    try {
                        npc.getNavigator().setTarget(target, true);
                    } catch (Exception ignored) { }
                }
            }.runTaskLater(PitSim.INSTANCE, 20);
        }


    }

    public void spawn(){
        PitBoss.spawn(this.npc, this.target, this.subLevel,
                new BossSkin(this.npc, skin.skin),
                this.sword,
                this.helmet,
                this.chestplate,
                this.leggings,
                this.boots);
    }


    private void modifiers(){
        // Manual Testing

        this.speed = Math.min(Math.max((this.difficulty)/2, 1) * 2, 5);

        // IntelliJ trippin
        this.aps = Math.min(Math.max(Math.min(this.difficulty*2, 8), 4), 20);

        this.reach = 10;

        this.health = Math.min((this.difficulty*20)*5, 1000);

        this.damageIncrease = difficulty * 10;

        GearType type;

        if(this.difficulty <= 3) type = GearType.MEDIUM;
        else if(this.difficulty == 5 || this.difficulty == 4) type = GearType.DAMAGE;
        else if(this.difficulty <= 8) type = GearType.GLASS_CANNON;
        else type = GearType.SHREDDER;


        this.helmet = type.helmet;
        this.chestplate = type.chestplate;
        this.leggings = type.leggings;
        this.boots = type.boots;
        this.sword = type.sword;

    }

//    public void bossBar(final @NonNull Audience player){
//        if(subLevel.equals(SubLevel.ZOMBIE_CAVE)){
//            final Component name = Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Zombie Boss");
//            final BossBar fullBar = BossBar.bossBar(name, 1F, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);
//
//            player.showBossBar(fullBar);
//            this.activeBar = fullBar;
//        }else if(subLevel.equals(SubLevel.SKELETON_CAVE)){
//            final Component name = Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Skeleton Boss");
//            final BossBar fullBar = BossBar.bossBar(name, 1F, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);
//
//            player.showBossBar(fullBar);
//            this.activeBar = fullBar;
//        }else if(subLevel.equals(SubLevel.SPIDER_CAVE)){
//            final Component name = Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Spider Boss");
//            final BossBar fullBar = BossBar.bossBar(name, 1F, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);
//
//            player.showBossBar(fullBar);
//            this.activeBar = fullBar;
//        }else if(subLevel.equals(SubLevel.CREEPER_CAVE)){
//            final Component name = Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Creeper Boss");
//            final BossBar fullBar = BossBar.bossBar(name, 1F, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);
//
//            player.showBossBar(fullBar);
//            this.activeBar = fullBar;
//        }else if(subLevel.equals(SubLevel.DEEP_SPIDER_CAVE)){
//            final Component name = Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Deep Spider Boss");
//            final BossBar fullBar = BossBar.bossBar(name, 1F, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);
//
//            player.showBossBar(fullBar);
//            this.activeBar = fullBar;
//        }else if(subLevel.equals(SubLevel.PIGMEN_CAVE)){
//            final Component name = Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Pigman Boss");
//            final BossBar fullBar = BossBar.bossBar(name, 1F, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);
//
//            player.showBossBar(fullBar);
//            this.activeBar = fullBar;
//        }else if(subLevel.equals(SubLevel.MAGMA_CAVE)){
//            final Component name = Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Magma Boss");
//            final BossBar fullBar = BossBar.bossBar(name, 1F, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);
//
//            player.showBossBar(fullBar);
//            this.activeBar = fullBar;
//        }
//    }

    public void bossBar(final @NonNull Audience player, String text) {
        final Component name = Component.text(text);
        final BossBar fullBar = BossBar.bossBar(name, 1F, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);

        player.showBossBar(fullBar);
        this.activeBar = fullBar;
    }

    public void hideActiveBossBar() {
        Audience player = PitSim.adventure.player(target);
        player.hideBossBar(this.activeBar);
        this.activeBar = null;
    }

    // Getters and setters for most variables should stay below this comment nothing other than getters and setters should be here

    public ItemStack getHelmet() {
        return helmet;
    }

    public void setHelmet(ItemStack helmet) {
        this.helmet = helmet;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public void setChestplate(ItemStack chestplate) {
        this.chestplate = chestplate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public void setLeggings(ItemStack leggings) {
        this.leggings = leggings;
    }

    public ItemStack getBoots() {
        return boots;
    }

    public void setBoots(ItemStack boots) {
        this.boots = boots;
    }


    public ItemStack getSword() {
        return sword;
    }

    public void setSword(ItemStack sword) {
        this.sword = sword;
    }

    public int getReach() {
        return reach;
    }

    public void setReach(int reach) {
        this.reach = reach;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getAps() {
        return aps;
    }

    public void setAps(int aps) {
        this.aps = aps;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setDifficulty(int difficulty){
        this.difficulty = difficulty;
    }

    public int getDifficulty(){
        return this.difficulty;
    }

    public BossBar getActiveBar() {
        return activeBar;
    }

    public void setActiveBar(BossBar activeBar) {
        this.activeBar = activeBar;
    }


    protected abstract void attackHigh();

    protected abstract void attackMedium();
    protected abstract void attackLow();

    protected abstract void  defend();


    public void attackDefault(AttackEvent.Apply event) throws Exception {
        event.increase += damageIncrease;

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

    public ItemStack getBillionaire() throws Exception {
        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.GREEN);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("bill"), 3, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("cd"), 3, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("kb"), 2, false);
        return itemStack;
    }

    public ItemStack getLifesteal() throws Exception {
        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.GREEN);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ls"), 3, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("pf"), 1, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("cheal"), 2, false);
        return itemStack;
    }

    public ItemStack getSolitude() throws Exception {
        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.GREEN);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("rgm"), 3, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("mirror"), 3, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("pero"), 2, false);
        return itemStack;
    }

    public ItemStack getExplosive() throws Exception {
        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.BOW, PantColor.GREEN);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("explo"), 3, false);
        return itemStack;
    }

    public ItemStack getPullbow() throws Exception {
        ItemStack itemStack;
        itemStack = FreshCommand.getFreshItem(MysticType.BOW, PantColor.GREEN);
//        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("pull"), 3, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("robin"), 3, false);
        return itemStack;
    }
}
