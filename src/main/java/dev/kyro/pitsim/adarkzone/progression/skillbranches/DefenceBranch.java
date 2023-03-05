package dev.kyro.pitsim.adarkzone.progression.skillbranches;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DefenceBranch extends SkillBranch {
	public static DefenceBranch INSTANCE;

	public DefenceBranch() {
		INSTANCE = this;
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
				return "&9Reduction from Mobs & Bosses";
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
