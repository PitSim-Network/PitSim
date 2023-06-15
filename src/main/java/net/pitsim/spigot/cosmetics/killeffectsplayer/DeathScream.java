package net.pitsim.spigot.cosmetics.killeffectsplayer;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.cosmetics.CosmeticType;
import net.pitsim.spigot.cosmetics.PitCosmetic;
import net.pitsim.spigot.controllers.PlayerManager;
import net.pitsim.spigot.events.KillEvent;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class DeathScream extends PitCosmetic {

	public DeathScream() {
		super("&4Wretched Scream", "scream", CosmeticType.PLAYER_KILL_EFFECT);
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
				.setLore(new ALoreBuilder(
						"&7Hear your enemies scream at",
						"&7the top of their lungs!"
				))
				.getItemStack();
		return itemStack;
	}
}
