package net.pitsim.pitsim.adarkzone.abilities;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.adarkzone.FastTravelDestination;
import net.pitsim.pitsim.adarkzone.FastTravelManager;
import net.pitsim.pitsim.adarkzone.progression.ProgressionManager;
import net.pitsim.pitsim.adarkzone.progression.SkillBranch;
import net.pitsim.pitsim.adarkzone.progression.skillbranches.SoulBranch;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.enums.NBTTag;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.Sounds;
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
		if(destination.subLevel != null && !pitPlayer.hasFastTravelUnlocked(destination.subLevel) && !PitSim.isDev()) {
			AOutput.error(event.getWhoClicked(), "&cYou must kill the boss to unlock this location!");
			Sounds.NO.play(player);
			return;
		}

		boolean hasNoCost = ProgressionManager.isUnlocked(pitPlayer, SoulBranch.INSTANCE, SkillBranch.MajorUnlockPosition.FIRST_PATH);
		if(!hasNoCost) {
			if(pitPlayer.taintedSouls < destination.cost) {
				AOutput.error(event.getWhoClicked(), "&cYou do not have enough souls to travel to this location!");
				Sounds.NO.play(player);
				return;
			}
			pitPlayer.taintedSouls -= destination.cost;
		}

		pitPlayer.stats.timesFastTraveled++;
		destination.travel(player);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {}

	@Override
	public void onClose(InventoryCloseEvent event) {}
}
