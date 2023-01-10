package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.aitems.misc.VeryYummyBread;
import dev.kyro.pitsim.aitems.misc.YummyBread;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.megastreaks.RNGesus;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Baker extends Killstreak {

	public static Baker INSTANCE;

	public Baker() {
		super("Baker", "Baker", 15, 22);
		INSTANCE = this;
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.megastreak.getClass() == RNGesus.class && pitPlayer.getKills() >= RNGesus.INSTABILITY_THRESHOLD) {
			AOutput.error(player, "&c&lUNSTABLE!&7 Baker cannot be used in this reality");
			return;
		}

		double random = Math.random();
		if(random > 0.9) ItemFactory.getItem(VeryYummyBread.class).giveItem(player, 1);
		else ItemFactory.getItem(YummyBread.class).giveItem(player, 1);
		Sounds.BREAD_GIVE.play(player);
	}

	@Override
	public void reset(Player player) {
	}

	@Override
	public ItemStack getDisplayItem(Player player) {

		AItemStackBuilder builder = new AItemStackBuilder(Material.BREAD);
		builder.setName("&e" + name);
		builder.setLore(new ALoreBuilder("&7Every: &c" + killInterval + " kills",
				"", "&7Obtain either a &6Yummy bread &7or", "&6Very yummy bread&7. (Lost on death)"));

		return builder.getItemStack();
	}
}
