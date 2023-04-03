package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class GoldStack extends Killstreak {
	public static GoldStack INSTANCE;

	public GoldStack() {
		super("Gold Stack", "GoldStack", 40, 16);
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer()) return;
		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
		killEvent.goldReward += pitPlayer.goldStack;
	}

	@Override
	public void proc(Player player) {
		if(getCurrent(player) >= getMax(player)) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.goldStack += 0.1;
	}

	@Override
	public void reset(Player player) {

	}

	public static double getMax(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return (pitPlayer.prestige * 0.1) + 1;
	}

	public static double getCurrent(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		return pitPlayer.goldStack;
	}

	@Override
	public ItemStack getDisplayStack(Player player) {
		DecimalFormat formatter = new DecimalFormat("#.##");
		AItemStackBuilder builder = new AItemStackBuilder(Material.GOLD_ORE)
				.setName("&e" + displayName)
				.setLore(new ALoreBuilder(
						"&7Every: &c" + killInterval + " kills",
						"",
						"&7Permanently gain &6+0.1g" + " &7per kill.",
						"&7Maximum: &6+" + formatter.format(getMax(player)) + "g",
						"",
						"&7You have: &6+" + formatter.format(getCurrent(player)) + "g",
						"",
						"&8Bonus applies when not selected.",
						"&8Resets on prestige."
				));

		return builder.getItemStack();
	}

	@Override
	public String getSummary() {
		return "&6Gold Stack&7 is a killstreak that increases the &6gold&7 you earn on kill by a very small amount " +
				"for the rest of your prestige (every &c40 kills&7)";
	}
}
