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

public class StashStreaker extends Megastreak {
	public static StashStreaker INSTANCE;

	public StashStreaker() {
		super("&8Stash Streaker", "stashstreaker", 100, 13, 50);
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
		killEvent.xpCap += 130;
		killEvent.xpMultipliers.add(2.0);
		killEvent.goldMultipliers.add(0.5);
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		Sounds.MEGA_GENERAL.play(player.getLocation());
		pitPlayer.stats.timesOnStashStreaker++;
		DailyMegastreakQuest.INSTANCE.onMegastreakComplete(pitPlayer);
	}

	@Override
	public void reset(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.isOnMega()) return;

//		TODO: On death effect
	}

	@Override
	public String getPrefix(Player player) {
		return "&8&lSTASH";
	}

	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		return new AItemStackBuilder(Material.DIAMOND_SWORD)
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
				"&c\u25a0 &7Receive &c+0.15% &7damage per kill over 50",
				"&7(5x damage from bots)",
				"&c\u25a0 &7Earn &c-50% &7gold from kills",
				"",
				"&7On Death:",
				"&e\u25a0 &7Earn between &b1000 &7and &b5000 XP&7"
		);
	}

	@Override
	public String getSummary() {
		return getCapsDisplayName() + "&7 is a Megastreak that";
	}
}
