package dev.kyro.pitsim.adarkzone.progression.skillbranches;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class aBranch extends SkillBranch {
	public static aBranch INSTANCE;

	public aBranch() {
		INSTANCE = this;
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
						"&7Develop your strength"
				))
				.getItemStack();
	}

	@Override
	public MajorProgressionUnlock createFirstUnlock() {
		return new MajorProgressionUnlock() {
			@Override
			public String getDisplayName() {
				return "FIRSTUNLOCK";
			}

			@Override
			public String getRefName() {
				return "REFNAME";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.BARRIER, 1, 1)
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
				return "LASTUNLOCK";
			}

			@Override
			public String getRefName() {
				return "REFNAME";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.BARRIER, 1, 1)
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
				return "FIRSTPATHUNLOCK";
			}

			@Override
			public String getRefName() {
				return "REFNAME";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.BARRIER, 1, 1)
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
				return "SECONDPATHUNLOCK";
			}

			@Override
			public String getRefName() {
				return "REFNAME";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.BARRIER, 1, 1)
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
				return "FIRSTPATH";
			}

			@Override
			public String getRefName() {
				return "REFNAME";
			}

			@Override
			public int getCost(int level) {
				return level;
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("REFNAME", "&c+%value%% &7something",
						100, 100, 100, 100, 100, 100));
			}
		};
	}

	@Override
	public Path createSecondPath() {
		return new Path() {
			@Override
			public String getDisplayName() {
				return "SECONDPATH";
			}

			@Override
			public String getRefName() {
				return "REFNAME";
			}

			@Override
			public int getCost(int level) {
				return level;
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("REFNAME", "&c+%value%% &7something",
						100, 100, 100, 100, 100, 100));
			}
		};
	}
}
