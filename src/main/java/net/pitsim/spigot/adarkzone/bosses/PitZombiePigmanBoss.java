package net.pitsim.spigot.adarkzone.bosses;

import net.pitsim.spigot.adarkzone.DarkzoneBalancing;
import net.pitsim.spigot.adarkzone.DropPool;
import net.pitsim.spigot.adarkzone.PitBoss;
import net.pitsim.spigot.adarkzone.SubLevelType;
import net.pitsim.spigot.adarkzone.abilities.*;
import net.pitsim.spigot.adarkzone.abilities.minion.GenericMinionAbility;
import net.pitsim.spigot.misc.BlockData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PitZombiePigmanBoss extends PitBoss {

	public PitZombiePigmanBoss(Player summoner) {
		super(summoner);

		abilities(
				new GenericMinionAbility(1, SubLevelType.ZOMBIE_PIGMAN, 3, 30),
				new PoundAbility(1, 10),
				new RuptureAbility(1, 25, getDamage() * 0.5),
				new PopupAbility(1, new BlockData(Material.FIRE, (byte) 0), getDamage(), 40, 150),

				new LightningAbility(4, 2, 0.05),
				new WorldBorderAbility(),
				null
		);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.ZOMBIE_PIGMAN;
	}

	@Override
	public String getRawDisplayName() {
		return "Pigman Boss";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.RED;
	}

	@Override
	public String getSkinName() {
		return "Pigman";
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
