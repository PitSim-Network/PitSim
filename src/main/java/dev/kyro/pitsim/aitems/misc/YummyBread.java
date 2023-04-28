package dev.kyro.pitsim.aitems.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.enums.MarketCategory;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
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
		return new PitLoreBuilder("&7Deal &c+" + getDamageIncrease() + "% &7damage (stacking) to bots for " +
				getSeconds() + " second" + Misc.s(getSeconds()))
				.addLongLine("&cLost on death")
				.getLore();
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
		ItemStack itemStack = player.getItemInHand();
		player.setFoodLevel(19);

		if(!isThisItem(itemStack)) return;
		int amount = player.isSneaking() ? itemStack.getAmount() : 1;

		breadStacks.put(player, breadStacks.getOrDefault(player, 0) + amount);
		if(itemStack.getAmount() <= amount) {
			player.setItemInHand(new ItemStack(Material.AIR));
		} else {
			itemStack.setAmount(itemStack.getAmount() - amount);
		}
		player.updateInventory();
		Sounds.YUMMY_BREAD.play(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				breadStacks.put(player, breadStacks.get(player) - amount);
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * getSeconds());
	}

	public int getDamageIncrease() {
		return 8;
	}

	public int getSeconds() {
		return 25;
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
