package net.pitsim.spigot.darkzone.bosses;

import net.pitsim.spigot.darkzone.DarkzoneBalancing;
import net.pitsim.spigot.darkzone.DropPool;
import net.pitsim.spigot.darkzone.PitBoss;
import net.pitsim.spigot.darkzone.SubLevelType;
import net.pitsim.spigot.darkzone.abilities.*;
import net.pitsim.spigot.darkzone.abilities.minion.DefensiveMinionAbility;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PitWitherSkeletonBoss extends PitBoss {

	public PitWitherSkeletonBoss(Player summoner) {
		super(summoner);

		abilities(
				new DefensiveMinionAbility(SubLevelType.WITHER_SKELETON, 2, 8, 6 * 20),
				new CageAbility(3, 80, 5),
				new SlamAbility(2, 50, getDamage() * 3),
				new ChargeAbility(5),
				new SnakeAbility(3, 25, getDamage() * 0.75, Material.BEDROCK, (byte) 0, Sounds.WITHER_SNAKE),

				new WorldBorderAbility(),
				null
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
		return "WitherSkeleton";
	}

	@Override
	public double getMaxHealth() {
		return DarkzoneBalancing.getAttributeAsInt(getSubLevelType(), DarkzoneBalancing.Attribute.BOSS_HEALTH);
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
		return DarkzoneBalancing.getAttributeAsRandomInt(getSubLevelType(), DarkzoneBalancing.Attribute.BOSS_SOULS);
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool();
	}
}
