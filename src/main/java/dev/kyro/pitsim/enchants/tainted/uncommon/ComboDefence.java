package dev.kyro.pitsim.enchants.tainted.uncommon;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.overworld.Regularity;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class ComboDefence extends PitEnchant {
	public static ComboDefence INSTANCE;

	public ComboDefence() {
		super("Combo: Defence", false, ApplyType.SCYTHES,
				"combodefence", "defence", "cdefence");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;
		if(attackEvent.isFakeHit()) return;

		int regLvl = attackEvent.getAttackerEnchantLevel(Regularity.INSTANCE);
		if(Regularity.isRegHit(attackEvent.getDefender()) && Regularity.skipIncrement(regLvl)) return;

		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, getStrikes(enchantLvl))) return;

		Misc.applyPotionEffect(attackEvent.getAttacker(), PotionEffectType.DAMAGE_RESISTANCE, getSeconds(enchantLvl) * 20,
				getAmplifier(enchantLvl), true, false);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Every &e" + Misc.ordinalWords(getStrikes(enchantLvl)) + " &7strike gain &9Resistance[]" +
				AUtil.toRoman(getAmplifier(enchantLvl) + 1) + " &7(" + getSeconds(enchantLvl) + "s)"
		).getLore();
	}

	public int getSeconds(int enchantLvl) {
		return enchantLvl + 2;
	}

	public int getAmplifier(int enchantLvl) {
		return Misc.linearEnchant(enchantLvl, 0.5, 0);
	}

	public int getStrikes(int enchantLvl) {
		return Math.max(Misc.linearEnchant(enchantLvl, -0.5, 4.5), 1);
	}
}
