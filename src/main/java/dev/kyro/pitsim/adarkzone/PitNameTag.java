package dev.kyro.pitsim.adarkzone;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PitNameTag {
	public static List<PitNameTag> activeNameTags = new ArrayList<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(PitNameTag nameTag : activeNameTags) nameTag.update();
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	public PitMob pitMob;
	public NameTagType nameTagType;

	private final List<LivingEntity> entities = new ArrayList<>();
	private ArmorStand armorStand;

	private Location spawnLocation;

	public PitNameTag(PitMob pitMob, NameTagType nameTagType) {
		this.pitMob = pitMob;
		this.nameTagType = nameTagType;
		this.spawnLocation = pitMob.getMob().getLocation();
		this.spawnLocation.setY(255);
		activeNameTags.add(this);
	}

	public PitNameTag addMob(RidingType ridingType) {
		entities.add(ridingType.createEntity(spawnLocation));
		return this;
	}

	public void attach() {
		LivingEntity lowerEntity = pitMob.getMob();
		for(LivingEntity nextEntity : entities) {
			lowerEntity.setPassenger(nextEntity);
			lowerEntity = nextEntity;
		}
		createArmorStand();
		lowerEntity.setPassenger(armorStand);
		update();
	}

	public void update() {
		setName(getText(pitMob.getMob(), pitMob.getDisplayName(), nameTagType, pitMob.getChatColor()));
	}

	public void remove() {
		for(LivingEntity entity : entities) entity.remove();
		armorStand.remove();
		activeNameTags.remove(this);
	}

	private void createArmorStand() {
		armorStand = (ArmorStand) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ARMOR_STAND);
		armorStand.setGravity(false);
		armorStand.setVisible(true);
		armorStand.setCustomNameVisible(true);
		armorStand.setRemoveWhenFarAway(false);
		armorStand.setVisible(false);
		armorStand.setSmall(true);
		armorStand.setMarker(true);
	}

	private void setName(String text) {
		armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&', text));
	}

	public List<LivingEntity> getEntities() {
		return entities;
	}

	public ArmorStand getArmorStand() {
		return armorStand;
	}

	public enum RidingType {
		SMALL_MAGMA_CUBE,
		BABY_RABBIT;

		public LivingEntity createEntity(Location spawnLocation) {
			LivingEntity livingEntity = null;
			switch(this) {
				case SMALL_MAGMA_CUBE:
					MagmaCube magmaCube = (MagmaCube) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.MAGMA_CUBE);
					magmaCube.setSize(1);
					livingEntity = magmaCube;
					break;
				case BABY_RABBIT:
					Rabbit rabbit = (Rabbit) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.RABBIT);
					rabbit.setBaby();
					livingEntity = rabbit;
			}
			livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false));
			livingEntity.setCustomNameVisible(false);
			livingEntity.setRemoveWhenFarAway(false);
			return livingEntity;
		}
	}

	public static String getText(LivingEntity entity, String displayName, NameTagType nameTagType, ChatColor chatColor) {
		if(nameTagType == NameTagType.NAME) {
			return displayName;
		} else if(nameTagType == NameTagType.NAME_AND_HEALTH) {
			double health = entity.getHealth() * DarkzoneBalancing.SPOOFED_HEALTH_INCREASE;
			int maxHealth = (int) (entity.getMaxHealth() * DarkzoneBalancing.SPOOFED_HEALTH_INCREASE);
			int length = (int) Math.ceil(Math.min(Math.pow(maxHealth, 1 / 2.5), 20));
//			if(entity instanceof Enderman) System.out.println(length);
			double percentFull = health / maxHealth;
			String healthBar = AUtil.createProgressBar("|", ChatColor.RED, ChatColor.GRAY, length, percentFull);
			return displayName + "&8 [" + healthBar + "&8]";
		}
		throw new RuntimeException();
	}

	public enum NameTagType {
		NAME,
		NAME_AND_HEALTH
	}
}
