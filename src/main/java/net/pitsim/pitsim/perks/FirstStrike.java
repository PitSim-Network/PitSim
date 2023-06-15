package net.pitsim.pitsim.perks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.controllers.objects.PitPerk;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.PitLoreBuilder;
import net.pitsim.pitsim.upgrades.UnlockFirstStrike;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstStrike extends PitPerk {
	public static FirstStrike INSTANCE;
	public static Map<Player, List<LivingEntity>> hitPlayers = new HashMap<>();

	public FirstStrike() {
		super("First Strike", "firststrike");
		renownUpgradeClass = UnlockFirstStrike.class;
		INSTANCE = this;
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		if(!hasPerk(attackEvent.getAttacker())) return;

		hitPlayers.putIfAbsent(attackEvent.getAttackerPlayer(), new ArrayList<>());
		List<LivingEntity> hitList = hitPlayers.get(attackEvent.getAttackerPlayer());

		if(hitList.contains(attackEvent.getDefender())) return;
		attackEvent.increasePercent += 30;

		hitPlayers.get(attackEvent.getAttackerPlayer()).add(attackEvent.getDefender());
		new BukkitRunnable() {
			@Override
			public void run() {
				hitPlayers.get(attackEvent.getAttackerPlayer()).remove(attackEvent.getDefender());
			}
		}.runTaskLater(PitSim.INSTANCE, 120L);
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.COOKED_CHICKEN)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, Player player) {
		loreBuilder.addLongLine(
				"&7First hit on a player or bot deals &c+30% damage"
		);
	}

	@Override
	public String getSummary() {
		return "&eFirst Strike &7is a perk unlocked in the &erenown shop&7 that increases your &cdamage&7 and " +
				"gives you &eSpeed&7 on your first hit against bots and players";
	}
}
