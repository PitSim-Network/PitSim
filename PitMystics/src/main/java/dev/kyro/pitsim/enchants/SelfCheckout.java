package dev.kyro.pitsim.enchants;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.megastreaks.Uberstreak;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SelfCheckout extends PitEnchant {

	public SelfCheckout() {
		super("Self-Checkout", true, ApplyType.PANTS,
				"selfcheckout", "self-checkout", "sco", "selfcheck", "checkout", "soco");
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {

		ItemStack leggings = killEvent.killer.getEquipment().getLeggings();
		int enchantLvl = EnchantManager.getEnchantLevel(leggings, this);
		if(enchantLvl == 0) return;

		PitPlayer pitKiller = PitPlayer.getPitPlayer(killEvent.killer);
		if(pitKiller.getKills() + 1 < 100 || pitKiller.megastreak.getClass() == Uberstreak.class) return;

		if(!EnchantManager.isJewelComplete(leggings)) {
			AOutput.error(killEvent.killer, "Self-Checkout only works on jewel items");
			return;
		}

		int renown = (int) ((pitKiller.getKills() + 1) / 100);
		if(renown != 0) {
			pitKiller.renown += renown;
			FileConfiguration playerData = APlayerData.getPlayerData(killEvent.killer);
			playerData.set("renown", pitKiller.renown);
			APlayerData.savePlayerData(killEvent.killer);
			AOutput.send(killEvent.killer, "&7You have been given &e" + renown + " renown");
		}

		DamageManager.death(killEvent.killer);

		NBTItem nbtItem = new NBTItem(leggings);
		if(nbtItem.hasKey(NBTTag.CURRENT_LIVES.getRef())) {
			int lives = nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef());
			if(lives - 2 <= 0) {
				killEvent.killer.getEquipment().setLeggings(new ItemStack(Material.AIR));

				if(pitKiller.stats != null) pitKiller.stats.itemsBroken++;
			} else {
				nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) - 2);
				EnchantManager.setItemLore(nbtItem.getItem());
				killEvent.killer.getEquipment().setLeggings(nbtItem.getItem());

				if(pitKiller.stats != null) pitKiller.stats.livesLost += 2;
			}
		}
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7On kill, if you have a killstreak", "&7of at least 100, &eExplode:",
				"&e\u25a0 &7Die! Keep jewel lives on death",
				"&a\u25a0 &7Gain &e+1 renown &7for every 100 killstreak",
				"&c\u25a0 &7Lose &c2 lives &7on this item").getLore();
	}
}
