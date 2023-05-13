package dev.kyro.pitsim.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.aitems.misc.GoldPickup;
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

public class HighStakes extends Megastreak {
	public static HighStakes INSTANCE;

	public HighStakes() {
		super("&2High Stakes", "highstakes", 50, 9, 50);
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
		killEvent.goldMultipliers.add(1 + (getGoldIncrease() / 100.0));
		killEvent.xpMultipliers.add(0.5);
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		Sounds.MEGA_GENERAL.play(player.getLocation());
		pitPlayer.stats.timesOnHighStakes++;
		DailyMegastreakQuest.INSTANCE.onMegastreakComplete(pitPlayer);
	}

	@Override
	public void reset(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.isOnMega()) return;

//		TODO: Explode into gold ingots
	}

	@Override
	public String getPrefix(Player player) {
		return "&2&lSTAKES";
	}

	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		return new AItemStackBuilder(Material.GOLD_NUGGET)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, PitPlayer pitPlayer) {
		loreBuilder.addLore(
				"&7On Trigger:",
				"&a\u25a0 &7Earn &6+" + getGoldIncrease() + "% gold &7from kills",
				"&a\u25a0 &7Every 17 kills, spawn",
				"   &65 gold ingots&7. Picking them up",
				"   &7grants &cRegen " + AUtil.toRoman(GoldPickup.getRegenAmplifier() + 1) +
						" &7(" + GoldPickup.getRegenSeconds() + "s) and &6" + GoldPickup.getPickupGold() + "g",
				"",
				"&7BUT:",
				"&c\u25a0 &7Have a &f1 &7in &f1,000 &7chance to &cDIE!",
				"   &7on each bot kill",
				"&c\u25a0 &7Earn &c-50% &7xp from kills",
				"",
				"&7On Death:",
				"&e\u25a0 &7Explode into a bunch of &6gold ingots"
		);
	}

	@Override
	public String getSummary() {
		return getCapsDisplayName() + "&7 is a Megastreak that";
	}

	public static int getGoldIncrease() {
		return 75;
	}
}
