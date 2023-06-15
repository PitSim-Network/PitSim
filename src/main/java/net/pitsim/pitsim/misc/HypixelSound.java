package net.pitsim.pitsim.misc;

import net.pitsim.pitsim.PitSim;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HypixelSound {
	public Map<Integer, List<SoundPart>> soundMap = new HashMap<>();

	public Player player;
	public Location location;

	public HypixelSound(Player player, Location location) {
		this.player = player;
		this.location = location;
	}

	public void addSound(int tick, String sound, double volume, double pitch) {
		soundMap.putIfAbsent(tick, new ArrayList<>());
		soundMap.get(tick).add(new SoundPart(sound, volume, pitch));
	}

	private class SoundPart {
		private final String sound;
		private final float volume;
		private final float pitch;

		public SoundPart(String sound, double volume, double pitch) {
			this.sound = sound;
			this.volume = (float) volume;
			this.pitch = (float) pitch;
		}

		public void play() {
			CraftPlayer craftPlayer = (CraftPlayer) player;
			PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(sound, location.getX(), location.getY(), location.getZ(), volume, pitch);
			craftPlayer.getHandle().playerConnection.sendPacket(packet);
		}
	}

	public enum Sound {
		FRESH_DROP(-1, 0),
		TIER_1(1, 60),
		TIER_2(2, 70),
		TIER_3(3, 82);

		public int tier;
		public int length;
		Sound(int tier, int length) {
			this.tier = tier;
			this.length = length;
		}

		public static Sound getTier(int tier) {
			if(tier > 3) tier = 3;
			for(Sound value : values()) {
				if(value.tier == tier) return value;
			}
			return null;
		}
	}

	public static void play(Player player, Location location, Sound sound) {
		new HypixelSound(player, location).play(sound);
	}

	public static void play(Player player, Location location, Sound sound, boolean rareSound) {
		new HypixelSound(player, location).play(sound, rareSound);
	}

	private void play(Sound sound) {
		play(sound, false);
	}

	//	The rare boolean is the same as artifact and other things worthy of displaying in chat message
	private void play(Sound sound, boolean rareSound) {
		int tick = 0;

		if(sound == Sound.FRESH_DROP) {
			addSound(tick, "random.orb", 1.0, 1.7936507);
			addSound(tick, "random.click", 0.25, 0.82539684);
			addSound(tick, "random.successful_hit", 0.4, 0.82539684);
			addSound(tick, "note.harp", 0.65, 0.82539684);
			addSound(tick, "random.click", 0.25, 1.0476191);
			addSound(tick, "random.successful_hit", 0.4, 1.0476191);
			addSound(tick, "note.harp", 0.65, 1.0476191);
			addSound(tick, "random.click", 0.25, 1.1111112);
			addSound(tick, "random.successful_hit", 0.4, 1.1111112);
			addSound(tick, "note.harp", 0.65, 1.1111112);
			addSound(tick, "random.click", 0.25, 1.4126984);
			addSound(tick, "random.successful_hit", 0.4, 1.4126984);
			addSound(tick, "note.harp", 0.65, 1.4126984);
			tick += 1;
			addSound(tick, "random.pop", 0.95, 0.82539684);
			tick += 1;
			addSound(tick, "random.pop", 0.95, 1.0476191);
			tick += 1;
			addSound(tick, "random.click", 0.25, 0.93650794);
			addSound(tick, "random.successful_hit", 0.4, 0.93650794);
			addSound(tick, "note.harp", 0.65, 0.93650794);
			addSound(tick, "random.click", 0.25, 1.1746032);
			addSound(tick, "random.successful_hit", 0.4, 1.1746032);
			addSound(tick, "note.harp", 0.65, 1.1746032);
			addSound(tick, "random.click", 0.25, 1.2539682);
			addSound(tick, "random.successful_hit", 0.4, 1.2539682);
			addSound(tick, "note.harp", 0.65, 1.2539682);
			addSound(tick, "random.click", 0.25, 1.5873016);
			addSound(tick, "random.successful_hit", 0.4, 1.5873016);
			addSound(tick, "note.harp", 0.65, 1.5873016);
			addSound(tick, "random.pop", 0.95, 1.1111112);
			tick += 1;
			addSound(tick, "random.pop", 0.95, 0.93650794);
			addSound(tick, "random.pop", 0.95, 1.4126984);
			tick += 1;
			addSound(tick, "random.pop", 0.95, 1.1746032);
			tick += 1;
			addSound(tick, "random.pop", 0.95, 1.2539682);
			tick += 1;
			addSound(tick, "random.click", 0.25, 1.0476191);
			addSound(tick, "random.successful_hit", 0.4, 1.0476191);
			addSound(tick, "note.harp", 0.65, 1.0476191);
			addSound(tick, "random.click", 0.25, 1.3333334);
			addSound(tick, "random.successful_hit", 0.4, 1.3333334);
			addSound(tick, "note.harp", 0.65, 1.3333334);
			addSound(tick, "random.click", 0.25, 1.4126984);
			addSound(tick, "random.successful_hit", 0.4, 1.4126984);
			addSound(tick, "note.harp", 0.65, 1.4126984);
			addSound(tick, "random.click", 0.25, 1.7777778);
			addSound(tick, "random.successful_hit", 0.4, 1.7777778);
			addSound(tick, "note.harp", 0.65, 1.7777778);
			addSound(tick, "random.pop", 0.95, 1.5873016);
			tick += 1;
			addSound(tick, "random.pop", 0.95, 1.0476191);
			tick += 1;
			addSound(tick, "random.pop", 0.95, 1.3333334);
			tick += 1;
			addSound(tick, "random.pop", 0.95, 1.4126984);
			tick += 1;
			addSound(tick, "random.click", 0.25, 1.1746032);
			addSound(tick, "random.successful_hit", 0.4, 1.1746032);
			addSound(tick, "note.harp", 0.65, 1.1746032);
			addSound(tick, "random.click", 0.25, 1.4920635);
			addSound(tick, "random.successful_hit", 0.4, 1.4920635);
			addSound(tick, "note.harp", 0.65, 1.4920635);
			addSound(tick, "random.click", 0.25, 1.5873016);
			addSound(tick, "random.successful_hit", 0.4, 1.5873016);
			addSound(tick, "note.harp", 0.65, 1.5873016);
			addSound(tick, "random.click", 0.25, 2.0);
			addSound(tick, "random.successful_hit", 0.4, 2.0);
			addSound(tick, "note.harp", 0.65, 2.0);
			addSound(tick, "random.pop", 0.95, 1.7777778);
			tick += 1;
			addSound(tick, "random.pop", 0.95, 1.1746032);
			tick += 1;
			addSound(tick, "random.pop", 0.95, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 0.95, 1.5873016);
			tick += 1;
			addSound(tick, "random.pop", 0.95, 2.0);
		}else if(sound == Sound.TIER_1) {
			addSound(tick, "random.pop", 0.8, 0.5555556);
			addSound(tick, "random.pop", 0.8, 1.1111112);
			tick += 3;
			addSound(tick, "random.click", 0.4, 0.82539684);
			tick += 3;
			addSound(tick, "random.pop", 0.9, 0.6984127);
			addSound(tick, "random.pop", 0.9, 1.4126984);
			tick += 3;
			addSound(tick, "random.click", 0.4, 0.82539684);
			tick += 6;

			addSound(tick, "random.pop", 1.16, 0.5555556);
			tick += 2;
			addSound(tick, "random.pop", 1.2, 0.6984127);
			tick += 2;
			addSound(tick, "random.pop", 1.24, 0.82539684);
			tick += 2;
			addSound(tick, "random.pop", 1.28, 1.1111112);
			tick += 2;
			addSound(tick, "random.pop", 1.32, 0.5873016);
			tick += 2;
			addSound(tick, "random.pop", 1.36, 0.74603176);
			tick += 2;
			addSound(tick, "random.pop", 1.4, 0.8888889);
			tick += 2;
			addSound(tick, "random.pop", 1.44, 1.1746032);
			tick += 2;
			addSound(tick, "random.pop", 1.48, 0.61904764);
			tick += 2;
			addSound(tick, "random.pop", 1.52, 0.7936508);
			tick += 2;
			addSound(tick, "random.pop", 1.56, 0.93650794);
			tick += 2;
			addSound(tick, "random.pop", 1.6, 1.2539682);
			tick += 2;
			addSound(tick, "random.pop", 1.64, 0.6666667);
			tick += 2;
			addSound(tick, "random.pop", 1.68, 0.82539684);
			tick += 2;

			addSound(tick, "random.pop", 1.72, 1.0);
			tick += 1;
			addSound(tick, "random.pop", 1.74, 1.0);
			tick += 1;
			addSound(tick, "random.pop", 1.76, 1.3333334);
			tick += 1;
			addSound(tick, "random.pop", 1.78, 1.3333334);
			tick += 1;
			addSound(tick, "random.pop", 1.8, 0.6984127);
			tick += 1;
			addSound(tick, "random.pop", 1.82, 0.6984127);
			tick += 1;
			addSound(tick, "random.pop", 1.84, 0.8888889);
			tick += 1;
			addSound(tick, "random.pop", 1.86, 0.8888889);
			tick += 1;
			addSound(tick, "random.pop", 1.88, 1.0476191);
			tick += 1;

			addSound(tick, "random.pop", 1.9, 1.0476191);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 1.92, 1.4126984);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 1.94, 1.4126984);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 1.96, 0.74603176);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 1.98, 0.74603176);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.0, 0.93650794);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.02, 0.93650794);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.04, 1.1111112);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			if(rareSound) addSound(tick, "mob.enderdragon.growl", 1, 1);
			tick += 1;
			addSound(tick, "fireworks.twinkle", 0.9, 1.7936507);
			addSound(tick, "fireworks.largeBlast", 0.9, 1.1904762);
		} else if(sound == Sound.TIER_2) {
			addSound(tick, "random.pop", 0.8, 0.5555556);
			addSound(tick, "random.pop", 0.8, 1.1111112);
			tick += 3;
			addSound(tick, "random.click", 0.4, 0.82539684);
			tick += 3;
			addSound(tick, "random.pop", 0.9, 0.6984127);
			addSound(tick, "random.pop", 0.9, 1.4126984);
			tick += 3;
			addSound(tick, "random.click", 0.4, 0.82539684);
			tick += 6;

			addSound(tick, "random.pop", 1.16, 0.5555556);
			addSound(tick, "note.harp", 0.85, 0.6984127);
			tick += 2;
			addSound(tick, "random.pop", 1.2, 0.6984127);
			addSound(tick, "note.harp", 0.85, 0.8888889);
			tick += 2;
			addSound(tick, "random.pop", 1.24, 0.82539684);
			addSound(tick, "note.harp", 0.85, 1.0476191);
			tick += 2;
			addSound(tick, "random.pop", 1.28, 1.1111112);
			addSound(tick, "note.harp", 0.85, 1.4126984);
			tick += 2;
			addSound(tick, "random.pop", 1.32, 0.5873016);
			addSound(tick, "note.harp", 0.85, 0.74603176);
			tick += 2;
			addSound(tick, "random.pop", 1.36, 0.74603176);
			addSound(tick, "note.harp", 0.85, 0.93650794);
			tick += 2;
			addSound(tick, "random.pop", 1.4, 0.8888889);
			addSound(tick, "note.harp", 0.85, 1.1111112);
			tick += 2;
			addSound(tick, "random.pop", 1.44, 1.1746032);
			addSound(tick, "note.harp", 0.85, 1.4920635);
			tick += 2;
			addSound(tick, "random.pop", 1.48, 0.61904764);
			addSound(tick, "note.harp", 0.85, 0.7936508);
			tick += 2;
			addSound(tick, "random.pop", 1.52, 0.7936508);
			addSound(tick, "note.harp", 0.85, 1.0);
			tick += 2;
			addSound(tick, "random.pop", 1.56, 0.93650794);
			addSound(tick, "note.harp", 0.85, 1.1746032);
			tick += 2;
			addSound(tick, "random.pop", 1.6, 1.2539682);
			addSound(tick, "note.harp", 0.85, 1.5873016);
			tick += 2;
			addSound(tick, "random.pop", 1.64, 0.6666667);
			addSound(tick, "note.harp", 0.85, 0.82539684);
			tick += 2;
			addSound(tick, "random.pop", 1.68, 0.82539684);
			addSound(tick, "note.harp", 0.85, 1.0476191);
			tick += 2;
			addSound(tick, "random.pop", 1.72, 1.0);
			addSound(tick, "note.harp", 0.85, 1.2539682);
			tick += 1;
			addSound(tick, "random.pop", 1.74, 1.0);
			addSound(tick, "note.harp", 0.85, 1.2539682);
			tick += 1;
			addSound(tick, "random.pop", 1.76, 1.3333334);
			addSound(tick, "note.harp", 0.85, 1.6666666);
			tick += 1;
			addSound(tick, "random.pop", 1.78, 1.3333334);
			addSound(tick, "note.harp", 0.85, 1.6666666);
			tick += 1;
			addSound(tick, "random.pop", 1.8, 0.6984127);
			addSound(tick, "note.harp", 0.85, 0.8888889);
			tick += 1;
			addSound(tick, "random.pop", 1.82, 0.6984127);
			addSound(tick, "note.harp", 0.85, 0.8888889);
			tick += 1;
			addSound(tick, "random.pop", 1.84, 0.8888889);
			addSound(tick, "note.harp", 0.85, 1.1111112);
			tick += 1;
			addSound(tick, "random.pop", 1.86, 0.8888889);
			addSound(tick, "note.harp", 0.85, 1.1111112);
			tick += 1;
			addSound(tick, "random.pop", 1.88, 1.0476191);
			addSound(tick, "note.harp", 0.85, 1.3333334);
			tick += 1;
			addSound(tick, "random.pop", 1.9, 1.0476191);
			addSound(tick, "note.harp", 0.85, 1.3333334);
			tick += 1;
			addSound(tick, "random.pop", 1.92, 1.4126984);
			addSound(tick, "note.harp", 0.85, 1.7777778);
			tick += 1;
			addSound(tick, "random.pop", 1.94, 1.4126984);
			addSound(tick, "note.harp", 0.85, 1.7777778);
			tick += 1;
			addSound(tick, "random.pop", 1.96, 0.74603176);
			addSound(tick, "note.harp", 0.85, 0.93650794);
			tick += 1;
			addSound(tick, "random.pop", 1.98, 0.74603176);
			addSound(tick, "note.harp", 0.85, 0.93650794);
			tick += 1;
			addSound(tick, "random.pop", 2.0, 0.93650794);
			addSound(tick, "note.harp", 0.85, 1.1746032);
			tick += 1;
			addSound(tick, "random.pop", 2.02, 0.93650794);
			addSound(tick, "note.harp", 0.85, 1.1746032);
			tick += 1;
			addSound(tick, "random.pop", 2.04, 1.1111112);
			addSound(tick, "note.harp", 0.85, 1.4126984);
			tick += 1;
			addSound(tick, "random.pop", 2.06, 1.1111112);
			addSound(tick, "note.harp", 0.85, 1.4126984);
			tick += 1;
			addSound(tick, "random.pop", 2.08, 1.4920635);
			addSound(tick, "note.harp", 0.85, 1.8730159);
			tick += 1;
			addSound(tick, "random.pop", 2.1, 1.4920635);
			addSound(tick, "note.harp", 0.85, 1.8730159);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.12, 0.7936508);
			addSound(tick, "note.harp", 0.85, 1.0);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.14, 0.7936508);
			addSound(tick, "note.harp", 0.85, 1.0);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.16, 1.0);
			addSound(tick, "note.harp", 0.85, 1.2539682);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.18, 1.0);
			addSound(tick, "note.harp", 0.85, 1.2539682);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.2, 1.1746032);
			addSound(tick, "note.harp", 0.85, 1.4920635);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.22, 1.1746032);
			addSound(tick, "note.harp", 0.85, 1.4920635);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.24, 1.5873016);
			addSound(tick, "note.harp", 0.85, 2.0);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			if(rareSound) addSound(tick, "mob.enderdragon.growl", 1, 1);
			tick += 1;
			addSound(tick, "fireworks.twinkle", 0.9, 1.7936507);
			addSound(tick, "fireworks.largeBlast", 0.9, 1.1904762);
		} else if(sound == Sound.TIER_3) {
			addSound(tick, "random.pop", 0.8, 0.5555556);
			addSound(tick, "random.pop", 0.8, 1.1111112);
			tick += 3;
			addSound(tick, "random.click", 0.4, 0.82539684);
			tick += 3;
			addSound(tick, "random.pop", 0.9, 0.6984127);
			addSound(tick, "random.pop", 0.9, 1.4126984);
			tick += 3;
			addSound(tick, "random.click", 0.4, 0.82539684);
			tick += 6;

			addSound(tick, "random.pop", 1.16, 0.5873016);
			addSound(tick, "note.harp", 0.85, 0.74603176);
			tick += 2;
			addSound(tick, "random.pop", 1.2, 0.74603176);
			addSound(tick, "note.harp", 0.85, 0.93650794);
			tick += 2;
			addSound(tick, "random.pop", 1.24, 0.8888889);
			addSound(tick, "note.harp", 0.85, 1.1111112);
			tick += 2;
			addSound(tick, "random.pop", 1.28, 1.1746032);
			addSound(tick, "note.harp", 0.85, 1.4920635);
			tick += 2;

			addSound(tick, "random.pop", 1.32, 0.61904764);
			addSound(tick, "note.harp", 0.85, 0.7936508);
			tick += 2;
			addSound(tick, "random.pop", 1.36, 0.7936508);
			addSound(tick, "note.harp", 0.85, 1.0);
			tick += 2;
			addSound(tick, "random.pop", 1.4, 0.93650794);
			addSound(tick, "note.harp", 0.85, 1.1746032);
			tick += 2;
			addSound(tick, "random.pop", 1.44, 1.2539682);
			addSound(tick, "note.harp", 0.85, 1.5873016);
			tick += 2;

			addSound(tick, "random.pop", 1.48, 0.6666667);
			addSound(tick, "note.harp", 0.85, 0.82539684);
			tick += 2;
			addSound(tick, "random.pop", 1.52, 0.82539684);
			addSound(tick, "note.harp", 0.85, 1.0476191);
			tick += 2;
			addSound(tick, "random.pop", 1.56, 1.0);
			addSound(tick, "note.harp", 0.85, 1.2539682);
			tick += 2;
			addSound(tick, "random.pop", 1.6, 1.3333334);
			addSound(tick, "note.harp", 0.85, 1.6666666);
			tick += 2;

			addSound(tick, "random.pop", 1.64, 0.6984127);
			addSound(tick, "note.harp", 0.85, 0.8888889);
			tick += 2;
			addSound(tick, "random.pop", 1.68, 0.8888889);
			addSound(tick, "note.harp", 0.85, 1.1111112);
			tick += 2;
			addSound(tick, "random.pop", 1.72, 1.0476191);
			addSound(tick, "note.harp", 0.85, 1.3333334);
			tick += 1;
			addSound(tick, "random.pop", 1.74, 1.0476191);
			addSound(tick, "note.harp", 0.85, 1.3333334);
			tick += 1;
			addSound(tick, "random.pop", 1.72, 1.4126984);
			addSound(tick, "note.harp", 0.85, 1.7777778);
			tick += 1;
			addSound(tick, "random.pop", 1.74, 1.4126984);
			addSound(tick, "note.harp", 0.85, 1.7777778);
			tick += 1;

			addSound(tick, "random.pop", 1.8, 0.74603176);
			addSound(tick, "note.harp", 0.85, 0.93650794);
			tick += 1;
			addSound(tick, "random.pop", 1.82, 0.74603176);
			addSound(tick, "note.harp", 0.85, 0.93650794);
			tick += 1;
			addSound(tick, "random.pop", 1.84, 0.93650794);
			addSound(tick, "note.harp", 0.85, 1.1746032);
			tick += 1;
			addSound(tick, "random.pop", 1.86, 0.93650794);
			addSound(tick, "note.harp", 0.85, 1.1746032);
			tick += 1;
			addSound(tick, "random.pop", 1.88, 1.1111112);
			addSound(tick, "note.harp", 0.85, 1.4126984);
			tick += 1;
			addSound(tick, "random.pop", 1.9, 1.1111112);
			addSound(tick, "note.harp", 0.85, 1.4126984);
			tick += 1;
			addSound(tick, "random.pop", 1.92, 1.4920635);
			addSound(tick, "note.harp", 0.85, 1.8730159);
			tick += 1;
			addSound(tick, "random.pop", 1.94, 1.4920635);
			addSound(tick, "note.harp", 0.85, 1.8730159);
			tick += 1;

			addSound(tick, "random.pop", 1.96, 0.7936508);
			addSound(tick, "note.harp", 0.85, 1.0);
			tick += 1;
			addSound(tick, "random.pop", 1.98, 0.7936508);
			addSound(tick, "note.harp", 0.85, 1.0);
			tick += 1;
			addSound(tick, "random.pop", 2.0, 1.0);
			addSound(tick, "note.harp", 0.85, 1.2539682);
			tick += 1;
			addSound(tick, "random.pop", 2.02, 1.0);
			addSound(tick, "note.harp", 0.85, 1.2539682);
			tick += 1;
			addSound(tick, "random.pop", 2.04, 1.1746032);
			addSound(tick, "note.harp", 0.85, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.06, 1.1746032);
			addSound(tick, "note.harp", 0.85, 1.4920635);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.08, 1.5873016);
			addSound(tick, "note.harp", 0.76, 0.3968254);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.1, 1.5873016);
			addSound(tick, "note.harp", 0.76, 0.47619048);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.12, 0.82539684);
			addSound(tick, "note.harp", 0.76, 0.5555556);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.14, 0.82539684);
			addSound(tick, "note.harp", 0.76, 0.63492066);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.16, 1.0476191);
			addSound(tick, "note.harp", 0.76, 0.71428573);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.18, 1.0476191);
			addSound(tick, "note.harp", 0.76, 0.7936508);
			addSound(tick, "random.successful_hit", 0.42, 1.1746032);
			addSound(tick, "random.successful_hit", 0.42, 1.4920635);
			tick += 1;
			addSound(tick, "random.pop", 2.2, 1.2539682);
			addSound(tick, "note.harp", 0.76, 0.8730159);
			addSound(tick, "random.successful_hit", 0.42, 1.4126984);
			addSound(tick, "random.successful_hit", 0.42, 1.6666666);
			tick += 1;
			addSound(tick, "random.pop", 2.22, 1.2539682);
			addSound(tick, "note.harp", 0.76, 0.95238096);
			addSound(tick, "random.successful_hit", 0.42, 1.4126984);
			addSound(tick, "random.successful_hit", 0.42, 1.6666666);
			tick += 1;
			addSound(tick, "random.pop", 2.24, 1.6666666);
			addSound(tick, "note.harp", 0.76, 1.031746);
			addSound(tick, "random.successful_hit", 0.42, 1.4126984);
			addSound(tick, "random.successful_hit", 0.42, 1.6666666);
			tick += 1;
			addSound(tick, "random.pop", 2.26, 1.6666666);
			addSound(tick, "note.harp", 0.76, 1.1111112);
			addSound(tick, "random.successful_hit", 0.42, 1.4126984);
			addSound(tick, "random.successful_hit", 0.42, 1.6666666);
			tick += 1;
			addSound(tick, "random.pop", 2.28, 0.8888889);
			addSound(tick, "note.harp", 0.76, 1.1904762);
			addSound(tick, "random.successful_hit", 0.42, 1.4126984);
			addSound(tick, "random.successful_hit", 0.42, 1.6666666);
			tick += 1;
			addSound(tick, "random.pop", 2.3, 0.8888889);
			addSound(tick, "note.harp", 0.76, 1.2698413);
			addSound(tick, "random.successful_hit", 0.42, 1.4126984);
			addSound(tick, "random.successful_hit", 0.42, 1.6666666);
			tick += 1;
			addSound(tick, "random.pop", 2.32, 1.1111112);
			addSound(tick, "note.harp", 0.76, 1.3492063);
			addSound(tick, "random.successful_hit", 0.42, 1.4126984);
			addSound(tick, "random.successful_hit", 0.42, 1.6666666);
			tick += 1;
			addSound(tick, "random.pop", 2.34, 1.1111112);
			addSound(tick, "note.harp", 0.76, 1.4285715);
			addSound(tick, "random.successful_hit", 0.42, 1.6666666);
			addSound(tick, "random.successful_hit", 0.42, 2.0);
			tick += 1;
			addSound(tick, "random.pop", 2.36, 1.3333334);
			addSound(tick, "note.harp", 0.76, 1.5079365);
			addSound(tick, "random.successful_hit", 0.42, 1.6666666);
			addSound(tick, "random.successful_hit", 0.42, 2.0);
			tick += 1;
			addSound(tick, "random.pop", 2.38, 1.3333334);
			addSound(tick, "note.harp", 0.76, 1.5873016);
			addSound(tick, "random.successful_hit", 0.42, 1.6666666);
			addSound(tick, "random.successful_hit", 0.42, 2.0);
			tick += 1;
			addSound(tick, "random.pop", 2.4, 1.7777778);
			addSound(tick, "note.harp", 0.76, 1.6666666);
			addSound(tick, "random.successful_hit", 0.42, 1.6666666);
			addSound(tick, "random.successful_hit", 0.42, 2.0);
			tick += 1;
			addSound(tick, "random.pop", 2.42, 1.7777778);
			addSound(tick, "note.harp", 0.76, 1.7460318);
			addSound(tick, "random.successful_hit", 0.42, 1.6666666);
			addSound(tick, "random.successful_hit", 0.42, 2.0);
			tick += 1;
			addSound(tick, "random.pop", 2.44, 0.93650794);
			addSound(tick, "note.harp", 0.76, 1.8253968);
			addSound(tick, "random.successful_hit", 0.42, 1.6666666);
			addSound(tick, "random.successful_hit", 0.42, 2.0);
			tick += 1;
			addSound(tick, "random.pop", 2.46, 0.93650794);
			addSound(tick, "note.harp", 0.76, 1.9047619);
			addSound(tick, "random.successful_hit", 0.42, 1.6666666);
			addSound(tick, "random.successful_hit", 0.42, 2.0);
			tick += 1;
			addSound(tick, "random.pop", 2.48, 1.1746032);
			addSound(tick, "note.harp", 0.76, 2.0);
			addSound(tick, "random.successful_hit", 0.42, 1.6666666);
			addSound(tick, "random.successful_hit", 0.42, 2.0);
			if(rareSound) addSound(tick, "mob.enderdragon.growl", 1, 1);
			tick += 1;
			addSound(tick, "fireworks.twinkle", 0.9, 1.7936507);
			addSound(tick, "fireworks.largeBlast", 0.9, 1.1904762);
		}

		new BukkitRunnable() {
			int tick = 0;
			@Override
			public void run() {
				if(!soundMap.containsKey(tick)) {
					tick++;
					return;
				}

				List<SoundPart> parts = soundMap.remove(tick);
				parts.forEach(SoundPart::play);
				if(soundMap.isEmpty()) cancel();
				tick++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);

//		new Thread(() -> {
//			int millis = 0;
//			while(true) {
//				if(!soundMap.containsKey(millis)) {
//					millis++;
//					sleep(1);
//					continue;
//				}
//
//				List<SoundPart> parts = soundMap.remove(millis);
//				parts.forEach(SoundPart::play);
//				if(soundMap.isEmpty()) break;
//				millis++;
//			}
//		}).start();
	}

	public static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
