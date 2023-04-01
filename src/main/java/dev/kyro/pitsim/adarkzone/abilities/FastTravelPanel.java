package dev.kyro.pitsim.adarkzone.abilities;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.adarkzone.FastTravelDestination;
import dev.kyro.pitsim.adarkzone.FastTravelManager;
import dev.kyro.pitsim.controllers.CombatManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class FastTravelPanel extends AGUIPanel {
	public FastTravelPanel(AGUI gui) {
		super(gui);
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7);

		int slot = 9;
		for(FastTravelDestination destination : FastTravelManager.destinations) {
			while(slot % 9 == 0 || slot % 9 == 8) slot++;

			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			boolean isUnlocked = destination.subLevel == null || pitPlayer.hasFastTravelUnlocked(destination.subLevel);
			boolean canAfford = pitPlayer.taintedSouls >= destination.cost;
			String clickMessage = isUnlocked ? (canAfford ? "&eClick to Travel!" : "&cNot enough souls!") : "&cKill Boss to unlock!";

			AItemStackBuilder builder = new AItemStackBuilder(destination.icon.getItemType(), 1, destination.icon.getData()
					).setName(destination.displayName).setLore(new ALoreBuilder(
					"&7Cost: &f" + destination.cost + (destination.cost > 1 ? " Souls" : " Soul"),
					"",
					clickMessage
			));

			NBTItem nbtItem = new NBTItem(builder.getItemStack(), true);
			nbtItem.setInteger(NBTTag.INVENTORY_INDEX.getRef(), FastTravelManager.destinations.indexOf(destination));
			getInventory().setItem(slot, builder.getItemStack());

			slot++;
		}
	}

	@Override
	public String getName() {
		return "Fast Travel";
	}

	@Override
	public int getRows() {
		return 4;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;

		if(Misc.isAirOrNull(event.getCurrentItem())) return;
		NBTItem nbtItem = new NBTItem(event.getCurrentItem(), true);
		if(!nbtItem.hasKey(NBTTag.INVENTORY_INDEX.getRef())) return;
		int index = nbtItem.getInteger(NBTTag.INVENTORY_INDEX.getRef());
		FastTravelDestination destination = FastTravelManager.destinations.get(index);

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(destination.subLevel != null && !pitPlayer.hasFastTravelUnlocked(destination.subLevel)) {
			AOutput.error(event.getWhoClicked(), "&cYou must kill the Boss to unlock this location!");
			Sounds.NO.play(player);
			return;
		}

		if(pitPlayer.taintedSouls < destination.cost) {
			AOutput.error(event.getWhoClicked(), "&cYou do not have enough souls to travel to this location!");
			Sounds.NO.play(player);
			return;
		}

		pitPlayer.stats.timesFastTraveled++;
		pitPlayer.taintedSouls -= destination.cost;
		destination.travel(player);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		if(CombatManager.isInCombat(player)) {
			AOutput.error(event.getPlayer(), "&cYou cannot use this while in combat!");
			Sounds.NO.play(player);
			event.setCancelled(true);
		}
	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
