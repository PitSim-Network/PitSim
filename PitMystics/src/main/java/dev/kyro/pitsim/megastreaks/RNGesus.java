package dev.kyro.pitsim.megastreaks;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.PrestigeValues;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.particles.HomeParticle;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class RNGesus extends Megastreak {
	public List<Reality> generatedRealityOrder = new ArrayList<>();
	public Map<Reality, RealityInfo> realityMap = new HashMap<>();
	public Reality reality = Reality.NONE;

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
		return 5;
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
	public int levelReq() {
		return 0;
	}

	@Override
	public ItemStack guiItem() {
		ItemStack item = new ItemStack(Material.BLAZE_POWDER);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Triggers on: &c100 kills"));
		lore.add("");
		lore.add(ChatColor.GRAY + "On trigger:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Earn &b+50% XP &7from kills"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Earn &6+100% gold &7from kills"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Permanent &eSpeed I&7"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Immune to &9Slowness&7"));
		lore.add("");
		lore.add(ChatColor.GRAY + "BUT:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &7Receive &c+" + Misc.getHearts(0.2) + " &7very true"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7damage per 10 kills (only from nons)"));
		lore.add("");
		lore.add(ChatColor.GRAY + "On death:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&e\u25a0 &7Earn between &61000 &7and &65000 gold&7"));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public RNGesus(PitPlayer pitPlayer) {
		super(pitPlayer);
		for(Reality value : Reality.values()) realityMap.put(value, new RealityInfo(reality));
		generateRealityOrder();
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.attacker);
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.megastreak.isOnMega() && pitPlayer.megastreak.getClass() == RNGesus.class) {
			List<Entity> entities = attackEvent.defender.getNearbyEntities(20, 20, 20);
			Collections.shuffle(entities);
			int count = 0;
			for(Entity entity : entities) {
				if(count++ >= 5) break;
				if(!(entity instanceof Player)) continue;
				Player target = (Player) entity;
				if(NonManager.getNon(target) == null) continue;

				BukkitRunnable callback = new BukkitRunnable() {
					@Override
					public void run() {
						Map<PitEnchant, Integer> attackerEnchant = EnchantManager.getEnchantsOnPlayer(attackEvent.attacker);
						Map<PitEnchant, Integer> defenderEnchant = new HashMap<>();
						EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(attackEvent.attacker, target, EntityDamageEvent.DamageCause.CUSTOM, 0);
						AttackEvent attackEvent = new AttackEvent(ev, attackerEnchant, defenderEnchant, false);
						DamageManager.fakeKill(attackEvent, attackEvent.attacker, target, false);
					}
				};

				new HomeParticle(attackEvent.attacker, attackEvent.defender.getLocation().add(0, 1, 0), target, 0.4, callback);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void kill(KillEvent killEvent) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.killer);
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.megastreak.isOnMega() && pitPlayer.megastreak.getClass() == RNGesus.class) {
			if(pitPlayer.getKills() % 20 == 0) shiftReality();
			setXPBar();

			if(reality == Reality.NONE) {
				realityMap.get(reality).progression += killEvent.getFinalGold();
			}
		}
	}

	@Override
	public void proc() {
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
					"&c&lMEGASTREAK! %luckperms_prefix%" + pitPlayer.player.getDisplayName() + " &7activated &c&lOVERDRIVE&7!");
			AOutput.send(player, PlaceholderAPI.setPlaceholders(pitPlayer.player, streakMessage));
		}

		if(pitPlayer.stats != null) pitPlayer.stats.timesOnOverdrive++;
	}

	@Override
	public void reset() {
		generateRealityOrder();
		realityMap.clear();
		for(Reality value : Reality.values()) realityMap.put(value, new RealityInfo(reality));
		reality = Reality.NONE;

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}

		if(pitPlayer.megastreak.isOnMega()) {
//			Death
		}
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
	}

	public void shiftReality() {
		reality = generatedRealityOrder.remove(0);
		AOutput.send(pitPlayer.player, "&e&lRNGESUS! &7Reality Shift: " + reality.displayName + "&7!");
		ASound.play(pitPlayer.player, Sound.FIZZ, 1, 1);
		Misc.applyPotionEffect(pitPlayer.player, PotionEffectType.BLINDNESS, 20, 0, true, false);
	}

	public void generateRealityOrder() {
		generatedRealityOrder.clear();
		for(Reality value : Reality.values()) {
			if(value == Reality.NONE) continue;
			generatedRealityOrder.add(value);
		}
		for(int i = generatedRealityOrder.size(); i < 9; i++) {
			List<Reality> randomRealities = new ArrayList<>(Arrays.asList(Reality.values()));
			randomRealities.remove(0);
			Collections.shuffle(randomRealities);
			generatedRealityOrder.add(randomRealities.get(0));
		}
		Collections.shuffle(generatedRealityOrder);
		generatedRealityOrder.add(0, Reality.NONE);
	}

	public void setXPBar() {
		RealityInfo realityInfo = realityMap.get(reality);

		int level = realityInfo.getLevel();
		float currentAmount = (float) realityInfo.progression;
		float currentTier = (float) realityInfo.getProgression(level);
		float nextTier = (float) realityInfo.getProgression(level + 1);

		pitPlayer.player.setLevel(pitPlayer.level);
		float ratio = (currentAmount - currentTier) / nextTier;
		pitPlayer.player.setExp(ratio);
	}

	public enum Reality {
		NONE("&eNormal?", "&e&lRNGSUS", 1),
		XP("&bXP", "&b&lRNG&e&lSUS", 1),
		GOLD("&6Gold", "&6&lRNG&e&lSUS", 1),
		DAMAGE("&cDamage", "&c&lRNG&e&lSUS", 1),
		DEFENCE("&9Defence", "&9&lRNG&e&lSUS", 1);

		public String displayName;
		public String prefix;
		public int baseMultiplier;

		Reality(String displayName, String prefix, int baseMultiplier) {
			this.displayName = displayName;
			this.prefix = prefix;
			this.baseMultiplier = baseMultiplier;
		}
	}

	public static class RealityInfo {
		public Reality reality;
		public int timesActivated = 1;
		public double progression;

		public RealityInfo(Reality reality) {
			this.reality = reality;
		}

		public int getLevel() {
			double modifiableProgression = progression / reality.baseMultiplier;
			int level = 0;
			while(modifiableProgression > level + 1) {
				modifiableProgression -= level + 1;
				level++;
			}
			return level;
		}

		public double getProgression(int level) {
			int progression = 0;
			for(int i = 0; i < level; i++) {
				progression += i;
			}
			return progression * reality.baseMultiplier;
		}
	}
}
