package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import org.bukkit.Effect;
import org.bukkit.Sound;

import java.util.List;

public class BulletTime extends PitEnchant {

	public BulletTime() {
		super("Bullet Time", false, ApplyType.SWORDS,
				"bullettime", "bullet-time", "bullet", "bt");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = EnchantManager.getEnchantLevel(damageEvent.defender, this);
		if(enchantLvl == 0) return damageEvent;

		if(damageEvent.arrow == null) return damageEvent;

		if(!(damageEvent.defender.isBlocking())) return damageEvent;

		damageEvent.defender.getWorld().playSound(damageEvent.defender.getLocation(), Sound.FIZZ, 1f, 1.5f);
		damageEvent.arrow.getWorld().playEffect(damageEvent.arrow.getLocation(), Effect.SMOKE, 0, 30);

		damageEvent.event.setCancelled(true);
		damageEvent.arrow.remove();


		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getHealing(enchantLvl) + "% &7melee damage").getLore();
	}

	//	TODO: Sharp damage calculation
	
	public int getHealing(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 0;
			case 2:
				return 2;
			case 3:
				return 3;

		}

		return 0;
	}
}
