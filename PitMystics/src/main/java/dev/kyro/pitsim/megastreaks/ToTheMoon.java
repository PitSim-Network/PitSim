package dev.kyro.pitsim.megastreaks;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.HopperManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.Hopper;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.upgrades.DoubleDeath;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
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

public class ToTheMoon extends Megastreak {
	public boolean hasCalledHopper = false;
	public BukkitTask runnable;

	@Override
	public String getName() {
		return "&b&lMOON";
	}

	@Override
	public String getRawName() {
		return "To the Moon";
	}

	@Override
	public String getPrefix() {
		return "&bTo the Moon";
	}

	@Override
	public List<String> getRefNames() {
		return Arrays.asList("moon");
	}

	@Override
	public int getRequiredKills() {
		return 100;
	}

	@Override
	public int guiSlot() {
		return 15;
	}

	@Override
	public int prestigeReq() {
		return 30;
	}

	@Override
	public int levelReq() {
		return 50;
	}


	@Override
	public ItemStack guiItem() {
		ItemStack item = new ItemStack(Material.ENDER_STONE);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Triggers on: &c100 kills"));
		lore.add("");
		lore.add(ChatColor.GRAY + "On trigger:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Earn &b+150% XP &7from kills"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Gain &b+2 max XP &7per kill"));
		lore.add("");
		lore.add(ChatColor.GRAY + "BUT:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &7Starting from 200, receive &c+5%"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7damage per 20 kills. (Tripled for bots)"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &7Starting from 400, receive &c+" + Misc.getHearts(0.2)));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7damage per 50 kills. (Tripled for bots)"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &7Starting from 700, receive &c+" + Misc.getHearts(0.2)));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7very true damage per 10 kills."));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &7Earn &c-50% &7gold from kills"));

		lore.add("");
		lore.add(ChatColor.GRAY + "On death:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&e\u25a0 &7Earn a permanent &b+5 max XP"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7until you prestige (50 max)"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7(If streak is at least 700)"));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public ToTheMoon(PitPlayer pitPlayer) {
		super(pitPlayer);
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.defender);
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.megastreak.getClass() == ToTheMoon.class) {
			if(pitPlayer.getKills() > 200) {
				double increase = (5 * ((pitPlayer.getKills() - 200) / 20))/100D;
				if(NonManager.getNon(attackEvent.attacker) == null) {
					attackEvent.increasePercent += increase;
				} else attackEvent.increasePercent += (increase * 3);
			}
			if(pitPlayer.getKills() > 400) {
				if(NonManager.getNon(attackEvent.attacker) == null) {
					attackEvent.increase += 0.2 * ((pitPlayer.getKills() - 400) / 50);
				} else attackEvent.increase += 0.6 * ((pitPlayer.getKills() - 400) / 50);
			}
			if(pitPlayer.getKills() > 700) {
				attackEvent.veryTrueDamage += 0.2 * ((pitPlayer.getKills() - 700) / 10);
			}
		}
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.killer);
		killEvent.xpCap += pitPlayer.moonBonus;
		if(pitPlayer != this.pitPlayer) return;
		if(!(pitPlayer.megastreak.getClass() == ToTheMoon.class)) return;
		if(!playerIsOnMega(killEvent)) return;

		killEvent.xpCap += (pitPlayer.getKills() - 100) * 2;
		killEvent.xpMultipliers.add(2.5);
		killEvent.goldMultipliers.add(0.5);

		if(pitPlayer.getKills() > 1200 && !hasCalledHopper) {
			HopperManager.callHopper("PayForTruce", Hopper.Type.VENOM, killEvent.killer);
			hasCalledHopper = true;
		}
	}

	@Override
	public void proc() {

		Sounds.MEGA_GENERAL.play(pitPlayer.player.getLocation());
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(pitPlayer.megastreak.getClass() == ToTheMoon.class && pitPlayer.megastreak.isOnMega()) {
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
					"&c&lMEGASTREAK! %luckperms_prefix%" + pitPlayer.player.getDisplayName() + " &7activated &b&lTO THE MOON&7!");
			AOutput.send(player, PlaceholderAPI.setPlaceholders(pitPlayer.player, streakMessage));
		}

		if(pitPlayer.stats != null) pitPlayer.stats.timesOnMoon++;
	}

	@Override
	public void reset() {
		hasCalledHopper = false;

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}

		if(pitPlayer.megastreak.isOnMega() && pitPlayer.getKills() >= 700) {
			int cap = 5;
			if(DoubleDeath.INSTANCE.isDoubleDeath(pitPlayer.player)) cap *= 2;
			if(pitPlayer.moonBonus + cap > 50) cap = 50 - pitPlayer.moonBonus;
			if(cap > 0) {
				pitPlayer.moonBonus += cap;
				AOutput.send(pitPlayer.player, "&b&lTO THE MOON! &7Gained &b+" + cap + " max XP &7until you prestige! (" + pitPlayer.moonBonus + "/50)");
			}
			FileConfiguration playerData = APlayerData.getPlayerData(pitPlayer.player);
			playerData.set("moonbonus", pitPlayer.moonBonus);
			APlayerData.savePlayerData(pitPlayer.player);
		}

		if(runnable != null) runnable.cancel();
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
	}

	@Override
	public void kill() {

		if(!isOnMega()) return;
	}
}
