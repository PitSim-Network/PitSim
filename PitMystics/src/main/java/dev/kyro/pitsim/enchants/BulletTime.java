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
		damageEvent.arrow.getWorld().playEffect(damageEvent.arrow.getLocation(), Effect.EXPLOSION, 0, 30);

		damageEvent.defender.setHealth(Math.min(damageEvent.defender.getHealth() + getHealing(enchantLvl), damageEvent.defender.getMaxHealth()));

		damageEvent.event.setCancelled(true);
		damageEvent.arrow.remove();


		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		if(enchantLvl == 1) {
			return new ALoreBuilder("&7Blocking destroys arrows that hit", "&7you").getLore();
		} else {
			return new ALoreBuilder("&7Blocking destroys arrows that hit", "&7you. Destroying arrows this way",
					"&7heals &c" + (double) getHealing(enchantLvl)/2 + "&c\u2764").getLore();
		}
	}
	
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
