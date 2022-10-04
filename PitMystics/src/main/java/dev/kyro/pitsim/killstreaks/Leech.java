package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Leech extends Killstreak {

	public static Leech INSTANCE;

	public Leech() {
		super("Leech", "Leech", 3, 18);
		INSTANCE = this;
	}

	List<LivingEntity> rewardPlayers = new ArrayList<>();

	@EventHandler(priority = EventPriority.HIGH)
	public void onHit(AttackEvent.Apply attackEvent) {
		if(rewardPlayers.contains(attackEvent.attacker)) {
			PitPlayer pitPlayer = attackEvent.attackerPitPlayer;
			pitPlayer.heal(attackEvent.getFinalDamageIncrease() * (getPercent() / 100D));
			rewardPlayers.remove(attackEvent.attacker);
		}
	}

	@Override
	public void proc(Player player) {
		rewardPlayers.add(player);
	}

	@Override
	public void reset(Player player) {
		rewardPlayers.remove(player);
	}

	@Override
	public ItemStack getDisplayItem(Player player) {

		AItemStackBuilder builder = new AItemStackBuilder(Material.FERMENTED_SPIDER_EYE);
		builder.setName("&e" + name);
		builder.setLore(new ALoreBuilder("&7Every: &c" + killInterval + " kills", "", "&7Next melee hit heals for", "&c" + getPercent() + "% &7of its damage."));

		return builder.getItemStack();
	}

	public int getPercent() {
		return 20;
	}
}
