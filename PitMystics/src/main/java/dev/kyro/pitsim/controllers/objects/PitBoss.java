package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.controllers.BossManager;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.misc.BossSkin;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.npc.skin.Skin;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public abstract class PitBoss {
    public Player target;
    public SubLevel subLevel;

    public PitBoss(Player target, SubLevel subLevel) {
        this.target = target;
        this.subLevel = subLevel;
    }

    public abstract void onAttack() throws Exception;

    public abstract void onDefend();

    public abstract void onDeath();

    public abstract Player getEntity();

    public static void spawn(NPC npc, Player target, SubLevel subLevel, BossSkin skin, ItemStack hand, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots){
        skin.skin();

        Equipment equipment = npc.getTrait(Equipment.class);


        equipment.set(Equipment.EquipmentSlot.HAND, hand);
        equipment.set(Equipment.EquipmentSlot.HELMET, helmet);
        equipment.set(Equipment.EquipmentSlot.CHESTPLATE, chestplate);
        equipment.set(Equipment.EquipmentSlot.LEGGINGS, leggings);
        equipment.set(Equipment.EquipmentSlot.BOOTS, boots);

        npc.spawn(subLevel.middle);
        npc.teleport(subLevel.middle, PlayerTeleportEvent.TeleportCause.COMMAND);






        npc.getNavigator().setTarget(target, true);
        BossManager.playMusic(target, subLevel.level);

    }

}
