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

public class DefenceBranch extends SkillBranch {
	public static DefenceBranch INSTANCE;

	public DefenceBranch() {
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		boolean hasFirstPath = ProgressionManager.isUnlocked(attackEvent.getDefenderPitPlayer(), this, MajorUnlockPosition.FIRST);
		if(hasFirstPath && Misc.isEntity(attackEvent.getAttacker(), PitEntityType.REAL_PLAYER))
			attackEvent.multipliers.add(Misc.getReductionMultiplier(getPlayerDamageDecrease()));

		if(Misc.isEntity(attackEvent.getAttacker(), PitEntityType.PIT_MOB, PitEntityType.PIT_BOSS)) {
			for(Double multiplier : ProgressionManager.getUnlockedEffectAsList(
					attackEvent.getDefenderPitPlayer(), this, PathPosition.FIRST_PATH, "defence"))
				attackEvent.multipliers.add(Misc.getReductionMultiplier(multiplier));
		}
	}

	public static int getShieldDamageReduction() {
		return 50;
	}

	public static int getReactivationReductionTicks() {
		return 40;
	}

	public static int getPlayerDamageDecrease() {
		return 50;
	}

	@Override
	public String getDisplayName() {
		return "&9Defence";
	}

	@Override
	public String getRefName() {
		return "defence";
	}

	@Override
	public ItemStack getBaseStack() {
		return new AItemStackBuilder(Material.DIAMOND_CHESTPLATE)
				.setLore(new ALoreBuilder(
						"&7Upgrade your defence and shield"
				))
				.getItemStack();
	}

	@Override
	public MajorProgressionUnlock createFirstUnlock() {
		return new MajorProgressionUnlock() {
			@Override
			public String getDisplayName() {
				return "&9Unlock: Shield";
			}

			@Override
			public String getRefName() {
				return "unlock-shield";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.DIAMOND_CHESTPLATE)
						.setLore(new ALoreBuilder(
								"&7Too lazy to write a description"
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
				return "&9Shield Strength";
			}

			@Override
			public String getRefName() {
				return "shield-vs-all";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.IRON_INGOT)
						.setLore(new ALoreBuilder(
								"&7Too lazy to write a description"
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
				return "&9Shield Strength vs Players";
			}

			@Override
			public String getRefName() {
				return "shield-vs-players";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.DIAMOND)
						.setLore(new ALoreBuilder(
								"&7Too lazy to write a description"
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
				return "&9Faster Shield Repair";
			}

			@Override
			public String getRefName() {
				return "shield-repair";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.ANVIL)
						.setLore(new ALoreBuilder(
								"&7Too lazy to write a description"
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
				return "&9Reduction from Mobs and Bosses";
			}

			@Override
			public String getRefName() {
				return "mob-boss-reduction";
			}

			@Override
			public int getCost(int level) {
				return level;
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("defence", "&c+%value%% &7something",
						100, 100, 100, 100, 100, 100));
			}
		};
	}

	@Override
	public Path createSecondPath() {
		return new Path() {
			@Override
			public String getDisplayName() {
				return "Max Shield";
			}

			@Override
			public String getRefName() {
				return "max-shield";
			}

			@Override
			public int getCost(int level) {
				return level;
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("shield", "&c+%value%% &7something",
						100, 100, 100, 100, 100, 100));
			}
		};
	}
}
