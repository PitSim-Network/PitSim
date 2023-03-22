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
						"&7with the &4Devil &7of the",
						"&5Darkzone"
				))
				.getItemStack();
	}

	public static int getTurboIncrease() {
		return 20;
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
								"&7Start with the basics:",
								"&7 - Pedestal of &fKnowledge",
								"&7 - Pedestal of &eRenown",
								"&7 - Pedestal of &4Heresy"
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
								"&7All pedestals are &4" + getTurboIncrease() + "%",
								"&7more effective"
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
								"&7Unlocks the Pedestal",
								"&7of &6Wealth"
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
								"&7Unlocks the Pedestal",
								"&7of &cTurmoil"
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
				return "&4Altar Output: Demonic Vouchers";
			}

			@Override
			public String getRefName() {
				return "altar-demonic-vouchers";
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("renown", "&4+%value%% &7more &4Demonic Vouchers",
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
			public void addEffects() {
				addEffect(new EffectData("renown", "&4+%value%% &7more &eRenown",
						100, 100, 100, 100, 100, 100));
			}
		};
	}
}
