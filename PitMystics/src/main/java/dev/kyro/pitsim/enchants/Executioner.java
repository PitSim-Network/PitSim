package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Executioner extends PitEnchant {

	public Executioner() {
		super("Executioner", true, ApplyType.SWORDS,
				"executioner", "exe");
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.exeDeath) return;

		Sounds.EXE.play(killEvent.killer);
		killEvent.dead.getWorld().playEffect(killEvent.dead.getLocation().add(0, 1, 0), Effect.STEP_SOUND, 152);

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.killer);
		if(pitPlayer.stats != null) pitPlayer.stats.executioner++;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		attackEvent.executeUnder = getExecuteHealth(enchantLvl);

//		if(attackEvent.attacker.getName().equals("KyroKrypt")) {
//			yeet(attackEvent.defender);
//		}
	}

	public void yeet(Player willBeCrashed){
		final EntityPlayer nmsPlayer = ((CraftPlayer) willBeCrashed).getHandle();
		final EntityCreeper entity = new EntityCreeper(nmsPlayer.world);
		final DataWatcher dataWatcher = new DataWatcher(entity);
		dataWatcher.a(18, (Object)Integer.MAX_VALUE);
		PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entity);
		nmsPlayer.playerConnection.sendPacket(spawnPacket);
		Bukkit.getScheduler().scheduleSyncDelayedTask(PitSim.INSTANCE, () -> {
			PacketPlayOutEntityMetadata crashPacket = new PacketPlayOutEntityMetadata(entity.getId(), dataWatcher, true);
			nmsPlayer.playerConnection.sendPacket(crashPacket);
		}, 5L);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Hitting an enemy below &c" + Misc.getHearts(getExecuteHealth(enchantLvl)),
				"&7instantly kills them").getLore();
	}

	public double getExecuteHealth(int enchantLvl) {
		if(enchantLvl > 2) return enchantLvl * 2 - 1;
		return enchantLvl + 1;
	}
}
