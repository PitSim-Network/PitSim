package net.pitsim.spigot.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.items.MysticFactory;
import net.pitsim.spigot.items.diamond.ProtBoots;
import net.pitsim.spigot.items.diamond.ProtChestplate;
import net.pitsim.spigot.items.diamond.ProtHelmet;
import net.pitsim.spigot.items.diamond.ProtLeggings;
import net.pitsim.spigot.items.misc.ChunkOfVile;
import net.pitsim.spigot.items.misc.FunkyFeather;
import net.pitsim.spigot.battlepass.quests.CompleteUbersQuest;
import net.pitsim.spigot.battlepass.quests.daily.DailyMegastreakQuest;
import net.pitsim.spigot.controllers.ItemFactory;
import net.pitsim.spigot.controllers.NonManager;
import net.pitsim.spigot.controllers.objects.Megastreak;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.controllers.objects.PluginMessage;
import net.pitsim.spigot.cosmetics.CosmeticManager;
import net.pitsim.spigot.cosmetics.CosmeticType;
import net.pitsim.spigot.cosmetics.PitCosmetic;
import net.pitsim.spigot.enchants.overworld.ComboPerun;
import net.pitsim.spigot.enchants.overworld.Executioner;
import net.pitsim.spigot.enums.MysticType;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.events.HealEvent;
import net.pitsim.spigot.events.IncrementKillsEvent;
import net.pitsim.spigot.misc.CustomSerializer;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.PitLoreBuilder;
import net.pitsim.spigot.misc.Sounds;
import net.pitsim.spigot.upgrades.DoubleDeath;
import net.pitsim.spigot.upgrades.VentureCapitalist;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

public class Uberstreak extends Megastreak {
	public static Uberstreak INSTANCE;

	public static double SHARD_MULTIPLIER = 2;
	public static List<Uberdrop> weightedDropList = new ArrayList<>();

	private static final Map<Player, List<UberEffect>> uberEffectMap = new HashMap<>();

	public Uberstreak() {
		super("&dUberstreak", "uberstreak", 100, 20, 100);
		hasDailyLimit = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		uberEffectMap.remove(player);
	}

	@EventHandler
	public void onPreAttack(AttackEvent.Pre attackEvent) {
		if(!hasMegastreak(attackEvent.getAttackerPlayer())) return;
		PitPlayer pitAttacker = attackEvent.getAttackerPitPlayer();
		if(!pitAttacker.isOnMega()) return;

		List<UberEffect> uberEffects = getUberEffects(attackEvent.getAttackerPlayer());
		Map<PitEnchant, Integer> attackerEnchantMap = attackEvent.getAttackerEnchantMap();

		if(uberEffects.contains(UberEffect.EXE_SUCKS) && attackerEnchantMap.containsKey(Executioner.INSTANCE))
			attackerEnchantMap.put(Executioner.INSTANCE, Math.max(0, attackerEnchantMap.get(Executioner.INSTANCE) - 1));

		if(uberEffects.contains(UberEffect.PERUN_SUCKS) && attackerEnchantMap.containsKey(ComboPerun.INSTANCE))
			attackerEnchantMap.put(ComboPerun.INSTANCE, Math.max(0, attackerEnchantMap.get(ComboPerun.INSTANCE) - 1));
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		PitPlayer pitDefender = PitPlayer.getPitPlayer(attackEvent.getDefender());
		if(hasMegastreak(attackEvent.getDefenderPlayer()) && pitDefender.isOnMega()) {
			List<UberEffect> uberEffects = getUberEffects(attackEvent.getDefenderPlayer());
			if(uberEffects.contains(UberEffect.TAKE_MORE_DAMAGE)) attackEvent.multipliers.add(1.25);
			if(uberEffects.contains(UberEffect.TAKE_LESS_DAMAGE)) attackEvent.multipliers.add(0.9);
		}

		PitPlayer pitAttacker = PitPlayer.getPitPlayer(attackEvent.getAttacker());
		if(hasMegastreak(attackEvent.getAttackerPlayer()) && pitAttacker.isOnMega()) {
			if(NonManager.getNon(attackEvent.getDefender()) != null) attackEvent.multipliers.add(0.5);
		}
	}

	@EventHandler
	public void onHeal(HealEvent event) {
		Player player = event.getPlayer();
		if(!hasMegastreak(player)) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.player);
		if(!pitPlayer.isOnMega()) return;

		List<UberEffect> uberEffects = getUberEffects(player);
		if(uberEffects.contains(UberEffect.HEAL_LESS)) event.multipliers.add(0.75);

