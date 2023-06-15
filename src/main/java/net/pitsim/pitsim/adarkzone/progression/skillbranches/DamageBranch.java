package net.pitsim.pitsim.adarkzone.progression.skillbranches;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.adarkzone.DarkzoneManager;
import net.pitsim.pitsim.adarkzone.SubLevel;
import net.pitsim.pitsim.adarkzone.progression.ProgressionManager;
import net.pitsim.pitsim.adarkzone.progression.SkillBranch;
import net.pitsim.pitsim.controllers.MapManager;
import net.pitsim.pitsim.enums.PitEntityType;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class DamageBranch extends SkillBranch {
	public static DamageBranch INSTANCE;

	public DamageBranch() {
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		boolean hasFirstPath = ProgressionManager.isUnlocked(killEvent.getKillerPitPlayer(), this, MajorUnlockPosition.FIRST_PATH);
		if(hasFirstPath && Misc.isEntity(killEvent.getDead(), PitEntityType.PIT_MOB))
			killEvent.getKillerPitPlayer().heal(getMobKillHealing());
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		boolean hasLast = ProgressionManager.isUnlocked(attackEvent.getAttackerPitPlayer(), this, MajorUnlockPosition.LAST);
		if(hasLast && Misc.isEntity(attackEvent.getDefender(), PitEntityType.PIT_MOB, PitEntityType.PIT_BOSS))
			attackEvent.multipliers.add(getMobBossDamageIncrease());

		if(Misc.isEntity(attackEvent.getDefender(), PitEntityType.PIT_MOB))
			attackEvent.multipliers.addAll(ProgressionManager.getUnlockedEffectAsList(
					attackEvent.getAttackerPitPlayer(), this, PathPosition.FIRST_PATH, "damage"));

		if(Misc.isEntity(attackEvent.getDefender(), PitEntityType.PIT_BOSS))
			attackEvent.multipliers.addAll(ProgressionManager.getUnlockedEffectAsList(
					attackEvent.getAttackerPitPlayer(), this, PathPosition.SECOND_PATH, "damage"));

		if(Misc.isEntity(attackEvent.getDefender(), PitEntityType.PIT_BOSS, PitEntityType.PIT_MOB) && MapManager.inDarkzone(attackEvent.getAttacker()) &&
				ProgressionManager.isUnlocked(attackEvent.getAttackerPitPlayer(), this, SkillBranch.MajorUnlockPosition.FIRST)) {
			double distance = 1;
			SubLevel subLevel = null;
			for(SubLevel testLevel : DarkzoneManager.subLevels) {
				distance = testLevel.getMiddle().distance(attackEvent.getAttacker().getLocation());
				if(distance > testLevel.spawnRadius) continue;
				subLevel = testLevel;
				break;
			}
			if(subLevel != null) {
				double targetDistance = attackEvent.getDefender().getLocation().distance(attackEvent.getAttacker().getLocation());
				distance = Math.max(distance, targetDistance);

				double multiplier = Math.max(1 + ((subLevel.spawnRadius - distance) / subLevel.spawnRadius) * (getMaxNearSpawnerMultiplier() - 1), 1);
				attackEvent.multipliers.add(multiplier);
			}
		}
	}

	public static double getMaxNearSpawnerMultiplier() {
		return 1.4;
	}

	public static double getMobBossDamageIncrease() {
		return 1.3;
	}

	public static double getMobKillHealing() {
		return 2;
	}

	public static int getSecondItemSpawnChance() {
		return 50;
	}

	@Override
	public String getDisplayName() {
		return "&cStrength";
	}

	@Override
	public String getRefName() {
		return "damage";
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.DIAMOND_SWORD)
				.setLore(new ALoreBuilder(
						"&7Upgrade your damage"
				))
				.getItemStack();
	}

	@Override
	public MajorProgressionUnlock createFirstUnlock() {
		return new MajorProgressionUnlock() {
			@Override
			public String getDisplayName() {
				return "&cDamage Near Spawners";
			}

			@Override
			public String getRefName() {
				return "damage-near-spawners";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
				DecimalFormat decimalFormat = new DecimalFormat("0.#");
				return new AItemStackBuilder(Material.MOB_SPAWNER)
						.setLore(new ALoreBuilder(
								"&7Increased damage when near",
								"&7the spawners in caves (up",
								"&7to &c" + decimalFormat.format(getMaxNearSpawnerMultiplier()) + "x&7)"
						))
						.getItemStack();
			}
		};
	}

	@Override
	public MajorProgressionUnlock createLastUnlock() {
		return new MajorProgressionUnlock() {
			@Override
			public String getDisplayName() {
				return "&cDamage vs Mobs and Bosses";
			}

			@Override
			public String getRefName() {
				return "mob-boss-damage";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.BLAZE_POWDER)
						.setLore(new ALoreBuilder(
								"&7Deal &c" + getMobBossDamageIncrease() + "x &7damage vs mobs",
								"&7and bosses"
						))
						.getItemStack();
			}
		};
	}

	@Override
	public MajorProgressionUnlock createFirstPathUnlock() {
		return new MajorProgressionUnlock() {
			@Override
			public String getDisplayName() {
				return "&cHeal on Mob Kill";
			}

			@Override
			public String getRefName() {
				return "mob-kill-heal";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.GOLDEN_APPLE)
						.setLore(new ALoreBuilder(
								"&7Heal &c" + Misc.getHearts(getMobKillHealing()) + " &7on mob kill"
						))
						.getItemStack();
			}
		};
	}

	@Override
	public MajorProgressionUnlock createSecondPathUnlock() {
		return new MajorProgressionUnlock() {
			@Override
			public String getDisplayName() {
				return "&cCheaper Boss Spawning";
			}

			@Override
			public String getRefName() {
				return "cheaper-boss-spawning";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.PUMPKIN)
						.setLore(new ALoreBuilder(
								"&6" + getSecondItemSpawnChance() + "% &7chance for mob drops to",
								"&7count as two when used to",
								"&7spawn bosses"
						))
						.getItemStack();
			}
		};
	}

	@Override
	public Path createFirstPath() {
		return new Path() {
			@Override
			public String getDisplayName() {
				return "&cDamage vs Mobs";
			}

			@Override
			public String getRefName() {
				return "mob-damage";
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("damage", "&c%value%x &7damage vs mobs",
						1.30, 1.30, 1.30, 1.30, 1.30, 1.30));
			}
		};
	}

	@Override
	public Path createSecondPath() {
		return new Path() {
			@Override
			public String getDisplayName() {
				return "&cDamage vs Bosses";
			}

			@Override
			public String getRefName() {
				return "boss-damage";
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("damage", "&c%value%x &7damage vs bosses",
						1.30, 1.30, 1.30, 1.30, 1.30, 1.30));
			}
		};
	}
}
