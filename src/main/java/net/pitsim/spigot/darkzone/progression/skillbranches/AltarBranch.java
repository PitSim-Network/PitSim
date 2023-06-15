package net.pitsim.spigot.darkzone.progression.skillbranches;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.darkzone.progression.SkillBranch;
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
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.ENDER_PORTAL_FRAME)
				.setLore(new ALoreBuilder(
						"&7Improve your relationship",
						"&7with the &4Devil &7of the",
						"&5Darkzone"
				))
				.getItemStack();
	}

	public static int getTurboIncrease() {
		return 10;
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
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.ENDER_PORTAL_FRAME)
						.setLore(new ALoreBuilder(
								"&7Start with the basics:",
								"&7 - Pedestal of &3Knowledge",
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
				return "&4Cultist";
			}

			@Override
			public String getRefName() {
				return "cultist";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.REDSTONE)
						.setLore(new ALoreBuilder(
								"&7Sacrificing &fsouls &7gives you",
								"&4+" + getTurboIncrease() + "% &7more resources"
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
			public ItemStack getBaseDisplayStack() {
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
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.WATCH)
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
				return "&4Altar Output: XP";
			}

			@Override
			public String getRefName() {
				return "altar-xp";
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("altar-xp", "&4+%value%% &7more &cAltar XP",
						25, 25, 25, 25, 25, 25));
			}
		};
	}

	@Override
	public Path createSecondPath() {
		return new Path() {
			@Override
			public String getDisplayName() {
				return "&4Altar Output: Renown & Vouchers";
			}

			@Override
			public String getRefName() {
				return "altar-renown-vouchers";
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("altar-renown", "&4+%value%% &7more &eRenown",
						30, 0, 30, 0, 30, 0));
				addEffect(new EffectData("altar-vouchers", "&4+%value%% &7more &4Demonic Vouchers",
						0, 15, 0, 15, 0, 15));
			}
		};
	}
}
