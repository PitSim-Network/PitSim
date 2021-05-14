package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;

import java.util.List;

public class BulletTime extends PitEnchant {

	public BulletTime() {
		super("Bullet Time", false, ApplyType.SWORDS,
				"bullettime", "bullet-time", "bullet", "bt");
	}

	@EventHandler
	public void cancel(AttackEvent.Pre attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.arrow == null) return;

		if(!(attackEvent.defender.isBlocking())) return;

		attackEvent.defender.getWorld().playSound(attackEvent.defender.getLocation(), Sound.FIZZ, 1f, 1.5f);
		attackEvent.arrow.getWorld().playEffect(attackEvent.arrow.getLocation(), Effect.EXPLOSION, 0, 30);

		attackEvent.defender.setHealth(Math.min(attackEvent.defender.getHealth() + getHealing(enchantLvl), attackEvent.defender.getMaxHealth()));

		attackEvent.setCancelled(true);
		attackEvent.arrow.remove();
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
