package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.BossManager;
import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PitEndermanBoss extends PitBoss {

	public PitEndermanBoss(Player summoner) {
		super(summoner);

		abilities(
				new GenericMinionAbility(1, SubLevelType.ENDERMAN, 3, 15),
				new DisorderAbility(1, 10),
				new ReincarnationAbility(5, 1000 * 60 * 3),
				new RuptureAbility(1, 30, 5, 40),
				new TeleportAbility(5, 17)
		);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.ENDERMAN;
	}

	@Override
	public String getRawDisplayName() {
		return "Enderman Boss";
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
		return 5;
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
		return 8;
	}
}
