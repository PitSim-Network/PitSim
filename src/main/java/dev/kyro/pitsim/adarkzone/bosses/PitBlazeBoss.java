package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.DarkzoneBalancing;
import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.ComboAbility;
import dev.kyro.pitsim.adarkzone.abilities.PopupAbility;
import dev.kyro.pitsim.adarkzone.abilities.PullAbility;
import dev.kyro.pitsim.adarkzone.abilities.WorldBorderAbility;
import dev.kyro.pitsim.adarkzone.abilities.blockrain.FirestormAbility;
import dev.kyro.pitsim.adarkzone.abilities.minion.GenericMinionAbility;
import dev.kyro.pitsim.misc.BlockData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class PitBlazeBoss extends PitBoss {

	public PitBlazeBoss(Player summoner) {
		super(summoner);

		abilities(
				new GenericMinionAbility(1, SubLevelType.BLAZE, 2, 5),
				new FirestormAbility(2, 25, 200, getDamage() * 0.75),
				new PopupAbility(2, new BlockData(Material.FIRE, (byte) 0), getDamage(), 40, 150),
				new PullAbility(2, 20, new MaterialData(Material.GLOWSTONE, (byte) 0)),

				new ComboAbility(15, 12, 0),
				new WorldBorderAbility(),
				null
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
		return -1;
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
