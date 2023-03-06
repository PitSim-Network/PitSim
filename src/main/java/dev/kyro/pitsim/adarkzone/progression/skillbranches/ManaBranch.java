package dev.kyro.pitsim.adarkzone.progression.skillbranches;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.adarkzone.progression.ProgressionManager;
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

		if(pitPlayer.shield.isUnlocked() && !pitPlayer.shield.isActive() &&
				ProgressionManager.isUnlocked(pitPlayer, this, MajorUnlockPosition.SECOND_PATH))
			event.multipliers.add(1 + (shieldDownManaIncrease() / 100.0));

		event.multipliers.add(1 + (ProgressionManager.getUnlockedEffectAsValue(pitPlayer, this,
				PathPosition.SECOND_PATH, "mana-regen") / 100.0));
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		boolean hasFirstPath = ProgressionManager.isUnlocked(attackEvent.getAttackerPitPlayer(), this, MajorUnlockPosition.FIRST_PATH);
		if(hasFirstPath && Misc.isEntity(attackEvent.getDefender(), PitEntityType.PIT_MOB))
			attackEvent.getAttackerPitPlayer().mana += getMobKillMana();
	}

	public static int getSpellManaReduction() {
		return 30;
	}

	public static int getMobKillMana() {
		return 5;
	}

	public static int shieldDownManaIncrease() {
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
						"&7Upgrade your mana"
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
								"&7Unlocks the ability to use",
								"&bmana"
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
				return "Mana Spell Reduction";
			}

			@Override
			public String getRefName() {
				return "mana-spell-reduction";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.GOLD_HOE)
						.setLore(new ALoreBuilder(
								"&7All spells are &b" + getSpellManaReduction() + "% cheaper",
								"&7to cast"
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
								"&7Gain &b+" + getMobKillMana() + " mana &7on mob kill"
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
				return "Mana Regen Without Shield";
			}

			@Override
			public String getRefName() {
				return "no-shield-mana-regen";
			}

			@Override
			public ItemStack getBaseStack() {
				return new AItemStackBuilder(Material.SPECKLED_MELON)
						.setLore(new ALoreBuilder(
								"&7When your shield is down,",
								"&7you regenerate mana &b+" + shieldDownManaIncrease() + "%",
								"&7faster"
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
				addEffect(new EffectData("max-mana", "&b+%value% max mana",
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
				addEffect(new EffectData("mana-regen", "&b+%value%% &7faster mana regen",
						100, 100, 100, 100, 100, 100));
			}
		};
	}
}
