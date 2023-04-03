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
	public static List<LivingEntity> rewardPlayers = new ArrayList<>();

	public Leech() {
		super("Leech", "Leech", 3, 18);
		INSTANCE = this;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onHit(AttackEvent.Apply attackEvent) {
		if(!rewardPlayers.contains(attackEvent.getAttacker())) return;
		rewardPlayers.remove(attackEvent.getAttacker());

		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		pitPlayer.heal(attackEvent.getFinalPitDamageIncrease() * (getPercent() / 100D));
	}

	@Override
	public void proc(Player player) {
		if(!rewardPlayers.contains(player)) rewardPlayers.add(player);
	}

	@Override
	public void reset(Player player) {
		rewardPlayers.remove(player);
	}

	@Override
	public ItemStack getDisplayStack(Player player) {
		AItemStackBuilder builder = new AItemStackBuilder(Material.FERMENTED_SPIDER_EYE)
				.setName("&e" + displayName)
				.setLore(new ALoreBuilder(
						"&7Every: &c" + killInterval + " kills",
						"",
						"&7Next melee hit heals for",
						"&c" + getPercent() + "% &7of its damage."
				));

		return builder.getItemStack();
	}

	@Override
	public String getSummary() {
		return "&eLeech&7 is a killstreak that gives you &chealing&7 on your next hit every &c3 kills";
	}

	public int getPercent() {
		return 20;
	}
}
