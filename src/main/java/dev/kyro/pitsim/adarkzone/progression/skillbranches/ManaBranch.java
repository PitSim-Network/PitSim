package dev.kyro.pitsim.adarkzone.progression.skillbranches;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.adarkzone.progression.SkillBranch;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.ManaRegenEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class ManaBranch extends SkillBranch {
	public static ManaBranch INSTANCE;

	public ManaBranch() {
		INSTANCE = this;
	}

	@EventHandler
	public void onManaRegen(ManaRegenEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		if(pitPlayer.shield.isUnlocked() && !pitPlayer.shield.isActive() && isUnlocked(pitPlayer, secondPathUnlock))
			event.multipliers.add(1 + (shieldDownManaIncrease() / 100.0));

		event.multipliers.add(1 + (getUnlockedEffectAsValue(pitPlayer, secondPath, "mana-regen") / 100.0));
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		boolean hasFirstPath = isUnlocked(attackEvent.getAttackerPitPlayer(), firstPathUnlock);
		if(hasFirstPath && Misc.isEntity(attackEvent.getDefender(), PitEntityType.PIT_MOB))
			attackEvent.getAttackerPitPlayer().mana += getMobKillMana();
	}

	public static int getSpellManaReduction() {
		return 30;
	}

	public static int getMobKillMana() {
		return 5;
	}

	public static double shieldDownManaIncrease() {
		return 100;
	}

	@Override
	public String getDisplayName() {
		return "&bMana";
	}

	@Override
	public String getRefName() {
		return "mana";
	}

	@Override
	public ItemStack getBaseStack() {
		return new AItemStackBuilder(Material.INK_SACK, 1, 12)
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
				return "&bUnlock: Mana";
			}

			@Override
			public String getRefName() {
				return "unlock-mana";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.INK_SACK, 1, 12)
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
				return "Spell Mana Reduction";
			}

			@Override
			public String getRefName() {
				return "spell-mana-reduction";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.GOLD_HOE)
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
				return "Mana on Mob Kill";
			}

			@Override
			public String getRefName() {
				return "mob-kill-mana";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.SKULL_ITEM, 1, 2)
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
				return "Mana Regeneration Without Shield";
			}

			@Override
			public String getRefName() {
				return "no-shield-mana-regen";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.SPECKLED_MELON)
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
				return "Max Mana";
			}

			@Override
			public String getRefName() {
				return "max-mana";
			}

			@Override
			public int getCost(int level) {
				return level;
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("max-mana", "&c+%value%% &7something",
						100, 100, 100, 100, 100, 100));
			}
		};
	}

	@Override
	public Path createSecondPath() {
		return new Path() {
			@Override
			public String getDisplayName() {
				return "Mana Regen";
			}

			@Override
			public String getRefName() {
				return "mana-regen";
			}

			@Override
			public int getCost(int level) {
				return level;
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("mana-regen", "&c+%value%% &7something",
						100, 100, 100, 100, 100, 100));
			}
		};
	}
}
