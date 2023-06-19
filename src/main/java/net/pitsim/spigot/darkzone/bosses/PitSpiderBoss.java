package net.pitsim.spigot.darkzone.bosses;

import net.pitsim.spigot.darkzone.DarkzoneBalancing;
import net.pitsim.spigot.darkzone.DropPool;
import net.pitsim.spigot.darkzone.PitBoss;
import net.pitsim.spigot.darkzone.SubLevelType;
import net.pitsim.spigot.darkzone.abilities.CollapseAbility;
import net.pitsim.spigot.darkzone.abilities.SlamAbility;
import net.pitsim.spigot.darkzone.abilities.minion.GenericMinionAbility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PitSpiderBoss extends PitBoss {

	public PitSpiderBoss(Player summoner) {
		super(summoner);

		abilities(
				new SlamAbility(2, 20, getDamage() * 3),
				new CollapseAbility(2, 5, 5, 20, getDamage() * 8),
				new GenericMinionAbility(1, SubLevelType.SPIDER, 2, 10,  5),
				null
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
		return "Spider";
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
		return DarkzoneBalancing.getAttributeAsRandomInt(getSubLevelType(), DarkzoneBalancing.Attribute.BOSS_SOULS);
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool();
	}
}
