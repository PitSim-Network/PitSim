package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.battlepass.quests.EarnRenownQuest;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.enums.KillModifier;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.megastreaks.NoMegastreak;
import dev.kyro.pitsim.megastreaks.RNGesus;
import dev.kyro.pitsim.megastreaks.Uberstreak;
import dev.kyro.pitsim.misc.wrappers.PlayerItemLocation;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class SelfCheckout extends PitEnchant {
	public static SelfCheckout INSTANCE;
	public static final int LIVES_ON_USE = 3;

	public SelfCheckout() {
		super("Self-Checkout", true, ApplyType.PANTS,
				"selfcheckout", "self-checkout", "sco", "selfcheck", "checkout", "soco");
		INSTANCE = this;
	}
	
	@EventHandler
	public void onScoDeath(KillEvent killEvent) {
		if(!killEvent.hasKillModifier(KillModifier.SELF_CHECKOUT)) return;
		for(Map.Entry<PlayerItemLocation, KillEvent.ItemInfo> entry : killEvent.getVulnerableItems().entrySet()) {
			KillEvent.ItemInfo itemInfo = entry.getValue();
			if(!itemInfo.pitItem.isMystic) continue;
			killEvent.removeVulnerableItem(entry.getKey());
		}
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer() || killEvent.getKiller() == killEvent.getDead()) return;

		ItemStack itemStack = killEvent.getKiller().getEquipment().getLeggings();
		int enchantLvl = killEvent.getDeadEnchantLevel(this);
		if(enchantLvl == 0) return;

		PitItem pitItem = ItemFactory.getItem(itemStack);
		assert pitItem != null;
		TemporaryItem temporaryItem = pitItem.getAsTemporaryItem();

		PitPlayer pitKiller = PitPlayer.getPitPlayer(killEvent.getKillerPlayer());
		if(pitKiller.getKills() + 1 < 200 || pitKiller.megastreak instanceof Uberstreak ||
				pitKiller.megastreak instanceof NoMegastreak || pitKiller.megastreak instanceof RNGesus) return;

		if(!MysticFactory.isJewel(itemStack, false)) {
			AOutput.error(killEvent.getKiller(), "Self-Checkout only works on jewel items");
			return;
		}

		itemStack = temporaryItem.damage(itemStack, LIVES_ON_USE).getItemStack();
		killEvent.getKillerPlayer().getEquipment().setLeggings(itemStack);
		killEvent.getKillerPlayer().updateInventory();

		String scoMessage = "&9&lSCO!&7 Self-Checkout pants activated";
		int renown = Math.min((pitKiller.getKills() + 1) / 300, 4);
		if(renown != 0) {
			pitKiller.renown += renown;
			EarnRenownQuest.INSTANCE.gainRenown(pitKiller, renown);
			scoMessage += " giving &e" + renown + " &7 renown";
		}

		AOutput.send(killEvent.getKillerPlayer(), scoMessage);
		DamageManager.death(killEvent.getKiller(), KillModifier.SELF_CHECKOUT);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new ALoreBuilder(
				"&7On kill, if you have a killstreak", "&7of at least 200, &eExplode:",
				"&e\u25a0 &7Die! Keep jewel lives on death",
				"&a\u25a0 &7Gain &e+1 renown &7for every 300 killstreak (max 4)",
				"&c\u25a0 &7Lose &c" + LIVES_ON_USE + " lives &7on this item"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that allows you to " +
				"save the lives on &3Jewels&7, while also giving you &eRenown";
	}
}
