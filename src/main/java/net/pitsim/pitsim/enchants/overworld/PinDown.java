package net.pitsim.pitsim.enchants.overworld;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.controllers.Cooldown;
import net.pitsim.pitsim.controllers.CooldownManager;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import net.pitsim.pitsim.misc.Sounds;
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
		pitPlayer.stats.pin++;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Fully charged shots &apin &7the victim removing their &eSpeed &7and &aJump Boost &7and prevents " +
				"&dRARE! &9Telebow &7cooldown from reducing (" + getDuration(enchantLvl) + "s cooldown)"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that removes " +
				"&eSpeed &7and &aJump Boost &7from your opponents when you shoot them. It also temporarily stops their " +
				Telebow.INSTANCE.getDisplayName(false, true) + "&7 cooldown from " +
				"counting down";
	}

	public int getDuration(int enchantLvl) {

		return Math.max(11 - enchantLvl * 2, 0);
	}
}
