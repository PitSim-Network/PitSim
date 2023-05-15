package dev.kyro.pitsim.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.battlepass.quests.daily.DailyMegastreakQuest;
import dev.kyro.pitsim.controllers.LevelManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Formatter;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.upgrades.DoubleDeath;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class Beastmode extends Megastreak {
	public static Beastmode INSTANCE;

	public Beastmode() {
		super("&aBeastmode", "beastmode", 50, 13, 50);
		INSTANCE = this;
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		if(!hasMegastreak(attackEvent.getDefenderPlayer())) return;
		PitPlayer pitPlayer = attackEvent.getDefenderPitPlayer();
		if(!pitPlayer.isOnMega()) return;
		if(NonManager.getNon(attackEvent.getAttacker()) == null) {
			attackEvent.increasePercent += (pitPlayer.getKills() - 50) * 0.3;
		} else {
			attackEvent.increasePercent += (pitPlayer.getKills() - 50) * 5 * 0.3;
		}
	}

	@EventHandler
	public void kill(KillEvent killEvent) {
		if(!hasMegastreak(killEvent.getKillerPlayer())) return;
		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
		if(!pitPlayer.isOnMega()) return;
		killEvent.xpCap += 130;
		killEvent.xpMultipliers.add(2.0);
		killEvent.goldMultipliers.add(0.5);
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		Sounds.MEGA_GENERAL.play(player.getLocation());
		pitPlayer.stats.timesOnBeastmode++;
		DailyMegastreakQuest.INSTANCE.onMegastreakComplete(pitPlayer);
	}

	@Override
	public void reset(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.isOnMega()) return;

		int randomXP = Misc.intBetween(1000, 5000);
		if(DoubleDeath.INSTANCE.isDoubleDeath(pitPlayer.player)) randomXP *= 2;
		AOutput.send(pitPlayer.player, getCapsDisplayName() + "!&7 Earned &b" + Formatter.commaFormat.format(randomXP) +
				"&b XP &7from megastreak!");
		LevelManager.addXP(pitPlayer.player, randomXP);
	}

	@Override
	public String getPrefix(Player player) {
		return "&a&lBEAST";
	}

	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		return new AItemStackBuilder(Material.DIAMOND_HELMET)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, PitPlayer pitPlayer) {
		loreBuilder.addLore(
				"&7On Trigger:",
				"&a\u25a0 &7Earn &b+100% XP &7from kills",
				"&a\u25a0 &7Gain &b+130 max XP &7from kills",
				"",
				"&7BUT:",
				"&c\u25a0 &7Receive &c+0.3% &7damage per kill over 50",
				"&7(5x damage from bots)",
				"&c\u25a0 &7Earn &c-50% &7gold from kills",
				"",
				"&7On Death:",
				"&e\u25a0 &7Earn between &b1000 &7and &b5000 XP&7"
		);
	}

	@Override
	public String getSummary() {
		return getCapsDisplayName() + "&7 is a Megastreak that grants you increased &bXP&7, &bmax XP&7, " +
				"gain &bXP&7 on death, but makes you earn less &6gold&7 and take more damage per kill over 50";
	}
}
