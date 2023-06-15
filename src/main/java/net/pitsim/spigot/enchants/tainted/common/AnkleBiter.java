package net.pitsim.spigot.enchants.tainted.common;

import net.pitsim.spigot.adarkzone.PitMob;
import net.pitsim.spigot.adarkzone.mobs.PitEnderman;
import net.pitsim.spigot.adarkzone.mobs.PitWitherSkeleton;
import net.pitsim.spigot.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.spigot.enums.ApplyType;

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
