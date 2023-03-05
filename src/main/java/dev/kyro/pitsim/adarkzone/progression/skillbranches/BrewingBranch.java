package dev.kyro.pitsim.adarkzone.progression.skillbranches;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
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
	public ItemStack getBaseStack() {
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
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.BREWING_STAND_ITEM)
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
				return "&5+1 Brewing Ingredient Slot";
			}

			@Override
			public String getRefName() {
				return "ingredient-slot";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.CAULDRON_ITEM)
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
				return "&5Unlock: Potion Pouch";
			}

			@Override
			public String getRefName() {
				return "unlock-potion-pouch";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.FLOWER_POT_ITEM)
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
				return "&5Unlock: Catalyst";
			}

			@Override
			public String getRefName() {
				return "unlock-catalyst";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.NETHER_STAR)
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
				return "&5Brewing Time Reduction";
			}

			@Override
			public String getRefName() {
				return "brewing-time-reduction";
			}

			@Override
			public int getCost(int level) {
				return level;
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("brew-time-reduction", "&c+%value%% &7something",
						100, 100, 100, 100, 100, 100));
			}
		};
	}

	@Override
	public Path createSecondPath() {
		return new Path() {
			@Override
			public String getDisplayName() {
				return "&5Brewing Luck";
			}

			@Override
			public String getRefName() {
				return "brewing-luck";
			}

			@Override
			public int getCost(int level) {
				return level;
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("brewing-luck", "&c+%value%% &7something",
						100, 100, 100, 100, 100, 100));
			}
		};
	}
}
