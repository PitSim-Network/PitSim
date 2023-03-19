package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.BossManager;
import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.CollapseAbility;
import dev.kyro.pitsim.adarkzone.abilities.GenericMinionAbility;
import dev.kyro.pitsim.adarkzone.abilities.SlamAbility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PitSpiderBoss extends PitBoss {

	public PitSpiderBoss(Player summoner) {
		super(summoner);

		abilities(
				new SlamAbility(2, 40, 40, 3),
				new CollapseAbility(2, 5, 5, 20, 20),
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
	public int getMaxHealth() {
		return BossManager.getHealth(getSubLevelType());
	}

	@Override
	public double getMeleeDamage() {
		return BossManager.getDamage(getSubLevelType());
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
	public DropPool createDropPool() {
		return new DropPool();
	}

	@Override
	public int getSpeedLevel() {
		return 3;
	}
}
