package dev.kyro.pitsim.megastreaks;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.battlepass.quests.CompleteUbersQuest;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.ItemManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

public class Uberstreak extends Megastreak {
	public static double SHARD_MULTIPLIER = 2;

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
	public int initialLevelReq() {
		return 100;
	}

	@Override
	public int getRequiredKills() {
		return 100;
	}

	@Override
	public ItemStack guiItem() {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");

		ItemStack item = new ItemStack(Material.GOLD_SWORD);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Triggers on: &c100 kills"));
		lore.add("");
		lore.add(ChatColor.GRAY + "On trigger:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Immune to enchants that &emove &7you"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &d" + decimalFormat.format(SHARD_MULTIPLIER) + "x &7chance to find &aAncient Gem Shards"));
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
		if(!attackEvent.isAttackerPlayer()) return;
		PitPlayer pitAttacker = attackEvent.getAttackerPitPlayer();
		if(pitAttacker != this.pitPlayer || pitAttacker.megastreak.getClass() != Uberstreak.class || pitAttacker.megastreak.isOnMega())
			return;

		Map<PitEnchant, Integer> attackerEnchantMap = attackEvent.getAttackerEnchantMap();

		PitEnchant exe = EnchantManager.getEnchant("executioner");
		if(uberEffects.contains(UberEffect.EXE_SUCKS) && attackerEnchantMap.containsKey(exe))
			attackerEnchantMap.put(exe,
					Math.max(0, attackerEnchantMap.get(exe) - 1));

		PitEnchant perun = EnchantManager.getEnchant("perun");
		if(uberEffects.contains(UberEffect.PERUN_SUCKS) && attackerEnchantMap.containsKey(perun))
			attackerEnchantMap.put(perun,
					Math.max(0, attackerEnchantMap.get(perun) - 1));
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isDefenderPlayer()) return;
		PitPlayer pitDefender = PitPlayer.getPitPlayer(attackEvent.getDefender());
		if(pitDefender == this.pitPlayer && pitDefender.megastreak.getClass() == Uberstreak.class) {
			if(uberEffects.contains(UberEffect.TAKE_MORE_DAMAGE)) attackEvent.multipliers.add(1.25);
			if(uberEffects.contains(UberEffect.TAKE_LESS_DAMAGE)) attackEvent.multipliers.add(0.9);
		}

		if(!attackEvent.isAttackerPlayer()) return;
		PitPlayer pitAttacker = PitPlayer.getPitPlayer(attackEvent.getAttacker());
		if(pitAttacker != this.pitPlayer || pitAttacker.megastreak.getClass() != Uberstreak.class) return;
		if(pitAttacker.megastreak.isOnMega()) {
			if(NonManager.getNon(attackEvent.getDefender()) != null) attackEvent.multipliers.add(0.5);
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
			double current = event.kills;

			if(current == 200) {
				Sounds.UBER_200.play(pitPlayer.player);
				UberEffect uberEffect = UberEffect.getRandom(uberEffects);
				if(uberEffects.size() < 1) uberEffects.add(uberEffect);
				if(uberEffect == UberEffect.SKIP_100) zoom();
				pitPlayer.updateMaxHealth();
				AOutput.send(pitPlayer.player, "&d&lUBERSTREAK &7Random Effect: " + uberEffect.description);
			}
			if(current == 300) {
				Sounds.UBER_300.play(pitPlayer.player);
				UberEffect uberEffect = UberEffect.getRandom(uberEffects);
				if(uberEffects.size() < 2) uberEffects.add(uberEffect);
				if(uberEffect == UberEffect.SKIP_100) zoom();
				pitPlayer.updateMaxHealth();
				AOutput.send(pitPlayer.player, "&d&lUBERSTREAK &7Random Effect: " + uberEffect.description);
			}
			if(current == 400) {
				Sounds.UBER_400.play(pitPlayer.player);
				UberEffect uberEffect = UberEffect.getRandom(uberEffects);
				if(uberEffects.size() < 3) uberEffects.add(uberEffect);
				if(uberEffect == UberEffect.SKIP_100) zoom();
				pitPlayer.updateMaxHealth();
				AOutput.send(pitPlayer.player, "&d&lUBERSTREAK &7Random Effect: " + uberEffect.description);
			}
			if(current == 500) {
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
				Misc.playKillSound(pitPlayer);
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
			if(pitPlayer2.streaksDisabled) continue;
			String message2 = ChatColor.translateAlternateColorCodes('&',
					"&c&lMEGASTREAK! %luckperms_prefix%" + pitPlayer.player.getDisplayName() + "&7 activated &d&lUBERSTREAK&7!");

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

		if(pitPlayer.dailyUbersLeft <= 0) {
			pitPlayer.megastreak = new NoMegastreak(pitPlayer);
			pitPlayer.save(true);
			stop();
		}

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}

		Uberdrop uberdrop = Uberdrop.getRandom();
		uberdrop.give(pitPlayer);

		if(pitPlayer.stats != null) pitPlayer.stats.ubersCompleted++;
		CompleteUbersQuest.INSTANCE.completeUber(pitPlayer);
	}

	public static void uberMessage(String message, PitPlayer pitPlayer) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			PitPlayer pitPlayer2 = PitPlayer.getPitPlayer(player);
			if(pitPlayer2.streaksDisabled) continue;
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

	public static List<Uberdrop> weightedDropList = new ArrayList<>();

	public enum Uberdrop {
		JEWEL_SWORD(5),
		JEWEL_BOW(5),
		JEWEL_PANTS(5),
		JEWEL_BUNDLE(2),
		FEATHER_1(3),
		FEATHER_2(2),
		FEATHER_3(1),
		VILE_3(6),
		VILE_5(4),
		VILE_10(2),
		P1_HELMET(5),
		P1_CHESTPLATE(5),
		P1_LEGGINGS(7),
		P1_BOOTS(5);

		public int weight;

		Uberdrop(int weight) {
			this.weight = weight;
			for(int i = 0; i < weight; i++) weightedDropList.add(this);
		}

		public static Uberdrop getRandom() {
			List<Uberdrop> tempDropList = new ArrayList<>(weightedDropList);
			Collections.shuffle(tempDropList);
			return tempDropList.get(0);
		}

		public void give(PitPlayer pitPlayer) {
			Player player = pitPlayer.player;
			if(this == JEWEL_SWORD) {
				ItemStack jewelSword = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.JEWEL);
				jewelSword = ItemManager.enableDropConfirm(jewelSword);
				NBTItem nbtItemSword = new NBTItem(jewelSword);
				nbtItemSword.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
				EnchantManager.setItemLore(nbtItemSword.getItem(), player);
				AUtil.giveItemSafely(player, nbtItemSword.getItem());
				uberMessage("&3Hidden Jewel " + MysticType.SWORD, pitPlayer);
			} else if(this == JEWEL_BOW) {
				ItemStack jewelBow = FreshCommand.getFreshItem(MysticType.BOW, PantColor.JEWEL);
				jewelBow = ItemManager.enableDropConfirm(jewelBow);
				NBTItem nbtItemBow = new NBTItem(jewelBow);
				nbtItemBow.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
				EnchantManager.setItemLore(nbtItemBow.getItem(), player);
				AUtil.giveItemSafely(player, nbtItemBow.getItem());
				uberMessage("&3Hidden Jewel " + MysticType.BOW, pitPlayer);
			} else if(this == JEWEL_PANTS) {
				ItemStack jewel = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.JEWEL);
				jewel = ItemManager.enableDropConfirm(jewel);
				NBTItem nbtItem = new NBTItem(jewel);
				nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
				EnchantManager.setItemLore(nbtItem.getItem(), player);
				AUtil.giveItemSafely(player, nbtItem.getItem());
				uberMessage("&3Hidden Jewel " + MysticType.PANTS, pitPlayer);
			} else if(this == JEWEL_BUNDLE) {
				ItemStack jbsword = FreshCommand.getFreshItem(MysticType.SWORD, PantColor.JEWEL);
				jbsword = ItemManager.enableDropConfirm(jbsword);
				NBTItem nbtjbsword = new NBTItem(jbsword);
				nbtjbsword.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
				EnchantManager.setItemLore(nbtjbsword.getItem(), player);
				AUtil.giveItemSafely(player, nbtjbsword.getItem());

				ItemStack jbbow = FreshCommand.getFreshItem(MysticType.BOW, PantColor.JEWEL);
				jbbow = ItemManager.enableDropConfirm(jbbow);
				NBTItem nbtjbbow = new NBTItem(jbbow);
				nbtjbbow.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
				EnchantManager.setItemLore(nbtjbbow.getItem(), player);
				AUtil.giveItemSafely(player, nbtjbbow.getItem());

				ItemStack jb = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.JEWEL);
				jb = ItemManager.enableDropConfirm(jb);
				NBTItem nbtjb = new NBTItem(jb);
				nbtjb.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
				EnchantManager.setItemLore(nbtjb.getItem(), player);
				AUtil.giveItemSafely(player, nbtjb.getItem());

				uberMessage("&3Hidden Jewel Bundle", pitPlayer);
			} else if(this == FEATHER_1) {
				FunkyFeather.giveFeather(player, 1);
				uberMessage("&31x Funky Feather", pitPlayer);
			} else if(this == FEATHER_2) {
				FunkyFeather.giveFeather(player, 2);
				uberMessage("&32x Funky Feather", pitPlayer);
			} else if(this == FEATHER_3) {
				FunkyFeather.giveFeather(player, 3);
				uberMessage("&33x Funky Feather", pitPlayer);
			} else if(this == VILE_3) {
				ChunkOfVile.giveVile(player, 3);
				uberMessage("&53x Chunk of Vile", pitPlayer);
			} else if(this == VILE_5) {
				ChunkOfVile.giveVile(player, 5);
				uberMessage("&55x Chunk of Vile", pitPlayer);
			} else if(this == VILE_10) {
				ChunkOfVile.giveVile(player, 10);
				uberMessage("&510x Chunk of Vile", pitPlayer);
			} else if(this == P1_HELMET) {
				ProtArmor.getArmor(player, "helmet");
				uberMessage("&bProtection I Diamond Helmet", pitPlayer);
			} else if(this == P1_CHESTPLATE) {
				ProtArmor.getArmor(player, "chestplate");
				uberMessage("&bProtection I Diamond Chestplate", pitPlayer);
			} else if(this == P1_LEGGINGS) {
				ProtArmor.getArmor(player, "leggings");
				uberMessage("&bProtection I Diamond Leggings", pitPlayer);
			} else if(this == P1_BOOTS) {
				ProtArmor.getArmor(player, "boots");
				uberMessage("&bProtection I Diamond Boots", pitPlayer);
			}
		}
	}
}
