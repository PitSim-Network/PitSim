package net.pitsim.spigot.darkzone.progression.skillbranches;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.darkzone.progression.SkillBranch;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BrewingBranch extends SkillBranch {
	public static BrewingBranch INSTANCE;

	public BrewingBranch() {
		INSTANCE = this;
	}

	@Override
	public String getDisplayName() {
		return "&5Brewing";
	}

	@Override
	public String getRefName() {
		return "brewing";
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.BREWING_STAND_ITEM)
				.setLore(new ALoreBuilder(
						"&7Upgrade potion brewing skills"
				))
				.getItemStack();
	}

	@Override
	public MajorProgressionUnlock createFirstUnlock() {
		return new MajorProgressionUnlock() {
			@Override
			public String getDisplayName() {
				return "&5+1 Potion Brewing Slot";
			}

			@Override
			public String getRefName() {
				return "brewing-slot";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.BREWING_STAND_ITEM)
						.setLore(new ALoreBuilder(
								"&7Adds an additional slot to",
								"&7brew potions with"
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
				return "&5+1 Potion Brewing Slot";
			}

			@Override
			public String getRefName() {
				return "brewing-slot-2";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.CAULDRON_ITEM)
						.setLore(new ALoreBuilder(
								"&7A larger cauldron! Unlock",
								"&7an extra brewing slot for",
								"&7making more potions!"
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
				return "&5Unlock: Splash Potions";
			}

			@Override
			public String getRefName() {
				return "unlock-splash-potions";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.POTION, 1, 16422)
						.setLore(new ALoreBuilder(
								"&7Unlock access to the &dPotion Master",
								"&7and create Splash Potions for &fSouls"
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
				return "&5Unlock: Lucky Brewer";
			}

			@Override
			public String getRefName() {
				return "unlock-lucky-brewer";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.NETHER_STAR)
						.setLore(new ALoreBuilder(
								"&7There is now a &f25% &7chance",
								"&7for potions to come out",
								"&f1 Tier &7higher than normal"
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
				return "&5Potion Tier Usage";
			}

			@Override
			public String getRefName() {
				return "potion-tier-usage";
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("potion-tier", "&7Ability to use &5Tier %value% &7potions",
						5, 6, 7, 8, 9, 10));
			}
		};
	}

	@Override
	public Path createSecondPath() {
		return new Path() {
			@Override
			public String getDisplayName() {
				return "&5Brew Time Reduction";
			}

			@Override
			public String getRefName() {
				return "brewing-time-reduction";
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("brew-time-reduction", "&5-%value%% &7potion brew time",
						5, 10, 15, 20, 25, 30));
			}
		};
	}
}
