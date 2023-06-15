package net.pitsim.spigot.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.APagedGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.controllers.ChatTriggerManager;
import net.pitsim.spigot.controllers.CombatManager;
import net.pitsim.spigot.controllers.PerkManager;
import net.pitsim.spigot.controllers.objects.Megastreak;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enums.DisplayItemType;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MegastreakPanel extends APagedGUIPanel {
	public PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public PerkGUI perkGUI;

	public MegastreakPanel(AGUI gui) {
		super(gui);
		perkGUI = (PerkGUI) gui;
		addBackButton(getRows() * 9 - 5);
		buildInventory();
	}

	@Override
	public void addItems() {
		for(Megastreak megastreak : PerkManager.megastreaks) addItem(createSupplier(megastreak), createConsumer(megastreak));
	}

	public Supplier<ItemStack> createSupplier(Megastreak megastreak) {
		return () -> megastreak.getDisplayStack(player, DisplayItemType.SELECT_PANEL);
	}

	public Consumer<InventoryClickEvent> createConsumer(Megastreak megastreak) {
		return event -> {
			if(!PerkManager.isUnlocked(player, megastreak)) {
				AOutput.error(player, "&c&lERROR!&7 You are not high enough prestige to equip this!");
				Sounds.ERROR.play(player);
				return;
			}

			if(pitPlayer.level < megastreak.getLevelReq(player)) {
				AOutput.error(player, "&c&lERROR!&7 You are not high enough level to equip this!");
				Sounds.ERROR.play(player);
				return;
			}

			if(CombatManager.isInCombat(player) && !player.isOp()) {
				AOutput.error(player, "&c&lERROR!&7 You cannot do this while in combat!");
				Sounds.ERROR.play(player);
				return;
			}

			if(megastreak.hasDailyLimit) {
				PitPlayer.MegastreakLimit limit = pitPlayer.getMegastreakCooldown(megastreak);
				if(limit.isAtLimit(pitPlayer)) {
					AOutput.error(perkGUI.player, "&c&lERROR!&7 You have already done the max amount of this streak today!");
					Sounds.ERROR.play(player);
					return;
				}
			}

			if(PerkManager.isEquipped(player, megastreak)) {
				AOutput.error(perkGUI.player, "&c&lERROR!&7 This megastreak is already equipped!");
				Sounds.ERROR.play(player);
				return;
			}

			megastreak.reset(player);
			pitPlayer.setMegastreak(megastreak);
			Sounds.SUCCESS.play(player);
			ChatTriggerManager.sendPerksInfo(pitPlayer);
			openPreviousGUI();
		};
	}

	@Override
	public String getName() {
		return "&7Choose a &cMegastreak";
	}

	@Override
	public void setInventory() {
		super.setInventory();
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7, false);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		setInventory();
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}
}
