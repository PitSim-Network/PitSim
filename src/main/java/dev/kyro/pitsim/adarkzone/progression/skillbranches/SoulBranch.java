package dev.kyro.pitsim.adarkzone.progression.skillbranches;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SoulBranch extends SkillBranch {
	public static SoulBranch INSTANCE;

	public SoulBranch() {
		INSTANCE = this;
	}

	public static int getEnchantingReductionPercent() {
		return 30;
	}

	@Override
	public String getDisplayName() {
		return "&fSpirit";
	}

	@Override
	public String getRefName() {
		return "spirit";
	}

	@Override
	public ItemStack getBaseStack() {
		return new AItemStackBuilder(Material.GHAST_TEAR)
				.setLore(new ALoreBuilder(
						"&7Increase your spiritual",
						"&7connection to the",
						"&5Darkzone"
				))
				.getItemStack();
	}

	@Override
	public MajorProgressionUnlock createFirstUnlock() {
		return new MajorProgressionUnlock() {
			@Override
			public String getDisplayName() {
				return "&5Tier III Items";
			}

			@Override
			public String getRefName() {
				return "tier-3-items";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.BOOKSHELF)
						.setLore(new ALoreBuilder(
								"&7Unlocks the ability to",
								"&7enchant items to &5Tier III"
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
				return "&5Tier IV Items";
			}

			@Override
			public String getRefName() {
				return "tier-4-items";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.ENCHANTMENT_TABLE)
						.setLore(new ALoreBuilder(
								"&7Unlocks the ability to",
								"&7enchant items to &5Tier IV"
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
				return "&fFree Fast Travel";
			}

			@Override
			public String getRefName() {
				return "free-fast-travel";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.SPRUCE_DOOR_ITEM)
						.setLore(new ALoreBuilder(
								"&7Removes the cost from fast",
								"&7traveling"
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
				return "&5Enchanting Cost Reduction";
			}

			@Override
			public String getRefName() {
				return "enchanting-cost-reduction";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.FURNACE)
						.setLore(new ALoreBuilder(
								"&7Reduces the cost of enchanting",
								"&7by &5" + getEnchantingReductionPercent() + "%"
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
				return "&fSoul Harvesting";
			}

			@Override
			public String getRefName() {
				return "souls-from-mobs";
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("soul-chance-mobs", "&5+%value%% &7soul drop chance from mobs",
						100, 100, 100, 100, 100, 100));
			}
		};
	}

	@Override
	public Path createSecondPath() {
		return new Path() {
			@Override
			public String getDisplayName() {
				return "&fFresh Drop Chance";
			}

			@Override
			public String getRefName() {
				return "fresh-chance";
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("fresh-chance", "&5+%value%% &7fresh drop chance from mobs",
						100, 100, 100, 100, 100, 100));
			}
		};
	}
}
