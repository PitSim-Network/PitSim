package dev.kyro.pitsim.enchants.tainted.uncommon.basic;

import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.mobs.PitCreeper;
import dev.kyro.pitsim.adarkzone.mobs.PitEnderman;
import dev.kyro.pitsim.adarkzone.mobs.PitIronGolem;
import dev.kyro.pitsim.controllers.objects.BasicDarkzoneEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Barricade extends BasicDarkzoneEnchant {
	public static Barricade INSTANCE;

	public Barricade() {
		super("Barricade", false, ApplyType.CHESTPLATES,
				"barricade", "shield3");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 5 + 6;
	}

	@Override
	public boolean isOffensive() {
		return false;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitCreeper.class, PitIronGolem.class, PitEnderman.class);
	}
}
