package dev.kyro.pitsim.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.battlepass.quests.daily.DailyMegastreakQuest;
import dev.kyro.pitsim.commands.FPSCommand;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Formatter;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.upgrades.HandOfGreed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Prosperity extends Megastreak {
	public static Prosperity INSTANCE;
	public static Map<Player, List<Player>> hiddenBotMap = new HashMap<>();

	public Prosperity() {
		super("&eProsperity", "prosperity", 50, 35, 50);
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer()) || !PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer()) ||
				attackEvent.getAttacker() == attackEvent.getDefender()) return;
		if(hasMegastreak(attackEvent.getAttackerPlayer()) && attackEvent.getAttackerPitPlayer().getKills() >= 1000) attackEvent.setCancelled(true);
	}

	@EventHandler
	public void onAttack2(AttackEvent.Pre attackEvent) {
		if(!attackEvent.isAttackerRealPlayer() || !attackEvent.isDefenderPlayer() ||
				!hiddenBotMap.containsKey(attackEvent.getAttackerPlayer())) return;
		List<Player> hiddenBotList = hiddenBotMap.get(attackEvent.getAttackerPlayer());
		if(!hiddenBotList.contains(attackEvent.getDefenderPlayer())) return;
		attackEvent.setCancelled(true);
		AOutput.error(attackEvent.getAttackerPlayer(), "&c&lERROR!&7 You cannot attack players you cannot see!");
	}

	@EventHandler
	public void kill(KillEvent killEvent) {
		if(!hasMegastreak(killEvent.getKillerPlayer()) || !killEvent.isDeadPlayer()) return;
		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
		if(!pitPlayer.isOnMega()) return;
		killEvent.goldMultipliers.add(1 + (getGoldIncrease() / 100.0));
		killEvent.xpMultipliers.add(0.5);

		if(pitPlayer.getKills() < 1000) return;
		hiddenBotMap.putIfAbsent(pitPlayer.player, new ArrayList<>());
		List<Player> hiddenBotList = hiddenBotMap.get(pitPlayer.player);
		hiddenBotList.add(killEvent.getDeadPlayer());
		killEvent.getKillerPlayer().hidePlayer(killEvent.getDeadPlayer());
		killEvent.goldMultipliers.add((double) getFinalGoldMultiplier());
		killEvent.goldCap *= getFinalGoldMultiplier();

		boolean allNonsHidden = true;
		for(Non non : NonManager.nons) {
			if(hiddenBotList.contains(non.non)) continue;
			allNonsHidden = false;
			break;
		}
		if(allNonsHidden) DamageManager.killPlayer(killEvent.getKillerPlayer());
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!hasMegastreak(attackEvent.getAttackerPlayer())) return;
		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		if(!pitPlayer.isOnMega() || NonManager.getNon(attackEvent.getDefender()) == null) return;
		attackEvent.increasePercent += getDamageIncrease();
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		Sounds.MEGA_GENERAL.play(player.getLocation());
		pitPlayer.stats.timesOnProsperity++;
		DailyMegastreakQuest.INSTANCE.onMegastreakComplete(pitPlayer);
	}

	@Override
	public void reset(Player player) {
		hiddenBotMap.remove(player);
		if(FPSCommand.fpsActivePlayers.contains(player)) return;
		for(Non non : NonManager.nons) player.showPlayer(non.non);
	}

	@Override
	public String getPrefix(Player player) {
		return "&e&lPROSP";
	}

	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		return new AItemStackBuilder(Material.SPECKLED_MELON)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, PitPlayer pitPlayer) {
		int prosperityBonus = HandOfGreed.getGoldIncrease(pitPlayer.player);
		loreBuilder.addLore(
				"&7On Trigger:",
				"&a\u25a0 &7Earn &6+" + getGoldIncrease() + "% gold &7from kills",
				"&a\u25a0 &7Deal &c+" + getDamageIncrease() + "% &7damage vs bots"
		);
		if(prosperityBonus != 0) loreBuilder.addLore(
				"&a\u25a0 &e" + HandOfGreed.INSTANCE.name + "&7: Earn &6EXACTLY +&6" + Formatter.commaFormat.format(prosperityBonus) + "g",
				"   &7from kills (ignores modifiers and",
				"   &7gold cap)"
		);
		loreBuilder.addLore(
				"",
				"&7BUT:",
				"&c\u25a0 &7If there are no bots in middle, &cDIE!",
				"&c\u25a0 &7Earn &c-50% &7xp from kills",
				"",
				"&7At 1,000 Kills:",
				"&a\u25a0 &7Gold on kill and gold cap is",
				"   &7increased by &6" + getFinalGoldMultiplier() + "x",
				"&c\u25a0 &7Bots do not respawn when you",
				"   &7kill them"
		);
	}

	@Override
	public String getSummary() {
		return getCapsDisplayName() + "&7 is a Megastreak that";
	}

	public static int getGoldIncrease() {
		return 100;
	}

	public static int getFinalGoldMultiplier() {
		return 5;
	}

	public static int getDamageIncrease() {
		return 33;
	}
}
