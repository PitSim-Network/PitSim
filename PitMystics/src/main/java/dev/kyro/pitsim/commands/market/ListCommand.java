//package dev.kyro.pitsim.commands.market;
//
//import dev.kyro.arcticapi.commands.ASubCommand;
//import dev.kyro.arcticapi.misc.AOutput;
//import dev.kyro.pitsim.controllers.market.AuctionItem;
//import dev.kyro.pitsim.controllers.market.Currency;
//import dev.kyro.pitsim.controllers.market.MarketManager;
//import dev.kyro.pitsim.controllers.market.MarketOrder;
//import dev.kyro.pitsim.misc.Misc;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ListCommand extends ASubCommand {
//
//	public ListCommand(String executor) {
//		super(executor);
//	}
//
//	@Override
//	public void execute(CommandSender sender, List<String> args) {
//		Player player = (Player) sender;
//		if(Misc.isAirOrNull(player.getItemInHand())) {
//
//			AOutput.error(player, "You need to be holding an item to use this command");
//			return;
//		}
//
//		List<Currency> currencyList = new ArrayList<>();
//		currencyList.add(Currency.FEATHER);
//
//		MarketOrder BIN = new MarketOrder(0, 0, 5, 0);
//
//		AuctionItem auctionItem = new AuctionItem(player, player.getItemInHand(), 7 * 24 * 60 * 60 * 1000, currencyList, BIN);
//		MarketManager.addItem(player, auctionItem);
//		AOutput.send(player, "Successfully added item to the market");
//	}
//}
