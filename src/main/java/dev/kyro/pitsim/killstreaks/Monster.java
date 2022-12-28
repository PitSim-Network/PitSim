package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Monster extends Killstreak {

	public static Monster INSTANCE;

	public Monster() {
		super("Monster", "Monster", 40, 0);
		INSTANCE = this;
	}

	public static Map<Player, Integer> healthMap = new HashMap<>();

	@Override
	public void proc(Player player) {
		if(healthMap.containsKey(player) && healthMap.get(player) >= 4) return;
		if(healthMap.containsKey(player)) healthMap.put(player, healthMap.get(player) + 1);
		else healthMap.put(player, 1);
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.updateMaxHealth();
	}

	@Override
	public void reset(Player player) {
		healthMap.remove(player);
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.updateMaxHealth();
	}

	@Override
	public ItemStack getDisplayItem(Player player) {

		AItemStackBuilder builder = new AItemStackBuilder(Material.APPLE);
		builder.setName("&e" + name);
		builder.setLore(new ALoreBuilder("&7Every: &c" + killInterval + " kills", "", "&7Gain &c0.5\u2764 &7max health (&c2\u2764 &7max)"));

		return builder.getItemStack();
	}
}
