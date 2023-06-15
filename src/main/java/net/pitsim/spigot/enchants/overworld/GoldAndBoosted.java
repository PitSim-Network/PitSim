package net.pitsim.spigot.enchants.overworld;

import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.misc.PitLoreBuilder;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.event.EventHandler;

import java.util.List;

public class GoldAndBoosted extends PitEnchant {

	public GoldAndBoosted() {
		super("Gold and Boosted", false, ApplyType.SWORDS,
				"gab", "gold-and-boosted", "goldandboosted");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		EntityLiving nmsPlayer = ((CraftLivingEntity) attackEvent.getAttacker()).getHandle();
		if(nmsPlayer.getAbsorptionHearts() == 0) return;
		attackEvent.increasePercent += getDamage(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Deal &c+" + getDamage(enchantLvl) + "% &7damage when you have absorption hearts"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that does more " +
				"damage when you have &6absorption";
	}

	public int getDamage(int enchantLvl) {
		return enchantLvl * 11 + 1;
	}
}
