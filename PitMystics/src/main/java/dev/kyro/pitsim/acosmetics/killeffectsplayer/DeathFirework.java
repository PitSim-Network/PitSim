package dev.kyro.pitsim.acosmetics.killeffectsplayer;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.acosmetics.CosmeticType;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class DeathFirework extends PitCosmetic {

	public DeathFirework() {
		super("&8Celebration", "firework", CosmeticType.PLAYER_KILL_EFFECT);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getDeadPlayer()) || !isEnabled(killEvent.getKillerPitPlayer()) ||
				nearMid(killEvent.getDeadPlayer())) return;

		Location deathLocation = killEvent.getDeadPlayer().getLocation();
		new BukkitRunnable() {
			private int i = 0;
			@Override
			public void run() {
				if(i++ == 3) {
					cancel();
					return;
				}
				launchFirework(deathLocation);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 5L);
	}

	public void launchFirework(Location deathLocation) {
		Firework firework = (Firework) deathLocation.getWorld().spawnEntity(deathLocation.add(
				Misc.randomOffset(3), 0, Misc.randomOffset(3)), EntityType.FIREWORK);
		FireworkMeta fireworkMeta = firework.getFireworkMeta();
		Random random = new Random();

		//Get the type
		int randInt = new Random().nextInt(5);
		FireworkEffect.Type type;
		if(randInt == 0) type = FireworkEffect.Type.BALL;
		else if(randInt == 1) type = FireworkEffect.Type.BALL_LARGE;
		else if(randInt == 2) type = FireworkEffect.Type.BURST;
		else if(randInt == 3) type = FireworkEffect.Type.CREEPER;
		else type = FireworkEffect.Type.STAR;

		//Get our random colours
		Color color1 = getColor(random.nextInt(18));
		Color color2 = getColor(random.nextInt(18));

		//Create our effect with this
		FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(color1).withFade(color2).with(type).trail(random.nextBoolean()).build();

		//Then apply the effect to the meta
		fireworkMeta.addEffect(effect);

		fireworkMeta.setPower(1);

		//Then apply this to our rocket
		firework.setFireworkMeta(fireworkMeta);
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.BONE)
				.setName(getDisplayName())
				.getItemStack();
		return itemStack;
	}

	private Color getColor(int i) {
		Color color = null;
		if(i == 0) color=Color.AQUA;
		else if(i == 1) color = Color.BLACK;
		else if(i == 2) color = Color.BLUE;
		else if(i == 3) color = Color.FUCHSIA;
		else if(i == 4) color = Color.GRAY;
		else if(i == 5) color = Color.GREEN;
		else if(i == 6) color = Color.LIME;
		else if(i == 7) color = Color.MAROON;
		else if(i == 8) color = Color.NAVY;
		else if(i == 9) color = Color.OLIVE;
		else if(i == 10) color = Color.ORANGE;
		else if(i == 11) color = Color.PURPLE;
		else if(i == 12) color = Color.RED;
		else if(i == 13) color = Color.SILVER;
		else if(i == 14) color = Color.TEAL;
		else if(i == 15) color = Color.WHITE;
		else if(i == 16) color = Color.YELLOW;
		return color;
	}
}
