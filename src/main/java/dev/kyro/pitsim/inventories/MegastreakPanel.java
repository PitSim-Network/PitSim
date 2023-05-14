package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.APagedGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.ChatTriggerManager;
import dev.kyro.pitsim.controllers.CombatManager;
import dev.kyro.pitsim.controllers.PerkManager;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.DisplayItemType;
import dev.kyro.pitsim.megastreaks.RNGesus;
import dev.kyro.pitsim.misc.Formatter;
import dev.kyro.pitsim.misc.Sounds;
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
				AOutput.error(player, "&c&lERROR!&7 You are not high enough prestige to equip this");
				Sounds.ERROR.play(player);
				return;
			}

			if(pitPlayer.level < megastreak.getLevelReq(player)) {
				AOutput.error(player, "&c&lERROR!&7 You are not high enough level to equip this");
				Sounds.ERROR.play(player);
				return;
			}

			if(CombatManager.isInCombat(player) && !player.isOp()) {
				AOutput.error(player, "&c&lERROR!&7 You cannot do this while in combat");
				Sounds.ERROR.play(player);
				return;
			}

			if(megastreak instanceof RNGesus && RNGesus.isOnCooldown(pitPlayer)) {
				if(pitPlayer.renown >= RNGesus.RENOWN_COST) {
					pitPlayer.renown = pitPlayer.renown - RNGesus.RENOWN_COST;
					pitPlayer.rngCooldown = 0;
					pitPlayer.getMegastreak().reset(player);
					pitPlayer.setMegastreak(megastreak);
					openPanel(perkGUI.getHomePanel());

					AOutput.send(player, "&aEquipped &6RNGsus &afor " + Formatter.formatRenown(RNGesus.RENOWN_COST) + "!");
					Sounds.SUCCESS.play(player);
					ChatTriggerManager.sendPerksInfo(pitPlayer);
				} else {
					AOutput.error(player, "&cYou do not have enough renown!");
					Sounds.NO.play(player);
				}
				return;
			}

			if(PerkManager.isEquipped(player, megastreak)) {
				AOutput.error(perkGUI.player, "&c&lERROR!&7 This megastreak is already equipped");
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
