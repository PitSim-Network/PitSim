package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.YummyBread;
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
		double random = Math.random();
		if(random > 0.8) YummyBread.giveVeryYummyBread(player, 1);
		else YummyBread.giveYummyBread(player, 1);
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
