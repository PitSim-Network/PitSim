package dev.kyro.pitsim.acosmetics;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleCollection {
	public Map<String, List<ParticleData>> particleCollectionMap = new HashMap<>();

	public static class ParticleData {
		public PitParticle pitParticle;
		public ParticleOffset particleOffset;

		public ParticleData(PitParticle pitParticle, ParticleOffset particleOffset) {
			this.pitParticle = pitParticle;
			this.particleOffset = particleOffset;
		}
	}

	public ParticleCollection addParticle(String ref, PitParticle pitParticle, ParticleOffset particleOffset) {
		particleCollectionMap.putIfAbsent(ref, new ArrayList<>());
		particleCollectionMap.get(ref).add(new ParticleData(pitParticle, particleOffset));
		return this;
	}

	public ParticleCollection addParticles(String ref, List<ParticleData> particleDataList) {
		for(ParticleData particleData : particleDataList) addParticle(ref, particleData.pitParticle, particleData.particleOffset);
		return this;
	}

	public void display(EntityPlayer entityPlayer, Location location) {
		for(Map.Entry<String, List<ParticleData>> entry : particleCollectionMap.entrySet()) {
			for(ParticleData particleData : entry.getValue()) {
				particleData.pitParticle.display(entityPlayer, location, particleData.particleOffset);
			}
		}
	}
}
