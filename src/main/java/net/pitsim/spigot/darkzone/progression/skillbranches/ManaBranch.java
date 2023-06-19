package net.pitsim.spigot.darkzone.progression.skillbranches;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.darkzone.progression.ProgressionManager;
import net.pitsim.spigot.darkzone.progression.SkillBranch;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enums.PitEntityType;
import net.pitsim.spigot.events.KillEvent;
import net.pitsim.spigot.events.ManaRegenEvent;
import net.pitsim.spigot.misc.Misc;
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
	public void onKill(KillEvent killEvent) {
		if(killEvent.getAttackEvent() == null || killEvent.getAttackEvent().getAttacker() != killEvent.getAttackEvent().getRealDamager() ||
				killEvent.getWrapperEvent().hasAttackInfo()) return;
		boolean hasFirstPath = ProgressionManager.isUnlocked(killEvent.getKillerPitPlayer(), this, MajorUnlockPosition.FIRST_PATH);
		if(hasFirstPath && Misc.isEntity(killEvent.getDead(), PitEntityType.PIT_MOB))
			killEvent.getKillerPitPlayer().giveMana(getMobKillMana());
	}

	public static int getSpellManaReduction() {
		return 30;
	}

	public static int getMobKillMana() {
		return 4;
	}

	public static int shieldDownManaIncrease() {
		return 70;
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
	public ItemStack getBaseDisplayStack() {
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
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.INK_SACK, 1, 12)
						.setLore(new ALoreBuilder(
								"&7Unlocks the ability to use",
								"&bmana"
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
				return "&bMana Spell Reduction";
			}

			@Override
			public String getRefName() {
				return "mana-spell-reduction";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.GOLD_HOE)
						.setLore(new ALoreBuilder(
								"&7All spells are &b" + getSpellManaReduction() + "% cheaper",
								"&7to cast"
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
				return "&bMana on Mob Kill";
			}

			@Override
			public String getRefName() {
				return "mob-kill-mana";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.SKULL_ITEM, 1, 2)
						.setLore(new ALoreBuilder(
								"&7Gain &b+" + getMobKillMana() + " mana &7on melee",
								"&7mob kill"
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
				return "&bMana Regen Without Shield";
			}

			@Override
			public String getRefName() {
				return "no-shield-mana-regen";
			}

			@Override
			public ItemStack getBaseDisplayStack() {
				return new AItemStackBuilder(Material.SPECKLED_MELON)
						.setLore(new ALoreBuilder(
								"&7When your shield is down,",
								"&7you regenerate mana &b+" + shieldDownManaIncrease() + "%",
								"&7faster"
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
				return "&bMax Mana";
			}

			@Override
			public String getRefName() {
				return "max-mana";
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("max-mana", "&b+%value% max mana",
						20, 30, 30, 40, 40, 40));
			}
		};
	}

	@Override
	public Path createSecondPath() {
		return new Path() {
			@Override
			public String getDisplayName() {
				return "&bMana Regen";
			}

			@Override
			public String getRefName() {
				return "mana-regen";
			}

			@Override
			public void addEffects() {
				addEffect(new EffectData("mana-regen", "&b+%value%% &7faster mana regen",
						15, 15, 15, 15, 15, 15));
			}
		};
	}
}
