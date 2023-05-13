package dev.kyro.pitsim.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.battlepass.quests.daily.DailyMegastreakQuest;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class Apostle extends Megastreak {
	public static Apostle INSTANCE;

	public Apostle() {
		super("&3Apostle", "apostle", 100, 40, 120);
		INSTANCE = this;
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		if(!hasMegastreak(attackEvent.getDefenderPlayer())) return;
		PitPlayer pitPlayer = attackEvent.getDefenderPitPlayer();
		if(!pitPlayer.isOnMega()) return;
		if(NonManager.getNon(attackEvent.getAttacker()) == null) {
			attackEvent.increasePercent += (pitPlayer.getKills() - 50) * 0.15;
		} else {
			attackEvent.increasePercent += (pitPlayer.getKills() - 50) * 5 * 0.15;
		}
	}

	@EventHandler
	public void kill(KillEvent killEvent) {
		if(!hasMegastreak(killEvent.getKillerPlayer())) return;
		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
		if(!pitPlayer.isOnMega()) return;
		killEvent.xpMultipliers.add(1 + (getXPIncrease() / 100.0));
		killEvent.xpCap += getMaxXPIncrease();
		killEvent.goldMultipliers.add(0.5);
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		Sounds.MEGA_GENERAL.play(player.getLocation());
		pitPlayer.stats.timesOnApostle++;
		DailyMegastreakQuest.INSTANCE.onMegastreakComplete(pitPlayer);
	}

	@Override
	public void reset(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.isOnMega()) return;

//		TODO: Stored xp
//		if(DoubleDeath.INSTANCE.isDoubleDeath(pitPlayer.player)) randomXP = randomXP * 2;
//		AOutput.send(pitPlayer.player, getCapsDisplayName() + "!&7 Earned &b" + randomXP + "&b XP &7from megastreak!");
//		LevelManager.addXP(pitPlayer.player, randomXP);
	}

	@Override
	public String getPrefix(Player player) {
		return "&3&lAPSTLE";
	}

	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		return new AItemStackBuilder(Material.INK_SACK, 1, 12)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, Player player) {
		loreBuilder.addLore(
				"&7On Trigger:",
				"&a\u25a0 &7Earn &b+" + getXPIncrease() + "% XP &7from kills",
				"&a\u25a0 &7Gain &b+" + getMaxXPIncrease() + " max XP &7from kills",
				"",
				"&7BUT:",
				"&c\u25a0 &7Receive &c+0.15% &7damage per kill over 50",
				"&7(5x damage from bots)",
				"&c\u25a0 &7Earn &c-50% &7gold from kills",
				"",
				"&7During the Streak:",
				"&e\u25a0 &7Store the &bXP &7you earn",
				"",
				"&7On Death:",
				"&e\u25a0 &7Earn the stored &bXP &7multiplied",
				"   &7by &b0.005x &7per kill above 100,",
				"   &7up to &b1x"
		);
	}

	@Override
	public String getSummary() {
		return getCapsDisplayName() + "&7 is a Megastreak that";
	}

	public static int getXPIncrease() {
		return 50;
	}

	public static int getMaxXPIncrease() {
		return 50;
	}
}
