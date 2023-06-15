package net.pitsim.spigot.enchants.tainted.common;

import net.pitsim.spigot.adarkzone.PitMob;
import net.pitsim.spigot.adarkzone.mobs.PitIronGolem;
import net.pitsim.spigot.adarkzone.mobs.PitWolf;
import net.pitsim.spigot.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.spigot.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Antagonist extends BasicDarkzoneEnchant {
	public static Antagonist INSTANCE;

	public Antagonist() {
		super("Antagonist", false, ApplyType.SCYTHES,
				"antagonist");
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
		return Arrays.asList(PitWolf.class, PitIronGolem.class);
	}
}
