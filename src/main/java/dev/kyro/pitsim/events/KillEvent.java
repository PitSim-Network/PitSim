package dev.kyro.pitsim.events;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.aitems.misc.CorruptedFeather;
import dev.kyro.pitsim.aitems.misc.FunkyFeather;
import dev.kyro.pitsim.controllers.BoosterManager;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.KillModifier;
import dev.kyro.pitsim.enums.KillType;
import dev.kyro.pitsim.misc.wrappers.PlayerInventoryWrapper;
import dev.kyro.pitsim.misc.wrappers.PlayerItemLocation;
import dev.kyro.pitsim.upgrades.DivineIntervention;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KillEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private EntityDamageByEntityEvent event;
	private final AttackEvent attackEvent;
	private final KillType killType;

	private final LivingEntity killer;
	private final LivingEntity dead;
	private final boolean isKillerPlayer;
	private final boolean isDeadPlayer;
	private final boolean isKillerRealPlayer;
	private final boolean isDeadRealPlayer;
	private final Player killerPlayer;
	private final Player deadPlayer;
	private PitPlayer killerPitPlayer;
	private PitPlayer deadPitPlayer;
	private Map<PitEnchant, Integer> killerEnchantMap;
	private Map<PitEnchant, Integer> deadEnchantMap;
	private final List<KillModifier> killModifiers;

	public int xpReward;
	public int bonusXpReward;
	public int xpCap = 50;
	public double goldReward = 20;
	public List<Double> xpMultipliers = new ArrayList<>();
	public List<Double> maxXPMultipliers = new ArrayList<>();
	public List<Double> goldMultipliers = new ArrayList<>();

	public double soulsLost;
	public List<Double> soulMultipliers = new ArrayList<>();

	public boolean isLuckyKill = false;

	private boolean shouldLoseItems = false;
	private PlayerInventoryWrapper deadInventoryWrapper;
	private final Map<PlayerItemLocation, ItemInfo> deadVulnerableItems = new HashMap<>();

	public KillEvent(AttackEvent attackEvent, LivingEntity killer, LivingEntity dead, KillType killType, KillModifier... killModifiers) {
		this.attackEvent = attackEvent;
		this.killType = killType;
		if(attackEvent != null) {
			this.event = attackEvent.getEvent();
			this.killerEnchantMap = killer == attackEvent.getAttacker() ? attackEvent.getAttackerEnchantMap() : attackEvent.getDefenderEnchantMap();
			this.deadEnchantMap = killer == attackEvent.getAttacker() ? attackEvent.getDefenderEnchantMap() : attackEvent.getAttackerEnchantMap();
		}
		this.killer = killer;
		this.dead = dead;
		this.isKillerPlayer = killer instanceof Player;
		this.isDeadPlayer = dead instanceof Player;
		this.isKillerRealPlayer = PlayerManager.isRealPlayer(getKillerPlayer());
		this.isDeadRealPlayer = PlayerManager.isRealPlayer(getDeadPlayer());
		this.killerPlayer = isKillerPlayer() ? (Player) killer : null;
		this.deadPlayer = isDeadPlayer() ? (Player) dead : null;
		this.killModifiers = new ArrayList<>(Arrays.asList(killModifiers));

		Non defendingNon = NonManager.getNon(getDead());
		this.xpReward = defendingNon == null ? 5 : 20;

		if(isDeadRealPlayer) this.soulsLost = getBaseSouls(getDeadPitPlayer());

		checkLoseLives();
	}

	public boolean hasKillModifier(KillModifier killModifier) {
		return Arrays.asList(killModifiers).contains(killModifier);
	}

	private void checkLoseLives() {
		if(!isDeadRealPlayer) return;

		if(BoosterManager.getBooster("pvp").minutes > 0) return;
		if(DivineIntervention.attemptDivine(getDeadPlayer())) return;

		if(PitSim.status.isOverworld()) {
			if(ItemFactory.getItem(FunkyFeather.class).useFeather(killer, getDeadPlayer())) return;
		} else {
			if(ItemFactory.getItem(CorruptedFeather.class).useCorruptedFeather(killer, getDeadPlayer())) return;
		}

		shouldLoseItems = true;
		deadInventoryWrapper = new PlayerInventoryWrapper(getDeadPlayer());
		for(Map.Entry<PlayerItemLocation, ItemStack> entry : deadInventoryWrapper.getItemMap().entrySet()) {
			ItemStack itemStack = entry.getValue();
			PitItem pitItem = ItemFactory.getItem(itemStack);
			if(!(pitItem instanceof TemporaryItem)) continue;
			deadVulnerableItems.put(entry.getKey(), new ItemInfo(pitItem, entry.getValue(), 1));
		}
	}

	public int getFinalXp() {
		double xpReward = this.xpReward;
		int xpCap = this.xpCap;
		for(Double xpMultiplier : xpMultipliers) xpReward *= xpMultiplier;
		for(Double maxXPMultiplier : maxXPMultipliers) xpCap *= maxXPMultiplier;
		xpReward += bonusXpReward;

		if(!(getDead() instanceof Player)) return 0;
		else if(xpReward > xpCap) return xpCap;
		else return (int) xpReward;
	}

	public double getFinalGold() {
		double goldReward = this.goldReward;
		for(Double goldMultiplier : goldMultipliers) goldReward *= goldMultiplier;
		if(!(getDead() instanceof Player)) return 0;
		else return Math.min(goldReward, 2000);
	}

	public static double getBaseSouls(PitPlayer deadPitPlayer) {
		return Math.max((1 / (Math.pow(Math.E, -0.002 * (deadPitPlayer.taintedSouls - 1200)) + 1)) * 110 - 10, 0);
	}

	public int getFinalSouls() {
		for(Double soulMultiplier : soulMultipliers) soulsLost *= soulMultiplier;
		return (int) Math.min(Math.ceil(soulsLost), getDeadPitPlayer().taintedSouls);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public int getKillerEnchantLevel(PitEnchant pitEnchant) {
		if(killerEnchantMap == null) return 0;
		return killerEnchantMap.getOrDefault(pitEnchant, 0);
	}

	public int getDeadEnchantLevel(PitEnchant pitEnchant) {
		if(deadEnchantMap == null) return 0;
		return deadEnchantMap.getOrDefault(pitEnchant, 0);
	}

	public Map<PitEnchant, Integer> getKillerEnchantMap() {
		return killerEnchantMap;
	}

	public Map<PitEnchant, Integer> getDeadEnchantMap() {
		return deadEnchantMap;
	}

	public LivingEntity getKiller() {
		return killer;
	}

	public LivingEntity getDead() {
		return dead;
	}

	public boolean isKillerPlayer() {
		return isKillerPlayer;
	}

	public boolean isDeadPlayer() {
		return isDeadPlayer;
	}

	public Player getKillerPlayer() {
		return killerPlayer;
	}

	public Player getDeadPlayer() {
		return deadPlayer;
	}

	public PitPlayer getKillerPitPlayer() {
		if(killerPitPlayer == null && isKillerPlayer) killerPitPlayer = PitPlayer.getPitPlayer(killerPlayer);
		return killerPitPlayer;
	}

	public PitPlayer getDeadPitPlayer() {
		if(deadPitPlayer == null && isDeadPlayer) deadPitPlayer = PitPlayer.getPitPlayer(deadPlayer);
		return deadPitPlayer;
	}

	public EntityDamageByEntityEvent getEvent() {
		return event;
	}

	public AttackEvent getAttackEvent() {
		return attackEvent;
	}

	public KillType getKillType() {
		return killType;
	}

	public Map<PlayerItemLocation, ItemInfo> getVulnerableItems() {
		return deadVulnerableItems;
	}

	public void removeVulnerableItem(PlayerItemLocation itemLocation) {
		deadVulnerableItems.remove(itemLocation);
	}

	public void updatePlayerItems() {
		for(Map.Entry<PlayerItemLocation, ItemInfo> entry : deadVulnerableItems.entrySet()) {
			ItemStack itemStack = entry.getValue().itemStack;
			PitItem pitItem = ItemFactory.getItem(itemStack);
			assert pitItem != null;
			TemporaryItem temporaryItem = (TemporaryItem) pitItem;
			itemStack = temporaryItem.damage(itemStack, entry.getValue().livesToLose);
			deadInventoryWrapper.putItem(entry.getKey(), itemStack);
		}
		deadInventoryWrapper.setInventory();
	}

	public static class ItemInfo {
		public PitItem pitItem;
		public ItemStack itemStack;
		public int livesToLose;

		public ItemInfo(PitItem pitItem, ItemStack itemStack, int livesToLose) {
			this.pitItem = pitItem;
			this.itemStack = itemStack;
			this.livesToLose = livesToLose;
		}
	}
}
