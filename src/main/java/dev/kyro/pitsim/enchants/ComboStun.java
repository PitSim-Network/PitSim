package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.PolarManager;
import dev.kyro.pitsim.controllers.objects.AnticheatManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

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
		int duration = (int) getDuration(enchantLvl) * 20;

		if(PitSim.anticheat instanceof PolarManager) {
			new BukkitRunnable() {
				@Override
				public void run() {
					PitSim.anticheat.exemptPlayer(attackEvent.getDefenderPlayer(), duration * 500L, AnticheatManager.FlagType.KNOCKBACK, AnticheatManager.FlagType.SIMULATION);
				}
			}.runTaskLater(PitSim.INSTANCE, 10);

		}

		Misc.applyPotionEffect(attackEvent.getDefender(), PotionEffectType.SLOW, duration, 7, true, false);
		Misc.applyPotionEffect(attackEvent.getDefender(), PotionEffectType.JUMP, duration, 254, true, false);
		Misc.applyPotionEffect(attackEvent.getDefender(), PotionEffectType.SLOW_DIGGING, duration, 99, true, false);

		if(attackEvent.isDefenderPlayer()) {
			Misc.sendTitle(attackEvent.getDefenderPlayer(), "&cSTUNNED", duration);
			Misc.sendSubTitle(attackEvent.getDefenderPlayer(), "&eYou cannot move!", duration);
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
