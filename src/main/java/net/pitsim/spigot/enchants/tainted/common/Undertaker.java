package net.pitsim.spigot.enchants.tainted.common;

import net.pitsim.spigot.darkzone.PitMob;
import net.pitsim.spigot.darkzone.mobs.PitSkeleton;
import net.pitsim.spigot.darkzone.mobs.PitWitherSkeleton;
import net.pitsim.spigot.darkzone.mobs.PitZombie;
import net.pitsim.spigot.darkzone.mobs.PitZombiePigman;
import net.pitsim.spigot.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.spigot.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Undertaker extends BasicDarkzoneEnchant {
	public static Undertaker INSTANCE;

	public Undertaker() {
		super("Undertaker", false, ApplyType.SCYTHES,
				"undertaker", "undertake", "under");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 9 + 8;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitZombie.class, PitSkeleton.class, PitZombiePigman.class, PitWitherSkeleton.class);
	}
}
