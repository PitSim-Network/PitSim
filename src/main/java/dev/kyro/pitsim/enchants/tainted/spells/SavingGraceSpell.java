package dev.kyro.pitsim.enchants.tainted.spells;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class SavingGraceSpell extends PitEnchant {
	public SavingGraceSpell() {
		super("Saving Grace", true, ApplyType.SCYTHES, "savinggrace", "save", "saving", "grace");
		isTainted = true;
		isRare = true;
	}

	@EventHandler
	public void onUse(PitPlayerAttemptAbilityEvent event) {
		Player player = event.getPlayer();

		int enchantLvl = event.getEnchantLevel(this);
		if(enchantLvl == 0) return;
		if(!player.isSneaking()) return;

		Cooldown cooldown = getCooldown(player, 10);
		if(cooldown.isOnCooldown()) return;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.useMana(getManaCost(enchantLvl))) {
			Sounds.NO.play(player);
			return;
		}

		pitPlayer.updateMaxHealth();
		if(player.getMaxHealth() <= 8) {
			AOutput.send(player, "&c&lERROR!&7 Not enough health!");
			Sounds.NO.play(player);
			return;
		}

		cooldown.restart();

		pitPlayer.heal(player.getMaxHealth(), HealEvent.HealType.ABSORPTION, (int) (player.getMaxHealth()));
		pitPlayer.graceTiers += 1;
		pitPlayer.updateMaxHealth();
		player.getWorld().spigot().playEffect(player.getLocation().add(0, 1, 0),
				Effect.HAPPY_VILLAGER, 0, 0, (float) 1, (float) 1, (float) 1, (float) 0.5, 25, 50);

		Sounds.SURVIVOR_HEAL.play(player);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isDeadPlayer()) return;
		if(killEvent.getDeadPitPlayer().graceTiers == 0) return;
		killEvent.getDeadPitPlayer().graceTiers = 0;

		new BukkitRunnable() {
			@Override
			public void run() {
				if(killEvent.getDeadPlayer() == null) return;
				killEvent.getDeadPitPlayer().heal(killEvent.getDeadPlayer().getMaxHealth());
			}
		}.runTaskLater(PitSim.INSTANCE, 10);
	}

	@EventHandler
	public void onOof(OofEvent event) {
		Player player = event.getPlayer();
		PitPlayer.getPitPlayer(player).graceTiers = 0;
		PitPlayer.getPitPlayer(player).heal(player.getMaxHealth());

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!player.isOnline()) return;
				PitPlayer.getPitPlayer(player).heal(player.getMaxHealth());
			}
		}.runTaskLater(PitSim.INSTANCE, 2);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new ALoreBuilder("&7Heal your max health in &6\u2764", "&7but lose &c2\u2764 &7until you die", "&7(Shift Right-Click)", "&d&o-" + getManaCost(enchantLvl) + " Mana").getLore();
	}

	public static int getManaCost(int enchantLvl) {
		return 30 * (4 - enchantLvl);
	}
}
