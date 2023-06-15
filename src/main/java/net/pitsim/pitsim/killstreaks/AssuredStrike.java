package net.pitsim.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.controllers.objects.Killstreak;
import net.pitsim.pitsim.events.AttackEvent;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class AssuredStrike extends Killstreak {
	public static AssuredStrike INSTANCE;
	public static List<LivingEntity> rewardPlayers = new ArrayList<>();

	public AssuredStrike() {
		super("Assured Strike", "AssuredStrike", 3, 10);
		INSTANCE = this;
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		if(!rewardPlayers.contains(attackEvent.getAttacker())) return;
		rewardPlayers.remove(attackEvent.getAttacker());

		attackEvent.increasePercent += 35;
		Misc.applyPotionEffect(attackEvent.getAttackerPlayer(), PotionEffectType.SPEED, 20 * 8, 0, true, false);
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
		AItemStackBuilder builder = new AItemStackBuilder(Material.DIAMOND_SWORD)
				.setName("&e" + displayName)
				.setLore(new ALoreBuilder(
						"&7Every: &c" + killInterval + " kills",
						"",
						"&7Next melee hit deals &c+35%",
						"&cdamage &7and grants &eSpeed I", "&7for 8 seconds."
				));

		return builder.getItemStack();
	}

	@Override
	public String getSummary() {
		return "&eAssured Strike&7 is a killstreak that increases &cdamage&7 on your next hit and grants &eSpeed I&7 " +
				"for a period of time every &c3 kills";
	}
}