		if(pitPlayer.getKills() >= 500) event.multipliers.add(0D);
	}

	@EventHandler
	public void onKill(IncrementKillsEvent event) {
		Player player = event.getPlayer();
		if(!hasMegastreak(player)) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.isOnMega()) return;

		if(event.getKills() == 200) {
			Sounds.UBER_200.play(pitPlayer.player);
		} else if(event.getKills() == 300) {
			Sounds.UBER_300.play(pitPlayer.player);
		} else if(event.getKills() == 400) {
			Sounds.UBER_400.play(pitPlayer.player);
		} else if(event.getKills() == 500) {
			Sounds.UBER_500.play(pitPlayer.player);
			AOutput.send(pitPlayer.player, getCapsDisplayName() + "!&c Cannot heal");
			return;
		} else {
			return;
		}

		List<UberEffect> uberEffects = getUberEffects(player);
		UberEffect uberEffect = UberEffect.getRandom(uberEffects);
		uberEffects.add(uberEffect);
		if(uberEffect == UberEffect.SKIP_100) zoom(pitPlayer);
		pitPlayer.updateMaxHealth();
		AOutput.send(pitPlayer.player, getCapsDisplayName() + "!&7 Random Effect: " + uberEffect.description);
	}

	public void zoom(PitPlayer pitPlayer) {
		new BukkitRunnable() {
			int count = 0;

			@Override
			public void run() {
				if(count++ == 50) {
					cancel();
					return;
				}

				pitPlayer.incrementKills();
				PitCosmetic botKill = CosmeticManager.getEquippedCosmetic(pitPlayer, CosmeticType.BOT_KILL_EFFECT);
				Misc.playKillSound(pitPlayer, botKill);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 3L);
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		Sounds.UBER_100.play(pitPlayer.player);
		pitPlayer.updateMaxHealth();
		AOutput.send(pitPlayer.player, getCapsDisplayName() + "!&c Deal -50% damage to bots");
	}

	@Override
	public void reset(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.updateMaxHealth();
		uberEffectMap.clear();

		if(pitPlayer.getKills() < 500) return;

		PitPlayer.MegastreakLimit limit = pitPlayer.getMegastreakCooldown(INSTANCE);
		limit.completeStreak(pitPlayer);

		Uberdrop uberdrop = Uberdrop.getRandom();
		for(int i = 0; i < (DoubleDeath.INSTANCE.isDoubleDeath(pitPlayer.player) ? 2 : 1); i++) uberdrop.give(pitPlayer);

		pitPlayer.stats.ubersCompleted++;
		CompleteUbersQuest.INSTANCE.onUberComplete(pitPlayer);
		DailyMegastreakQuest.INSTANCE.onMegastreakComplete(pitPlayer);
	}

	@Override
	public int getMaxDailyStreaks(PitPlayer pitPlayer) {
		return 5 + VentureCapitalist.getUberIncrease(pitPlayer.player);
	}

	public static void sendUberMessage(String displayName, ItemStack itemStack) {
		TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&',
				"&d&lUBERDROP!&7 " + displayName + " &7obtained an &dUberdrop: &7"));
		message.addExtra(Misc.createItemHover(itemStack));
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) onlinePlayer.sendMessage(message);
	}

	public static ItemStack getUberDropDisplayStack(String displayName, ItemStack displayStack) {
		return new AItemStackBuilder(displayStack).setName(displayName).getItemStack();
	}

	public static List<UberEffect> getUberEffects(Player player) {
		uberEffectMap.putIfAbsent(player, new ArrayList<>());
		return uberEffectMap.get(player);
	}

	@Override
	public String getPrefix(Player player) {
		return "&d&lUBER";
	}

	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		return new AItemStackBuilder(Material.GOLD_SWORD)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, PitPlayer pitPlayer) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");
		loreBuilder.addLore(
				"&7On Trigger:",
				"&a\u25a0 &7Immune to enchants that &emove &7you",
				"&a\u25a0 &d" + decimalFormat.format(SHARD_MULTIPLIER) + "x &7chance to find &aAncient Gem Shards",
				"",
				"&7BUT:",
				"&c\u25a0 &7Deal &c-50% &7damage to bots",
				"",
				"&7During the Streak:",
				"&d\u25a0 &7200 kills: Random &dbuff &7or &cdebuff",
				"&d\u25a0 &7300 kills: Random &dbuff &7or &cdebuff",
				"&d\u25a0 &7400 kills: Random &dbuff &7or &cdebuff",
				"&d\u25a0 &7500 kills: &cNo longer gain health",
				"",
				"&7On Death:",
				"&e\u25a0 &7If your streak is at least 500,",
				"   &7earn a random &dUberdrop&7"
		);
	}

	@Override
	public String getSummary() {
		return getCapsDisplayName() + "&7 grants you immunity to enchants that move you, double chance to find &agem shards&7, " +
				"gain random &abuffs&7 or &cdebuffs&7 every 100 kills, gain a reward at &c500 streak&7, but deal a " +
				"lot less damage to bots and only have five &cUberstreaks&7 daily";
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

	public enum Uberdrop {
		JEWEL_SWORD(5),
		JEWEL_BOW(5),
		JEWEL_PANTS(5),
		JEWEL_BUNDLE(2),
		FEATHER_1(3),
		FEATHER_2(2),
		FEATHER_3(1),
		VILE_2(6),
		VILE_3(4),
		VILE_5(2),
		P1_HELMET(5),
		P1_CHESTPLATE(5),
		P1_LEGGINGS(7),
		P1_BOOTS(5);

		public final int weight;

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
			ItemStack displayStack = null;
			if(this == JEWEL_SWORD) {
				ItemStack jewelSword = MysticFactory.getJewelItem(MysticType.SWORD);
				AUtil.giveItemSafely(player, jewelSword);
				displayStack = getUberDropDisplayStack("&eHidden Jewel Sword", jewelSword);
			} else if(this == JEWEL_BOW) {
				ItemStack jewelBow = MysticFactory.getJewelItem(MysticType.BOW);
				AUtil.giveItemSafely(player, jewelBow);
				displayStack = getUberDropDisplayStack("&bHidden Jewel Bow", jewelBow);
			} else if(this == JEWEL_PANTS) {
				ItemStack jewelPants = MysticFactory.getJewelItem(MysticType.PANTS);
				AUtil.giveItemSafely(player, jewelPants);
				displayStack = getUberDropDisplayStack("&3Hidden Jewel Pants", jewelPants);
			} else if(this == JEWEL_BUNDLE) {
				ItemStack jbSword = MysticFactory.getJewelItem(MysticType.SWORD);
				AUtil.giveItemSafely(player, jbSword);

				ItemStack jbBow = MysticFactory.getJewelItem(MysticType.BOW);
				AUtil.giveItemSafely(player, jbBow);

				ItemStack jbPants = MysticFactory.getJewelItem(MysticType.PANTS);
				AUtil.giveItemSafely(player, jbPants);

				displayStack = new AItemStackBuilder(Material.STORAGE_MINECART)
						.setName("&3Hidden Jewel Bundle")
						.setLore(new ALoreBuilder(
								"",
								"&7Contents:",
								"&31x Hidden Jewel Pants",
								"&e1x Hidden Jewel Sword",
								"&b1x Hidden Jewel Bow"
						)).getItemStack();
			} else if(this == FEATHER_1) {
				ItemFactory.getItem(FunkyFeather.class).giveItem(player, 1);
				displayStack = getUberDropDisplayStack("&31x Funky Feather", ItemFactory.getItem(FunkyFeather.class).getItem(1));
			} else if(this == FEATHER_2) {
				ItemFactory.getItem(FunkyFeather.class).giveItem(player, 2);
				displayStack = getUberDropDisplayStack("&32x Funky Feather", ItemFactory.getItem(FunkyFeather.class).getItem(2));
			} else if(this == FEATHER_3) {
				ItemFactory.getItem(FunkyFeather.class).giveItem(player, 3);
				displayStack = getUberDropDisplayStack("&33x Funky Feather", ItemFactory.getItem(FunkyFeather.class).getItem(3));
			} else if(this == VILE_2) {
				ItemFactory.getItem(ChunkOfVile.class).giveItem(player, 2);
				displayStack = getUberDropDisplayStack("&52x Chunk of Vile", ItemFactory.getItem(ChunkOfVile.class).getItem(2));
			} else if(this == VILE_3) {
				ItemFactory.getItem(ChunkOfVile.class).giveItem(player, 3);
				displayStack = getUberDropDisplayStack("&53x Chunk of Vile", ItemFactory.getItem(ChunkOfVile.class).getItem(3));
			} else if(this == VILE_5) {
				ItemFactory.getItem(ChunkOfVile.class).giveItem(player, 5);
				displayStack = getUberDropDisplayStack("&55x Chunk of Vile", ItemFactory.getItem(ChunkOfVile.class).getItem(5));
			} else if(this == P1_HELMET) {
				ItemFactory.getItem(ProtHelmet.class).giveItem(player, 1);
				displayStack = getUberDropDisplayStack("&bProtection I Diamond Helmet", ItemFactory.getItem(ProtHelmet.class).getItem(1));
			} else if(this == P1_CHESTPLATE) {
				ItemFactory.getItem(ProtChestplate.class).giveItem(player, 1);
				displayStack = getUberDropDisplayStack("&bProtection I Diamond Chestplate", ItemFactory.getItem(ProtChestplate.class).getItem(1));
			} else if(this == P1_LEGGINGS) {
				ItemFactory.getItem(ProtLeggings.class).giveItem(player, 1);
				displayStack = getUberDropDisplayStack("&bProtection I Diamond Leggings", ItemFactory.getItem(ProtLeggings.class).getItem(1));
			} else if(this == P1_BOOTS) {
				ItemFactory.getItem(ProtBoots.class).giveItem(player, 1);
				displayStack = getUberDropDisplayStack("&bProtection I Diamond Boots", ItemFactory.getItem(ProtBoots.class).getItem(1));
			}

			sendUberMessage(Misc.getDisplayName(player), displayStack);

			new PluginMessage()
					.writeString("UBERDROP")
					.writeString(PitSim.serverName)
					.writeString(Misc.getDisplayName(player))
					.writeString(CustomSerializer.serialize(displayStack))
					.send();
		}
	}
}
