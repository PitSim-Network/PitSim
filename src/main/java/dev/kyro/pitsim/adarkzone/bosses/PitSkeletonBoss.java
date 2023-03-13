package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.BossManager;
import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.RuptureAbility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PitSkeletonBoss extends PitBoss {

	public PitSkeletonBoss(Player summoner) {
		super(summoner);

		abilities(
				new RuptureAbility(1, 15, 1, 40)
//				new SnakeAbility(2, 20, BossManager.getDamage(1, getSubLevelType()), Material.QUARTZ_BLOCK, (byte) 0, Sounds.BONE_SNAKE),
//				new HailAbility(2, 25, 100, BossManager.getDamage(2, getSubLevelType())),
//				new SkeletonMinionAbility(1, 3, 5000)
		);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.SKELETON;
	}

	@Override
	public String getRawDisplayName() {
		return "Skeleton Boss";
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
		return 1;
	}
}
