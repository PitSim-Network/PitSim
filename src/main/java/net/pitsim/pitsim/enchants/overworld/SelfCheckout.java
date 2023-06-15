package net.pitsim.pitsim.enchants.overworld;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.pitsim.aitems.MysticFactory;
import net.pitsim.pitsim.aitems.PitItem;
import net.pitsim.pitsim.aitems.TemporaryItem;
import net.pitsim.pitsim.battlepass.quests.EarnRenownQuest;
import net.pitsim.pitsim.controllers.DamageManager;
import net.pitsim.pitsim.controllers.EnchantManager;
import net.pitsim.pitsim.controllers.ItemFactory;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.enums.ApplyType;
import net.pitsim.pitsim.enums.KillModifier;
import net.pitsim.pitsim.events.KillEvent;
import net.pitsim.pitsim.megastreaks.NoMegastreak;
import net.pitsim.pitsim.megastreaks.RNGesus;
import net.pitsim.pitsim.megastreaks.Uberstreak;
import net.pitsim.pitsim.misc.Formatter;
import net.pitsim.pitsim.misc.PlayerItemLocation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelfCheckout extends PitEnchant {
	public static SelfCheckout INSTANCE;

	public SelfCheckout() {
		super("Self-Checkout", true, ApplyType.PANTS,
				"selfcheckout", "self-checkout", "sco", "selfcheck", "checkout", "soco");
		INSTANCE = this;
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onScoDeath(KillEvent killEvent) {
		if(!killEvent.hasKillModifier(KillModifier.SELF_CHECKOUT)) return;
		for(Map.Entry<PlayerItemLocation, KillEvent.ItemInfo> entry : new ArrayList<>(killEvent.getVulnerableItems().entrySet())) {
			KillEvent.ItemInfo itemInfo = entry.getValue();
			if(!itemInfo.pitItem.isMystic) continue;
			killEvent.removeVulnerableItem(entry.getKey());
		}
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer() || killEvent.getKiller() == killEvent.getDead()) return;

		ItemStack itemStack = killEvent.getKiller().getEquipment().getLeggings();
		int enchantLvl = EnchantManager.getEnchantLevel(itemStack, this);
		if(enchantLvl == 0) return;

		PitItem pitItem = ItemFactory.getItem(itemStack);
		assert pitItem != null;
		TemporaryItem temporaryItem = pitItem.getAsTemporaryItem();

		if(killEvent.getKillerPitPlayer().getKills() + 1 < 200 || killEvent.getKillerPitPlayer().getMegastreak() instanceof Uberstreak ||
				killEvent.getKillerPitPlayer().getMegastreak() instanceof NoMegastreak || killEvent.getKillerPitPlayer().getMegastreak() instanceof RNGesus) return;

		if(!MysticFactory.isJewel(itemStack, false)) {
			AOutput.error(killEvent.getKiller(), "Self-Checkout only works on jewel items");
			return;
		}

		itemStack = temporaryItem.damage(itemStack, getLivesOnUse()).getItemStack();
		killEvent.getKillerPlayer().getEquipment().setLeggings(itemStack);
		killEvent.getKillerPlayer().updateInventory();

		String scoMessage = "&9&lSCO!&7 Self-Checkout pants activated";
		int renown = Math.min((killEvent.getKillerPitPlayer().getKills() + 1) / getRenownEveryKills(), getMaxRenown());
		if(renown != 0) {
			killEvent.getKillerPitPlayer().renown += renown;
			EarnRenownQuest.INSTANCE.gainRenown(killEvent.getKillerPitPlayer(), renown);
			scoMessage += " giving " + Formatter.formatRenown(renown);
		}

		AOutput.send(killEvent.getKillerPlayer(), scoMessage + "&7!");
		DamageManager.killPlayer(killEvent.getKillerPlayer(), KillModifier.SELF_CHECKOUT);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new ALoreBuilder(
				"&7On kill, if you have a killstreak", "&7of at least " + getRequiredKills() + ", &eExplode:",
				"&e\u25a0 &7Die! Keep lives on &3Jewel &7items",
				"&a\u25a0 &7Gain &e+1 renown &7for every " + getRenownEveryKills() + " killstreak (max " + getMaxRenown() + ")",
				"&c\u25a0 &7Lose &c" + getLivesOnUse() + " lives &7on this item"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that allows you to " +
				"save the lives on &3Jewels&7, while also giving you &eRenown";
	}

	public static int getRequiredKills() {
		return 200;
	}

	public static int getRenownEveryKills() {
		return 300;
	}

	public static int getMaxRenown() {
		return 4;
	}

	public static int getLivesOnUse() {
		return 3;
	}
}
