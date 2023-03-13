package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.BossManager;
import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.*;
import dev.kyro.pitsim.misc.BlockData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class PitBlazeBoss extends PitBoss {

	public PitBlazeBoss(Player summoner) {
		super(summoner);

		abilities(
				new BlazeMinionAbility(1, 1, 5),
				new FirestormAbility(2, 40, 200, 4),
				new PopupAbility(2, new BlockData(Material.FIRE, (byte) 0), 4, 40, 150),
				new ComboAbility(20, 12),
				new PullAbility(2, 20, 1, new MaterialData(Material.GLOWSTONE, (byte) 0))
		);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.BLAZE;
	}

	@Override
	public String getRawDisplayName() {
		return "Blaze Boss";
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
		return -1;
	}
}
