package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;

import java.util.List;

public class BulletTime extends PitEnchant {

	public BulletTime() {
		super("Bullet Time", false, ApplyType.SWORDS,
				"bullettime", "bullet-time", "bullet", "bt");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void cancel(AttackEvent.Pre attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0 || attackEvent.arrow == null || !(attackEvent.defender.isBlocking())) return;

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
					"&7heals &c" + Misc.getHearts(getHealing(enchantLvl))).getLore();
		}
	}
	
	public int getHealing(int enchantLvl) {

		return (int) (Math.pow(enchantLvl, 0.75) * 3 - 3);
	}
}
