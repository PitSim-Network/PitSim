package net.pitsim.spigot.enchants.overworld;

import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class PainFocus extends PitEnchant {

	public PainFocus() {
		super("Pain Focus", false, ApplyType.MELEE,
				"painfocus", "pf", "pain-focus");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		attackEvent.increasePercent += getDamage(attackEvent.getAttackerPlayer(), enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Deal &c+" + getDamage(enchantLvl) + "% &7damage per &c\u2764 &7you're missing"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that does more " +
				"damage the lower you are";
	}

	public int getDamage(int enchantLvl) {
		return enchantLvl * 2 - 1;
	}

	public double getDamage(Player player, int enchantLvl) {

		int missingHearts = (int) ((player.getMaxHealth() - player.getHealth()) / 2);
		return missingHearts * getDamage(enchantLvl);
	}
}
