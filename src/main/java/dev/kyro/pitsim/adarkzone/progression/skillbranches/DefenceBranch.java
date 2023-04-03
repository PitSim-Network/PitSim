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
		if(Misc.isEntity(attackEvent.getAttacker(), PitEntityType.PIT_MOB)) {
			attackEvent.multipliers.addAll(ProgressionManager.getUnlockedEffectAsList(
					attackEvent.getDefenderPitPlayer(), this, PathPosition.FIRST_PATH, "mob-defence"));
		}

		if(Misc.isEntity(attackEvent.getAttacker(), PitEntityType.PIT_BOSS)) {
			attackEvent.multipliers.addAll(ProgressionManager.getUnlockedEffectAsList(
					attackEvent.getDefenderPitPlayer(), this, PathPosition.FIRST_PATH, "boss-defence"));
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

	@Override
	public String getDisplayName() {
		return "&9Defence";
	}

	@Override
	public String getRefName() {
		return "defence";
	}

	@Override
	public ItemStack getBaseDisplayStack() {
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
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.DIAMOND_CHESTPLATE)
						.setLore(new ALoreBuilder(
								"&7Unlocks &9shield &7(displayed using",
								"&7your xp level and bar)"
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
				return "&9Shield Strength";
			}

			@Override
			public String getRefName() {
				return "shield-vs-all";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.IRON_INGOT)
						.setLore(new ALoreBuilder(
								"&7Your shield takes &9-" + getShieldDamageReduction() + "% &7damage",
								"&7from all sources"
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
				return "&9Shield Strength vs Players";
			}

			@Override
			public String getRefName() {
				return "shield-vs-players";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.DIAMOND)
						.setLore(new ALoreBuilder(
								"&7Your shield takes &9-" + getShieldDamageFromPlayersReduction() + "% &7damage",
								"&7from other players"
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
				return "&9Faster Shield Repair";
			}

			@Override
			public String getRefName() {
				return "shield-repair";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
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
			public void addEffects() {
				addEffect(new EffectData("mob-defence", "&9%value%x &7damage from mobs",
						0.7, 0.7, 0.7, 0.7, 0.7, 0.7));
				addEffect(new EffectData("boss-defence", "&9%value%x &7damage from bosses",
						0.7, 0.7, 0.7, 0.7, 0.7, 0.7));
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
			public void addEffects() {
				addEffect(new EffectData("shield", "&9+%value% &7max shield",
						25, 25, 25, 25, 25, 25));
			}
		};
	}
}
