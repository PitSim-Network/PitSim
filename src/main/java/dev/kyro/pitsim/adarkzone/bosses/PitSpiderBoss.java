package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.*;
import dev.kyro.pitsim.adarkzone.abilities.CollapseAbility;
import dev.kyro.pitsim.adarkzone.abilities.minion.GenericMinionAbility;
import dev.kyro.pitsim.adarkzone.abilities.SlamAbility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PitSpiderBoss extends PitBoss {

	public PitSpiderBoss(Player summoner) {
		super(summoner);

		abilities(
				new SlamAbility(2, 40, 40, getDamage()),
				new CollapseAbility(2, 5, 5, 20, getDamage()),
				new GenericMinionAbility(1, SubLevelType.SPIDER, 2, 10,  5)
		);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.SPIDER;
	}

	@Override
	public String getRawDisplayName() {
		return "Spider Boss";
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
		return 3;
	}

	@Override
	public double getReachRanged() {
		return 0;
	}

	@Override
	public int getSpeedLevel() {
		return 3;
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
