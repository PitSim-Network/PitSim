package net.pitsim.pitsim.enchants.tainted.common;

import net.pitsim.pitsim.adarkzone.PitMob;
import net.pitsim.pitsim.adarkzone.mobs.PitSkeleton;
import net.pitsim.pitsim.adarkzone.mobs.PitWitherSkeleton;
import net.pitsim.pitsim.adarkzone.mobs.PitZombie;
import net.pitsim.pitsim.adarkzone.mobs.PitZombiePigman;
import net.pitsim.pitsim.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Embalm extends BasicDarkzoneEnchant {
	public static Embalm INSTANCE;

	public Embalm() {
		super("Embalm", false, ApplyType.CHESTPLATES,
				"embalm");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 4 + 4;
	}

	@Override
	public boolean isOffensive() {
		return false;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitZombie.class, PitSkeleton.class, PitZombiePigman.class, PitWitherSkeleton.class);
	}
}
