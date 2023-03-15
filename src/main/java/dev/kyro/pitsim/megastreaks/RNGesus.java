package dev.kyro.pitsim.megastreaks;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.battlepass.quests.daily.DailyMegastreakQuest;
import dev.kyro.pitsim.controllers.*;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.KillType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.WrapperEntityDamageEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.particles.HomeParticle;
import me.clip.placeholderapi.PlaceholderAPI;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.util.*;

public class RNGesus extends Megastreak {
	public static int RENOWN_COST = 3;
	public static int COOLDOWN_MINUTES = 60;
	public static int INSTABILITY_THRESHOLD = 1000;

	public List<Reality> generatedRealityOrder = new ArrayList<>();
	public Map<Reality, RealityInfo> realityMap = new HashMap<>();
	public Reality reality = Reality.NONE;
	public BukkitTask runnable;

	public RNGesus(PitPlayer pitPlayer) {
		super(pitPlayer);
		for(Reality value : Reality.values()) realityMap.put(value, new RealityInfo(value));
		generateRealityOrder();
	}

	@EventHandler
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!PlayerManager.isRealPlayer(attackEvent.getAttackerPlayer()) || !PlayerManager.isRealPlayer(attackEvent.getDefenderPlayer()))
			return;
		if((attackEvent.getDefenderPitPlayer() == pitPlayer || attackEvent.getAttackerPitPlayer() == pitPlayer) && pitPlayer.megastreak instanceof RNGesus &&
				attackEvent.getAttackerPlayer() != attackEvent.getDefenderPlayer() && pitPlayer.getKills() >= INSTABILITY_THRESHOLD) {
			attackEvent.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onHit(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		if(pitPlayer != this.pitPlayer || !(pitPlayer.megastreak instanceof RNGesus)) return;
		if(NonManager.getNon(attackEvent.getAttacker()) != null) return;

		if(pitPlayer.getKills() + 1 >= INSTABILITY_THRESHOLD) {
			attackEvent.multipliers.clear();
			attackEvent.increaseCalcDecrease.clear();
			attackEvent.increase = 0;
			attackEvent.increasePercent = 0;
			attackEvent.decreasePercent = 0;
			double damage = getDamage(realityMap.get(Reality.DAMAGE).getLevel());
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
	public void kill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer()) return;
		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.getKills() >= INSTABILITY_THRESHOLD && pitPlayer.megastreak instanceof RNGesus) {
			killEvent.xpMultipliers.clear();
			killEvent.xpReward = 0;
			killEvent.xpCap = 0;
			killEvent.xpReward += getXP(realityMap.get(Reality.XP).getLevel() * 2);
			killEvent.xpCap += getXP(realityMap.get(Reality.XP).getLevel());

			killEvent.goldMultipliers.clear();
			killEvent.goldReward = 0;
			killEvent.goldReward += getGold(realityMap.get(Reality.GOLD).getLevel());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void kill2(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer()) return;
		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.megastreak instanceof RNGesus) {
			if((pitPlayer.getKills() + 1) % 100 == 0 && pitPlayer.getKills() + 1 < INSTABILITY_THRESHOLD) {
				shiftReality();
			}
			if(pitPlayer.getKills() + 1 == INSTABILITY_THRESHOLD) destabilize();

			if(reality == Reality.XP) {
				realityMap.get(reality).progression += killEvent.getFinalXp();
			} else if(reality == Reality.GOLD) {
				realityMap.get(reality).progression += killEvent.getFinalGold();
			}
			if(pitPlayer.getKills() + 1 < INSTABILITY_THRESHOLD) setXPBar();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void attack(AttackEvent.Post attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(NonManager.getNon(attackEvent.getDefender()) == null) return;
		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.megastreak.isOnMega() && pitPlayer.megastreak instanceof RNGesus) {

			if(reality == Reality.DAMAGE) {
				realityMap.get(reality).progression += attackEvent.getFinalDamage();
			} else if(reality == Reality.ABSORPTION) {
				realityMap.get(reality).progression += attackEvent.getApplyEvent().trueDamage;
			}
			if(pitPlayer.getKills() + 1 < INSTABILITY_THRESHOLD) setXPBar();
		}
	}

	@EventHandler
	public void onHeal(HealEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.getKills() + 1 < INSTABILITY_THRESHOLD || !(pitPlayer.megastreak instanceof RNGesus)) return;
		event.multipliers.add(0.0);
	}

	@Override
	public void proc() {
		putOnCooldown();
		Sounds.MEGA_RNGESUS.play(pitPlayer.player.getLocation());

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}

		pitPlayer.megastreak = this;
		for(Player player : Bukkit.getOnlinePlayers()) {
			PitPlayer pitPlayer2 = PitPlayer.getPitPlayer(player);
			if(pitPlayer2.streaksDisabled) continue;
			String streakMessage = ChatColor.translateAlternateColorCodes('&',
					"&c&lMEGASTREAK! %luckperms_prefix%" + pitPlayer.player.getDisplayName() + " &7activated &e&lRNGESUS&7!");
			AOutput.send(player, PlaceholderAPI.setPlaceholders(pitPlayer.player, streakMessage));
		}

		runnable = new BukkitRunnable() {
			int count = 0;

			@Override
			public void run() {
				if(pitPlayer.getKills() > INSTABILITY_THRESHOLD) {
					for(int i = 0; i < 3; i++) {
						pitPlayer.player.getWorld().playEffect(pitPlayer.player.getLocation()
								.add(Math.random() * 0.2 - 0.1, Math.random() * 0.2 + 2.1, Math.random() * 0.2 - 0.1), Effect.HAPPY_VILLAGER, 1);
					}
				}
				if(count++ % 5 != 0) return;

				if(!(pitPlayer.megastreak instanceof RNGesus)) return;
				if(pitPlayer.getKills() < INSTABILITY_THRESHOLD && isOnMega()) {
					DecimalFormat decimalFormat = new DecimalFormat("#,####,##0");
					switch(reality) {
						case NONE:
							String realityString = Misc.distortMessage("Reality appears normal", 0.2);
							sendActionBar(pitPlayer.player, "&7" + realityString);
							break;
						case XP:
							double xp = realityMap.get(Reality.XP).progression;
							sendActionBar(pitPlayer.player, "&bXP: " + decimalFormat.format(xp));
							break;
						case GOLD:
							double gold = realityMap.get(Reality.GOLD).progression;
							sendActionBar(pitPlayer.player, "&6Gold: " + decimalFormat.format(gold));
							break;
						case DAMAGE:
							double damage = realityMap.get(Reality.DAMAGE).progression;
							sendActionBar(pitPlayer.player, "&cDamage: " + decimalFormat.format(damage));
							break;
						case ABSORPTION:
							double absorption = realityMap.get(Reality.ABSORPTION).progression;
							sendActionBar(pitPlayer.player, "&9True Damage: " + decimalFormat.format(absorption));
					}
				} else {
					EntityPlayer nmsPlayer = ((CraftPlayer) pitPlayer.player).getHandle();
					if(nmsPlayer.getAbsorptionHearts() > 0) {
						nmsPlayer.setAbsorptionHearts(Math.max(nmsPlayer.getAbsorptionHearts() - 1F, 0));
					} else if(pitPlayer.player.getHealth() > 1) {
						pitPlayer.player.setHealth(pitPlayer.player.getHealth() - 1);
					} else {
						UUID attackerUUID = pitPlayer.lastHitUUID;
						for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
							if(onlinePlayer.getUniqueId().equals(attackerUUID)) {
								Map<PitEnchant, Integer> attackerEnchant = new HashMap<>();
								Map<PitEnchant, Integer> defenderEnchant = new HashMap<>();
								EntityDamageByEntityEvent newEvent = new EntityDamageByEntityEvent(onlinePlayer, pitPlayer.player, EntityDamageEvent.DamageCause.CUSTOM, 0);
								AttackEvent attackEvent = new AttackEvent(new WrapperEntityDamageEvent(newEvent), attackerEnchant, defenderEnchant, false);

								DamageManager.kill(attackEvent, onlinePlayer, pitPlayer.player, KillType.KILL);
								return;
							}
						}
						DamageManager.death(pitPlayer.player);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 4L);
	}

	@Override
	public void reset() {
		if(pitPlayer.getKills() >= INSTABILITY_THRESHOLD) {
			if(pitPlayer.stats != null) pitPlayer.stats.rngesusCompleted++;
			DailyMegastreakQuest.INSTANCE.onMegastreakComplete(pitPlayer);
		}

		if(isOnMega()) {
			int xp = getXP(realityMap.get(Reality.XP).getLevel());
			double gold = getGold(realityMap.get(Reality.GOLD).getLevel());
			double damage = getDamage(realityMap.get(Reality.DAMAGE).getLevel());
			float absorption = getAbsorption(realityMap.get(Reality.ABSORPTION).getLevel());
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
		}

		generateRealityOrder();
		realityMap.clear();
		for(Reality value : Reality.values()) realityMap.put(value, new RealityInfo(value));
		reality = Reality.NONE;

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}

		if(runnable != null) runnable.cancel();
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
		if(runnable != null) runnable.cancel();
	}

	public void shiftReality() {
		reality = generatedRealityOrder.remove(0);
		if(reality == null) return;
		AOutput.send(pitPlayer.player, "&e&lRNGESUS!&7 Reality Shift: " + reality.displayName + "&7!");
		ASound.play(pitPlayer.player, Sound.FIZZ, 1000, 0.5F);
		Misc.applyPotionEffect(pitPlayer.player, PotionEffectType.BLINDNESS, 40, 0, true, false);

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}
	}

	public void destabilize() {
		reality = Reality.NONE;
		String message = "Reality destabilizes. Will it make you stronger or will " +
				"you succumb to the endless void of time";
		AOutput.send(pitPlayer.player, "&e&lRNGESUS!&7 " + message);
		Sounds.RNGESUS_DESTABILIZE.play(pitPlayer.player);

		EntityPlayer nmsPlayer = ((CraftPlayer) pitPlayer.player).getHandle();
		nmsPlayer.setAbsorptionHearts(getAbsorption(realityMap.get(Reality.ABSORPTION).getLevel()));

		DecimalFormat decimalFormat = new DecimalFormat("#,####,##0");
		int xp = getXP(realityMap.get(Reality.XP).getLevel());
		double gold = getGold(realityMap.get(Reality.GOLD).getLevel());
		double damage = getDamage(realityMap.get(Reality.DAMAGE).getLevel());
		float absorption = getAbsorption(realityMap.get(Reality.ABSORPTION).getLevel());
		AOutput.send(pitPlayer.player, "&bXP &7increased by &b" + decimalFormat.format(xp));
		AOutput.send(pitPlayer.player, "&6Gold &7increased by &6" + decimalFormat.format(gold));
		AOutput.send(pitPlayer.player, "&cDamage &7increased by &c" + decimalFormat.format(damage));
		AOutput.send(pitPlayer.player, "&6Absorption &7increased by &9" + decimalFormat.format(absorption));

		String prefix = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, prefix);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, prefix);
		}
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

	public void setXPBar() {
		RealityInfo realityInfo = realityMap.get(reality);

		int level = realityInfo.getLevel();
		float currentAmount = (float) realityInfo.progression;
		float currentTier = (float) realityInfo.getProgression(level);
		float nextTier = (float) realityInfo.getProgression(level + 1);

		pitPlayer.player.setLevel(level);
		float ratio = (currentAmount - currentTier) / (nextTier - currentTier);
		pitPlayer.player.setExp(ratio);
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

	@Override
	public String getName() {
		return reality.prefix;
	}

	@Override
	public String getRawName() {
		return "RNGesus";
	}

	@Override
	public String getPrefix() {
		return "&eRNGesus";
	}

	@Override
	public List<String> getRefNames() {
		return Arrays.asList("rngesus");
	}

	@Override
	public int getRequiredKills() {
		return 100;
	}

	@Override
	public int guiSlot() {
		return 16;
	}

	@Override
	public int prestigeReq() {
		return 50;
	}

	@Override
	public int initialLevelReq() {
		return 0;
	}

	@Override
	public ItemStack guiItem() {
		ItemStack item = new ItemStack(Material.EYE_OF_ENDER);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Triggers on: &c100 kills"));
		lore.add("");
		lore.add(ChatColor.GRAY + "On trigger:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Immune to enchants that &emove &7you"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&e\u25a0 &eShift &7into a random reality (&6Gold&7, &bXP&7,"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&cDamage&7, &eAbsorption&7)"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &7Start a &b1 hour &7cooldown for this streak"));
		lore.add("");
		lore.add(ChatColor.GRAY + "Every 100 kills:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&e\u25a0 &eShift &7into a random reality (&6Gold&7, &bXP&7,"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&cDamage&7, &eAbsorption&7)"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&e\u25a0 &7Build up stats for each reality as you streak"));
		lore.add("");
		lore.add(ChatColor.GRAY + "At 1000 kills:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&e&k\u25a0&7 Reality &fdestabilizes"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Use the stats earned from each reality as"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7&cdamage&7, &9health&7, &bXP&7, and &6gold &7on each kill"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &7You can no longer heal"));
		lore.add("");
		lore.add(ChatColor.GRAY + "On death:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&e\u25a0 &7View a recap of your stats from each reality"));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public String getSummary() {
		return "&e&lRNGesus&7 allows you to enter four different random realities, &bXP&7, &6Gold&7, &cDamage&7 and, " +
				"&9Absorption&7. At an &c1000 streak&7, gain the buffs that you earned throughout the realities, but " +
				"you no longer &cheal&7. Throughout the &cMegastreak&7 you are immune to enchants that would &emove&7 " +
				"you There is an hour cooldown on this &cMegastreak&7, which can be skipped by using &eRenown";
	}

	public enum Reality {
		NONE("&eAbnormal", "&e&lRNGSUS", 1),
		XP("&bXP", "&b&lRNG&e&lSUS", 0.05),
		GOLD("&6Gold", "&6&lRNG&e&lSUS", 50),
		DAMAGE("&cDamage", "&c&lRNG&e&lSUS", 1),
		ABSORPTION("&6Absorption", "&9&lRNG&e&lSUS", 0.3);

		public String displayName;
		public String prefix;
		public double baseMultiplier;

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

	public static void sendActionBar(Player player, String message) {
		PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" +
				ChatColor.translateAlternateColorCodes('&', message) + "\"}"), (byte) 2);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	public boolean isOnCooldown() {
		return isOnCooldown(pitPlayer);
	}

	public static boolean isOnCooldown(PitPlayer pitPlayer) {
		return System.currentTimeMillis() < pitPlayer.rngCooldown;
	}

	public String getTimeLeft() {
		return getTimeLeft(pitPlayer);
	}

	public static String getTimeLeft(PitPlayer pitPlayer) {
		long timeRemaining = pitPlayer.rngCooldown - System.currentTimeMillis();
		return Misc.formatDurationFull(timeRemaining, true);
	}

	public void putOnCooldown() {
		pitPlayer.rngCooldown = System.currentTimeMillis() + COOLDOWN_MINUTES * 60L * 1000;
	}
}
