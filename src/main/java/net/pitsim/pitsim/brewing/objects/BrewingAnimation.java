package net.pitsim.pitsim.brewing.objects;

import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.aitems.mobdrops.SpiderEye;
import net.pitsim.pitsim.brewing.BrewingManager;
import net.pitsim.pitsim.controllers.MapManager;
import net.pitsim.pitsim.events.PitQuitEvent;
import net.pitsim.pitsim.misc.Misc;
import net.pitsim.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Cauldron;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.*;

public class BrewingAnimation {

	public Location location;
	public List<ArmorStand> stands = new ArrayList<>();
	public List<Player> players = new ArrayList<>();
	public List<ArmorStand> personalStands = new ArrayList<>();

	public Map<Player, ArmorStand> identityStands = new HashMap<>();
	public Map<Player, ArmorStand> potencyStands = new HashMap<>();
	public Map<Player, ArmorStand> durationStands = new HashMap<>();
	public Map<Player, ArmorStand> brewingTimeStands = new HashMap<>();
	public Map<Player, ArmorStand> confirmStands = new HashMap<>();
	public Map<Player, ArmorStand> cancelStands = new HashMap<>();
	public List<Player> buttonPlayers = new ArrayList<>();
	public Map<Player, ItemStack[]> ingredients = new HashMap<>();

	public String[] originalMessages = new String[]{"&e&lBrewing Stand", "&7Use &5Tainted &7mob drops", "&7to change different", "&7aspects of the potion", "&eRight-Click the Barriers!"};

