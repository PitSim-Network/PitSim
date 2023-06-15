package net.pitsim.pitsim.enchants.overworld;

import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.pitsim.controllers.Cooldown;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import net.pitsim.pitsim.misc.Sounds;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Crush extends PitEnchant {

	public Crush() {
		super("Crush", false, ApplyType.MELEE,
				"crush");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(attackEvent.getAttackerPlayer(), 2 * 20);
		if(cooldown.isOnCooldown()) return;
		else cooldown.restart();

		Misc.applyPotionEffect(attackEvent.getDefender(), PotionEffectType.WEAKNESS, getDuration(enchantLvl), enchantLvl + 3, true, false);
		Sounds.CRUSH.play(attackEvent.getAttacker());
		Sounds.CRUSH.play(attackEvent.getDefender());
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Strikes apply &cWeakness " + AUtil.toRoman(enchantLvl + 4) + " &7(lasts " +
				(getDuration(enchantLvl) / 20D) + "s, 2s cooldown)"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that briefly " +
				"applies a high level of &cWeakness &7to your opponent on strike";
	}

	public int getDuration(int enchantLvl) {

		return enchantLvl * 6 + 2;
	}
}
