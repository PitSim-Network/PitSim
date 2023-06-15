package net.pitsim.spigot.inventories;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericConfirmationPanel extends AGUIPanel {
	private final ChatColor chatColor;
	private final Consumer<GenericConfirmationPanel> confirm;
	private final Consumer<GenericConfirmationPanel> cancel;

	private boolean hasInvokedAction;

	public GenericConfirmationPanel(AGUI gui, ChatColor chatColor, ALoreBuilder confirmLore, ALoreBuilder cancelLore,
									Consumer<GenericConfirmationPanel> confirm, Consumer<GenericConfirmationPanel> cancel) {
		super(gui, true);
		this.chatColor = chatColor;
		this.confirm = confirm;
		this.cancel = cancel;
		buildInventory();

		Supplier<ItemStack> confirmStack = () -> new AItemStackBuilder(Material.STAINED_CLAY, 1, 13)
				.setName("&aConfirm")
				.setLore(confirmLore)
				.getItemStack();
		addTaggedItem(11, confirmStack, event -> {
			if(hasInvokedAction) return;
			hasInvokedAction = true;
			confirm.accept(this);
		}).setItem();

		Supplier<ItemStack> cancelStack = () -> new AItemStackBuilder(Material.STAINED_CLAY, 1, 14)
				.setName("&cCancel")
				.setLore(cancelLore)
				.getItemStack();
		addTaggedItem(15, cancelStack, event -> {
			if(hasInvokedAction) return;
			hasInvokedAction = true;
			cancel.accept(this);
		}).setItem();
	}

	@Override
	public String getName() {
		return chatColor + "Are you sure?";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {}

	@Override
	public void onOpen(InventoryOpenEvent event) {}

	@Override
	public void onClose(InventoryCloseEvent event) {
		if(hasInvokedAction) return;
		hasInvokedAction = true;
		cancel.accept(this);
	}
}
