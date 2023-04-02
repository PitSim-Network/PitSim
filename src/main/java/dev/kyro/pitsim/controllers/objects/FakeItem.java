package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.ItemManager;
import dev.kyro.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class FakeItem {
	private final EntityItem entityItem;
	private final ItemStack itemStack;
	private final Location location;

	private final List<Player> viewers = new ArrayList<>();

	private boolean hasBeenRemoved;
	private boolean canBePickedUp = true;
	private BiConsumer<Player, ItemStack> pickupCallback;

	public FakeItem(ItemStack itemStack, Location location) {
		this.itemStack = itemStack;
		this.location = location;

		World world = ((CraftWorld) location.getWorld()).getHandle();
		EntityItem entityItem = new EntityItem(world);
		entityItem.setPosition(location.getX(), location.getY(), location.getZ());
		entityItem.setItemStack(CraftItemStack.asNMSCopy(new ItemStack(itemStack)));
		this.entityItem = entityItem;

		ItemManager.fakeItems.add(this);
	}

	public FakeItem showToAllPlayers() {
		if(hasBeenRemoved) return this;
		addViewers(Bukkit.getOnlinePlayers().toArray(new Player[0]));
		return this;
	}

	public FakeItem addViewers(Player... players) {
		if(hasBeenRemoved) return this;
		for(Player player : players) addViewer(player);
		return this;
	}

	public FakeItem addViewer(Player player) {
		if(viewers.contains(player) || hasBeenRemoved) return this;
		viewers.add(player);

		PacketPlayOutSpawnEntity spawn = new PacketPlayOutSpawnEntity(entityItem, 2, itemStack.getAmount());
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawn);
		PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(entityItem.getId(), entityItem.getDataWatcher(), true);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(meta);

		return this;
	}

	public FakeItem disablePickup() {
		canBePickedUp = false;
		return this;
	}

	public FakeItem onPickup(BiConsumer<Player, ItemStack> pickupCallback) {
		this.pickupCallback = pickupCallback;
		return this;
	}

	public FakeItem removeAfter(long ticks) {
		new BukkitRunnable() {
			@Override
			public void run() {
				remove();
			}
		}.runTaskLater(PitSim.INSTANCE, ticks);
		return this;
	}

	public void pickup(Player player) {
		if(hasBeenRemoved) return;
		hasBeenRemoved = true;

		PacketPlayOutCollect packet = new PacketPlayOutCollect(entityItem.getId(), player.getEntityId());
		for(Player viewer : viewers) ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(packet);

		Sounds.ITEM_PICKUP.play(player);
		if(pickupCallback != null) pickupCallback.accept(player, itemStack);
		ItemManager.fakeItems.remove(this);
	}

	public void remove() {
		if(hasBeenRemoved) return;
		hasBeenRemoved = true;
		for(Player viewer : new ArrayList<>(viewers)) removeViewers(viewer);
		ItemManager.fakeItems.remove(this);
	}

	public void removeViewers(Player... viewers) {
		if(hasBeenRemoved) return;
		for(Player viewer : viewers) removeViewer(viewer);
	}

	public FakeItem removeViewer(Player viewer) {
		PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entityItem.getId());
		((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(destroy);
		this.viewers.remove(viewer);
		return this;
	}

	public EntityItem getEntityItem() {
		return entityItem;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public Location getLocation() {
		return location;
	}

	public List<Player> getViewers() {
		return viewers;
	}

	public boolean hasBeenRemoved() {
		return hasBeenRemoved;
	}

	public boolean canBePickedUp() {
		return canBePickedUp;
	}

	public BiConsumer<Player, ItemStack> getPickupCallback() {
		return pickupCallback;
	}
}
