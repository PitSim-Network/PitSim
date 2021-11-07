package dev.kyro.pitsim.misc;

import dev.kyro.pitsim.PitSim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Sounds {

//	General
	public static final SoundEffect SUCCESS = new SoundEffect(Sound.NOTE_PLING, 1, 2);
	public static final SoundEffect ERROR = new SoundEffect(Sound.ENDERMAN_TELEPORT, 1, 0.5);
	public static final SoundEffect NO = new SoundEffect(Sound.VILLAGER_NO, 1, 1);
	public static final SoundEffect WARNING_LOUD = new SoundEffect(Sound.NOTE_PLING, 1000, 1);

//	Game / Misc
	public static final SoundEffect LEVEL_UP = new SoundEffect(Sound.LEVEL_UP, 1, 1);
	public static final SoundEffect PRESTIGE = new SoundEffect(Sound.ENDERDRAGON_GROWL, 1, 1);
	public static final SoundEffect ASSIST = new SoundEffect(Sound.ORB_PICKUP, 1, 1.7301587);
	public static final SoundEffect BOUNTY = new SoundEffect(Sound.WITHER_SPAWN, 1, 1);
	public static final SoundEffect DEATH_FALL = new SoundEffect(Sound.FALL_BIG, 1000, 1);
	public static final SoundEffect JEWEL_FIND = new SoundEffect(Sound.ENDERDRAGON_GROWL, 1, 1);
	public static final SoundEffect MYSTIC_WELL_OPEN_1 = new SoundEffect(Sound.GHAST_FIREBALL, 0.1F, 0.5F);
	public static final SoundEffect MYSTIC_WELL_OPEN_2 = new SoundEffect(Sound.ITEM_PICKUP, 1F, 0.9F);
	public static final SoundEffect MULTI_1 = new SoundEffect(Sound.ORB_PICKUP, 1, 1.7936507);
	public static final SoundEffect MULTI_2 = new SoundEffect(Sound.ORB_PICKUP, 1, 1.8253968);
	public static final SoundEffect MULTI_3 = new SoundEffect(Sound.ORB_PICKUP, 1, 1.8730159);
	public static final SoundEffect MULTI_4 = new SoundEffect(Sound.ORB_PICKUP, 1, 1.9047619);
	public static final SoundEffect MULTI_5 = new SoundEffect(Sound.ORB_PICKUP, 1, 1.9523809);
	public static final SoundEffect GEM_CRAFT = new SoundEffect(Sound.ORB_PICKUP, 2, 1.5F);
	public static final SoundEffect GEM_USE = new SoundEffect(Sound.GLASS, 1, 2);
	public static final SoundEffect SHARD_FIND = new SoundEffect(Sound.GLASS, 1, 2);
	public static final SoundEffect WITHERCRAFT_1 = new SoundEffect(Sound.ENDERMAN_IDLE, 2F, 1.2F);
	public static final SoundEffect WITHERCRAFT_2 = new SoundEffect(Sound.ENDERMAN_TELEPORT, 1, 1.5F);
	public static final SoundEffect ENDERCHEST_OPEN = new SoundEffect(Sound.CHEST_OPEN, 1, 1);
	public static final SoundEffect ARMOR_SWAP = new SoundEffect(Sound.HORSE_ARMOR, 1F, 1.3F);
	public static final SoundEffect COMPENSATION = new SoundEffect(Sound.NOTE_PLING, 2, 1.5F);
	public static final SoundEffect RENOWN_SHOP_PURCHASE = new SoundEffect(Sound.ORB_PICKUP, 2, 1.5F);
	public static final SoundEffect FUNKY_FEATHER = new SoundEffect(Sound.BAT_TAKEOFF, 2, 2F);
	public static final SoundEffect CLEAR_JEWEL = new SoundEffect(Sound.SHEEP_SHEAR, 1, 2);
	public static final SoundEffect YUMMY_BREAD = new SoundEffect(Sound.EAT, 1, 1.2);
	public static final SoundEffect BREAD_GIVE = new SoundEffect(Sound.GHAST_FIREBALL, 1, 0.3);
	public static final SoundEffect SURVIVOR_HEAL = new SoundEffect(Sound.SILVERFISH_KILL, 1, 1.8);
	public static final SoundEffect SHOCKWAVE = new SoundEffect(Sound.EXPLODE, 2, 1.6);
	public static final SoundEffect BOOSTER_REMIND = new SoundEffect(Sound.CHICKEN_EGG_POP, 2, 1.6);

//	Enchants
	public static final SoundEffect BILLIONAIRE = new SoundEffect(Sound.ORB_PICKUP, 1, 0.73);
	public static final SoundEffect BULLET_TIME = new SoundEffect(Sound.FIZZ, 1, 1.5);
	public static final SoundEffect COMBO_PROC = new SoundEffect(Sound.DONKEY_HIT, 1, 0.5);
	public static final SoundEffect COMBO_STUN = new SoundEffect(Sound.ANVIL_LAND, 1, 1);
	public static final SoundEffect CRUSH = new SoundEffect(Sound.GLASS, 1, 0.80);
	public static final SoundEffect EXE = new SoundEffect(Sound.VILLAGER_DEATH, 1, 0.5);
	public static final SoundEffect EXPLOSIVE_1 = new SoundEffect(Sound.EXPLODE, 0.75, 2);
	public static final SoundEffect EXPLOSIVE_2 = new SoundEffect(Sound.EXPLODE, 0.75, 1);
	public static final SoundEffect EXPLOSIVE_3 = new SoundEffect(Sound.EXPLODE, 0.75, 1.4);
	public static final SoundEffect GAMBLE_YES = new SoundEffect(Sound.NOTE_PLING, 1, 3);
	public static final SoundEffect GAMBLE_NO = new SoundEffect(Sound.NOTE_PLING, 1, 1.5);
	public static final SoundEffect LAST_STAND = new SoundEffect(Sound.ZOMBIE_WOODBREAK, 1, 1);
	public static final SoundEffect LUCKY_SHOT = new SoundEffect(Sound.ZOMBIE_WOODBREAK, 1, 1);
	public static final SoundEffect PIN_DOWN = new SoundEffect(Sound.BURP, 1, 1);
	public static final SoundEffect RGM = new SoundEffect(Sound.ENDERMAN_HIT, 1, 1);
	public static final SoundEffect TELEBOW = new SoundEffect(Sound.ENDERMAN_TELEPORT, 1, 2);
	public static final SoundEffect VENOM = new SoundEffect(Sound.SPIDER_IDLE, 1, 1);
	public static final SoundEffect VOLLEY = new SoundEffect(Sound.SHOOT_ARROW, 1, 1);

//	Megastreaks
	public static final SoundEffect MEGA_GENERAL = new SoundEffect(Sound.WITHER_SPAWN, 1000, 1);
	public static final SoundEffect UBER_100 = new SoundEffect("mob.guardian.curse", 1000, 1);
	public static final SoundEffect UBER_200 = new SoundEffect("mob.guardian.curse", 1000, 1);
	public static final SoundEffect UBER_300 = new SoundEffect("mob.guardian.curse", 1000, 1);
	public static final SoundEffect UBER_400 = new SoundEffect("mob.guardian.curse", 1000, 1);
	public static final SoundEffect UBER_500 = new SoundEffect("mob.guardian.curse", 1000, 1);

//	Upgrades
	public static final SoundEffect STREAKER = new SoundEffect(Sound.BURP, 2, 1.2F);

//	Helmets
	public static final SoundEffect HELMET_CRAFT = new SoundEffect(Sound.ORB_PICKUP, 2, 1.5F);
	public static final SoundEffect HELMET_GUI_OPEN = new SoundEffect(Sound.ANVIL_BREAK, 1, 2);
	public static final SoundEffect HELMET_DOWNGRADE = new SoundEffect(Sound.ANVIL_BREAK, 1, 2);
	public static final SoundEffect HELMET_ACTIVATE = new SoundEffect(Sound.NOTE_PLING, 1.3, 2);
	public static final SoundEffect HELMET_DEPOSIT_GOLD = new SoundEffect(Sound.ZOMBIE_METAL, 1, 2);
	public static final SoundEffect HELMET_TICK = new SoundEffect(Sound.NOTE_STICKS, 2, 1.5);
	public static final SoundEffect GOLD_RUSH = new SoundEffect(Sound.ORB_PICKUP, 1, 0.9);
	public static final SoundEffect LEAP = new SoundEffect(Sound.BAT_TAKEOFF, 1, 1);
	public static final SoundEffect PHOENIX = new SoundEffect()
			.add(new SoundMoment(0).add(Sound.ENDERDRAGON_GROWL, 1, 1).add(Sound.FIZZ, 1, 1));
//	Judgement
	public static final SoundEffect JUDGEMENT_HEAL = new SoundEffect(Sound.BURP, 1, 1);
	public static final SoundEffect JUDGEMENT_WITHER = new SoundEffect(Sound.WITHER_SHOOT, 1, 1);
	public static final SoundEffect JUDGEMENT_RESISTANCE = new SoundEffect(Sound.IRONGOLEM_HIT, 1, 1);
	public static final SoundEffect JUDGEMENT_STRENGTH = new SoundEffect(Sound.ENDERMAN_SCREAM, 1, 1);
	public static final SoundEffect JUDGEMENT_SLOW = new SoundEffect(Sound.ANVIL_LAND, 1, 1);
	public static final SoundEffect JUDGEMENT_HALF_ATTACKER = new SoundEffect(Sound.ZOMBIE_WOODBREAK, 1, 1);
	public static final SoundEffect JUDGEMENT_HALF_DEFENDER = new SoundEffect("mob.guardian.curse", 1000, 1);
	public static final SoundEffect JUDGEMENT_ZEUS_ATTACKER = new SoundEffect(Sound.ENDERDRAGON_GROWL, 1, 1);
	public static final SoundEffect JUDGEMENT_ZEUS_DEFENDER = new SoundEffect(Sound.IRONGOLEM_DEATH, 1, 1);
	public static final SoundEffect JUDGEMENT_HOPPER = new SoundEffect(Sound.ENDERMAN_TELEPORT, 1, 1);

//	Events
	public static final SoundEffect EVENT_START = new SoundEffect(Sound.ENDERDRAGON_GROWL, 2  , 1);
	public static final SoundEffect EVENT_PING = new SoundEffect(Sound.NOTE_PLING, 2, 1F);
	public static final SoundEffect CTF_EXPLOSION = new SoundEffect(Sound.EXPLODE, 1, 2);
	public static final SoundEffect CTF_FLAG_STEAL = new SoundEffect(Sound.NOTE_PLING, 2, 2F);
	public static final SoundEffect CTF_FLAG_STOLEN = new SoundEffect(Sound.NOTE_PLING, 2, 0.5F);
	public static final SoundEffect CTF_FLAG_CAPTURED = new SoundEffect(Sound.BLAZE_DEATH, 2, 0.5F);
	public static final SoundEffect CTF_FLAG_SCORE = new SoundEffect(Sound.LEVEL_UP, 2, 0.5F);
	public static final SoundEffect JUGGERNAUT_EXPLOSION = new SoundEffect(Sound.EXPLODE, 1, 2);
	public static final SoundEffect JUGGERNAUT_END = new SoundEffect(Sound.ENDERDRAGON_DEATH, 1, 2);

//	Kill / Death Effects
	public static final SoundEffect DEATH_GHAST_SCREAM = new SoundEffect(Sound.GHAST_SCREAM, 1, 1);
	public static final SoundEffect KILL_FIRE = new SoundEffect(Sound.FIZZ, 2, 2);

	/*
	public static final SoundEffect SIMPLE_SOUND = new SoundEffect(Sound.ZOMBIE_WOODBREAK, 1, 1);
	public static final SoundEffect COMPLEX_SOUND = new SoundEffect();
	static {
		COMPLEX_SOUND
				.add(new SoundMoment(0)
						.add(Sound.ORB_PICKUP, 1, 1)
						.add(Sound.EXPLODE, 1, 1))
				.add(new SoundMoment(2)
						.add(Sound.ORB_PICKUP, 1, 1)
						.add(Sound.IRONGOLEM_DEATH, 1, 1));
	}
	*/

	public static class SoundEffect {
		private SoundMoment soundMoment;
		private final List<SoundMoment> soundTimeList = new ArrayList<>();

		public SoundEffect() {
		}

		public SoundEffect(Sound sound, double volume, double pitch) {
			this.soundMoment = new SoundMoment(new SoundMoment.BukkitSound(sound, volume, pitch));
		}

		public SoundEffect(String soundString, double volume, double pitch) {
			this.soundMoment = new SoundMoment(new SoundMoment.BukkitSound(soundString, volume, pitch));
		}

		public SoundEffect add(SoundMoment soundMoment) {
			soundTimeList.add(soundMoment);
			return this;
		}

		public void play(Location location, double radius) {
			for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				if(onlinePlayer.getWorld() != location.getWorld() || onlinePlayer.getLocation().distance(location) > radius) continue;
				play(onlinePlayer);
			}
		}

		public void play(Player player) {
			if(!player.isOnline()) return;
			if(soundMoment != null) {
				soundMoment.play(player);
				return;
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					int count = 0;
					List<SoundMoment> toRemove = new ArrayList<>();
					for(SoundMoment soundMoment : soundTimeList) {
						if(soundMoment.tick == count) {
							toRemove.add(soundMoment);
							soundMoment.play(player);
						}
						count++;
					}
					soundTimeList.removeAll(toRemove);
					if(soundTimeList.isEmpty()) cancel();
				}
			}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
		}

		public void play(Location location) {
			if(soundMoment != null) {
				soundMoment.play(location);
				return;
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					int count = 0;
					List<SoundMoment> toRemove = new ArrayList<>();
					for(SoundMoment soundMoment : soundTimeList) {
						if(soundMoment.tick == count) {
							toRemove.add(soundMoment);
							soundMoment.play(location);
						}
						count++;
					}
					soundTimeList.removeAll(toRemove);
					if(soundTimeList.isEmpty()) cancel();
				}
			}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
		}
	}

	public static class SoundMoment {
		private final List<BukkitSound> bukkitSounds = new ArrayList<>();
		private int tick;

		//			Time constructor
		public SoundMoment(int tick) {
			this.tick = tick;
		}

		//			Sound with no time
		public SoundMoment(BukkitSound bukkitSound) {
			this.bukkitSounds.add(bukkitSound);
		}

		//			Add sound to time constructed with time constructor
		public SoundMoment add(Sound sound, int volume, double pitch) {
			bukkitSounds.add(new BukkitSound(sound, volume, pitch));
			return this;
		}
		public SoundMoment add(String soundString, int volume, double pitch) {
			bukkitSounds.add(new BukkitSound(soundString, volume, pitch));
			return this;
		}

		public void play(Player player) {
			if(!player.isOnline()) return;
			for(BukkitSound bukkitSound : bukkitSounds) {
				if(bukkitSound.sound != null) {
					player.playSound(player.getLocation(), bukkitSound.sound, bukkitSound.volume, bukkitSound.pitch);
				} else {
					player.playSound(player.getLocation(), bukkitSound.soundString, bukkitSound.volume, bukkitSound.pitch);
				}
			}
		}
		public void play(Location location) {
			for(BukkitSound bukkitSound : bukkitSounds) {
				if(bukkitSound.sound != null) location.getWorld().playSound(location, bukkitSound.sound, bukkitSound.volume, bukkitSound.pitch);
			}
		}

		public static class BukkitSound {
			private Sound sound;
			private String soundString;
			private final float volume;
			private final float pitch;

			private BukkitSound(Sound sound, double volume, double pitch) {
				this.sound = sound;
				this.volume = (float) volume;
				this.pitch = (float) pitch;
			}

			private BukkitSound(String soundString, double volume, double pitch) {
				this.soundString = soundString;
				this.volume = (float) volume;
				this.pitch = (float) pitch;
			}
		}
	}
}
