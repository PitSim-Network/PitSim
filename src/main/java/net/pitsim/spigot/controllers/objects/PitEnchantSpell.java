package net.pitsim.spigot.controllers.objects;

import net.pitsim.spigot.enums.ApplyType;

public abstract class PitEnchantSpell extends PitEnchant {
	public boolean onlyInSubLevels;

	public PitEnchantSpell(String name, String... refNames) {
		super(name, true, ApplyType.SCYTHES, refNames);
	}

	public abstract int getManaCost(int enchantLvl);
	public abstract int getCooldownTicks(int enchantLvl);

	public boolean isThisSpell(PitEnchant pitSpell) {
		return pitSpell == this;
	}
}
