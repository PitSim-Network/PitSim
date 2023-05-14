package dev.kyro.pitsim.events;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.DarkzoneLeveling;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.aitems.misc.CorruptedFeather;
import dev.kyro.pitsim.aitems.misc.FunkyFeather;
import dev.kyro.pitsim.boosters.PvPBooster;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.KillModifier;
import dev.kyro.pitsim.enums.KillType;
import dev.kyro.pitsim.megastreaks.Prosperity;
import dev.kyro.pitsim.misc.PlayerItemLocation;
import dev.kyro.pitsim.misc.wrappers.WrapperPlayerInventory;
import dev.kyro.pitsim.upgrades.DivineIntervention;
import dev.kyro.pitsim.upgrades.HandOfGreed;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KillEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private WrapperEntityDamageEvent event;
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
	public double goldCap = 2000;
	public double goldReward = 20;
	public List<Double> xpMultipliers = new ArrayList<>();
	public List<Double> maxXPMultipliers = new ArrayList<>();
	public List<Double> goldMultipliers = new ArrayList<>();

	public double soulsLost;
	public List<Double> soulMultipliers = new ArrayList<>();

	private boolean shouldLoseItems = false;
	private WrapperPlayerInventory deadInventoryWrapper;

	private final Map<PlayerItemLocation, ItemInfo> deadVulnerableItems = new LinkedHashMap<>();

	public KillEvent(AttackEvent attackEvent, LivingEntity killer, LivingEntity dead, KillType killType, KillModifier... killModifiers) {
		this.attackEvent = attackEvent;
		this.killType = killType;
		if(attackEvent != null) {
			this.event = attackEvent.getWrapperEvent();
			this.killerEnchantMap = killer == attackEvent.getAttacker() ? attackEvent.getAttackerEnchantMap() : attackEvent.getDefenderEnchantMap();
			this.deadEnchantMap = killer == attackEvent.getAttacker() ? attackEvent.getDefenderEnchantMap() : attackEvent.getAttackerEnchantMap();
		}
		this.killer = killer;
		this.dead = dead;
		this.isKillerPlayer = killer instanceof Player;
		this.isDeadPlayer = dead instanceof Player;
		this.killerPlayer = isKillerPlayer() ? (Player) killer : null;
		this.deadPlayer = isDeadPlayer() ? (Player) dead : null;
		this.isKillerRealPlayer = PlayerManager.isRealPlayer(getKillerPlayer());
		this.isDeadRealPlayer = PlayerManager.isRealPlayer(getDeadPlayer());
		this.killModifiers = new ArrayList<>(Arrays.asList(killModifiers));

		Non defendingNon = NonManager.getNon(getDead());
		this.xpReward = defendingNon == null ? 5 : 20;

		if(isDeadRealPlayer) this.soulsLost = getBaseSouls(getDeadPitPlayer());

		if(isDeadPlayer) deadInventoryWrapper = new WrapperPlayerInventory(getDeadPlayer());
		checkLoseLives();
	}

	public int getFinalXp() {
		if(!isKillerRealPlayer || !isDeadPlayer) return 0;
		double xpReward = this.xpReward;
		int xpCap = this.xpCap;
		for(Double xpMultiplier : xpMultipliers) xpReward *= xpMultiplier;
		for(Double maxXPMultiplier : maxXPMultipliers) xpCap *= maxXPMultiplier;
		xpReward += bonusXpReward;

		double postXPCap = Math.min(xpReward, xpCap);
		double altarMultiplier = DarkzoneLeveling.getReductionMultiplier(getKillerPitPlayer());
		postXPCap *= altarMultiplier;

		return (int) Math.floor(postXPCap);
	}

	public double getFinalGold() {
		if(!isKillerRealPlayer || !isDeadPlayer) return 0;
		double goldReward = this.goldReward;
		for(Double goldMultiplier : goldMultipliers) goldReward *= goldMultiplier;

		double postGoldCap = Math.min(goldReward, goldCap);
		double altarMultiplier = DarkzoneLeveling.getReductionMultiplier(getKillerPitPlayer());
		postGoldCap *= altarMultiplier;
		if(getKillerPitPlayer().getMegastreak() instanceof Prosperity && getKillerPitPlayer().isOnMega())
			postGoldCap += HandOfGreed.getGoldIncrease(getKillerPlayer());

		return postGoldCap;
	}

	public static double getBaseSouls(PitPlayer deadPitPlayer) {
		return Math.max((1 / (Math.pow(Math.E, -0.0025 * (deadPitPlayer.taintedSouls - 1700)) + 1)) * 103 - 3, 0);
	}

	public int getFinalSouls() {
		if(!isKillerRealPlayer || !shouldLoseItems) return 0;
		double soulsLost = this.soulsLost;
		for(Double soulMultiplier : soulMultipliers) soulsLost *= soulMultiplier;
		return (int) Math.min(Math.ceil(soulsLost), getDeadPitPlayer().taintedSouls);
	}

	private void checkLoseLives() {
		if(!isDeadRealPlayer) return;

		if(PvPBooster.INSTANCE.isActive()) return;
		if(DivineIntervention.attemptDivine(getDeadPlayer())) return;

		if(PitSim.status.isOverworld()) {
			if(ItemFactory.getItem(FunkyFeather.class).useFeather(this)) return;
		} else {
			if(ItemFactory.getItem(CorruptedFeather.class).useCorruptedFeather(this)) return;
		}

		shouldLoseItems = true;
		for(Map.Entry<PlayerItemLocation, ItemStack> entry : deadInventoryWrapper.getItemMap().entrySet()) {
			ItemStack itemStack = entry.getValue();
			PitItem pitItem = ItemFactory.getItem(itemStack);
			if(!(pitItem instanceof TemporaryItem)) continue;

			TemporaryItem temporaryItem = (TemporaryItem) pitItem;
			if(temporaryItem.getTemporaryType() == TemporaryItem.TemporaryType.LOOSES_LIVES_ON_DEATH) {
				int currentLives = temporaryItem.getLives(itemStack);
				if(currentLives == 0) continue;
				deadVulnerableItems.put(entry.getKey(), new ItemInfo(pitItem, entry.getValue(), 1));
			} else if(temporaryItem.getTemporaryType() == TemporaryItem.TemporaryType.LOST_ON_DEATH) {
				deadVulnerableItems.put(entry.getKey(), new ItemInfo(pitItem, entry.getValue(), 0));
			} else {
				throw new RuntimeException();
			}
		}
	}

	public void updateItems() {
		if(shouldLoseItems) {
			int livesLost = 0;
			for(Map.Entry<PlayerItemLocation, ItemInfo> entry : deadVulnerableItems.entrySet()) {
				ItemStack itemStack = entry.getValue().itemStack;
				PitItem pitItem = ItemFactory.getItem(itemStack);
				assert pitItem != null;
				TemporaryItem temporaryItem = (TemporaryItem) pitItem;

				TemporaryItem.ItemDamageResult damageResult = temporaryItem.damage(itemStack, entry.getValue().livesToLose);
				livesLost += damageResult.getLivesLost();

				deadInventoryWrapper.putItem(entry.getKey(), damageResult.getItemStack());
				if(damageResult.wasRemoved()) {
					temporaryItem.onItemRemove(itemStack);
					PlayerManager.sendItemLossMessage(deadPlayer, itemStack);
				}
			}

			if(livesLost != 0) PlayerManager.sendLivesLostMessage(deadPlayer, livesLost);
		}

		if(deadInventoryWrapper != null) deadInventoryWrapper.setInventory();
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public boolean hasKillModifier(KillModifier killModifier) {
		return new ArrayList<>(killModifiers).contains(killModifier);
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

	public boolean isKillerRealPlayer() {
		return isKillerRealPlayer;
	}

	public boolean isDeadRealPlayer() {
		return isDeadRealPlayer;
	}

	public boolean hasKiller() {
		return killer != null;
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

	public WrapperEntityDamageEvent getWrapperEvent() {
		return event;
	}

	public AttackEvent getAttackEvent() {
		return attackEvent;
	}

	public KillType getKillType() {
		return killType;
	}

	public WrapperPlayerInventory getDeadInventoryWrapper() {
		return deadInventoryWrapper;
	}

	public boolean shouldLoseItems() {
		return shouldLoseItems;
	}

	public Map<PlayerItemLocation, ItemInfo> getVulnerableItems() {
		return deadVulnerableItems;
	}

	public void removeVulnerableItem(PlayerItemLocation itemLocation) {
		deadVulnerableItems.remove(itemLocation);
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
