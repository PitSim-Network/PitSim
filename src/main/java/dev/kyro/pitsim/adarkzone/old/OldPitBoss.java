package dev.kyro.pitsim.adarkzone.old;

import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.BossSkin;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.npc.ai.CitizensNavigator;
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public abstract class OldPitBoss {
	public Player target;
	public SubLevel subLevel;
	public int soulReward;

	public OldPitBoss(Player target, SubLevel subLevel, int soulReward) {
		this.target = target;
		this.subLevel = subLevel;
		this.soulReward = soulReward;
	}

	public abstract void onAttack(AttackEvent.Apply event) throws Exception;

	public abstract void onDefend();

	public abstract void onDeath();

	public abstract Player getEntity();

	public abstract void setNPC(NPC npc);

	public static void spawn(NPC npc, Player target, SubLevel subLevel, BossSkin skin, ItemStack hand, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
		Equipment equipment = npc.getTrait(Equipment.class);

		equipment.set(Equipment.EquipmentSlot.HAND, hand);
		equipment.set(Equipment.EquipmentSlot.HELMET, helmet);
		equipment.set(Equipment.EquipmentSlot.CHESTPLATE, chestplate);
		equipment.set(Equipment.EquipmentSlot.LEGGINGS, leggings);
		equipment.set(Equipment.EquipmentSlot.BOOTS, boots);

		npc.spawn(subLevel.middle);
		skin.skin();
		npc.teleport(subLevel.middle.clone().add(0, 3, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);

		Entity player = npc.getEntity();

		int radius = 2;
		double thetaRand = 360 * Math.random();
		double phiRand = 360 * Math.random();

		for(int i = 0; i < (48) * 30; i++) {
			double x2 = radius * Math.cos(phiRand) * Math.sin(thetaRand);
			double z2 = radius * Math.sin(phiRand) * Math.sin(thetaRand);
			double y2 = radius * Math.cos(thetaRand);

			int size = player.getWorld().getNearbyEntities(player.getLocation().add(x2, y2 + 1, z2), 0.7, 0.7, 0.7).size();

			player.getWorld().spigot().playEffect(player.getLocation().add(x2, y2 + 1, z2), Effect.COLOURED_DUST, 0, 0, (float) 0, (float) 0, (float) 0, 1, 0, 64);

			thetaRand = 360 * Math.random();
			phiRand = 360 * Math.random();
		}

		CitizensNavigator navigator = (CitizensNavigator) npc.getNavigator();
		navigator.getDefaultParameters()
				.attackDelayTicks(10)
				.attackRange(4);
		npc.getNavigator().setTarget(target, true);
		npc.setProtected(false);
		OldBossManager.playMusic(target, subLevel.level);
	}

	public static boolean isPitBoss(Player player) {
		for(OldPitBoss value : OldBossManager.bosses.values()) {
			if(value.getEntity() == player) return true;
		}
		return false;
	}
}
