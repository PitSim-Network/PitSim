package dev.kyro.pitsim.acosmetics.killeffectsplayer;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.acosmetics.CosmeticType;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class DeathHowl extends PitCosmetic {

	public DeathHowl() {
		super("&7Howl", "howl", CosmeticType.PLAYER_KILL_EFFECT);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getDeadPlayer()) || !isEnabled(killEvent.getKillerPitPlayer()) ||
				nearMid(killEvent.getDeadPlayer())) return;
		Sounds.DEATH_HOWL.play(killEvent.getDeadPlayer().getLocation(), SOUND_RANGE);
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.BONE)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Devour your enemies and",
						"&7howl like a werewolf!"
				))
				.getItemStack();
		return itemStack;
	}
}
