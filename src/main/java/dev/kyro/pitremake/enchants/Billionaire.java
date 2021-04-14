package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;
import org.bukkit.Sound;

import java.util.List;

public class Billionaire extends PitEnchant {

	public Billionaire() {
		super("billionaire", true, ApplyType.SWORDS,
				"bill", "billionaire");
	}

	@Override
	public List<String> getDescription() {

		return new ALoreBuilder("ajewjfoijaweiofj").getLore();
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		damageEvent.multiplier.add(2D);

		ASound.play(damageEvent.attacker, Sound.ORB_PICKUP, 1, 0.73F);

		return damageEvent;
	}
}
