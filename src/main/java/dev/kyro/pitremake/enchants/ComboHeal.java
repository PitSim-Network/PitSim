package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitremake.controllers.*;
import dev.kyro.pitremake.enums.ApplyType;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import java.util.List;

public class ComboHeal extends PitEnchant {

	public ComboHeal() {
		super("Combo: Heal", false, ApplyType.SWORDS,
				"comboheal", "ch", "combo-heal", "cheal");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(damageEvent.attacker);
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, 4)) return damageEvent;

		damageEvent.attacker.setHealth(Math.min(damageEvent.attacker.getHealth() + getEffect(enchantLvl), damageEvent.attacker.getMaxHealth()));
		EntityPlayer nmsPlayer = ((CraftPlayer) damageEvent.attacker).getHandle();
		if(nmsPlayer.getAbsorptionHearts() < 8) {
			nmsPlayer.setAbsorptionHearts(Math.min((float) (nmsPlayer.getAbsorptionHearts() + getEffect(enchantLvl)), 8));
		}

		ASound.play(damageEvent.attacker, Sound.DONKEY_HIT, 1F, 0.5F);

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Every &efourth &7strike heals",
				"&c" + getEffect(enchantLvl) + "\u2764 &7and grants &6" + getEffect(enchantLvl) + "\u2764").getLore();
	}

	public double getEffect(int enchantLvl) {

		return enchantLvl * 0.8;
	}
}
