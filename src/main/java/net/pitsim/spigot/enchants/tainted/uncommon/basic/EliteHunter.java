package net.pitsim.spigot.enchants.tainted.uncommon.basic;

import net.pitsim.spigot.adarkzone.PitMob;
import net.pitsim.spigot.adarkzone.mobs.PitBlaze;
import net.pitsim.spigot.adarkzone.mobs.PitWitherSkeleton;
import net.pitsim.spigot.adarkzone.mobs.PitZombiePigman;
import net.pitsim.spigot.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.spigot.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class EliteHunter extends BasicDarkzoneEnchant {
	public static EliteHunter INSTANCE;

	public EliteHunter() {
		super("Elite Hunter", false, ApplyType.SCYTHES,
				"elitehunter", "elite", "hunter2", "hunt2");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 11 + 12;
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
