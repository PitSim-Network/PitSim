package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.mobs.PitIronGolem;
import dev.kyro.pitsim.adarkzone.mobs.PitWolf;
import dev.kyro.pitsim.controllers.objects.BasicDarkzoneEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Territorial extends BasicDarkzoneEnchant {
	public static Territorial INSTANCE;

	public Territorial() {
		super("Territorial", false, ApplyType.CHESTPLATES,
				"territorial", "territory");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getStatPercent(int enchantLvl) {
		return enchantLvl * 5 + 3;
	}

	@Override
	public boolean isOffensive() {
		return false;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitWolf.class, PitIronGolem.class);
	}
}
