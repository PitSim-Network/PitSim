package dev.kyro.pitsim.aitems.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
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
import org.bukkit.event.player.PlayerItemConsumeEvent;
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
				getSeconds() + " second" + Misc.s(getSeconds()) + ". Shifting while eating consumes the whole stack")
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
	public void onEat(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		ItemStack itemStack = player.getItemInHand().clone();
		player.setFoodLevel(19);

		if(!isThisItem(itemStack) || !player.isSneaking()) return;

		event.setCancelled(true);
		int amount = itemStack.getAmount();
		player.setItemInHand(new ItemStack(Material.AIR));
		player.updateInventory();
		AOutput.send(player, "&6&lYUM!&7 Consumed " + amount + "x " + getName());
		consumeBread(player, amount);
	}

	@EventHandler
	public void onEat(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack itemStack = player.getItemInHand().clone();
		player.setFoodLevel(19);

		if(!isThisItem(itemStack) || player.isSneaking()) return;

		if(itemStack.getAmount() <= 1) {
			player.setItemInHand(new ItemStack(Material.AIR));
		} else {
			itemStack.setAmount(itemStack.getAmount() - 1);
			player.setItemInHand(itemStack);
		}
		player.updateInventory();
		consumeBread(player, 1);
	}

	public static void consumeBread(Player player, int amount) {
		breadStacks.put(player, breadStacks.getOrDefault(player, 0) + amount);
		new BukkitRunnable() {
			@Override
			public void run() {
				breadStacks.put(player, breadStacks.get(player) - amount);
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * getSeconds());
		for(int i = 0; i < Math.pow(amount, 1.0 / 2.0); i++) Sounds.YUMMY_BREAD.play(player);
	}

	public static int getDamageIncrease() {
		return 8;
	}

	public static int getSeconds() {
		return 30;
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
