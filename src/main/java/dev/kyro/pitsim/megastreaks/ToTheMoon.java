package dev.kyro.pitsim.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.battlepass.quests.daily.DailyMegastreakQuest;
import dev.kyro.pitsim.controllers.HopperManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.Hopper;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.upgrades.DoubleDeath;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ToTheMoon extends Megastreak {
	public static ToTheMoon INSTANCE;
	public static List<Player> hopperCallList = new ArrayList<>();

	public ToTheMoon() {
		super("&bTo the Moon", "tothemoon", 100, 30, 60);
		INSTANCE = this;
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		if(!hasMegastreak(attackEvent.getDefenderPlayer())) return;
		PitPlayer pitPlayer = attackEvent.getDefenderPitPlayer();
		if(!pitPlayer.isOnMega()) return;

		if(pitPlayer.getKills() > 200) {
			double increase = 3 * ((pitPlayer.getKills() - 200) / 20);
			if(NonManager.getNon(attackEvent.getAttacker()) == null) {
				attackEvent.increasePercent += increase;
			} else attackEvent.increasePercent += increase * 5;
		}
		if(pitPlayer.getKills() > 400) {
			if(NonManager.getNon(attackEvent.getAttacker()) == null) {
				attackEvent.increase += 0.2 * ((pitPlayer.getKills() - 400) / 100);
			} else attackEvent.increase += 1.0 * ((pitPlayer.getKills() - 400) / 100);
		}
		if(pitPlayer.getKills() > 700) {
			attackEvent.veryTrueDamage += 0.2 * ((pitPlayer.getKills() - 700) / 10);
		}
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!hasMegastreak(killEvent.getKillerPlayer())) return;
		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
		if(!pitPlayer.isOnMega()) return;

		killEvent.xpMultipliers.add(1 + (getXPIncrease() / 100.0));
		killEvent.xpCap += getMaxXPIncrease();
		killEvent.xpCap += (pitPlayer.getKills() - 100) * 1.0;
		killEvent.goldMultipliers.add(0.5);

		if(pitPlayer.getKills() > 1500 && !hopperCallList.contains(killEvent.getKillerPlayer())) {
			HopperManager.callHopper("PayForTruce", Hopper.Type.VENOM, killEvent.getKiller());
			hopperCallList.add(killEvent.getKillerPlayer());
		}
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		Sounds.MEGA_GENERAL.play(pitPlayer.player.getLocation());
		pitPlayer.stats.timesOnMoon++;
		DailyMegastreakQuest.INSTANCE.onMegastreakComplete(pitPlayer);
	}

	@Override
	public void reset(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		hopperCallList.remove(player);

		if(!pitPlayer.isOnMega()) return;

		if(pitPlayer.getKills() >= 700) {
			int capIncrease = 5;
			if(DoubleDeath.INSTANCE.isDoubleDeath(pitPlayer.player)) capIncrease *= 2;
			capIncrease = Math.min(capIncrease, 50 - pitPlayer.moonBonus);
			if(capIncrease > 0) {
				pitPlayer.moonBonus += capIncrease;
				AOutput.send(pitPlayer.player, getCapsDisplayName() + "!&7 Gained &b+" + capIncrease +
						" max XP &7until you prestige! (" + pitPlayer.moonBonus + "/50)");
			}
		}
	}

	@Override
	public String getPrefix(Player player) {
		return "&b&lMOON";
	}

	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		return new AItemStackBuilder(Material.ENDER_STONE)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, PitPlayer pitPlayer) {
		loreBuilder.addLore(
				"&7On Trigger:",
				"&a\u25a0 &7Earn &b+" + getXPIncrease() + "% XP &7from kills",
				"&a\u25a0 &7Gain &b+" + getMaxXPIncrease() + " max XP &7from kills",
				"&a\u25a0 &7Gain &b+1 max XP &7per kill",
				"",
				"&7BUT:",
				"&c\u25a0 &7Starting at 200 kills, receive &c+3%",
				"   &7damage per 20 kills. (5x damage from bots)",
				"&c\u25a0 &7Starting at 400 kills, receive &c+" + Misc.getHearts(0.2),
				"   &7damage per 100 kills. (5x damage from bots)",
				"&c\u25a0 &7Starting at 700 kills, receive &c+" + Misc.getHearts(0.2),
				"   &7very true damage per 10 kills.",
				"&c\u25a0 &7Earn &c-50% &7gold from kills",
				"",
				"&7On Death:",
				"&e\u25a0 &7Earn a permanent &b+5 max XP",
				"&7until you prestige (50 max) if",
				"&7your streak is at least 700"
		);
	}

	@Override
	public String getSummary() {
		return getCapsDisplayName() + "&7 grants you increased &bXP&7 and &bXP cap&7 both which increase per kill, but take " +
				"&cdamage&7, &9true damage&7, and &cvery &9true damage&7 based on your streak, spawns a &5hopper&7 " +
				"at a very high killstreak";
	}

	public static int getXPIncrease() {
		return 120;
	}

	public static int getMaxXPIncrease() {
		return 300;
	}
}
