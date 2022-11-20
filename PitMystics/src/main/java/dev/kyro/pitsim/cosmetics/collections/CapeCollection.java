package dev.kyro.pitsim.cosmetics.collections;

import dev.kyro.pitsim.cosmetics.particles.ParticleColor;
import dev.kyro.pitsim.cosmetics.CosmeticManager;
import dev.kyro.pitsim.cosmetics.ParticleOffset;
import dev.kyro.pitsim.cosmetics.PitCosmetic;
import dev.kyro.pitsim.cosmetics.PitParticle;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CapeCollection extends ParticleCollection {

	public CapeCollection(PitParticle particle) {
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 7; j++) {
				addParticle("standing", particle,
						new ParticleOffset(-0.225 + 0.15 * i, -0.15 * j, -0.25 - 0.04 * j));
				addParticle("sneaking", particle,
						new ParticleOffset(-0.225 + 0.15 * i, -0.13 * j, -0.25 - 0.08 * j));
			}
		}
	}

	public void draw(PitCosmetic pitCosmetic, PitPlayer pitPlayer, ParticleColor particleColor) {
		if(!CosmeticManager.isStandingStill(pitPlayer.player)) return;

		Location displayLocation = pitPlayer.player.getLocation();
		double increase = 1.4;
		if(pitPlayer.player.isSneaking()) increase -= 0.25;
		displayLocation.add(0, increase, 0);

		for(Player onlinePlayer : CosmeticManager.getDisplayPlayers(pitPlayer.player, displayLocation)) {
			EntityPlayer entityPlayer = ((CraftPlayer) onlinePlayer).getHandle();
			if(pitPlayer.player.isSneaking()) {
				display("sneaking", entityPlayer, displayLocation, particleColor);
			} else {
				display("standing", entityPlayer, displayLocation, particleColor);
			}
		}
	}
}
