package dev.kyro.pitsim.enchants;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.enums.KillModifier;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.megastreaks.NoMegastreak;
import dev.kyro.pitsim.megastreaks.Uberstreak;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SelfCheckout extends PitEnchant {

	public static SelfCheckout INSTANCE;

	public SelfCheckout() {
		super("Self-Checkout", true, ApplyType.PANTS,
				"selfcheckout", "self-checkout", "sco", "selfcheck", "checkout", "soco");
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.killerIsPlayer || killEvent.killer == killEvent.dead) return;

		ItemStack leggings = killEvent.killer.getEquipment().getLeggings();
		int enchantLvl = EnchantManager.getEnchantLevel(leggings, this);
		if(enchantLvl == 0) return;

		PitPlayer pitKiller = PitPlayer.getPitPlayer(killEvent.killerPlayer);
		if(pitKiller.getKills() + 1 < 200 || pitKiller.megastreak.getClass() == Uberstreak.class || pitKiller.megastreak.getClass() == NoMegastreak.class)
			return;

		NBTItem nbtItem = new NBTItem(leggings);
		if(!EnchantManager.isJewelComplete(leggings) || !nbtItem.getString(NBTTag.ITEM_JEWEL_ENCHANT.getRef()).equalsIgnoreCase(refNames.get(0))) {
			AOutput.error(killEvent.killer, "Self-Checkout only works on jewel items");
			return;
		}

		String scoMessage = "&9&lSCO!&7 Self-Checkout pants activated";
		int renown = Math.min((pitKiller.getKills() + 1) / 300, 4);
		if(renown != 0) {
			pitKiller.renown += renown;
			scoMessage += " giving &e" + renown + " &7 renown";
		}

		AOutput.send(killEvent.killerPlayer, scoMessage);
		DamageManager.death(killEvent.killer, KillModifier.SELF_CHECKOUT);

		if(nbtItem.hasKey(NBTTag.CURRENT_LIVES.getRef())) {
			int lives = nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef());
			if(lives - 3 <= 0) {
				killEvent.killerPlayer.getEquipment().setLeggings(new ItemStack(Material.AIR));
				killEvent.killerPlayer.updateInventory();

				if(pitKiller.stats != null) pitKiller.stats.itemsBroken++;
			} else {
				nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) - 3);
				EnchantManager.setItemLore(nbtItem.getItem(), pitKiller.player);
				killEvent.killerPlayer.getEquipment().setLeggings(nbtItem.getItem());
				killEvent.killerPlayer.updateInventory();

				if(pitKiller.stats != null) pitKiller.stats.livesLost += 3;
			}
		}
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7On kill, if you have a killstreak", "&7of at least 200, &eExplode:",
				"&e\u25a0 &7Die! Keep jewel lives on death",
				"&a\u25a0 &7Gain &e+1 renown &7for every 300 killstreak (max 4)",
				"&c\u25a0 &7Lose &c3 lives &7on this item").getLore();
	}
}
