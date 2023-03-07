package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.event.EventHandler;

import java.text.DecimalFormat;
import java.util.List;

public class ComboStun extends PitEnchant {

	public ComboStun() {
		super("Combo: Stun", true, ApplyType.MELEE,
				"combostun", "stun", "combo-stun", "cstun");
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

		Cooldown cooldown = getCooldown(attackEvent.getAttackerPlayer(), 8 * 20);
		if(cooldown.isOnCooldown()) return;

		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, 5)) return;

		else cooldown.restart();

		Misc.stunEntity(attackEvent.getDefender(), (int) getDuration(enchantLvl) * 20);
		Sounds.COMBO_STUN.play(attackEvent.getAttacker());

		if(pitPlayer.stats != null) pitPlayer.stats.stun += getDuration(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		return new PitLoreBuilder(
				"&7The &efifth &7strike on an enemy stuns them for " +
						decimalFormat.format(getDuration(enchantLvl)) + " &7seconds &o(Can only be stunned every 8s)"
		).getLore();
	}

	public double getDuration(int enchantLvl) {
		return enchantLvl * 0.4 + 0.8;
	}
}
