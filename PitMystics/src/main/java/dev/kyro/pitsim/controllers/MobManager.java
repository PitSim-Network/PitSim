package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.controllers.objects.PitMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobManager {
	public static List<PitMob> mobs = new ArrayList<>();
	public static Map<PitMob, ArmorStand> nameTags = new HashMap<>();

//	static {
//		new BukkitRunnable() {
//			@Override
////			public void run() {
////				for(PitMob mob : mobs) {
////					List<Entity> entities = mob.entity.getNearbyEntities(10, 10, 10);
////					for(Entity entity : entities) {
////						if(mob.entity == null) continue;
////						if(!(entity instanceof Player)) continue;
////
////						PacketContainer packet = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE);
////						PacketPlayOutEntity.PacketPlayOutRelEntityMove
////						packet.getIntegers()
////								.write(0, nameTags.get(mob).getEntityId());
////
////						Vector v = mob.entity.getLocation().toVector();
////						packet.getBytes()
////								.write(0, (byte) (v.getX()))
////								.write(1, (byte) (v.getY()))
////								.write(2, (byte) (v.getZ()));
////
////						packet.getBooleans()
////								.write(0, false);
////						try {
////							PitSim.PROTOCOL_MANAGER.sendServerPacket((Player) entity, packet);
////						} catch (Exception e) {
////							e.printStackTrace();
////						}
////					}
////				}
//			}
//		}.runTaskTimer(PitSim.INSTANCE, 1, 1);
//	}

	public static void makeTag(PitMob mob) {
		Location op = new Location(Bukkit.getWorld("world"), 0, 100, 0);

		ArmorStand stand = op.getWorld().spawn(op, ArmorStand.class);
		stand.setGravity(false);
		stand.setVisible(false);
		stand.setCustomNameVisible(true);
		stand.setCustomName(mob.entity.getCustomName());

		nameTags.put(mob, stand);
	}


}
