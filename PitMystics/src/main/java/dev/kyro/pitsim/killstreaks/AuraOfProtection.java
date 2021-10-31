package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class AuraOfProtection extends Killstreak {

	public static AuraOfProtection INSTANCE;

	public AuraOfProtection() {
		super("Aura of Protection", "AuraOfProtection", 25, 0);
		INSTANCE = this;
	}

	List<Player> rewardPlayers = new ArrayList<>();

	@EventHandler
	public void onHit(AttackEvent.Apply event) {
		if(rewardPlayers.contains(event.defender)) {
			event.trueDamage = 0;
		}
	}

	@Override
	public void proc(Player player) {
		rewardPlayers.add(player);
		Sounds.SoundMoment soundMoment = new Sounds.SoundMoment(10);
		soundMoment.add(Sound.ZOMBIE_UNFECT,2, 0.79);
		soundMoment.add(Sound.ZOMBIE_UNFECT,2, 0.84);
		soundMoment.add(Sound.ZOMBIE_UNFECT,2, 0.88);
		soundMoment.add(Sound.ZOMBIE_UNFECT,2, 0.93);
		soundMoment.play(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				rewardPlayers.remove(player);
			}
		}.runTaskLater(PitSim.INSTANCE, 15 * 20L);
	}

	@Override
	public void reset(Player player) {
		rewardPlayers.remove(player);
	}

	@Override
	public ItemStack getDisplayItem() {

		AItemStackBuilder builder = new AItemStackBuilder(Material.SLIME_BALL);
		builder.setName("&e" + name);
		builder.setLore(new ALoreBuilder("&7Every: &c" + killInterval + " kills", "", "&7Gain &eTrue Damage &7immunity &7for", "&715 seconds."));

		return builder.getItemStack();
	}
}
