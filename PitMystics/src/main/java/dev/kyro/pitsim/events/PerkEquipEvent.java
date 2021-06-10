package dev.kyro.pitsim.events;

import dev.kyro.pitsim.controllers.objects.PitPerk;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PerkEquipEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;
    private PitPerk perk;
    private Player player;
    private PitPerk replacedPerk;

    public PerkEquipEvent(PitPerk perk, Player player, PitPerk replacedPerk) {
        this.perk = perk;
        this.player = player;
        this.replacedPerk = replacedPerk;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }


    public PitPerk getPerk() {
        return perk;
    }

    public Player getPlayer() {
        return player;
    }

    public PitPerk getReplacedPerk() {
        return replacedPerk;
    }


//    public void setPerk(PitPerk perk) {
//        this.perk = perk;
//    }



}
