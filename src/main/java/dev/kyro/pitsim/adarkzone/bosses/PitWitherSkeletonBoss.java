package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.*;
import dev.kyro.pitsim.adarkzone.abilities.*;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PitWitherSkeletonBoss extends PitBoss {

	public PitWitherSkeletonBoss(Player summoner) {
		super(summoner);

		abilities(
				new DefensiveMinionAbility(SubLevelType.WITHER_SKELETON, 2, 8, 5000),
				new CageAbility(3, 60, 5),
				new SlamAbility(2, 40, 50, 8),
				new ChargeAbility(3),
				new SnakeAbility(3, 25, 4, Material.BEDROCK, (byte) 0, Sounds.WITHER_SNAKE)
		);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.WITHER_SKELETON;
	}

	@Override
	public String getRawDisplayName() {
		return "Wither Boss";
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
		return 5;
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
