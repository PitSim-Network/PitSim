package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.*;
import javafx.scene.effect.Light;
import dev.kyro.pitsim.adarkzone.abilities.TNTAbility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class PitZombieBoss extends PitBoss {

	public PitZombieBoss(Player summoner) {
		super(summoner);

		abilities(
				new TNTAbility(1.0, 1),
//				new TrueDamageAbility(4),
//				new RuptureAbility(0.3, 25, 8, 50),
//				new PoundAbility(0.3, 5),
//				new SnakeAbility(0.3, 15, 8,  Material.ICE, (byte) 0, Sounds.SNAKE_ICE),
//				new SlamAbility(0.3, 25, 25,  25),
//				new AnvilRainAbility(0.3, 40, 100, 100),
//				new HailAbility(0.3, 40, 100, 100),
//				new FirestormAbility(0.3, 40, 250, 100),
//				new LandMineAbility(0.3, 4, 40, 100, 500),
//				new DisorderAbility(0.3, 5),
//				new ComboAbility(5, 20),
//				new CollapseAbility(0.3, 5, 5, 40, 10)
//				new CageAbility(0.3, 80, 5)
//				new LightningAbility(5, 1)
//				new ChargeAbility(0.3)
				new PullAbility(0.3, 20, 1, new MaterialData(Material.DIRT, (byte) 0))
		);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.ZOMBIE;
	}

	@Override
	public String getRawDisplayName() {
		return "Zombie Boss";
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
		return 100;
	}

	@Override
	public double getMeleeDamage() {
		return 10;
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
}
