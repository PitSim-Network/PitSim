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
		if(pitPlayer.megastreak instanceof RNGesus && pitPlayer.getKills() >= RNGesus.INSTABILITY_THRESHOLD) {
			AOutput.error(player, "&c&lUNSTABLE!&7 Baker cannot be used in this reality");
			return;
		}

		double random = Math.random();
		if(random > 0.9) ItemFactory.getItem(VeryYummyBread.class).giveItem(player, 1);
		else ItemFactory.getItem(YummyBread.class).giveItem(player, 1);
		player.updateInventory();
		Sounds.BREAD_GIVE.play(player);
	}

	@Override
	public void reset(Player player) {
	}

	@Override
	public ItemStack getDisplayStack(Player player) {
		AItemStackBuilder builder = new AItemStackBuilder(Material.BREAD)
				.setName("&e" + displayName)
				.setLore(new ALoreBuilder(
						"&7Every: &c" + killInterval + " kills",
						"",
						"&7Obtain either a &6Yummy bread &7or",
						"&6Very yummy bread&7. (Lost on death)"
				));

		return builder.getItemStack();
	}

	@Override
	public String getSummary() {
		return "&eBaker&7 is a killstreak that gives you one of two types of bread every &c15 kills&7: &eyummy bread, " +
				"which increases your &cdamage &7against bots (and stacks), and &6very yummy bread&7, which &cheals&7 " +
				"you and gives &9absorption&7";
	}
}
