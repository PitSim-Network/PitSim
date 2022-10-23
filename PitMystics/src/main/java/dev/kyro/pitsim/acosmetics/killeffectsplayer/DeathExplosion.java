package dev.kyro.pitsim.acosmetics.killeffectsplayer;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.acosmetics.CosmeticType;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class DeathExplosion extends PitCosmetic {

	public DeathExplosion() {
		super("&9BANG!", "explosion", CosmeticType.PLAYER_KILL_EFFECT);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getDeadPlayer()) || !isEnabled(killEvent.getKillerPitPlayer()) ||
				nearMid(killEvent.getDeadPlayer())) return;

		for(int i = 0; i < 5; i++) {
			Location location = killEvent.getDeadPlayer().getLocation();
			if(i != 0) location.add(Misc.randomOffset(20), Misc.randomOffsetPositive(4), Misc.randomOffset(20));
			location.getWorld().playEffect(location, Effect.EXPLOSION_HUGE, 1);
			Sounds.DEATH_EXPLOSION.play(location, SOUND_RANGE);
		}
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.BONE)
				.setName(getDisplayName())
				.getItemStack();
		return itemStack;
	}
}
