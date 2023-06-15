package net.pitsim.spigot.adarkzone.bosses;

import net.pitsim.spigot.adarkzone.DarkzoneBalancing;
import net.pitsim.spigot.adarkzone.DropPool;
import net.pitsim.spigot.adarkzone.PitBoss;
import net.pitsim.spigot.adarkzone.SubLevelType;
import net.pitsim.spigot.adarkzone.abilities.CageAbility;
import net.pitsim.spigot.adarkzone.abilities.ChargeAbility;
import net.pitsim.spigot.adarkzone.abilities.minion.WolfMinionAbility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PitWolfBoss extends PitBoss {

	public PitWolfBoss(Player summoner) {
		super(summoner);

		abilities(
				new ChargeAbility(5),
				new CageAbility(1, 60, 5),
				new WolfMinionAbility(2, 5, 50),
				null
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
		return "Wolf";
	}

	@Override
	public double getMaxHealth() {
		return DarkzoneBalancing.getAttributeAsInt(getSubLevelType(), DarkzoneBalancing.Attribute.BOSS_HEALTH) * 0.75;
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
		return 4;
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
