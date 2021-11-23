package dev.kyro.pitsim.megastreaks;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.*;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.IncrementKillsEvent;
import dev.kyro.pitsim.misc.*;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Uberstreak extends Megastreak {
	public List<UberEffect> uberEffects = new ArrayList<>();
	MysticType mysticType;

	public Uberstreak(PitPlayer pitPlayer) {
		super(pitPlayer);
	}

	@Override
	public String getName() {
		return "&d&lUBER";
	}

	@Override
	public String getRawName() {
		return "Uberstreak";
	}

	@Override
	public String getPrefix() {
		return "&dUberstreak";
	}

	@Override
	public List<String> getRefNames() {
		return Arrays.asList("uberstreak");
	}

	@Override
	public int guiSlot() {
		return 14;
	}

	@Override
	public int prestigeReq() {
		return 20;
	}

	@Override
	public int levelReq() {
		return 100;
	}

	@Override
	public int getRequiredKills() {
		return 100;
	}

	@Override
	public ItemStack guiItem() {
		ItemStack item = new ItemStack(Material.GOLD_SWORD);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Triggers on: &c100 kills"));
		lore.add("");
		lore.add(ChatColor.GRAY + "On trigger:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Immune to enchants that &emove &7you"));
		lore.add("");
		lore.add(ChatColor.GRAY + "BUT:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &7Deal &c-50% &7damage to nons"));
		lore.add("");
		lore.add(ChatColor.GRAY + "During the streak:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&d\u25a0 &7200 kills: Random &dbuff &7or &cdebuff"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&d\u25a0 &7300 kills: Random &dbuff &7or &cdebuff"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&d\u25a0 &7400 kills: Random &dbuff &7or &cdebuff"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&d\u25a0 &7500 kills: &cNo longer gain health"));
		lore.add("");
		lore.add(ChatColor.GRAY + "On death:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&e\u25a0 &7Earn a random &dUberdrop&7"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7(If streak is at least 500)"));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	@EventHandler
	public void onPreAttack(AttackEvent.Pre attackEvent) {
		PitPlayer pitAttacker = PitPlayer.getPitPlayer(attackEvent.attacker);
		if(pitAttacker != this.pitPlayer || pitAttacker.megastreak.getClass() != Uberstreak.class || pitAttacker.megastreak.isOnMega()) return;

		Map<PitEnchant, Integer> attackerEnchantMap = attackEvent.getAttackerEnchantMap();

		PitEnchant exe = EnchantManager.getEnchant("executioner");
		if(uberEffects.contains(UberEffect.EXE_SUCKS) && attackerEnchantMap.containsKey(exe)) attackerEnchantMap.put(exe,
				Math.max(0, attackerEnchantMap.get(exe) - 1));

		PitEnchant perun = EnchantManager.getEnchant("perun");
		if(uberEffects.contains(UberEffect.PERUN_SUCKS) && attackerEnchantMap.containsKey(perun)) attackerEnchantMap.put(perun,
				Math.max(0, attackerEnchantMap.get(perun) - 1));
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		PitPlayer pitDefender = PitPlayer.getPitPlayer(attackEvent.defender);
		if(pitDefender == this.pitPlayer && pitDefender.megastreak.getClass() == Uberstreak.class) {
			if(uberEffects.contains(UberEffect.TAKE_MORE_DAMAGE)) attackEvent.multiplier.add(1.25);
			if(uberEffects.contains(UberEffect.TAKE_LESS_DAMAGE)) attackEvent.multiplier.add(0.9);
		}

		PitPlayer pitAttacker = PitPlayer.getPitPlayer(attackEvent.attacker);
		if(pitAttacker != this.pitPlayer || pitAttacker.megastreak.getClass() != Uberstreak.class) return;
		if(pitAttacker.megastreak.isOnMega()) {
			if(NonManager.getNon(attackEvent.defender) != null) attackEvent.multiplier.add(0.5);
		}
	}

	@EventHandler
	public void onHeal(HealEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.player);
		if(pitPlayer != this.pitPlayer) return;

		if(uberEffects.contains(UberEffect.HEAL_LESS)) event.multipliers.add(0.75);

		if(pitPlayer.getKills() < 500) return;
		event.multipliers.add(0D);
	}

	@EventHandler
	public void onKill(IncrementKillsEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.player);
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.megastreak.isOnMega() && pitPlayer.megastreak.getClass() == Uberstreak.class) {

			double current = event.currentAmount;
			double newKills = event.newAmount;

			if(current < 200 && newKills >= 200) {
				Sounds.UBER_200.play(pitPlayer.player);
				UberEffect uberEffect = UberEffect.getRandom(uberEffects);
				if(uberEffects.size() < 1) uberEffects.add(uberEffect);
				if(uberEffect == UberEffect.SKIP_100) zoom();
				pitPlayer.updateMaxHealth();
				AOutput.send(pitPlayer.player, "&d&lUBERSTREAK &7Random Effect: " + uberEffect.description);
			}
			if(current < 300 && newKills >= 300) {
				Sounds.UBER_300.play(pitPlayer.player);
				UberEffect uberEffect = UberEffect.getRandom(uberEffects);
				if(uberEffects.size() < 2) uberEffects.add(uberEffect);
				if(uberEffect == UberEffect.SKIP_100) zoom();
				pitPlayer.updateMaxHealth();
				AOutput.send(pitPlayer.player, "&d&lUBERSTREAK &7Random Effect: " + uberEffect.description);
			}
			if(current < 400 && newKills >= 400) {
				Sounds.UBER_400.play(pitPlayer.player);
				UberEffect uberEffect = UberEffect.getRandom(uberEffects);
				if(uberEffects.size() < 3) uberEffects.add(uberEffect);
				if(uberEffect == UberEffect.SKIP_100) zoom();
				pitPlayer.updateMaxHealth();
				AOutput.send(pitPlayer.player, "&d&lUBERSTREAK &7Random Effect: " + uberEffect.description);
			}
			if(current < 500 && newKills >= 500) {
				Sounds.UBER_500.play(pitPlayer.player);
				pitPlayer.updateMaxHealth();
				AOutput.send(pitPlayer.player, "&d&lUBERSTREAK &cCannot heal");
			}
		}
	}

	public void zoom() {
		new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {
				if(count++ == 50) {
					cancel();
					return;
				}

				pitPlayer.incrementKills();
				Misc.multiKill(pitPlayer.player);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 3L);
	}

	@Override
	public void proc() {
		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}

		Sounds.UBER_100.play(pitPlayer.player);
		for(Player player : Bukkit.getOnlinePlayers()) {
			PitPlayer pitPlayer2 = PitPlayer.getPitPlayer(player);
			if(pitPlayer2.disabledStreaks) continue;
			String message2 = ChatColor.translateAlternateColorCodes('&',
					"&c&lMEGASTREAK!&7 %luckperms_prefix%" + pitPlayer.player.getDisplayName() + "&7 activated &d&lUBERSTREAK&7!");

			player.sendMessage(PlaceholderAPI.setPlaceholders(pitPlayer.player, message2));
		}
		pitPlayer.updateMaxHealth();
		AOutput.send(pitPlayer.player, "&d&lUBERSTREAK &cDeal -50% damage to nons");
	}

	@Override
	public void reset() {
		pitPlayer.updateMaxHealth();
		uberEffects.clear();
		if(pitPlayer.getKills() < 500) return;
		if(!isOnMega()) return;

		if(pitPlayer.uberReset == 0) {
			pitPlayer.uberReset = System.currentTimeMillis() / 1000L;
		}
		pitPlayer.dailyUbersLeft = pitPlayer.dailyUbersLeft - 1;

		FileConfiguration playerData = APlayerData.getPlayerData(pitPlayer.player);
		playerData.set("ubercooldown", pitPlayer.uberReset);
		playerData.set("ubersleft", pitPlayer.dailyUbersLeft);
		APlayerData.savePlayerData(pitPlayer.player);

		if(pitPlayer.dailyUbersLeft <= 0) {
			pitPlayer.megastreak = new NoMegastreak(pitPlayer);
			playerData.set("megastreak", pitPlayer.megastreak.getRawName());
			APlayerData.savePlayerData(pitPlayer.player);
			stop();
		}

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}

		int rand = (int) (Math.random() * 10);
		if(rand == 0 || rand == 1 || rand == 2) {

			int rand2 = (int) (Math.random() * 3);
			if(rand2 == 0) mysticType = MysticType.SWORD;
			else if(rand2 == 1) mysticType = MysticType.BOW;
			else if(rand2 == 2) mysticType = MysticType.PANTS;

			ItemStack jewel = FreshCommand.getFreshItem(mysticType, PantColor.JEWEL);
			jewel = ItemManager.enableDropConfirm(jewel);
			assert jewel != null;
			NBTItem nbtItem = new NBTItem(jewel);
			nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);

			EnchantManager.setItemLore(nbtItem.getItem());

			AUtil.giveItemSafely(pitPlayer.player, nbtItem.getItem(), true);
			uberMessage("&3Hidden Jewel " + mysticType.displayName, pitPlayer);

		} else if(rand == 3 || rand == 4 || rand == 5) {
			int rand2 = (int) (Math.random() * 3);
			if(rand2 == 0) {
				FunkyFeather.giveFeather(pitPlayer.player, 1);
				uberMessage("&3Funky Feather", pitPlayer);
			} else if(rand2 == 1) {
				FunkyFeather.giveFeather(pitPlayer.player, 2);
				uberMessage("&32x Funky Feather", pitPlayer);
			}
			else if(rand2 == 2) {
				FunkyFeather.giveFeather(pitPlayer.player, 3);
				uberMessage("&33x Funky Feather", pitPlayer);
			}
		} else if(rand == 9) {
			int rand2 = (int) (Math.random() * 3);
			if(rand2 == 0) {
				ChunkOfVile.giveVile(pitPlayer.player, 2);
				uberMessage("&52x Chunk of Vile", pitPlayer);
			}
			else if(rand2 == 1) {
				ChunkOfVile.giveVile(pitPlayer.player, 4);
				uberMessage("&54x Chunk of Vile", pitPlayer);
			}
			else if(rand2 == 2) {
				ChunkOfVile.giveVile(pitPlayer.player, 6);
				uberMessage("&56x Chunk of Vile", pitPlayer);
			}
		} else if(rand == 6 || rand == 7 || rand == 8) {
			int rand2 = (int) (Math.random() * 4);
			if(rand2 == 0) {
				ProtArmor.getArmor(pitPlayer.player, "helmet");
				uberMessage("&bProtection I Diamond Helmet", pitPlayer);
			}
			else if(rand2 == 1) {
				ProtArmor.getArmor(pitPlayer.player, "chestplate");
				uberMessage("&bProtection I Diamond Chestplate", pitPlayer);
			}
			else if(rand2 == 2) {
				ProtArmor.getArmor(pitPlayer.player, "leggings");
				uberMessage("&bProtection I Diamond Leggings", pitPlayer);
			}
			else if(rand2 == 3) {
				ProtArmor.getArmor(pitPlayer.player, "boots");
				uberMessage("&bProtection I Diamond Boots", pitPlayer);
			}
		}

		if(pitPlayer.stats != null) pitPlayer.stats.ubersCompleted++;
	}

	public static void uberMessage(String message, PitPlayer pitPlayer) {
	if(PitEventManager.majorEvent) return;
		for(Player player : Bukkit.getOnlinePlayers()) {
			PitPlayer pitPlayer2 = PitPlayer.getPitPlayer(player);
			if(pitPlayer2.disabledStreaks) continue;
			String message2 = ChatColor.translateAlternateColorCodes('&',
					"&d&lUBERDROP!&7 %luckperms_prefix%" + pitPlayer.player.getDisplayName() + "&7 obtained an &dUberdrop: &7" + message);
			player.sendMessage(PlaceholderAPI.setPlaceholders(pitPlayer.player, message2));
		}
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
	}

	@Override
	public void kill() {

		if(!isOnMega()) return;
	}

	public enum UberEffect {
		NO_SPEED("&CYou cannot gain speed"),
		TAKE_MORE_DAMAGE("&CTake 25% more damage"),
		EXE_SUCKS("&cExecutioner level -1"),
		PERUN_SUCKS("&cCombo: Perun level -1"),
		LOSE_MAX_HEALTH("&c-" + Misc.getHearts(4) + " max hp"),
		HEAL_LESS("&cHeal 25% less from all sources"),

		NONE("&7Nothing happens... Yay?"),

		TAKE_LESS_DAMAGE("&dTake 10% less damage"),
		SKIP_100("&dZoooom");

		public String description;

		UberEffect(String description) {
			this.description = description;
		}

		public static UberEffect getRandom(List<UberEffect> uberEffects) {
			List<UberEffect> possible = new ArrayList<>();
			for(UberEffect value : values()) {
				if(!uberEffects.contains(value)) possible.add(value);
			}

			Collections.shuffle(possible);
			return possible.get(0);
		}
	}
}
