package dev.kyro.pitsim.adarkzone;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.NameTaggable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PitNameTag {
	public NameTaggable taggableEntity;
	public NameTagType nameTagType;

	private List<LivingEntity> entities = new ArrayList<>();
	private ArmorStand armorStand;

	public PitNameTag(NameTaggable taggableEntity, NameTagType nameTagType) {
		this.taggableEntity = taggableEntity;
		this.nameTagType = nameTagType;
	}

	public PitNameTag addMob(RidingType ridingType) {
		entities.add(ridingType.createEntity(taggableEntity.getTaggableEntity().getLocation()));
		return this;
	}

	public void attach() {
		LivingEntity lowerEntity = taggableEntity.getTaggableEntity();
		for(LivingEntity nextEntity : entities) {
			lowerEntity.setPassenger(nextEntity);
			lowerEntity = nextEntity;
		}
		createArmorStand(taggableEntity.getTaggableEntity().getLocation());
		lowerEntity.setPassenger(armorStand);
		update();
	}

	public void update() {
		LivingEntity baseEntity = taggableEntity.getTaggableEntity();
		if(nameTagType == NameTagType.NAME) {
			setName(taggableEntity.getDisplayName());
		} else if(nameTagType == NameTagType.NAME_AND_HEALTH) {
			int maxHealth = (int) baseEntity.getMaxHealth();
			int length = (int) Math.min(Math.max(maxHealth - 20, 0) + Math.sqrt(maxHealth), 20);
			double percentFull = baseEntity.getHealth() / baseEntity.getMaxHealth();
			String healthBar = AUtil.createProgressBar("|", taggableEntity.getChatColor(), ChatColor.GRAY, length, percentFull);
			setName(taggableEntity.getDisplayName() + "&8 [" + healthBar + "&8]");
		}
	}

	public void remove() {
		for(LivingEntity entity : entities) entity.remove();
		armorStand.remove();
	}

	private void createArmorStand(Location location) {
		armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
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

		public LivingEntity createEntity(Location location) {
			LivingEntity livingEntity = null;
			switch(this) {
				case SMALL_MAGMA_CUBE:
					MagmaCube magmaCube = (MagmaCube) location.getWorld().spawnEntity(location, EntityType.MAGMA_CUBE);
					magmaCube.setSize(1);
					livingEntity = magmaCube;
					break;
				case BABY_RABBIT:
					Rabbit rabbit = (Rabbit) location.getWorld().spawnEntity(location, EntityType.RABBIT);
					rabbit.setBaby();
					livingEntity = rabbit;
			}
			livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false));
			livingEntity.setCustomNameVisible(false);
			livingEntity.setRemoveWhenFarAway(false);
			return livingEntity;
		}
	}

	public enum NameTagType {
		NAME,
		NAME_AND_HEALTH
	}
}
