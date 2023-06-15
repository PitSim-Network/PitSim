package net.pitsim.spigot.enchants.tainted.common;

import net.pitsim.spigot.darkzone.PitMob;
import net.pitsim.spigot.darkzone.mobs.PitBlaze;
import net.pitsim.spigot.darkzone.mobs.PitSkeleton;
import net.pitsim.spigot.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.spigot.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class WhoNeedsBows extends BasicDarkzoneEnchant {
	public static WhoNeedsBows INSTANCE;

	public WhoNeedsBows() {
		super("Who Needs Bows?", false, ApplyType.SCYTHES,
				"whoneedsbows", "whatsabow");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 9 + 11;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitSkeleton.class, PitBlaze.class);
	}
}
