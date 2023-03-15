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

import java.text.DecimalFormat;

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

		if(Misc.isEntity(attackEvent.getAttacker(), PitEntityType.PIT_MOB)) {
			for(Double multiplier : ProgressionManager.getUnlockedEffectAsList(
					attackEvent.getDefenderPitPlayer(), this, PathPosition.FIRST_PATH, "mob-defence"))
				attackEvent.multipliers.add(Misc.getReductionMultiplier(multiplier));
		}

		if(Misc.isEntity(attackEvent.getAttacker(), PitEntityType.PIT_BOSS)) {
			for(Double multiplier : ProgressionManager.getUnlockedEffectAsList(
					attackEvent.getDefenderPitPlayer(), this, PathPosition.FIRST_PATH, "boss-defence"))
				attackEvent.multipliers.add(Misc.getReductionMultiplier(multiplier));
		}
	}

	public static int getShieldDamageReduction() {
		return 20;
	}

	public static int getShieldDamageFromPlayersReduction() {
		return 20;
	}

	public static int getReactivationReductionTicks() {
		return 60;
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
								"&7Unlocks &9shield &7(displayed using",
								"&7your xp level and bar)"
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
								"&7Your shield takes &9-" + getShieldDamageReduction() + "% &7damage",
								"&7from all sources"
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
								"&7Your shield takes &9-" + getShieldDamageFromPlayersReduction() + "% &7damage",
								"&7from other players"
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
				DecimalFormat decimalFormat = new DecimalFormat("0.#");
				double seconds = getReactivationReductionTicks() / 20.0;
				return new AItemStackBuilder(Material.ANVIL)
						.setLore(new ALoreBuilder(
								"&7Your shield repairs itself",
								"&9" + decimalFormat.format(seconds) + " &7second" + (seconds == 1 ? "" : "s") + " faster after it",
								"&7breaks"
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
				addEffect(new EffectData("mob-defence", "&9-%value%% &7damage from mobs",
						20, 20, 20, 20, 20, 20));
				addEffect(new EffectData("boss-defence", "&9-%value%% &7damage from bosses",
						20, 20, 20, 20, 20, 20));
			}
		};
	}

	@Override
	public Path createSecondPath() {
		return new Path() {
			@Override
			public String getDisplayName() {
				return "&9Max Shield";
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
				addEffect(new EffectData("shield", "&9+%value% &7max shield",
						50, 50, 50, 50, 50, 50));
			}
		};
	}
}
