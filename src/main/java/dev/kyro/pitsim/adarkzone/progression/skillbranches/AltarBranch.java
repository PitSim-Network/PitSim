package dev.kyro.pitsim.adarkzone.progression.skillbranches;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AltarBranch extends SkillBranch {
	public static AltarBranch INSTANCE;

	public AltarBranch() {
		INSTANCE = this;
	}

	@Override
	public String getDisplayName() {
		return "&4Altar";
	}

	@Override
	public String getRefName() {
		return "altar";
	}

	@Override
	public ItemStack getBaseStack() {
		return new AItemStackBuilder(Material.ENDER_PORTAL_FRAME)
				.setLore(new ALoreBuilder(
						"&7Improve your relationship",
						"&7with the &4devil &7of the",
						"&5Darkzone"
				))
				.getItemStack();
	}

	@Override
	public MajorProgressionUnlock createFirstUnlock() {
		return new MajorProgressionUnlock() {
			@Override
			public String getDisplayName() {
				return "&4Unlock: Basic Pedestals";
			}

			@Override
			public String getRefName() {
				return "unlock-basic-pedestals";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.ENDER_PORTAL_FRAME)
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
				return "&4Pedestal Turbo";
			}

			@Override
			public String getRefName() {
				return "pedestal-turbo";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.WATCH)
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
				return "&4Unlock: Pedestal of Wealth";
			}

			@Override
			public String getRefName() {
				return "unlock-pedestal-wealth";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.GOLD_BLOCK)
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
				return "&4Unlock: Pedestal of Turmoil";
			}

			@Override
			public String getRefName() {
				return "unlock-pedestal-turmoil";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.COMPASS)
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
				return "&4Altar Output: Demonic Vouchers";
			}

			@Override
			public String getRefName() {
				return "altar-demonic-vouchers";
			}

			@Override
			public int getCost(int level) {
				return level;
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("renown", "&c+%value%% &7something",
						100, 100, 100, 100, 100, 100));
			}
		};
	}

	@Override
	public Path createSecondPath() {
		return new Path() {
			@Override
			public String getDisplayName() {
				return "&4Altar Output: Renown";
			}

			@Override
			public String getRefName() {
				return "altar-renown";
			}

			@Override
			public int getCost(int level) {
				return level;
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("renown", "&c+%value%% &7something",
						100, 100, 100, 100, 100, 100));
			}
		};
	}
}
