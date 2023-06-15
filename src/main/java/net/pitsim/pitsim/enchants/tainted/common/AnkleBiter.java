package net.pitsim.pitsim.enchants.tainted.common;

import net.pitsim.pitsim.adarkzone.PitMob;
import net.pitsim.pitsim.adarkzone.mobs.PitEnderman;
import net.pitsim.pitsim.adarkzone.mobs.PitWitherSkeleton;
import net.pitsim.pitsim.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class AnkleBiter extends BasicDarkzoneEnchant {
	public static AnkleBiter INSTANCE;

	public AnkleBiter() {
		super("Ankle-Biter", false, ApplyType.SCYTHES,
				"anklebiter", "ankle");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 10 + 6;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitWitherSkeleton.class, PitEnderman.class);
	}
}
