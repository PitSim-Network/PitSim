package net.pitsim.pitsim.controllers;

import net.pitsim.pitsim.commands.ShowCommand;
import net.pitsim.pitsim.controllers.objects.PluginMessage;
import net.pitsim.pitsim.events.MessageEvent;
import net.pitsim.pitsim.megastreaks.Uberstreak;
import net.pitsim.pitsim.misc.CustomSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CrossServerMessageManager implements Listener {

	@EventHandler
	public void onMessage(MessageEvent event) {
		PluginMessage message = event.getMessage();
		List<String> strings = message.getStrings();
		List<Integer> integers = message.getIntegers();
		List<Long> longs = message.getLongs();
		List<Boolean> booleans = message.getBooleans();
		if(strings.isEmpty()) return;

		if(strings.get(0).equals("ITEMSHOW")) {
			String displayName = strings.get(1);
			ItemStack itemStack = CustomSerializer.deserializeDirectly(strings.get(2));
			ShowCommand.sendShowMessage(displayName, itemStack);
		} else if(strings.get(0).equals("FINDJEWEL")) {
			String displayName = strings.get(1);
			ItemStack itemStack = CustomSerializer.deserializeDirectly(strings.get(2));
			EnchantManager.sendJewelFindMessage(displayName, itemStack);
		} else if(strings.get(0).equals("PRESTIGE")) {
			String displayName = strings.get(1);
			int prestige = integers.get(0);
			LevelManager.onPrestige(displayName, prestige);
		} else if(strings.get(0).equals("UBERDROP")) {
			String displayName = strings.get(1);
			ItemStack itemStack = CustomSerializer.deserializeDirectly(strings.get(2));
			Uberstreak.sendUberMessage(displayName, itemStack);
		} else if(strings.get(0).equals("TAINTEDENCHANT")) {
			String displayName = strings.get(1);
			ItemStack itemStack = CustomSerializer.deserializeDirectly(strings.get(2));
			TaintedEnchanting.sendTaintedEnchantMessage(displayName, itemStack);
		}
	}
}
