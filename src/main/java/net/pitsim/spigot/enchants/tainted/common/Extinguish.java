package net.pitsim.spigot.enchants.tainted.common;

import net.pitsim.spigot.darkzone.PitMob;
import net.pitsim.spigot.darkzone.mobs.PitBlaze;
import net.pitsim.spigot.darkzone.mobs.PitWitherSkeleton;
import net.pitsim.spigot.darkzone.mobs.PitZombiePigman;
import net.pitsim.spigot.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.spigot.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Extinguish extends BasicDarkzoneEnchant {
	public static Extinguish INSTANCE;

	public Extinguish() {
		super("Extinguish", false, ApplyType.SCYTHES,
				"extinguish");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 10 + 8;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitBlaze.class, PitZombiePigman.class, PitWitherSkeleton.class);
	}
}
