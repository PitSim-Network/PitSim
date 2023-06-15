package net.pitsim.pitsim.adarkzone.progression;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.adarkzone.notdarkzone.UnlockState;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class SkillBranch implements Listener {
	public static int nextIndex = 0;
	public int index;

	public MajorProgressionUnlock firstUnlock;
	public MajorProgressionUnlock lastUnlock;
	public MajorProgressionUnlock firstPathUnlock;
	public MajorProgressionUnlock secondPathUnlock;
	public Path firstPath;
	public Path secondPath;

	public SkillBranch() {
		this.index = nextIndex++;
		this.firstUnlock = createFirstUnlock();
		this.firstUnlock.position = MajorUnlockPosition.FIRST;
		this.lastUnlock = createLastUnlock();
		this.lastUnlock.position = MajorUnlockPosition.LAST;
		this.firstPathUnlock = createFirstPathUnlock();
		this.firstPathUnlock.position = MajorUnlockPosition.FIRST_PATH;
		this.secondPathUnlock = createSecondPathUnlock();
		this.secondPathUnlock.position = MajorUnlockPosition.SECOND_PATH;
		this.firstPath = createFirstPath();
		this.firstPath.position = PathPosition.FIRST_PATH;
		this.secondPath = createSecondPath();
		this.secondPath.position = PathPosition.SECOND_PATH;

		Bukkit.getPluginManager().registerEvents(this, PitSim.INSTANCE);
	}

	public abstract String getDisplayName();
	public abstract String getRefName();
	public abstract ItemStack getBaseDisplayStack();

	public abstract MajorProgressionUnlock createFirstUnlock();
	public abstract MajorProgressionUnlock createLastUnlock();
	public abstract MajorProgressionUnlock createFirstPathUnlock();
	public abstract MajorProgressionUnlock createSecondPathUnlock();
	public abstract Path createFirstPath();
	public abstract Path createSecondPath();

//	This is for the main gui
	public ItemStack getMainDisplayStack(PitPlayer pitPlayer, MainProgressionUnlock unlock, UnlockState unlockState) {
		int cost = ProgressionManager.getUnlockCost(pitPlayer, unlock);
		ItemStack baseStack = getBaseDisplayStack();
		ALoreBuilder loreBuilder = new ALoreBuilder();
		if(getBaseDisplayStack().getItemMeta().hasLore()) loreBuilder.addLore(getBaseDisplayStack().getItemMeta().getLore()).addLore("");

		ProgressionManager.addPurchaseCostLore(unlock, loreBuilder, unlockState, pitPlayer.taintedSouls, cost, false);
		if(unlockState == UnlockState.UNLOCKED) Misc.addEnchantGlint(baseStack);

		return new AItemStackBuilder(baseStack)
				.setName(getDisplayName())
				.setLore(loreBuilder)
				.getItemStack();
	}

	public enum MajorUnlockPosition {
		FIRST,
		LAST,
		FIRST_PATH,
		SECOND_PATH
	}

	public enum PathPosition {
		FIRST_PATH,
		SECOND_PATH
	}

	public abstract class MajorProgressionUnlock {
		public SkillBranch skillBranch;
		public MajorUnlockPosition position;

		public abstract String getDisplayName();
		public abstract String getRefName();
		public abstract ItemStack getBaseDisplayStack();

		public MajorProgressionUnlock() {
			this.skillBranch = SkillBranch.this;
		}

		public ItemStack getDisplayStack(PitPlayer pitPlayer) {
			UnlockState unlockState = ProgressionManager.getUnlockState(pitPlayer, this);
			ItemStack baseStack = getBaseDisplayStack();
			ALoreBuilder loreBuilder = new ALoreBuilder();
			if(getBaseDisplayStack().getItemMeta().hasLore()) loreBuilder.addLore(getBaseDisplayStack().getItemMeta().getLore()).addLore("");

			ProgressionManager.addPurchaseCostLore(MajorProgressionUnlock.this, loreBuilder,
					unlockState, pitPlayer.taintedSouls, ProgressionManager.getInitialSoulCost(this), true);
			if(unlockState == UnlockState.UNLOCKED) Misc.addEnchantGlint(baseStack);

			return new AItemStackBuilder(baseStack)
					.setName(unlockState.chatColor + getDisplayName())
					.setLore(loreBuilder)
					.getItemStack();
		}

		public Path getAssociatedPath() {
			if(this == firstPathUnlock) return firstPath;
			if(this == secondPathUnlock) return secondPath;
			throw new RuntimeException();
		}
	}

	public abstract class Path {
		public SkillBranch skillBranch;
		public PathPosition position;

		public List<EffectData> effectData = new ArrayList<>();

		public Path() {
			this.skillBranch = SkillBranch.this;
			addEffects();
		}

		public abstract String getDisplayName();
		public abstract String getRefName();
		public abstract void addEffects();

		public void addEffect(EffectData data) {
			effectData.add(data);
		}

		public ItemStack getDisplayStack(PitPlayer pitPlayer, int level) {
			UnlockState unlockState = ProgressionManager.getUnlockState(pitPlayer, this, level);
			ItemStack baseStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) unlockState.data);
			ALoreBuilder loreBuilder = new ALoreBuilder();

			DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");
			for(EffectData data : effectData) {
				double value = data.values[level - 1];
				if(value == 0) continue;
				loreBuilder.addLore(data.formatting.replaceAll("%value%", decimalFormat.format(value)));
			}
			loreBuilder.addLore("");

			ProgressionManager.addPurchaseCostLore(Path.this, loreBuilder, unlockState, pitPlayer.taintedSouls,
					ProgressionManager.getInitialSoulCost(this, level), true);
			if(unlockState == UnlockState.UNLOCKED) Misc.addEnchantGlint(baseStack);

			return new AItemStackBuilder(baseStack)
					.setName(unlockState.chatColor + "Path Unlock")
					.setLore(loreBuilder)
					.getItemStack();
		}

		public MajorProgressionUnlock getAssociatedUnlock() {
			if(this == firstPath) return firstPathUnlock;
			if(this == secondPath) return secondPathUnlock;
			throw new RuntimeException();
		}

		public class EffectData {
			public String refName;
			public String formatting;
			public Double[] values = new Double[6];

			public EffectData(String refName, String formatting, double level1, double level2, double level3, double level4, double level5, double level6) {
				this.refName = refName;
				this.formatting = formatting;
				this.values[0] = level1;
				this.values[1] = level2;
				this.values[2] = level3;
				this.values[3] = level4;
				this.values[4] = level5;
				this.values[5] = level6;
			}
		}
	}
}
