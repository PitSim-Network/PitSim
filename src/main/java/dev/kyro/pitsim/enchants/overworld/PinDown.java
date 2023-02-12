package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.CooldownManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PinDown extends PitEnchant {

	public PinDown() {
		super("Pin down", false, ApplyType.BOWS,
				"pindown", "pin", "pd", "pin-down");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.getAttacker() == attackEvent.getDefender()) return;
		if(!attackEvent.getArrow().isCritical()) return;

		Cooldown cooldown = getCooldown(attackEvent.getAttackerPlayer(), getDuration(enchantLvl) * 20);
		if(cooldown.isOnCooldown()) return;
		else cooldown.restart();

		if(attackEvent.isDefenderPlayer()) CooldownManager.addModifierForPlayer(attackEvent.getDefenderPlayer(),
				new CooldownManager.CooldownData(Cooldown.CooldownModifier.TELEBOW, 20 * 5));

		if(attackEvent.getDefender().hasPotionEffect(PotionEffectType.SPEED))
			attackEvent.getDefender().removePotionEffect(PotionEffectType.SPEED);
		if(attackEvent.getDefender().hasPotionEffect(PotionEffectType.JUMP))
			attackEvent.getDefender().removePotionEffect(PotionEffectType.JUMP);
		Sounds.PIN_DOWN.play(attackEvent.getDefender());
		String pinMessage = "&c&lPINNED!&7 by %luckperms_prefix%%player_name%&7. Speed and Jump Boost cancelled!";
		String pinMessage2 = "&a&lITS A PIN!&7 Removed Speed and Jump Boost from %luckperms_prefix%%player_name%&7!";
		AOutput.send(attackEvent.getDefender(), PlaceholderAPI.setPlaceholders(attackEvent.getAttackerPlayer(), pinMessage));
		if(attackEvent.isDefenderPlayer()) {
			AOutput.send(attackEvent.getAttacker(), PlaceholderAPI.setPlaceholders(attackEvent.getDefenderPlayer(), pinMessage2));
		}

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.getAttackerPlayer());
		if(pitPlayer.stats != null) pitPlayer.stats.pin++;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Fully charged shots &apin &7the victim removing their &eSpeed &7and &aJump Boost &7and prevents " +
				"&dRARE! &9Telebow &7cooldown from reducing (" + getDuration(enchantLvl) + "s cooldown)"
		).getLore();
	}

	public int getDuration(int enchantLvl) {

		return Math.max(11 - enchantLvl * 2, 0);
	}
}
