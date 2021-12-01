package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class aEntanglement extends PitEnchant {

	public aEntanglement() {
		super("Entanglement", true, ApplyType.NONE,
				"entanglement", "entangle", "tangle", "quantum");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int heldLvl = EnchantManager.getEnchantLevel(attackEvent.attacker.getItemInHand(), this);
		int wornLvl = EnchantManager.getEnchantLevel(attackEvent.defender.getEquipment().getLeggings(), this);

		if(heldLvl != 0) attackEvent.attacker.setItemInHand(scramble(attackEvent.attacker.getItemInHand(), heldLvl));
		if(wornLvl != 0) attackEvent.defender.getEquipment().setLeggings(scramble(attackEvent.defender.getEquipment().getLeggings(), wornLvl));
	}

	public ItemStack scramble(ItemStack itemStack, int enchantLvl) {
		for(PitEnchant pitEnchant : EnchantManager.pitEnchants) {
			if(pitEnchant == this) continue;
			int thisLevel = EnchantManager.getEnchantLevel(itemStack, pitEnchant);
			if(thisLevel == 0) continue;
			try {
				itemStack = EnchantManager.addEnchant(itemStack, pitEnchant, 0, false);
			} catch(Exception ignored) { }
		}
		List<PitEnchant> enchantList = EnchantManager.getEnchants(MysticType.getMysticType(itemStack));
		Collections.shuffle(enchantList);
		for(int i = 0; i < 5; i++) {
			try {
				itemStack = EnchantManager.addEnchant(itemStack, enchantList.remove(0), enchantLvl + 1, false);
			} catch(Exception ignore) { }
		}

		return itemStack;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7The enchants on this item are entangled", "&7with the server's enchant list").getLore();
	}
}
