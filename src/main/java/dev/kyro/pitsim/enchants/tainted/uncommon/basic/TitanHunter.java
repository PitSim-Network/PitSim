package dev.kyro.pitsim.enchants.tainted.uncommon.basic;

import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.mobs.PitCreeper;
import dev.kyro.pitsim.adarkzone.mobs.PitEnderman;
import dev.kyro.pitsim.adarkzone.mobs.PitIronGolem;
import dev.kyro.pitsim.controllers.objects.BasicDarkzoneEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class TitanHunter extends BasicDarkzoneEnchant {
	public static TitanHunter INSTANCE;

	public TitanHunter() {
		super("Titan Hunter", false, ApplyType.SCYTHES,
				"titanhunter", "titan", "hunter3", "hunt3");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 11 + 15
				;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitCreeper.class, PitIronGolem.class, PitEnderman.class);
	}
}
