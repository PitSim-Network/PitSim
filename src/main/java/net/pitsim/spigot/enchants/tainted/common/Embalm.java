package net.pitsim.spigot.enchants.tainted.common;

import net.pitsim.spigot.adarkzone.PitMob;
import net.pitsim.spigot.adarkzone.mobs.PitSkeleton;
import net.pitsim.spigot.adarkzone.mobs.PitWitherSkeleton;
import net.pitsim.spigot.adarkzone.mobs.PitZombie;
import net.pitsim.spigot.adarkzone.mobs.PitZombiePigman;
import net.pitsim.spigot.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.spigot.enums.ApplyType;

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