	public BrewingAnimation(Location location) {
		this.location = location;
		location.getBlock().setType(Material.CAULDRON);
		Cauldron cauldron = (Cauldron) location.getBlock().getState().getData();

		BlockState cauldronState = location.getBlock().getState();
		cauldronState.getData().setData((byte) (cauldron.getData() + 3));
		cauldronState.update();

		for(int i = 0; i < 5; i++) {
			ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5, (0.3 * (i + 1)), 0.5), EntityType.ARMOR_STAND);
			stand.setCustomNameVisible(true);
			stand.setCustomName(ChatColor.translateAlternateColorCodes('&', "&c"));
			stand.setGravity(false);
			stand.setVisible(false);
			stands.add(stand);
			BrewingManager.brewingStands.add(stand);

		}
	}

	public void setText(Player player, String[] text) {
		for(int i = 0; i < stands.size(); i++) {
			PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving((EntityLiving) ((CraftEntity) stands.get(i)).getHandle());

			ArmorStand ogStand = stands.get(i);
			EntityArmorStand test = new EntityArmorStand(((CraftWorld) ogStand.getWorld()).getHandle());
			ArmorStand newStand = (ArmorStand) test.getBukkitEntity();
			newStand.setVisible(false);
			newStand.setGravity(false);
			newStand.setCustomNameVisible(true);

			DataWatcher dw = ((CraftEntity) stands.get(i)).getHandle().getDataWatcher();
			DataWatcher newDw = ((CraftEntity) newStand).getHandle().getDataWatcher();
			newDw.watch(2, dw.getString(2));
			newStand.remove();

			((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawn);

			String originalName = dw.getString(2);
			if(text[text.length - (i + 1)] == null) newDw.watch(2, "§c");
			else newDw.watch(2, ChatColor.translateAlternateColorCodes('&', text[text.length - (i + 1)]));
			PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(getStandID(stands.get(i)), newDw, false);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(metaPacket);
			dw.watch(2, originalName);
		}
	}

	public void setItemText(Player player) {
		String[] strings = new String[5];

		strings[0] = ChatColor.YELLOW + "" + ChatColor.BOLD + "Brew Details";

		ItemStack identifier = ingredients.get(player)[0];
		if(BrewingIngredient.isIngredient(identifier))
			strings[1] = ChatColor.LIGHT_PURPLE + "Type: " + Objects.requireNonNull(BrewingIngredient.getIngredientFromItemStack(identifier)).color + Objects.requireNonNull(BrewingIngredient.getIngredientFromItemStack(identifier)).name;
		else strings[1] = ChatColor.LIGHT_PURPLE + "Type: " + ChatColor.YELLOW + "Place an Item!";

		ItemStack potency = ingredients.get(player)[1];
		if(BrewingIngredient.isIngredient(potency))
			strings[2] = ChatColor.LIGHT_PURPLE + "Potency: " + ChatColor.DARK_PURPLE + "Tier " + AUtil.toRoman(Objects.requireNonNull(BrewingIngredient.getIngredientFromItemStack(potency)).tier);
		else strings[2] = ChatColor.LIGHT_PURPLE + "Potency: " + ChatColor.YELLOW + "Place an Item!";

		ItemStack duration = ingredients.get(player)[2];
		if(BrewingIngredient.isIngredient(duration) && BrewingIngredient.isIngredient(identifier) && (!(BrewingIngredient.getIngredientFromItemStack(identifier) instanceof SpiderEye)))
			strings[3] = ChatColor.LIGHT_PURPLE + "Duration: " +
					ChatColor.DARK_PURPLE + Misc.ticksToTime(Objects.requireNonNull(BrewingIngredient.getIngredientFromItemStack(identifier)).getDuration(BrewingIngredient.getIngredientFromItemStack(duration)));
		else if(BrewingIngredient.getIngredientFromItemStack(identifier) instanceof SpiderEye) {
			strings[3] = ChatColor.LIGHT_PURPLE + "Duration: " + ChatColor.DARK_PURPLE + "INSTANT!";
		} else strings[3] = ChatColor.LIGHT_PURPLE + "Duration: " + ChatColor.YELLOW + "Decided by Type!";

		ItemStack reduction = ingredients.get(player)[3];
		if(BrewingIngredient.isIngredient(reduction))
			strings[4] = ChatColor.LIGHT_PURPLE + "Brew Time: " + ChatColor.DARK_PURPLE + (105 - (Objects.requireNonNull(BrewingIngredient.getIngredientFromItemStack(reduction)).tier * 10) + "m");
		else strings[4] = ChatColor.LIGHT_PURPLE + "Brew Time: " + ChatColor.YELLOW + "Place an Item!";

		setText(player, strings);
	}

	public int getStandID(final ArmorStand stand) {
		for(Entity entity : location.getWorld().getNearbyEntities(location, 5.0, 5.0, 5.0)) {
			if(!(entity instanceof ArmorStand)) continue;
			if(entity.getUniqueId().equals(stand.getUniqueId())) return entity.getEntityId();
		}
		return 0;
	}

	public void addPlayer(Player player) {
		players.add(player);
		setText(player, originalMessages);
		ingredients.put(player, new ItemStack[]{null, null, null, null});
		showButtons(player);
	}

	public void showButtons(Player player) {
		ArmorStand identityStand = (ArmorStand) location.getWorld().spawnEntity(new Location(location.getWorld(), location.getX() + 0.5, location.getY(), location.getZ() - 0.5, -90, 0), EntityType.ARMOR_STAND);
		identityStand.setCustomNameVisible(true);
		identityStand.setCustomName(ChatColor.LIGHT_PURPLE + "Potion Type");
		identityStand.setGravity(false);
		identityStand.setVisible(false);
		identityStand.setArms(true);
		identityStand.setRightArmPose(new EulerAngle(Math.toRadians(90), Math.toRadians(90), Math.toRadians(180)));
		identityStands.put(player, identityStand);
		BrewingManager.brewingStands.add(identityStand);
		personalStands.add(identityStand);

		ArmorStand potencyStand = (ArmorStand) location.getWorld().spawnEntity(new Location(location.getWorld(), location.getX() + 0.5, location.getY(), location.getZ() - 0.5, -90, 0), EntityType.ARMOR_STAND);
		potencyStand.setCustomNameVisible(true);
		potencyStand.setCustomName(ChatColor.LIGHT_PURPLE + "Potency");
		potencyStand.setGravity(false);
		potencyStand.setVisible(false);
		potencyStand.setArms(true);
		potencyStand.setRightArmPose(new EulerAngle(Math.toRadians(90), Math.toRadians(90), Math.toRadians(180)));
		potencyStands.put(player, potencyStand);
		BrewingManager.brewingStands.add(potencyStand);
		personalStands.add(potencyStand);

		ArmorStand durationStand = (ArmorStand) location.getWorld().spawnEntity(new Location(location.getWorld(), location.getX() + 0.5, location.getY(), location.getZ() - 0.5, -90, 0), EntityType.ARMOR_STAND);
		durationStand.setCustomNameVisible(true);
		durationStand.setCustomName(ChatColor.LIGHT_PURPLE + "Duration");
		durationStand.setGravity(false);
		durationStand.setVisible(false);
		durationStand.setArms(true);
		durationStand.setRightArmPose(new EulerAngle(Math.toRadians(90), Math.toRadians(90), Math.toRadians(180)));
		durationStands.put(player, durationStand);
		BrewingManager.brewingStands.add(durationStand);
		personalStands.add(durationStand);

		ArmorStand brewingTimeStand = (ArmorStand) location.getWorld().spawnEntity(new Location(location.getWorld(), location.getX() + 0.5, location.getY(), location.getZ() - 0.5, -90, 0), EntityType.ARMOR_STAND);
		brewingTimeStand.setCustomNameVisible(true);
		brewingTimeStand.setCustomName(ChatColor.LIGHT_PURPLE + "Brewing Time");
		brewingTimeStand.setGravity(false);
		brewingTimeStand.setVisible(false);
		brewingTimeStand.setArms(true);
		brewingTimeStand.setRightArmPose(new EulerAngle(Math.toRadians(90), Math.toRadians(90), Math.toRadians(180)));
		brewingTimeStands.put(player, brewingTimeStand);
		BrewingManager.brewingStands.add(brewingTimeStand);
		personalStands.add(brewingTimeStand);

		ArmorStand confirmStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(-0.5, -0.8, 0.5), EntityType.ARMOR_STAND);
		confirmStand.setCustomNameVisible(true);
		confirmStand.setCustomName(ChatColor.GREEN + "Confirm");
		confirmStand.setGravity(false);
		confirmStand.setVisible(false);
		confirmStand.setArms(true);
		confirmStand.setHelmet(new ItemStack(Material.EMERALD_BLOCK));
		confirmStands.put(player, confirmStand);
		BrewingManager.brewingStands.add(confirmStand);
		personalStands.add(confirmStand);

		ArmorStand cancelStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(1.5, -0.8, 0.5), EntityType.ARMOR_STAND);
		cancelStand.setCustomNameVisible(true);
		cancelStand.setCustomName(ChatColor.RED + "Cancel");
		cancelStand.setGravity(false);
		cancelStand.setVisible(false);
		cancelStand.setArms(true);
		cancelStand.setHelmet(new ItemStack(Material.REDSTONE_BLOCK));
		cancelStands.put(player, cancelStand);
		BrewingManager.brewingStands.add(cancelStand);
		personalStands.add(cancelStand);

		PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook identityTpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(identityStand), (byte) -127, (byte) 0, (byte) 16, (byte) 192, (byte) 0, false);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(identityTpPacket);
		PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook potencyTpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(potencyStand), (byte) -64, (byte) 0, (byte) 16, (byte) 192, (byte) 0, false);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(potencyTpPacket);
		PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook durationTpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(durationStand), (byte) 64, (byte) 0, (byte) 16, (byte) 192, (byte) 0, false);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(durationTpPacket);
		PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook brewingTimeTpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(brewingTimeStand), (byte) 127, (byte) 0, (byte) 16, (byte) 192, (byte) 0, false);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(brewingTimeTpPacket);

		new BukkitRunnable() {
			@Override
			public void run() {
//				identityStand.teleport(identityStand.getLocation().clone().add(3.97, 0, 0.5));
//				identityStand.getLocation().setPitch(64);
//				potencyStand.teleport(potencyStand.getLocation().clone().add(2, 0, 0.5));
//				durationStand.teleport(durationStand.getLocation().clone().subtract(2, 0, -0.5));
//				brewingTimeStand.teleport(brewingTimeStand.getLocation().clone().subtract(3.97, 0, -0.5));

				identityStand.teleport(identityStand.getLocation().clone().subtract(3.97, 0, -0.5));
				identityStand.getLocation().setPitch(64);
				potencyStand.teleport(potencyStand.getLocation().clone().subtract(2, 0, -0.5));
				durationStand.teleport(durationStand.getLocation().clone().add(2, 0, 0.5));
				brewingTimeStand.teleport(brewingTimeStand.getLocation().clone().add(3.97, 0, 0.5));
			}
		}.runTaskLater(PitSim.INSTANCE, 10);

		new BukkitRunnable() {
			@Override
			public void run() {
				PacketPlayOutEntityEquipment identityEquipmentPacket = new PacketPlayOutEntityEquipment(getStandID(identityStand), 0, CraftItemStack.asNMSCopy(new ItemStack(Material.BARRIER)));
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(identityEquipmentPacket);
				PacketPlayOutEntityEquipment potencyEquipmentPacket = new PacketPlayOutEntityEquipment(getStandID(potencyStand), 0, CraftItemStack.asNMSCopy(new ItemStack(Material.BARRIER)));
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(potencyEquipmentPacket);
				PacketPlayOutEntityEquipment durationEquipmentPacket = new PacketPlayOutEntityEquipment(getStandID(durationStand), 0, CraftItemStack.asNMSCopy(new ItemStack(Material.BARRIER)));
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(durationEquipmentPacket);
				PacketPlayOutEntityEquipment brewingTimeEquipmentPacket = new PacketPlayOutEntityEquipment(getStandID(brewingTimeStand), 0, CraftItemStack.asNMSCopy(new ItemStack(Material.BARRIER)));
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(brewingTimeEquipmentPacket);
			}
		}.runTaskLater(PitSim.INSTANCE, 1);
	}

	public void hideButtons(Player player) {
		ArmorStand identityStand = identityStands.get(player);
		ArmorStand potencyStand = potencyStands.get(player);
		ArmorStand durationStand = durationStands.get(player);
		ArmorStand brewingTimeStand = brewingTimeStands.get(player);
		ArmorStand confirmStand = confirmStands.get(player);
		ArmorStand cancelStand = cancelStands.get(player);
		PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook identityTpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(identityStand), (byte) 127, (byte) 0, (byte) 16, (byte) 192, (byte) 0, false);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(identityTpPacket);
		PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook potencyTpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(potencyStand), (byte) 64, (byte) 0, (byte) 16, (byte) 192, (byte) 0, false);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(potencyTpPacket);
		PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook durationTpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(durationStand), (byte) -64, (byte) 0, (byte) 16, (byte) 192, (byte) 0, false);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(durationTpPacket);
		PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook brewingTimeTpPacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(brewingTimeStand), (byte) -127, (byte) 0, (byte) 16, (byte) 192, (byte) 0, false);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(brewingTimeTpPacket);

		new BukkitRunnable() {
			@Override
			public void run() {
				BrewingManager.brewingStands.remove(identityStand);
				identityStands.remove(player);
				personalStands.remove(identityStand);
				identityStand.remove();
				BrewingManager.brewingStands.remove(potencyStand);
				potencyStands.remove(player);
				personalStands.remove(potencyStand);
				potencyStand.remove();
				BrewingManager.brewingStands.remove(durationStand);
				durationStands.remove(player);
				personalStands.remove(durationStand);
				durationStand.remove();
				BrewingManager.brewingStands.remove(brewingTimeStand);
				brewingTimeStands.remove(player);
				personalStands.remove(brewingTimeStand);
				brewingTimeStand.remove();

				BrewingManager.brewingStands.remove(cancelStand);
				cancelStands.remove(player);
				personalStands.remove(cancelStand);
				cancelStand.remove();
				BrewingManager.brewingStands.remove(confirmStand);
				confirmStands.remove(player);
				personalStands.remove(confirmStand);
				confirmStand.remove();

				players.remove(player);
			}
		}.runTaskLater(PitSim.INSTANCE, 3);
	}

	public void onButtonPush(PlayerInteractAtEntityEvent event) {
		if(!players.contains(event.getPlayer())) return;

		if(!buttonPlayers.contains(event.getPlayer()) && event.getRightClicked().getUniqueId().equals(cancelStands.get(event.getPlayer()).getUniqueId())) {
			ArmorStand cancel = cancelStands.get(event.getPlayer());
			buttonPlayers.add(event.getPlayer());
			PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook moveDown = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(cancel), (byte) 0, (byte) -6.4, (byte) 0, (byte) 0, (byte) 0, false);
			((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(moveDown);
			Sounds.BUTTON.play(event.getPlayer());

			new BukkitRunnable() {
				@Override
				public void run() {
					PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook moveUp = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(cancel), (byte) 0, (byte) 6.4, (byte) 0, (byte) 0, (byte) 0, false);
					((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(moveUp);
				}
			}.runTaskLater(PitSim.INSTANCE, 5);
			new BukkitRunnable() {
				@Override
				public void run() {
					hideButtons(event.getPlayer());
					players.remove(event.getPlayer());
					buttonPlayers.remove(event.getPlayer());
					for(ItemStack itemStack : ingredients.get(event.getPlayer())) {
						if(itemStack != null) AUtil.giveItemSafely(event.getPlayer(), itemStack);
					}
					ingredients.remove(event.getPlayer());
					setText(event.getPlayer(), originalMessages);

				}
			}.runTaskLater(PitSim.INSTANCE, 10);

		}

		if(!buttonPlayers.contains(event.getPlayer()) && event.getRightClicked().getUniqueId().equals(confirmStands.get(event.getPlayer()).getUniqueId())) {
			ArmorStand confirm = confirmStands.get(event.getPlayer());
			buttonPlayers.add(event.getPlayer());
			PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook moveDown = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(confirm), (byte) 0, (byte) -6.4, (byte) 0, (byte) 0, (byte) 0, false);
			((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(moveDown);
			Sounds.BUTTON.play(event.getPlayer());

			new BukkitRunnable() {
				@Override
				public void run() {
					PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook moveUp = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getStandID(confirm), (byte) 0, (byte) 6.4, (byte) 0, (byte) 0, (byte) 0, false);
					((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(moveUp);
				}
			}.runTaskLater(PitSim.INSTANCE, 5);
			new BukkitRunnable() {
				@Override
				public void run() {
					for(ItemStack itemStack : ingredients.get(event.getPlayer())) {
						if(Misc.isAirOrNull(itemStack)) {
							setText(event.getPlayer(), new String[]{"&e&lBrewing Stand", "&cYou must fill in all", "&cthe Ingredient Slots", "&cto start brewing a", "&cpotion!"});

							new BukkitRunnable() {
								@Override
								public void run() {
									setItemText(event.getPlayer());
									buttonPlayers.remove(event.getPlayer());
								}
							}.runTaskLater(PitSim.INSTANCE, 40);
							return;
						}
					}

					hideButtons(event.getPlayer());
					players.remove(event.getPlayer());
					buttonPlayers.remove(event.getPlayer());
					BrewingSession session = new BrewingSession(event.getPlayer(), BrewingManager.getBrewingSlot(event.getPlayer()), null,
							BrewingIngredient.getIngredientFromItemStack(ingredients.get(event.getPlayer())[0]),
							BrewingIngredient.getIngredientFromItemStack(ingredients.get(event.getPlayer())[1]),
							BrewingIngredient.getIngredientFromItemStack(ingredients.get(event.getPlayer())[2]),
							BrewingIngredient.getIngredientFromItemStack(ingredients.get(event.getPlayer())[3]));

					BrewingManager.brewingSessions.add(session);
					ingredients.remove(event.getPlayer());
					setText(event.getPlayer(), originalMessages);

				}
			}.runTaskLater(PitSim.INSTANCE, 10);

		}

		if(event.getRightClicked().getUniqueId().equals(identityStands.get(event.getPlayer()).getUniqueId())) {
			ArmorStand identityStand = identityStands.get(event.getPlayer());

			ItemStack replace = whatToReplace(event.getPlayer(), 0);
			if(replace != null) {
				if(ingredients.get(event.getPlayer())[0] != null)
					AUtil.giveItemSafely(event.getPlayer(), ingredients.get(event.getPlayer())[0]);
				ingredients.get(event.getPlayer())[0] = replace;
				PacketPlayOutEntityEquipment identityEquipmentPacket = new PacketPlayOutEntityEquipment(getStandID(identityStand), 0, CraftItemStack.asNMSCopy(replace));
				((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(identityEquipmentPacket);
				Sounds.BOOSTER_REMIND.play(event.getPlayer());
			} else if(ingredients.get(event.getPlayer())[0] != null) {
				PacketPlayOutEntityEquipment identityEquipmentPacket = new PacketPlayOutEntityEquipment(getStandID(identityStand), 0, CraftItemStack.asNMSCopy(new ItemStack(Material.BARRIER)));
				((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(identityEquipmentPacket);
				AUtil.giveItemSafely(event.getPlayer(), ingredients.get(event.getPlayer())[0]);
				ingredients.get(event.getPlayer())[0] = null;
				Sounds.BOOSTER_REMIND.play(event.getPlayer());
			}
			setItemText(event.getPlayer());
		}

		if(event.getRightClicked().getUniqueId().equals(potencyStands.get(event.getPlayer()).getUniqueId())) {
			ArmorStand potencyStand = potencyStands.get(event.getPlayer());

			ItemStack replace = whatToReplace(event.getPlayer(), 1);
			if(replace != null) {
				if(ingredients.get(event.getPlayer())[1] != null)
					AUtil.giveItemSafely(event.getPlayer(), ingredients.get(event.getPlayer())[1]);
				ingredients.get(event.getPlayer())[1] = replace;
				PacketPlayOutEntityEquipment potencyEquipment = new PacketPlayOutEntityEquipment(getStandID(potencyStand), 0, CraftItemStack.asNMSCopy(replace));
				((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(potencyEquipment);
				Sounds.BOOSTER_REMIND.play(event.getPlayer());
			} else if(ingredients.get(event.getPlayer())[1] != null) {
				PacketPlayOutEntityEquipment potencyEquipment = new PacketPlayOutEntityEquipment(getStandID(potencyStand), 0, CraftItemStack.asNMSCopy(new ItemStack(Material.BARRIER)));
				((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(potencyEquipment);
				AUtil.giveItemSafely(event.getPlayer(), ingredients.get(event.getPlayer())[1]);
				ingredients.get(event.getPlayer())[1] = null;
				Sounds.BOOSTER_REMIND.play(event.getPlayer());
			}
			setItemText(event.getPlayer());
		}

		if(event.getRightClicked().getUniqueId().equals(durationStands.get(event.getPlayer()).getUniqueId())) {
			ArmorStand durationStand = durationStands.get(event.getPlayer());

			ItemStack replace = whatToReplace(event.getPlayer(), 2);
			if(replace != null) {
				if(ingredients.get(event.getPlayer())[2] != null)
					AUtil.giveItemSafely(event.getPlayer(), ingredients.get(event.getPlayer())[2]);
				ingredients.get(event.getPlayer())[2] = replace;
				PacketPlayOutEntityEquipment durationEquipment = new PacketPlayOutEntityEquipment(getStandID(durationStand), 0, CraftItemStack.asNMSCopy(replace));
				((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(durationEquipment);
				Sounds.BOOSTER_REMIND.play(event.getPlayer());
			} else if(ingredients.get(event.getPlayer())[2] != null) {
				PacketPlayOutEntityEquipment durationEquipment = new PacketPlayOutEntityEquipment(getStandID(durationStand), 0, CraftItemStack.asNMSCopy(new ItemStack(Material.BARRIER)));
				((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(durationEquipment);
				AUtil.giveItemSafely(event.getPlayer(), ingredients.get(event.getPlayer())[2]);
				ingredients.get(event.getPlayer())[2] = null;
				Sounds.BOOSTER_REMIND.play(event.getPlayer());
			}
			setItemText(event.getPlayer());
		}

		if(event.getRightClicked().getUniqueId().equals(brewingTimeStands.get(event.getPlayer()).getUniqueId())) {
			ArmorStand brewingTimeStand = brewingTimeStands.get(event.getPlayer());

			ItemStack replace = whatToReplace(event.getPlayer(), 3);
			if(replace != null) {
				if(ingredients.get(event.getPlayer())[3] != null)
					AUtil.giveItemSafely(event.getPlayer(), ingredients.get(event.getPlayer())[3]);
				ingredients.get(event.getPlayer())[3] = replace;
				PacketPlayOutEntityEquipment brewingTimeEquipment = new PacketPlayOutEntityEquipment(getStandID(brewingTimeStand), 0, CraftItemStack.asNMSCopy(replace));
				((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(brewingTimeEquipment);
				Sounds.BOOSTER_REMIND.play(event.getPlayer());
			} else if(ingredients.get(event.getPlayer())[3] != null) {
				PacketPlayOutEntityEquipment brewingTimeEquipment = new PacketPlayOutEntityEquipment(getStandID(brewingTimeStand), 0, CraftItemStack.asNMSCopy(new ItemStack(Material.BARRIER)));
				((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(brewingTimeEquipment);
				AUtil.giveItemSafely(event.getPlayer(), ingredients.get(event.getPlayer())[3]);
				ingredients.get(event.getPlayer())[3] = null;
				Sounds.BOOSTER_REMIND.play(event.getPlayer());
			}
			setItemText(event.getPlayer());
		}
	}

	public void onMove(PlayerMoveEvent event) {
		if(!players.contains(event.getPlayer())) return;
		if(event.getPlayer().getWorld() != MapManager.getDarkzone() || event.getPlayer().getLocation().distance(location) > 10) {
			hideButtons(event.getPlayer());
			players.remove(event.getPlayer());
			for(ItemStack itemStack : ingredients.get(event.getPlayer())) {
				if(itemStack != null) AUtil.giveItemSafely(event.getPlayer(), itemStack);
			}
			ingredients.remove(event.getPlayer());
		}
	}

	public void onQuit(PitQuitEvent event) {
		if(!players.contains(event.getPlayer())) return;
		hideButtons(event.getPlayer());
		players.remove(event.getPlayer());
		for(ItemStack itemStack : ingredients.get(event.getPlayer())) {
			if(itemStack != null) AUtil.giveItemSafely(event.getPlayer(), itemStack);
		}
		ingredients.remove(event.getPlayer());
	}

	public ItemStack whatToReplace(Player player, int index) {
		ItemStack itemStack = player.getItemInHand();
		if(BrewingIngredient.isIngredient(itemStack) && !BrewingIngredient.isSame(itemStack, ingredients.get(player)[index])) {
			if(itemStack.getAmount() > 1) {
				ItemStack newPlayerItem = itemStack.clone();
				newPlayerItem.setAmount(newPlayerItem.getAmount() - 1);
				player.setItemInHand(newPlayerItem);
			} else {
				player.setItemInHand(new ItemStack(Material.AIR));
			}
			ItemStack returnStack = itemStack.clone();
			returnStack.setAmount(1);
			return returnStack;
		} else return null;
	}

}
