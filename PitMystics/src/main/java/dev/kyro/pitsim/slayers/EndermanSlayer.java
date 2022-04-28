package dev.kyro.pitsim.slayers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.BossManager;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitBoss;
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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import static org.mozilla.javascript.tools.shell.Global.spawn;

public class EndermanSlayer extends PitBoss {

    public NPC npc;
    public Player entity;
    public Player target;
    public String name = "&c&lEnderman Boss";
    public SubLevel subLevel = SubLevel.ZOMBIE_CAVE;
    public BossBar activeBar;

    public EndermanSlayer(Player target, SubLevel subLevel) throws Exception {
        super(target, subLevel);


        this.target = target;

        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

        CitizensNavigator navigator = (CitizensNavigator) npc.getNavigator();
        navigator.getDefaultParameters()
                .attackDelayTicks(10)
                .attackRange(10)
                .updatePathRate(5)
                .speed(2);


        npc.setProtected(false);

        skin(npc,"zombie");
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


        equipment.set(Equipment.EquipmentSlot.HAND, getSword());
        equipment.set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.DIAMOND_HELMET));
        equipment.set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.DIAMOND_CHESTPLATE));
        equipment.set(Equipment.EquipmentSlot.LEGGINGS, getPants());
        equipment.set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.DIAMOND_BOOTS));

        npc.spawn(subLevel.middle);
        npc.getNavigator().setTarget(target, true);
    }

    public void showMyBossBar(final @NonNull Audience player) {
        final Component name = Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Enderman Boss");
        final BossBar fullBar = BossBar.bossBar(name, 1F, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);

        player.showBossBar(fullBar);
        this.activeBar = fullBar;
    }

    public static ItemStack getSword() throws Exception {

        ItemStack itemStack;

        itemStack = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.BLOOD_RED);

        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("bill"), 3, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("ls"), 3, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("perun"), 3, false);

        return itemStack;
    }

    public static ItemStack getPants() throws Exception {

        ItemStack itemStack;

        itemStack = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.BLOOD_RED);

        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("rgm"), 3, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("critfunky"), 3, false);
        itemStack = EnchantManager.addEnchant(itemStack, EnchantManager.getEnchant("prot"), 3, false);

        return itemStack;
    }

    @Override
    public void onAttack() throws Exception {

    }

    @Override
    public void onDefend() {

    }

    @Override
    public void onDeath() {

    }

    @Override
    public Player getEntity() {
        return null;
    }
}
