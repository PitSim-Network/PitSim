package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;

import java.util.*;

//TODO: Can maybe just re-route the attack to hit someone else instead
public class DispersionEnchant extends PitEnchant {
	public static List<EnchantAndLevel> toDisperse = new ArrayList<>();

	public DispersionEnchant() {
		super("Dispersion", false, ApplyType.PANTS,
				"dispersion", "dis", "disperse");
	}

	@EventHandler
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;

		if(!MapManager.currentMap.lobbies.contains(attackEvent.defender.getWorld())) return;
		if(attackEvent.defender.getLocation().distance(MapManager.currentMap.getMid(attackEvent.defender.getWorld())) > 12) return;

		PitPlayer pitDefender = PitPlayer.getPitPlayer(attackEvent.defender);
		if(pitDefender.megastreak.isOnMega()) {
			Map<PitEnchant, Integer> selected = new LinkedHashMap<>();
			for(Map.Entry<PitEnchant, Integer> entry : attackEvent.getAttackerEnchantMap().entrySet()) {
				if(Math.random() * 100 > getPercent(enchantLvl)) continue;
				selected.put(entry.getKey(), entry.getValue());
			}

			for(Map.Entry<PitEnchant, Integer> entry : selected.entrySet()) {
				if(toDisperse.size() > 250) continue;
				attackEvent.getAttackerEnchantMap().remove(entry.getKey());
				toDisperse.add(new EnchantAndLevel(entry.getKey(), entry.getValue(),
						attackEvent.attacker.getUniqueId(), attackEvent.defender.getUniqueId()));
			}
		}

		for(int i = 0; i < toDisperse.size() / 10 + 1; i++) {
			for(int j = 0; j < toDisperse.size(); j++) {
				EnchantAndLevel enchantAndLevel = toDisperse.get(i);
				if(enchantAndLevel.defender.equals(attackEvent.defender.getUniqueId())) continue;
//				if(NonManager.getNon(attackEvent.attacker) == null) {
//					if(attackEvent.getAttackerEnchantLevel(enchantAndLevel.pitEnchant) != 0)
//						attackEvent.getAttackerEnchantMap().put(enchantAndLevel.pitEnchant, enchantAndLevel.level);
//				} else {
//					attackEvent.getAttackerEnchantMap().put(enchantAndLevel.pitEnchant,
//							Math.min(attackEvent.getAttackerEnchantMap().get(enchantAndLevel.pitEnchant) + enchantAndLevel.level, 5));
//				}
				if(attackEvent.getAttackerEnchantLevel(enchantAndLevel.pitEnchant) < enchantAndLevel.level)
					attackEvent.getAttackerEnchantMap().put(enchantAndLevel.pitEnchant, enchantAndLevel.level);
				toDisperse.remove(enchantAndLevel);
				break;
			}
		}
	}

	@Override
	public List<String> getDescription(int enchantLvl) {
		return new ALoreBuilder("&7If you are on mega and near mid", "&ddisperse " + getPercent(enchantLvl) + "% &7of the enchants",
				"&7on your opponent's attacks to", "&7nearby players using this enchant").getLore();
	}

	public int getPercent(int enchantLvl) {
		return enchantLvl * 20;
	}

	public static class EnchantAndLevel {
		public PitEnchant pitEnchant;
		public int level;

		public UUID attacker;
		public UUID defender;

		public EnchantAndLevel(PitEnchant pitEnchant, int level, UUID attacker, UUID defender) {
			this.pitEnchant = pitEnchant;
			this.level = level;
			this.attacker = attacker;
			this.defender = defender;
		}
	}
}
