package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

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

		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, 5)) return;

		Cooldown cooldown = getCooldown(attackEvent.getAttackerPlayer(), 8 * 20);
		if(cooldown.isOnCooldown()) return;
		else cooldown.restart();

		Misc.applyPotionEffect(attackEvent.getDefender(), PotionEffectType.SLOW, (int) getDuration(enchantLvl) * 20, 7, true, false);
		Misc.applyPotionEffect(attackEvent.getDefender(), PotionEffectType.JUMP, (int) getDuration(enchantLvl) * 20, 128, true, false);
		Misc.applyPotionEffect(attackEvent.getDefender(), PotionEffectType.SLOW_DIGGING, (int) getDuration(enchantLvl) * 20, 99, true, false);

		if(attackEvent.isDefenderPlayer()) {
			Misc.sendTitle(attackEvent.getDefenderPlayer(), "&cSTUNNED", (int) getDuration(enchantLvl) * 20);
			Misc.sendSubTitle(attackEvent.getDefenderPlayer(), "&eYou cannot move!", (int) getDuration(enchantLvl) * 20);
		}

		Sounds.COMBO_STUN.play(attackEvent.getAttacker());
		Sounds.COMBO_STUN.play(attackEvent.getDefender());

		if(pitPlayer.stats != null) pitPlayer.stats.stun += getDuration(enchantLvl);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {
		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		return new ALoreBuilder("&7The &efifth &7strike on an enemy", "&7stuns them for " + decimalFormat.format(getDuration(enchantLvl)) + " &7seconds",
				"&7&o(Can only be stunned every 8s)").getLore();
	}

	public double getDuration(int enchantLvl) {
		return enchantLvl * 0.4 + 0.8;
	}
}
