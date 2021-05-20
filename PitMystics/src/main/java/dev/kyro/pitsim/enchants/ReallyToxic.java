package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ReallyToxic extends PitEnchant {

	public ReallyToxic() {
		super("Really Toxic", false, ApplyType.PANTS,
				"reallytoic", "really-toxic", "toxic", "rt");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		int charge = HitCounter.getCharge(attackEvent.defender, this);
		HitCounter.setCharge(attackEvent.defender, this, ++charge);

		PitEnchant thisEnchant = this;
		new BukkitRunnable() {
			@Override
			public void run() {

				int charge = HitCounter.getCharge(attackEvent.defender, thisEnchant);
				HitCounter.setCharge(attackEvent.defender, thisEnchant, --charge);
			}
		}.runTaskLater(PitSim.INSTANCE, 200L);

		for(int i = 0; i < charge; i++) {
			attackEvent.multiplier.add(0.9);
		}
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Hits").getLore();
	}
}
