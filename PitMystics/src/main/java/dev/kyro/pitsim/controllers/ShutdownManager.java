package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class ShutdownManager {

    public static int minutes = 0;
    public static int seconds = 0;
    public static int counter = 0;
    public static boolean enderchestDisabled = false;
    public static boolean isShuttingDown = false;


    public static void initiateShutdown(int minute) {
        isShuttingDown = true;
        minutes = minute;

        new BukkitRunnable() {
            @Override
            public void run() {
                counter++;
                if(counter >= 20) {
                    counter = 0;
                    if(seconds > 0) {
                        seconds--;
                    } else {
                        if(minutes > 0) {
                            minutes--;
                            seconds = 60;
                        } else {
                            execute();
                        }
                    }
                }

                if(seconds == 0 && counter == 0) {
                    if(minutes == 1) {
                        disableEnderChest();
                    }
                    Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "SERVER RESTARTING DOWN IN "
                            + minutes + " MINUTES!");
                }
            }
        }.runTaskTimer(PitSim.INSTANCE, 1, 1);
    }

    public static void execute() {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        String command = "restart";
		Bukkit.dispatchCommand(console, command);
    }

    public static void disableEnderChest() {
        enderchestDisabled = true;
    }
 }
