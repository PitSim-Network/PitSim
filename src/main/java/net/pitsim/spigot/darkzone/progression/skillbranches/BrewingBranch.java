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
				return "&5+1 Brewing Ingredient Slot";
			}

			@Override
			public String getRefName() {
				return "ingredient-slot";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.CAULDRON_ITEM)
						.setLore(new ALoreBuilder(
								"&7A larger cauldron! Perfect",
								"&7for packing another ingredient",
								"&7into brews!"
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
				return "&5Unlock: Potion Pouch";
			}

			@Override
			public String getRefName() {
				return "unlock-potion-pouch";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.FLOWER_POT_ITEM)
						.setLore(new ALoreBuilder(
								"&7The perfect item for carrying",
								"&7around your potions"
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
				return "&5Unlock: Catalyst";
			}

			@Override
			public String getRefName() {
				return "unlock-catalyst";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.NETHER_STAR)
						.setLore(new ALoreBuilder(
								"&7The most powerful potion",
								"&7ingredient now craftable",
								"&7and at your disposal"
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
				return "&5Brewing Time Reduction";
			}

			@Override
			public String getRefName() {
				return "brewing-time-reduction";
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("brew-time-reduction", "&5-%value%% &7potion brew time",
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
			public void addEffects() {
				addEffect(new EffectData("brewing-luck", "&5+%value%% &7brewing luck",
						100, 100, 100, 100, 100, 100));
			}
		};
	}
}
