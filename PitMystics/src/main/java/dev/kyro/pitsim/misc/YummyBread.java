package dev.kyro.pitsim.misc;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class YummyBread implements Listener {

	Map<Player, Integer> breadStacks = new HashMap<>();

	@EventHandler
	public void onHit(AttackEvent.Apply event) {
		if(breadStacks.containsKey(event.getAttacker())) {
			if(NonManager.getNon(event.getDefender()) == null) return;
			event.increasePercent += ((10 * breadStacks.get(event.getAttacker())) / 100D);
		}
	}

	@EventHandler
	public void onEat(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		event.getPlayer().setFoodLevel(19);

		if(Misc.isAirOrNull(event.getItem())) return;
		NBTItem nbtItem = new NBTItem(event.getItem());
		if(nbtItem.hasKey(NBTTag.IS_VERY_YUMMY_BREAD.getRef())) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			pitPlayer.heal(12);
			pitPlayer.heal(4, HealEvent.HealType.ABSORPTION, 20);
		}
	}

	@EventHandler
	public void onEat(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(Misc.isAirOrNull(event.getItem())) return;
		NBTItem nbtItem = new NBTItem(event.getItem());
		player.setFoodLevel(19);
		if(!nbtItem.hasKey(NBTTag.IS_YUMMY_BREAD.getRef())) return;

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
		}.runTaskLater(PitSim.INSTANCE, 20 * 20L);
		player.updateInventory();
	}

	public static void deleteBread(Player player) {
		if(NonManager.getNon(player) != null) return;
		for(int i = 0; i < player.getInventory().getSize(); i++) {
			if(Misc.isAirOrNull(player.getInventory().getItem(i))) continue;
			NBTItem nbtItem = new NBTItem(player.getInventory().getItem(i));
			if(nbtItem.hasKey(NBTTag.IS_YUMMY_BREAD.getRef()) || nbtItem.hasKey(NBTTag.IS_VERY_YUMMY_BREAD.getRef())) {

				player.getInventory().remove(player.getInventory().getItem(i));
			}
		}
	}

	public static void giveVeryYummyBread(Player player, int amount) {
		AItemStackBuilder veryBuilder = new AItemStackBuilder(Material.BREAD, amount);
		veryBuilder.setName("&6Very yummy bread");
		ALoreBuilder veryLoreBuilder = new ALoreBuilder("&7Heals &c" + Misc.getHearts(12), "&7Grants &6" + Misc.getHearts(4));
		veryBuilder.setLore(veryLoreBuilder);

		NBTItem nbtItem = new NBTItem(veryBuilder.getItemStack());
		nbtItem.setBoolean(NBTTag.IS_VERY_YUMMY_BREAD.getRef(), true);

		AUtil.giveItemSafely(player, nbtItem.getItem(), true);
		player.updateInventory();
	}

	public static void giveYummyBread(Player player, int amount) {
		AItemStackBuilder veryBuilder = new AItemStackBuilder(Material.BREAD, amount);
		veryBuilder.setName("&6Yummy bread");
		ALoreBuilder veryLoreBuilder = new ALoreBuilder("&7Deal &c+10% &7damage to bots", "&7for 20 seconds. (Stacking)");
		veryBuilder.setLore(veryLoreBuilder);

		NBTItem nbtItem = new NBTItem(veryBuilder.getItemStack());
		nbtItem.setBoolean(NBTTag.IS_YUMMY_BREAD.getRef(), true);

		AUtil.giveItemSafely(player, nbtItem.getItem(), true);
		player.updateInventory();
	}
}
