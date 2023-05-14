package dev.kyro.pitsim.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.battlepass.quests.daily.DailyMegastreakQuest;
import dev.kyro.pitsim.controllers.LevelManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.upgrades.DoubleDeath;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Overdrive extends Megastreak {
	public static Overdrive INSTANCE;
	
	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					if(pitPlayer.getMegastreak() != Overdrive.INSTANCE || !pitPlayer.isOnMega()) continue;
					Misc.applyPotionEffect(onlinePlayer, PotionEffectType.SPEED, 200, 0, true, false);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 60L);
	}

	public Overdrive() {
		super("&cOverdrive", "overdrive", 50, 0, 0);
		INSTANCE = this;
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		if(!hasMegastreak(attackEvent.getDefenderPlayer())) return;
		PitPlayer pitPlayer = attackEvent.getDefenderPitPlayer();
		if(!pitPlayer.isOnMega() || NonManager.getNon(attackEvent.getAttacker()) == null) return;
		attackEvent.veryTrueDamage += (pitPlayer.getKills() - 50) / 50D;
	}

	@EventHandler
	public void kill(KillEvent killEvent) {
		if(!hasMegastreak(killEvent.getKillerPlayer())) return;
		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
		if(!pitPlayer.isOnMega()) return;
		killEvent.xpMultipliers.add(1 + (getXPIncrease() / 100.0));
		killEvent.goldMultipliers.add(1 + (getGoldIncrease() / 100.0));
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		
		Sounds.MEGA_GENERAL.play(player.getLocation());
		pitPlayer.stats.timesOnOverdrive++;
		DailyMegastreakQuest.INSTANCE.onMegastreakComplete(pitPlayer);
	}

	@Override
	public void reset(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.isOnMega()) return;

		int randomGold = Misc.intBetween(1000, 5000);
		if(DoubleDeath.INSTANCE.isDoubleDeath(pitPlayer.player)) randomGold = randomGold * 2;
		AOutput.send(pitPlayer.player, getCapsDisplayName() + "!&7 Earned &6+" + randomGold + "&6g &7from megastreak!");
		LevelManager.addGold(pitPlayer.player, randomGold);
	}

	@Override
	public String getPrefix(Player player) {
		return "&c&lOVRDV";
	}

	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		return new AItemStackBuilder(Material.BLAZE_POWDER)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, PitPlayer pitPlayer) {
		loreBuilder.addLore(
				"&7On Trigger:",
				"&a\u25a0 &7Earn &b+" + getXPIncrease() + "% XP &7from kills",
				"&a\u25a0 &7Earn &6+" + getGoldIncrease() + "% gold &7from kills",
				"&a\u25a0 &7Permanent &eSpeed I&7",
				"&a\u25a0 &7Immune to &9Slowness&7",
				"",
				"&7BUT:",
				"&c\u25a0 &7Receive &c+" + Misc.getHearts(0.2) + " &7very true",
				"&7damage per 10 kills (only from bots)",
				"",
				"&7On Death:",
				"&e\u25a0 &7Earn between &61000 &7and &65000 gold&7"
		);
	}

	@Override
	public String getSummary() {
		return getCapsDisplayName() + "&7 is a Megastreak grants you permanent &eSpeed I&7, increases your &6gold &7and &bXP&7, " +
				"grants immunity to &9Slowless&7, gain &6gold &7on death, but makes you take very true damage every 10 kills";
	}

	public static int getXPIncrease() {
		return 50;
	}

	public static int getGoldIncrease() {
		return 50;
	}
}
