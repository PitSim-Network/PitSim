package net.pitsim.spigot.enchants.tainted.uncommon;

import net.pitsim.spigot.controllers.HitCounter;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enchants.overworld.Regularity;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class ComboMana extends PitEnchant {
	public static ComboMana INSTANCE;

	public ComboMana() {
		super("Combo: Mana", false, ApplyType.SCYTHES,
				"combomana", "cmana");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;
		if(attackEvent.getAttacker() != attackEvent.getRealDamager() || attackEvent.getWrapperEvent().hasAttackInfo()) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		int regLvl = attackEvent.getAttackerEnchantLevel(Regularity.INSTANCE);
		if(Regularity.isRegHit(attackEvent.getDefender()) && Regularity.skipIncrement(regLvl)) return;

		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, getStrikes(enchantLvl))) return;

		pitPlayer.giveMana(getMana(enchantLvl));
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Every &e" + Misc.ordinalWords(getStrikes(enchantLvl)) + " &7melee strike gain &b+" + getMana(enchantLvl) +
				"[]mana"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"gives you &bmana &7every few melee strikes";
	}

	public int getMana(int enchantLvl) {
		return enchantLvl * 2 + 2;
	}

	public int getStrikes(int enchantLvl) {
		return Math.max(Misc.linearEnchant(enchantLvl, -0.5, 6), 1);
	}
}
