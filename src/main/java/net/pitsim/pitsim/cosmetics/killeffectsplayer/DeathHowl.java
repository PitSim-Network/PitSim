package net.pitsim.pitsim.cosmetics.killeffectsplayer;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.cosmetics.CosmeticType;
import net.pitsim.pitsim.cosmetics.PitCosmetic;
import net.pitsim.pitsim.controllers.PlayerManager;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.misc.Sounds;
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
