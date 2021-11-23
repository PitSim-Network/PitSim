package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
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
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(attackEvent.attacker, getDuration(enchantLvl) * 20);
		if(cooldown.isOnCooldown()) return; else cooldown.reset();

		if(attackEvent.attacker == attackEvent.defender) return;
		if(!attackEvent.arrow.isCritical()) return;

		if(attackEvent.defender.hasPotionEffect(PotionEffectType.SPEED)) attackEvent.defender.removePotionEffect(PotionEffectType.SPEED);
		if(attackEvent.defender.hasPotionEffect(PotionEffectType.JUMP)) attackEvent.defender.removePotionEffect(PotionEffectType.JUMP);
		Sounds.PIN_DOWN.play(attackEvent.defender);
		String pinMessage = "&c&lPINNED! &7by %luckperms_prefix%%player_name%&7. Speed and Jump Boost cancelled!";
		String pinMessage2 = "&a&lITS A PIN! &7Removed Speed and Jump Boost from %luckperms_prefix%%player_name%&7!";
		AOutput.send(attackEvent.defender, PlaceholderAPI.setPlaceholders(attackEvent.attacker, pinMessage));
		AOutput.send(attackEvent.attacker, PlaceholderAPI.setPlaceholders(attackEvent.defender, pinMessage2));

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.attacker);
		if(pitPlayer.stats != null) pitPlayer.stats.pin++;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Fully charged shots pin the victim", "&7down, removing their Speed and", "&7Jump Boost (" + getDuration(enchantLvl) + "s cd)").getLore();
	}

	public int getDuration(int enchantLvl) {

		return Math.max(8 - enchantLvl * 2, 0);
	}
}
