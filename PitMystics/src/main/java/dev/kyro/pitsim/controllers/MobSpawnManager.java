package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.mobs.ZombieMob;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityTypes;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MobSpawnManager {
	public static void spawn(Location loc){
		World world = loc.getWorld();
		final ZombieMob customEnt = new ZombieMob(world);
		customEnt.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		((CraftLivingEntity) customEnt.getBukkitEntity()).setRemoveWhenFarAway(false);
	}


	public static void registerEntity(String name, int id, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass){
		try {

			List<Map<?, ?>> dataMap = new ArrayList<Map<?, ?>>();
			for (Field f : EntityTypes.class.getDeclaredFields()){
				if (f.getType().getSimpleName().equals(Map.class.getSimpleName())){
					f.setAccessible(true);
					dataMap.add((Map<?, ?>) f.get(null));
				}
			}

			if (dataMap.get(2).containsKey(id)){
				dataMap.get(0).remove(name);
				dataMap.get(2).remove(id);
			}

			Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class);
			method.setAccessible(true);
			method.invoke(null, customClass, name, id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
