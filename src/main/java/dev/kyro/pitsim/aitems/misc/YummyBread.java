package dev.kyro.pitsim.aitems.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.enums.MarketCategory;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class YummyBread extends StaticPitItem implements TemporaryItem {
	public static Map<Player, Integer> breadStacks = new HashMap<>();

	public YummyBread() {
		marketCategory = MarketCategory.MISC;
	}

	@Override
	public String getNBTID() {
		return "yummy-bread";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("yummybread"));
	}

	@Override
	public Material getMaterial() {
		return Material.BREAD;
	}

	@Override
	public String getName() {
		return "&6Yummy Bread";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Deal &c+" + getDamageIncrease() + "% &7damage to bots",
				"&7for " + getSeconds() + " seconds. (Stacking)",
				"",
				"&cLost on death"
		).getLore();
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(breadStacks.containsKey(attackEvent.getAttackerPlayer())) {
			if(NonManager.getNon(attackEvent.getDefender()) == null) return;
			attackEvent.increasePercent += getDamageIncrease() * breadStacks.get(attackEvent.getAttackerPlayer());
		}
	}

	@EventHandler
	public void onEat(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack itemStack = event.getItem();
		player.setFoodLevel(19);

		if(!isThisItem(itemStack)) return;

		if(breadStacks.containsKey(player)) {
			breadStacks.put(player, breadStacks.get(player) + 1);
		} else breadStacks.put(player, 1);
		Sounds.YUMMY_BREAD.play(player);
		if(event.getItem().getAmount() == 1) {
			player.getInventory().remove(event.getItem());
		} else event.getItem().setAmount(event.getItem().getAmount() - 1);

		new BukkitRunnable() {
			@Override
			public void run() {
				if(breadStacks.containsKey(player)) {
					if(breadStacks.get(player) - 1 <= 0) breadStacks.remove(player);
					else breadStacks.put(player, breadStacks.get(player) - 1);
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * getSeconds());
		player.updateInventory();
	}

	public int getDamageIncrease() {
		return 10;
	}

	public int getSeconds() {
		return 20;
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return nbtItem.hasKey(NBTTag.IS_YUMMY_BREAD.getRef());
	}

	@Override
	public TemporaryType getTemporaryType() {
		return TemporaryType.LOST_ON_DEATH;
	}
}
