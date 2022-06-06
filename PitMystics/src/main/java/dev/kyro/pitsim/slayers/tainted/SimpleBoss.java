package dev.kyro.pitsim.slayers.tainted;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.BossManager;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.misc.BossSkin;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.ai.CitizensNavigator;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;

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

    public void attackHigh(){

    }

    public void attackMedium(){

    }

    public void attackLow(){

    }

    private void attackAbility(){
        int random = 10;
        switch (random){
            case 1:
                attackHigh();
                break;
            case 2:
                attackMedium();
                break;
            case 3:
                attackLow();
                break;
        }
    }

    private void defendAbility(){

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
}
