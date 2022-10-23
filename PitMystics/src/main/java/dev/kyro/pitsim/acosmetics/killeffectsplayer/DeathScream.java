package dev.kyro.pitsim.acosmetics.killeffectsplayer;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.acosmetics.CosmeticType;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class DeathScream extends PitCosmetic {

	public DeathScream() {
		super("&fScream", "scream", CosmeticType.PLAYER_KILL_EFFECT);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getDeadPlayer()) || !isEnabled(killEvent.getKillerPitPlayer()) ||
				nearMid(killEvent.getDeadPlayer())) return;
		Sounds.DEATH_GHAST_SCREAM.play(killEvent.getDeadPlayer().getLocation(), SOUND_RANGE);
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.GHAST_TEAR)
				.setName(getDisplayName())
				.getItemStack();
		return itemStack;
	}
}
