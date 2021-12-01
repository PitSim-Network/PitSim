package dev.kyro.pitsim.megastreaks;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.LevelManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.upgrades.DoubleDeath;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Highlander extends Megastreak {
	public Highlander(PitPlayer pitPlayer) {
		super(pitPlayer);

	}

	public BukkitTask runnable;

	@Override
	public String getName() {
		return "&6&lHIGH";
	}

	@Override
	public String getRawName() {
		return "Highlander";
	}

	@Override
	public String getPrefix() {
		return "&6Highlander";
	}

	@Override
	public List<String> getRefNames() {
		return Arrays.asList("highlander", "high");
	}

	@Override
	public int getRequiredKills() {
		return 50;
	}

	@Override
	public int guiSlot() {
		return 13;
	}

	@Override
	public int prestigeReq() {
		return 17;
	}

	@Override
	public int levelReq() {
		return 90;
	}

	@Override
	public ItemStack guiItem() {


		ItemStack item = new ItemStack(Material.GOLD_BOOTS);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Triggers on: &c50 kills"));
		lore.add("");
		lore.add(ChatColor.GRAY + "On trigger:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Perma &eSpeed I&7"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Earn &6+150% gold &7from kills"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Deal &c+33% &7damage vs bountied players"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Deal &c+25% &7damage vs bots"));
		lore.add("");
		lore.add(ChatColor.GRAY + "BUT:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &7Heal &cless &7per kill over 50"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &7Earn &c-50% &7XP from kills"));
		lore.add("");
		lore.add(ChatColor.GRAY + "On death:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&e\u25a0 &7Earn your own bounty as well"));

		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.killer);
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.megastreak.playerIsOnMega(killEvent) && pitPlayer.megastreak.getClass() == Highlander.class) {
			killEvent.goldMultipliers.add(2.5);
			killEvent.xpMultipliers.add(0.5);
		}
	}

	@EventHandler
	public void ohHeal(HealEvent healEvent) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(healEvent.player);
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.megastreak.isOnMega() && pitPlayer.megastreak.getClass() == Highlander.class) {
			int ks = (int) Math.floor(pitPlayer.getKills());
			healEvent.multipliers.add(1 / ((ks - 50) / 100D + 1));
		}
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.attacker);
		PitPlayer pitDefender = PitPlayer.getPitPlayer(attackEvent.defender);
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.megastreak.isOnMega() && pitPlayer.megastreak.getClass() == Highlander.class) {
			if(pitDefender.bounty > 0) {
				attackEvent.increasePercent += 33 / 100D;
			}
			if(NonManager.getNon(attackEvent.defender) != null) {
				attackEvent.increasePercent += 25 / 100D;
			}
		}
	}

	@Override
	public void proc() {

		Sounds.MEGA_GENERAL.play(pitPlayer.player.getLocation());
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(pitPlayer.megastreak.getClass() == Highlander.class && pitPlayer.megastreak.isOnMega()) {
					Misc.applyPotionEffect(pitPlayer.player, PotionEffectType.SPEED, 200, 0, true, false);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 60L);

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}

		pitPlayer.megastreak = this;
		for(Player player : Bukkit.getOnlinePlayers()) {
			PitPlayer pitPlayer2 = PitPlayer.getPitPlayer(player);
			if(pitPlayer2.disabledStreaks) continue;
			String streakMessage = ChatColor.translateAlternateColorCodes('&',
					"&c&lMEGASTREAK! %luckperms_prefix%" + pitPlayer.player.getDisplayName() + " &7activated &6&lHIGHLANDER&7!");
			AOutput.send(player, PlaceholderAPI.setPlaceholders(pitPlayer.player, streakMessage));
		}

		if(pitPlayer.stats != null) pitPlayer.stats.timesOnHighlander++;
	}

	@Override
	public void reset() {

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}

		if(isOnMega()) {
			if(DoubleDeath.INSTANCE.isDoubleDeath(pitPlayer.player)) pitPlayer.bounty = pitPlayer.bounty * 2;
			LevelManager.addGold(pitPlayer.player, pitPlayer.bounty);
			if(pitPlayer.bounty != 0 && pitPlayer.megastreak.isOnMega()) {
				AOutput.send(pitPlayer.player, "&6&lHIGHLANDER! &7Earned &6+" + pitPlayer.bounty + "&6g &7from megastreak!");
				pitPlayer.bounty = 0;
			}
		}
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
	}
}
