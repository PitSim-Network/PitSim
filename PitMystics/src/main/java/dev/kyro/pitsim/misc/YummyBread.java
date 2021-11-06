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
		if(breadStacks.containsKey(event.attacker)) {
			if(NonManager.getNon(event.defender) == null) return;
			event.increasePercent += ((10 * breadStacks.get(event.attacker)) /  100D);
		}
	}

	@EventHandler
	public void onEat(PlayerItemConsumeEvent event) {
		event.getPlayer().setFoodLevel(19);

		if(Misc.isAirOrNull(event.getItem())) return;
		NBTItem nbtItem = new NBTItem(event.getItem());
		if(nbtItem.hasKey(NBTTag.IS_VERY_YUMMY_BREAD.getRef())) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
			pitPlayer.heal(8);
			pitPlayer.heal(4, HealEvent.HealType.ABSORPTION, 16);
		}
	}

	@EventHandler
	public void onEat(PlayerInteractEvent event) {
		if(Misc.isAirOrNull(event.getItem())) return;
		NBTItem nbtItem = new NBTItem(event.getItem());
		if(nbtItem.hasKey(NBTTag.IS_YUMMY_BREAD.getRef())) {
			if(breadStacks.containsKey(event.getPlayer())) {
				breadStacks.put(event.getPlayer(), breadStacks.get(event.getPlayer()) + 1);
			} else breadStacks.put(event.getPlayer(), 1);
			Sounds.YUMMY_BREAD.play(event.getPlayer());
			if(event.getItem().getAmount() == 1) {
				event.getPlayer().getInventory().remove(event.getItem());
			} else event.getItem().setAmount(event.getItem().getAmount() - 1);

			new BukkitRunnable() {
				@Override
				public void run() {
					if(breadStacks.containsKey(event.getPlayer())) {
						if(breadStacks.get(event.getPlayer()) - 1 <= 0) breadStacks.remove(event.getPlayer());
						else breadStacks.put(event.getPlayer(), breadStacks.get(event.getPlayer()) - 1);
					}
				}
			}.runTaskLater(PitSim.INSTANCE, 20 * 30L);
		}

		event.getPlayer().setFoodLevel(19);
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
		ALoreBuilder veryLoreBuilder = new ALoreBuilder("&7Heals &c4\u2764", "&7Grants &62\u2764");
		veryBuilder.setLore(veryLoreBuilder);

		NBTItem nbtItem = new NBTItem(veryBuilder.getItemStack());
		nbtItem.setBoolean(NBTTag.IS_VERY_YUMMY_BREAD.getRef(), true);

		AUtil.giveItemSafely(player, nbtItem.getItem(), true);
	}

	public static void giveYummyBread(Player player, int amount) {
		AItemStackBuilder veryBuilder = new AItemStackBuilder(Material.BREAD, amount);
		veryBuilder.setName("&6Yummy bread");
		ALoreBuilder veryLoreBuilder = new ALoreBuilder("&7Deal &c+10% &7damage to bots", "&7for 30 seconds. (Stacking)");
		veryBuilder.setLore(veryLoreBuilder);

		NBTItem nbtItem = new NBTItem(veryBuilder.getItemStack());
		nbtItem.setBoolean(NBTTag.IS_YUMMY_BREAD.getRef(), true);

		AUtil.giveItemSafely(player, nbtItem.getItem(), true);
	}

}
