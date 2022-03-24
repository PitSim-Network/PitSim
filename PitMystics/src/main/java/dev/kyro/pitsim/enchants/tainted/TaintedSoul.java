package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;

import java.util.List;

public class TaintedSoul extends PitEnchant {

	public TaintedSoul() {
		super("Tainted Soul", false, ApplyType.TAINTED,
				"taintedsoul", "soul");
		isUncommonEnchant = true;
		tainted = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;
		if(!fakeHits && attackEvent.fakeHit) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(attackEvent.defender.getHealth() > 1) {
			attackEvent.defender.setHealth(attackEvent.defender.getHealth() / 2.0);
		} else {
			attackEvent.veryTrueDamage = 1000;
		}
		attackEvent.defender.getWorld().strikeLightningEffect(attackEvent.defender.getLocation());
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Enjoy").getLore();
	}
}
