package dev.kyro.pitsim.adarkzone.abilities.minion;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.SubLevel;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.enums.MobStatus;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.PitQuitEvent;
import dev.kyro.pitsim.misc.CustomPitBat;
import dev.kyro.pitsim.misc.EntityManager;
import dev.kyro.pitsim.misc.Misc;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.*;


public class EndermanMinionAbility extends MinionAbility {

	static {
		EntityManager.registerEntity("PitBat", 65, CustomPitBat.class);
	}

	public List<PitMob> minionList;
	public Map<PitMob, ItemRope> itemRopes;

	public EndermanMinionAbility(SubLevelType subLevelType, int maxMobs) {
		super(subLevelType, maxMobs);

		minionList = new ArrayList<>();
		itemRopes = new HashMap<>();
	}

	@Override
	public void spawnMobs(Location location, int spawnAmount) {
		for(int i = 0; i < spawnAmount; i++) {
			SubLevel subLevel = subLevelType.getSubLevel();
			if(subLevel.mobs.size() >= maxMobs) return;
			PitMob pitMob = subLevel.spawnMob(location, MobStatus.MINION);
			minionList.add(pitMob);
		}
	}

	@Override
	public void onDisable() {
		itemRopes.values().forEach(ItemRope::remove);
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		if(!Misc.isEntity(attackEvent.getAttacker(), PitEntityType.REAL_PLAYER)) return;

		for(PitMob pitMob : minionList) {
			LivingEntity livingEntity = pitMob.getMob();
			if(livingEntity != attackEvent.getAttacker()) continue;
			if(itemRopes.containsKey(pitMob)) return;

			Player player = (Player) attackEvent.getDefender();
			int itemSlot = new Random().nextInt(9);
			org.bukkit.inventory.ItemStack firstItem = player.getInventory().getItem(itemSlot);
			if(firstItem == null) return;
			player.getInventory().setItem(itemSlot, null);

			ItemRope itemRope = new ItemRope(pitMob, this, firstItem, player);
			itemRopes.put(pitMob, itemRope);
		}
	}

	@EventHandler
	public void onEndermanDeath(KillEvent killEvent) {
		if(!Misc.isEntity(killEvent.getKiller(), PitEntityType.REAL_PLAYER)) return;

		for(PitMob pitMob : minionList) {
			LivingEntity livingEntity = pitMob.getMob();
			if(livingEntity != killEvent.getDead()) continue;
			ItemRope itemRope = itemRopes.get(pitMob);
			if(itemRope == null) continue;

			itemRope.remove();
			itemRopes.remove(pitMob);
		}
	}

	@EventHandler
	public void onQuit(PitQuitEvent event) {
		List<PitMob> toRemove = new ArrayList<>();

		for(Map.Entry<PitMob, ItemRope> entry : itemRopes.entrySet()) {
			ItemRope itemRope = entry.getValue();
			if(itemRope.currentlyHolding != event.getPlayer()) continue;

			AUtil.giveItemSafely(event.getPlayer(), itemRope.itemStack);

			itemRope.remove();
			toRemove.add(entry.getKey());
		}

		toRemove.forEach(itemRopes::remove);
	}

	public static class ItemRope {
		public PitMob pitMob;
		public PitBossAbility ability;

		public CustomPitBat bat;
		public EntityArmorStand armorStand;
		public org.bukkit.inventory.ItemStack itemStack;
		public Player currentlyHolding;

		public ItemRope(PitMob pitMob, PitBossAbility ability, org.bukkit.inventory.ItemStack itemStack, Player currentlyHolding) {
			this.pitMob = pitMob;
			this.ability = ability;
			this.itemStack = itemStack;
			this.currentlyHolding = currentlyHolding;

			spawnBat();
			sendArmorStand();
		}

		public void spawnBat() {
			World nmsWorld = ((CraftWorld) pitMob.getMob().getWorld()).getHandle();

			CustomPitBat bat = new CustomPitBat(nmsWorld, ((CraftEntity) pitMob.getMob()).getHandle());
			Location spawnLocation = pitMob.getMob().getLocation().add(0, 2, 0);

			bat.setLocation(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(), 0, 0);
			nmsWorld.addEntity(bat);

			MobEffect mobEffect = new MobEffect(14, Integer.MAX_VALUE, 0, false, false);
			bat.addEffect(mobEffect);

			this.bat = bat;
		}

		public void sendArmorStand() {
			World nmsWorld = ((CraftWorld) pitMob.getMob().getWorld()).getHandle();
			Location spawnLocation = pitMob.getMob().getLocation().add(0, 2, 0);

			EntityArmorStand armorStand = new EntityArmorStand(nmsWorld, spawnLocation.getX(), spawnLocation.getY(),
					spawnLocation.getZ());
			armorStand.setArms(true);
			armorStand.setBasePlate(false);
			armorStand.setRightArmPose(new Vector3f(0, 90, 330));
			armorStand.setInvisible(true);

			PacketPlayOutSpawnEntityLiving armorStandSpawn = new PacketPlayOutSpawnEntityLiving(armorStand);
			PacketPlayOutEntityEquipment standSword = new PacketPlayOutEntityEquipment(armorStand.getId(), 0,
					new net.minecraft.server.v1_8_R3.ItemStack(Items.DIAMOND_SWORD));
			PacketPlayOutAttachEntity batAttach = new PacketPlayOutAttachEntity(0, armorStand, bat);
			PacketPlayOutAttachEntity attachPacket = new PacketPlayOutAttachEntity(1, bat,
					((CraftEntity) pitMob.getMob()).getHandle());

			for(Player player : ability.getViewers()) {
				EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

				nmsPlayer.playerConnection.sendPacket(armorStandSpawn);
				nmsPlayer.playerConnection.sendPacket(standSword);
				nmsPlayer.playerConnection.sendPacket(batAttach);
				nmsPlayer.playerConnection.sendPacket(attachPacket);
			}
		}

		public void remove() {
			if(itemStack != null && currentlyHolding != null) {
				AUtil.giveItemSafely(currentlyHolding, itemStack, true);
			}

			bat.getBukkitEntity().remove();
			armorStand.getBukkitEntity().remove();

			PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(armorStand.getId());
			for(Player player : ability.getViewers()) {
				EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
				nmsPlayer.playerConnection.sendPacket(destroyPacket);
			}
		}
	}
}
