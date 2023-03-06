package dev.kyro.pitsim.adarkzone.progression.skillbranches;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.adarkzone.progression.ProgressionManager;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class DamageBranch extends SkillBranch {
	public static DamageBranch INSTANCE;

	public DamageBranch() {
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		boolean hasFirstPath = ProgressionManager.isUnlocked(attackEvent.getAttackerPitPlayer(), this, MajorUnlockPosition.FIRST_PATH);
		if(hasFirstPath && Misc.isEntity(attackEvent.getDefender(), PitEntityType.PIT_MOB))
			attackEvent.getAttackerPitPlayer().heal(getMobKillHealing());

		boolean hasLast = ProgressionManager.isUnlocked(attackEvent.getAttackerPitPlayer(), this, MajorUnlockPosition.LAST);
		if(hasLast && Misc.isEntity(attackEvent.getDefender(), PitEntityType.PIT_MOB, PitEntityType.PIT_BOSS))
			attackEvent.increasePercent += getMobBossDamageIncrease();

		if(Misc.isEntity(attackEvent.getDefender(), PitEntityType.PIT_MOB))
			attackEvent.multipliers.addAll(ProgressionManager.getUnlockedEffectAsList(
					attackEvent.getAttackerPitPlayer(), this, PathPosition.FIRST_PATH, "damage"));

		if(Misc.isEntity(attackEvent.getDefender(), PitEntityType.PIT_BOSS))
			attackEvent.multipliers.addAll(ProgressionManager.getUnlockedEffectAsList(
					attackEvent.getAttackerPitPlayer(), this, PathPosition.SECOND_PATH, "damage"));
	}

	public static int getMobBossDamageIncrease() {
		return 100;
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
	public ItemStack getBaseStack() {
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
				return "&cUnlock: Bosses";
			}

			@Override
			public String getRefName() {
				return "unlock-bosses";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.SKULL_ITEM, 1, 1)
						.setLore(new ALoreBuilder(
								"&7Unlocks the ability to summon",
								"&7bosses in the &5Darkzone"
						))
						.getItemStack();
			}

			@Override
			public int getCost() {
				return 10;
			}
		};
	}

	@Override
	public MajorProgressionUnlock createLastUnlock() {
		return new MajorProgressionUnlock() {
			@Override
			public String getDisplayName() {
				return "Damage vs Mobs and Bosses";
			}

			@Override
			public String getRefName() {
				return "mob-boss-damage";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.BLAZE_POWDER)
						.setLore(new ALoreBuilder(
								"&7Deal &c+" + getMobBossDamageIncrease() + "% &7damage vs mobs",
								"&7and bosses"
						))
						.getItemStack();
			}

			@Override
			public int getCost() {
				return 10;
			}
		};
	}

	@Override
	public MajorProgressionUnlock createFirstPathUnlock() {
		return new MajorProgressionUnlock() {
			@Override
			public String getDisplayName() {
				return "Heal on Mob Kill";
			}

			@Override
			public String getRefName() {
				return "mob-kill-heal";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.GOLDEN_APPLE)
						.setLore(new ALoreBuilder(
								"&7Heal &c" + Misc.getHearts(getMobKillHealing()) + " &7on mob kill"
						))
						.getItemStack();
			}

			@Override
			public int getCost() {
				return 10;
			}
		};
	}

	@Override
	public MajorProgressionUnlock createSecondPathUnlock() {
		return new MajorProgressionUnlock() {
			@Override
			public String getDisplayName() {
				return "Cheaper Boss Spawning";
			}

			@Override
			public String getRefName() {
				return "cheaper-boss-spawning";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.PUMPKIN)
						.setLore(new ALoreBuilder(
								"&6" + getSecondItemSpawnChance() + "% &7chance for mob drops to",
								"&7count as two when used to",
								"&7spawn bosses"
						))
						.getItemStack();
			}

			@Override
			public int getCost() {
				return 10;
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
			public int getCost(int level) {
				return level;
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("damage", "&c+%value%% &7damage vs mobs",
						2, 2, 2, 3, 3, 3));
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
			public int getCost(int level) {
				return level;
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("damage", "&c+%value%% &7damage vs bosses",
						1, 2, 3, 4, 5, 6));
			}
		};
	}
}
