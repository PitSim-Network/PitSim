package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Spongesteve extends Killstreak {
	public static int AMOUNT = 50;
	public static Spongesteve INSTANCE;

	public Spongesteve() {
		super("Spongesteve", "Spongesteve", 40, 8);
		INSTANCE = this;
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.heal(AMOUNT, HealEvent.HealType.ABSORPTION, AMOUNT * 2);
	}

	@Override
	public void reset(Player player) {

	}

	@Override
	public ItemStack getDisplayItem(Player player) {

		AItemStackBuilder builder = new AItemStackBuilder(Material.SPONGE);
		builder.setName("&e" + name);
		builder.setLore(new ALoreBuilder("&7Every: &c" + killInterval + " kills", "", "&7Gain &6" + Misc.getHearts(AMOUNT) + " Absorption&7."));

		return builder.getItemStack();
	}
}
