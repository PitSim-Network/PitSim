package dev.kyro.pitsim.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.battlepass.quests.daily.DailyMegastreakQuest;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.WrapperEntityDamageEvent;
import dev.kyro.pitsim.misc.Formatter;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.particles.HomeParticle;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.util.*;

public class RNGesus extends Megastreak {
	public static RNGesus INSTANCE;
	private static final Map<Player, RNGesusInfo> rngesusInfoMap = new HashMap<>();

	public static final int RENOWN_COST = 3;
	public static final int COOLDOWN_MINUTES = 60;
	public static final int INSTABILITY_THRESHOLD = 1000;

	public RNGesus() {
		super("&4RNGesus", "rngesus", 100, 50, 0);
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer()) || !PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer()) ||
				attackEvent.getAttacker() == attackEvent.getDefender()) return;
		if(hasMegastreak(attackEvent.getDefenderPlayer()) && attackEvent.getDefenderPitPlayer().getKills() >= INSTABILITY_THRESHOLD) attackEvent.setCancelled(true);
		if(hasMegastreak(attackEvent.getAttackerPlayer()) && attackEvent.getAttackerPitPlayer().getKills() >= INSTABILITY_THRESHOLD) attackEvent.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onHit(AttackEvent.Apply attackEvent) {
		if(!hasMegastreak(attackEvent.getAttackerPlayer())) return;
		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		RNGesusInfo rngesusInfo = getRNGesusInfo(attackEvent.getAttackerPlayer());

		if(pitPlayer.getKills() + 1 >= INSTABILITY_THRESHOLD) {
			attackEvent.multipliers.clear();
			attackEvent.increaseCalcDecrease.clear();
			attackEvent.increase = 0;
			attackEvent.increasePercent = 0;
			attackEvent.decreasePercent = 0;
			double damage = getDamage(rngesusInfo.realityMap.get(Reality.DAMAGE).getLevel());
			attackEvent.increase += damage;

			if(attackEvent.getWrapperEvent().getDamager() instanceof Slime && Math.random() > 0.1) return;

			List<Entity> entities = attackEvent.getDefender().getNearbyEntities(20, 20, 20);
			Collections.shuffle(entities);
			int count = 0;
			for(Entity entity : entities) {
				if(count++ >= 10) break;
				if(!(entity instanceof Player)) continue;
				Player target = (Player) entity;
				if(NonManager.getNon(target) == null) continue;

				BukkitRunnable callback = new BukkitRunnable() {
					@Override
					public void run() {
						Map<PitEnchant, Integer> attackerEnchant = EnchantManager.getEnchantsOnPlayer(attackEvent.getAttacker());
						Map<PitEnchant, Integer> defenderEnchant = new HashMap<>();
						EntityDamageByEntityEvent newEvent = new EntityDamageByEntityEvent(attackEvent.getAttacker(), target, EntityDamageEvent.DamageCause.CUSTOM, 0);
						AttackEvent attackEvent = new AttackEvent(new WrapperEntityDamageEvent(newEvent), attackerEnchant, defenderEnchant, false);

						double chance = damage / target.getMaxHealth();
						if(Math.random() < chance)
							DamageManager.fakeKill(attackEvent, attackEvent.getAttacker(), target);
					}
				};

				if(attackEvent.isAttackerPlayer()) new HomeParticle(attackEvent.getAttackerPlayer(),
						attackEvent.getDefender().getLocation().add(0, 1, 0), target, 0.5, callback);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onKill(KillEvent killEvent) {
		if(!hasMegastreak(killEvent.getKillerPlayer())) return;
		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
		if(pitPlayer.getKills() < INSTABILITY_THRESHOLD) return;
		RNGesusInfo rngesusInfo = getRNGesusInfo(killEvent.getKillerPlayer());

		killEvent.xpMultipliers.clear();
		killEvent.maxXPMultipliers.clear();
		killEvent.xpReward = 0;
		killEvent.xpCap = 0;
		killEvent.xpReward += getXP(rngesusInfo.realityMap.get(Reality.XP).getLevel() * 2);
		killEvent.xpCap += getXP(rngesusInfo.realityMap.get(Reality.XP).getLevel());

		killEvent.goldMultipliers.clear();
		killEvent.goldReward = 0;
		killEvent.goldReward += getGold(rngesusInfo.realityMap.get(Reality.GOLD).getLevel());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onKill2(KillEvent killEvent) {
		if(!hasMegastreak(killEvent.getKillerPlayer())) return;
		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
		RNGesusInfo rngesusInfo = getRNGesusInfo(killEvent.getKillerPlayer());

		if((pitPlayer.getKills() + 1) % 100 == 0 && pitPlayer.getKills() + 1 < INSTABILITY_THRESHOLD)
			shiftReality(killEvent.getKillerPlayer());
		if(pitPlayer.getKills() + 1 == INSTABILITY_THRESHOLD) destabilize(killEvent.getKillerPlayer());

		if(rngesusInfo.reality == Reality.XP) {
			rngesusInfo.realityMap.get(rngesusInfo.reality).progression += killEvent.getFinalXp();
		} else if(rngesusInfo.reality == Reality.GOLD) {
			rngesusInfo.realityMap.get(rngesusInfo.reality).progression += killEvent.getFinalGold();
		}
		if(pitPlayer.getKills() + 1 < INSTABILITY_THRESHOLD) pitPlayer.updateXPBar();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void attack(AttackEvent.Apply attackEvent) {
		if(!hasMegastreak(attackEvent.getAttackerPlayer())) return;
		if(NonManager.getNon(attackEvent.getDefender()) == null) return;
		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		if(!pitPlayer.isOnMega()) return;
		RNGesusInfo rngesusInfo = getRNGesusInfo(attackEvent.getAttackerPlayer());

		if(rngesusInfo.reality == Reality.DAMAGE) {
			rngesusInfo.realityMap.get(rngesusInfo.reality).progression += attackEvent.getFinalPitDamage();
		} else if(rngesusInfo.reality == Reality.ABSORPTION) {
			rngesusInfo.realityMap.get(rngesusInfo.reality).progression += attackEvent.trueDamage;
		}
		if(pitPlayer.getKills() + 1 < INSTABILITY_THRESHOLD) pitPlayer.updateXPBar();
	}

	@EventHandler
	public void onHeal(HealEvent event) {
		Player player = event.getPlayer();
		if(!hasMegastreak(player)) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.getKills() + 1 < INSTABILITY_THRESHOLD) return;
		event.multipliers.add(0.0);
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		RNGesusInfo rngesusInfo = getRNGesusInfo(player);

		putOnCooldown(pitPlayer);
		Sounds.MEGA_RNGESUS.play(player.getLocation());

		rngesusInfo.runnable = new BukkitRunnable() {
			int count = 0;

			@Override
			public void run() {
				if(pitPlayer.getKills() > INSTABILITY_THRESHOLD) {
					for(int i = 0; i < 3; i++) {
						player.getWorld().playEffect(player.getLocation()
								.add(Math.random() * 0.2 - 0.1, Math.random() * 0.2 + 2.1, Math.random() * 0.2 - 0.1), Effect.HAPPY_VILLAGER, 1);
					}
				}
				if(count++ % 5 != 0) return;

				if(pitPlayer.getKills() < INSTABILITY_THRESHOLD && pitPlayer.isOnMega()) {
					DecimalFormat decimalFormat = new DecimalFormat("#,####,##0");
					switch(rngesusInfo.reality) {
						case NONE:
							String realityString = Misc.distortMessage("Reality appears normal", 0.2);
							Misc.sendActionBar(player, "&7" + realityString);
							break;
						case XP:
							double xp = rngesusInfo.realityMap.get(Reality.XP).progression;
							Misc.sendActionBar(player, "&bXP: " + decimalFormat.format(xp));
							break;
						case GOLD:
							double gold = rngesusInfo.realityMap.get(Reality.GOLD).progression;
							Misc.sendActionBar(player, "&6Gold: " + decimalFormat.format(gold));
							break;
						case DAMAGE:
							double damage = rngesusInfo.realityMap.get(Reality.DAMAGE).progression;
							Misc.sendActionBar(player, "&cDamage: " + decimalFormat.format(damage));
							break;
						case ABSORPTION:
							double absorption = rngesusInfo.realityMap.get(Reality.ABSORPTION).progression;
							Misc.sendActionBar(player, "&9True Damage: " + decimalFormat.format(absorption));
					}
				} else {
					EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
					if(nmsPlayer.getAbsorptionHearts() > 0) {
						nmsPlayer.setAbsorptionHearts(Math.max(nmsPlayer.getAbsorptionHearts() - 1F, 0));
					} else if(player.getHealth() > 1) {
						player.setHealth(player.getHealth() - 1);
					} else {
						DamageManager.killPlayer(player);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 4L);
	}

	@Override
	public void reset(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		RNGesusInfo rngesusInfo = getRNGesusInfo(player);
		rngesusInfoMap.remove(player);

		if(pitPlayer.getKills() >= INSTABILITY_THRESHOLD) {
			pitPlayer.stats.rngesusCompleted++;
			DailyMegastreakQuest.INSTANCE.onMegastreakComplete(pitPlayer);
		}

		if(!pitPlayer.isOnMega()) return;

		int xp = getXP(rngesusInfo.realityMap.get(Reality.XP).getLevel());
		double gold = getGold(rngesusInfo.realityMap.get(Reality.GOLD).getLevel());
		double damage = getDamage(rngesusInfo.realityMap.get(Reality.DAMAGE).getLevel());
		float absorption = getAbsorption(rngesusInfo.realityMap.get(Reality.ABSORPTION).getLevel());
		new BukkitRunnable() {
			@Override
			public void run() {
				DecimalFormat decimalFormat = new DecimalFormat("#,####,##0");
				AOutput.send(pitPlayer.player, "&bXP &7increased by &b" + decimalFormat.format(xp));
				AOutput.send(pitPlayer.player, "&6Gold &7increased by &6" + decimalFormat.format(gold));
				AOutput.send(pitPlayer.player, "&cDamage &7increased by &c" + decimalFormat.format(damage));
				AOutput.send(pitPlayer.player, "&6Absorption &7increased by &9" + decimalFormat.format(absorption));
			}
		}.runTaskLater(PitSim.INSTANCE, 20L);

		rngesusInfo.generateRealityOrder();
		rngesusInfo.realityMap.clear();
		for(Reality value : Reality.values()) rngesusInfo.realityMap.put(value, new RealityInfo(value));
		rngesusInfo.reality = Reality.NONE;

		if(rngesusInfo.runnable != null) rngesusInfo.runnable.cancel();

		if(isOnCooldown(pitPlayer)) pitPlayer.setMegastreak(NoMegastreak.INSTANCE);
	}

	public void shiftReality(Player player) {
		RNGesusInfo rngesusInfo = getRNGesusInfo(player);
		rngesusInfo.reality = rngesusInfo.generatedRealityOrder.remove(0);
		if(rngesusInfo.reality == null) return;
		AOutput.send(player, getCapsDisplayName() + "!&7 Reality Shift: " + rngesusInfo.reality.displayName + "&7!");
		ASound.play(player, Sound.FIZZ, 1000, 0.5F);
		Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, 40, 0, true, false);
	}

	public void destabilize(Player player) {
		RNGesusInfo rngesusInfo = getRNGesusInfo(player);
		rngesusInfo.reality = Reality.NONE;
		String message = "Reality destabilizes. Will it make you stronger or will " +
				"you succumb to the endless void of time";
		AOutput.send(player, getCapsDisplayName() + "!&7 " + message);
		Sounds.RNGESUS_DESTABILIZE.play(player);

		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
		nmsPlayer.setAbsorptionHearts(getAbsorption(rngesusInfo.realityMap.get(Reality.ABSORPTION).getLevel()));

		DecimalFormat decimalFormat = new DecimalFormat("#,####,##0");
		int xp = getXP(rngesusInfo.realityMap.get(Reality.XP).getLevel());
		double gold = getGold(rngesusInfo.realityMap.get(Reality.GOLD).getLevel());
		double damage = getDamage(rngesusInfo.realityMap.get(Reality.DAMAGE).getLevel());
		float absorption = getAbsorption(rngesusInfo.realityMap.get(Reality.ABSORPTION).getLevel());
		AOutput.send(player, "&bXP &7increased by &b" + decimalFormat.format(xp));
		AOutput.send(player, "&6Gold &7increased by &6" + decimalFormat.format(gold));
		AOutput.send(player, "&cDamage &7increased by &c" + decimalFormat.format(damage));
		AOutput.send(player, "&6Absorption &7increased by &9" + decimalFormat.format(absorption));
	}

	public static RNGesusInfo getRNGesusInfo(Player player) {
		rngesusInfoMap.putIfAbsent(player, new RNGesusInfo());
		return rngesusInfoMap.get(player);
	}

	public int getXP(double progression) {
		return (int) (progression);
	}

	public double getGold(double progression) {
		return progression;
	}

	public double getDamage(double progression) {
		return progression * 0.10;
	}

	public float getAbsorption(double progression) {
		return (float) progression;
	}

	public static boolean isOnCooldown(PitPlayer pitPlayer) {
		return System.currentTimeMillis() < pitPlayer.rngCooldown;
	}

	public static String getTimeLeft(PitPlayer pitPlayer) {
		long timeRemaining = pitPlayer.rngCooldown - System.currentTimeMillis();
		return Formatter.formatDurationFull(timeRemaining, true);
	}

	public void putOnCooldown(PitPlayer pitPlayer) {
		pitPlayer.rngCooldown = System.currentTimeMillis() + COOLDOWN_MINUTES * 60L * 1000;
	}

	@Override
	public String getPrefix(Player player) {
		return getRNGesusInfo(player).reality.prefix;
	}

	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return new AItemStackBuilder(isOnCooldown(pitPlayer) ? Material.ENDER_PEARL : Material.EYE_OF_ENDER)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, PitPlayer pitPlayer) {
		loreBuilder.addLore(
				"&7On Trigger:",
				"&a\u25a0 &7Immune to enchants that &emove &7you",
				"&a\u25a0 &eShift &7into a random reality (&bXP&7, &6Gold&7,",
				"   &cDamage&7, &eAbsorption&7)",
				"&c\u25a0 &7Start a &f1 hour &7cooldown for this streak",
				"",
				"&7Every 100 Kills:",
				"&a\u25a0 &eShift &7into a random reality (&bXP&7, &6Gold&7,",
				"   &cDamage&7, &eAbsorption&7)",
				"&a\u25a0 &7Build up stats for each reality as you streak",
				"",
				"&7At 1,000 Kills:",
				"&e&k\u25a0&7 Reality " + Misc.distortMessage("&fDestabilizes", 0.2),
				"&a\u25a0 &7Use the stats earned from each reality as",
				"   &7&cdamage&7, &9health&7, &bXP&7, and &6gold &7on each kill",
				"&c\u25a0 &7You can no longer heal",
				"",
				"&7On Death:",
				"&e\u25a0 &7View a recap of your streak"
		);
		if(isOnCooldown(pitPlayer)) loreBuilder.addLore(
				"",
				"&eMegastreak on Cooldown!&7 (" + getTimeLeft(pitPlayer) + ")"
		);
	}

	@Override
	public String getSummary() {
		return getCapsDisplayName() + "&7 allows you to enter four different random realities, &bXP&7, &6Gold&7, &cDamage&7 and, " +
				"&9Absorption&7. At an &c1000 streak&7, gain the buffs that you earned throughout the realities, but " +
				"you no longer &cheal&7. Throughout the &cMegastreak&7 you are immune to enchants that would &emove&7 " +
				"you There is an hour cooldown on this &cMegastreak&7, which can be skipped by using &eRenown";
	}

	public enum Reality {
		NONE("&eAbnormal", "&4&lRNGSUS", 1),
		XP("&bXP", "&b&lRNG&4&lSUS", 0.05),
		GOLD("&6Gold", "&6&lRNG&4&lSUS", 50),
		DAMAGE("&cDamage", "&c&lRNG&4&lSUS", 1),
		ABSORPTION("&6Absorption", "&9&lRNG&4&lSUS", 0.3);

		public final String displayName;
		public final String prefix;
		public final double baseMultiplier;

		Reality(String displayName, String prefix, double baseMultiplier) {
			this.displayName = displayName;
			this.prefix = prefix;
			this.baseMultiplier = baseMultiplier;
		}
	}

	public static class RealityInfo {
		public Reality reality;
		public double progression;

		public RealityInfo(Reality reality) {
			this.reality = reality;
		}

		public int getLevel() {
			double modifiableProgression = progression / reality.baseMultiplier;
			int level = 0;
			while(modifiableProgression >= level + 1) {
				modifiableProgression -= level + 1;
				level++;
			}
			return level;
		}

		public double getProgression(int level) {
			int progression = 0;
			for(int i = 0; i < level + 1; i++) {
				progression += i;
			}
			return progression * reality.baseMultiplier;
		}
	}

	public static class RNGesusInfo {
		public List<Reality> generatedRealityOrder = new ArrayList<>();
		public Map<Reality, RealityInfo> realityMap = new HashMap<>();
		public Reality reality = Reality.NONE;
		public BukkitTask runnable;

		public RNGesusInfo() {
			for(Reality value : Reality.values()) realityMap.put(value, new RealityInfo(value));
			generateRealityOrder();
		}

		public void generateRealityOrder() {
			generatedRealityOrder.clear();
			for(Reality value : Reality.values()) {
				if(value == Reality.NONE) continue;
				generatedRealityOrder.add(value);
			}
			for(int i = generatedRealityOrder.size(); i < 9; i++) {
				List<Reality> randomRealities = new ArrayList<>(Arrays.asList(Reality.values()));
				Collections.shuffle(randomRealities);
				generatedRealityOrder.add(randomRealities.get(0));
			}
			Collections.shuffle(generatedRealityOrder);
		}
	}
}
