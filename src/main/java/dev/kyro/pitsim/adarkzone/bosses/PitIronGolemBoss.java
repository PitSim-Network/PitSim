package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PitIronGolemBoss extends PitBoss {

	public PitIronGolemBoss(Player summoner) {
		super(summoner);

		abilities(
				new GolemMinionAbility(1, 1, 1),
				new AnvilRainAbility(2, 40, 250, 10),
				new CollapseAbility(2, 5, 5, 10, 20),
				new LightningAbility(3, 1, 0.05)
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
	public int getMaxHealth() {
		return 900;
	}

	@Override
	public double getMeleeDamage() {
		return 90;
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
	public DropPool createDropPool() {
		return new DropPool();
	}

	@Override
	public int getSpeedLevel() {
		return 0;
	}
}
