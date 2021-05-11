package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;

import java.util.List;

public class GoldAndBoosted extends PitEnchant {

	public GoldAndBoosted() {
		super("Gold and Boosted", false, ApplyType.SWORDS,
				"gab", "gold-and-boosted", "goldandboosted");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		EntityLiving nmsPlayer = ((CraftLivingEntity) damageEvent.attacker).getHandle();
		if(nmsPlayer.getAbsorptionHearts() == 0) return damageEvent;
		damageEvent.increasePercent += getDamage(enchantLvl) / 100D;

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getDamage(enchantLvl) + "% &7damage when you have", "&7absorption hearts").getLore();
	}

	public int getDamage(int enchantLvl) {

		return (int) (Math.floor(Math.pow(enchantLvl, 1.15) * 4) + 1);
	}
}
