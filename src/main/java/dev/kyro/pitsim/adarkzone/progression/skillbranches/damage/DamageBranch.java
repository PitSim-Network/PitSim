package dev.kyro.pitsim.adarkzone.progression.skillbranches.damage;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
import dev.kyro.pitsim.enums.UIColor;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class DamageBranch extends SkillBranch {
	public static DamageBranch INSTANCE;

	public DamageBranch() {
		super(UIColor.RED);
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
//		boolean hasMajor = isUnlocked(attackEvent.getAttackerPitPlayer(), firstUnlock);
//		if(hasMajor) AOutput.send(attackEvent.getAttackerPlayer(), "You have the first upgrade");
//
//		double mobDamageIncrease = getUnlockedEffect(attackEvent.getAttackerPitPlayer(), firstPath, "damage");
//		if(mobDamageIncrease != 0) {
//			AOutput.send(attackEvent.getAttackerPlayer(), "this is cool: " + mobDamageIncrease);
//			attackEvent.increasePercent += mobDamageIncrease;
//		}
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
				return "Unlock Bosses";
			}

			@Override
			public String getRefName() {
				return "unlock-bosses";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.SKULL_ITEM, 1, 1)
						.setLore(new ALoreBuilder(
								"&7Unlocks the ability to summon",
								"&7bosses in the &5Darkzone"
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
				return "something";
			}

			@Override
			public String getRefName() {
				return "something";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.BARRIER)
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
				return "something2";
			}

			@Override
			public String getRefName() {
				return "something2";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.BARRIER)
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
				return "something3";
			}

			@Override
			public String getRefName() {
				return "something3";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.BARRIER)
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
				return "&cIncreased Damage vs Mobs";
			}

			@Override
			public String getRefName() {
				return "mob-damage";
			}

			@Override
			public int getCost(int level) {
				return level;
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("damage", "&c+%value%% &7damage vs mobs",
						2, 2, 2, 3, 3, 3));
			}
		};
	}

	@Override
	public Path createSecondPath() {
		return new Path() {
			@Override
			public String getDisplayName() {
				return "&cIncreased Damage vs Bosses";
			}

			@Override
			public String getRefName() {
				return "boss-damage";
			}

			@Override
			public int getCost(int level) {
				return level;
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("damage", "&c+%value%% &7damage vs bosses",
						1, 2, 3, 4, 5, 6));
			}
		};
	}
}
