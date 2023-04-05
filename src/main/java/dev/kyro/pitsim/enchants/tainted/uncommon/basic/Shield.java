package dev.kyro.pitsim.enchants.tainted.uncommon.basic;

import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.mobs.PitBlaze;
import dev.kyro.pitsim.adarkzone.mobs.PitWitherSkeleton;
import dev.kyro.pitsim.adarkzone.mobs.PitZombiePigman;
import dev.kyro.pitsim.controllers.objects.BasicDarkzoneEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Shield extends BasicDarkzoneEnchant {
	public static Shield INSTANCE;

	public Shield() {
		super("Shield", false, ApplyType.CHESTPLATES,
				"shield", "shield2");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 6 + 7;
	}

	@Override
	public boolean isOffensive() {
		return false;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitBlaze.class, PitZombiePigman.class, PitWitherSkeleton.class);
	}
}
