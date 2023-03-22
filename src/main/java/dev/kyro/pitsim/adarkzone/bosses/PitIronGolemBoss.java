package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.*;
import dev.kyro.pitsim.adarkzone.abilities.AnvilRainAbility;
import dev.kyro.pitsim.adarkzone.abilities.CollapseAbility;
import dev.kyro.pitsim.adarkzone.abilities.ComboAbility;
import dev.kyro.pitsim.adarkzone.abilities.GenericMinionAbility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PitIronGolemBoss extends PitBoss {

	public PitIronGolemBoss(Player summoner) {
		super(summoner);

		abilities(
				new GenericMinionAbility(1, SubLevelType.IRON_GOLEM, 1, 1),
				new AnvilRainAbility(2, 40, 250, getDamage()),
				new CollapseAbility(2, 5, 5, 10, getDamage()),

				new ComboAbility(5, 20, 0)
		);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.IRON_GOLEM;
	}

	@Override
	public String getRawDisplayName() {
		return "Golem Boss";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.RED;
	}

	@Override
	public String getSkinName() {
		return "wiji1";
	}

	@Override
	public double getMaxHealth() {
		return DarkzoneBalancing.getAttribute(getSubLevelType(), DarkzoneBalancing.Attribute.BOSS_HEALTH);
	}

	@Override
	public double getDamage() {
		return DarkzoneBalancing.getAttribute(getSubLevelType(), DarkzoneBalancing.Attribute.BOSS_DAMAGE);
	}

	@Override
	public double getReach() {
		return 4;
	}

	@Override
	public double getReachRanged() {
		return 0;
	}

	@Override
	public int getSpeedLevel() {
		return 0;
	}

	@Override
	public int getDroppedSouls() {
		return DarkzoneBalancing.getAttributeAsInt(getSubLevelType(), DarkzoneBalancing.Attribute.BOSS_SOULS);
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool();
	}
}
