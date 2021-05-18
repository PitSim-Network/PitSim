package dev.kyro.pitsim.events;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;

public class ArrowHitBlockEvent extends BlockEvent {

    private static final HandlerList handlers = new HandlerList();

    private Arrow arrow;

    public ArrowHitBlockEvent(Arrow arrow, Block block) {
        super(block);
        this.arrow = arrow;
    }

    public Arrow getArrow() {
        return arrow;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }





}