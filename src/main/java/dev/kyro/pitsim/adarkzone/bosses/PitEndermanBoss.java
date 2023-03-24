package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.*;
import dev.kyro.pitsim.adarkzone.abilities.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PitEndermanBoss extends PitBoss {

	public PitEndermanBoss(Player summoner) {
		super(summoner);

		abilities(
				new GenericMinionAbility(1, SubLevelType.ENDERMAN, 3, 15),
				new WorldBorderAbility(),
				new DisorderAbility(1, 2),
				new ReincarnationAbility(5),
				new RuptureAbility(1, 30, getDamage(), 40),
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
	public double getMaxHealth() {
		return DarkzoneBalancing.getAttribute(getSubLevelType(), DarkzoneBalancing.Attribute.BOSS_HEALTH);
	}

	@Override
	public double getDamage() {
		return DarkzoneBalancing.getAttribute(getSubLevelType(), DarkzoneBalancing.Attribute.BOSS_DAMAGE);
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
	public int getSpeedLevel() {
		return 8;
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
