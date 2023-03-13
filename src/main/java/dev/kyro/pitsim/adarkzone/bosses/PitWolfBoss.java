package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.BossManager;
import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.CageAbility;
import dev.kyro.pitsim.adarkzone.abilities.ChargeAbility;
import dev.kyro.pitsim.adarkzone.abilities.WolfMinionAbility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PitWolfBoss extends PitBoss {

	public PitWolfBoss(Player summoner) {
		super(summoner);

		abilities(
				new ChargeAbility(2),
				new CageAbility(1, 40, 5),
				new WolfMinionAbility(3, 5, 50)
		);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.WOLF;
	}

	@Override
	public String getRawDisplayName() {
		return "Wolf Boss";
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
		return (BossManager.getHealth(getSubLevelType()) / 2);
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
		return 4;
	}
}
