package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.builders.AInventoryBuilder;
import dev.kyro.arcticapi.commands.ASubCommand;
import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.Misc;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ViewCommand extends ASubCommand {
    public OfflinePlayer offlinePlayer = null;
    public String uuidString = null;
    public ViewCommand(String executor) {
        super(executor);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;

        if(args.size() != 1) {
            AOutput.error(player, "&cUsage: /ps view <player>");
            return;
        }
        String playerName = args.get(0);
        AOutput.send(player, "&aLoading...");

        new BukkitRunnable() {
            @Override
            public void run() {

                for(Map.Entry<UUID, FileConfiguration> entry : APlayerData.getAllData().entrySet()) {
                    FileConfiguration data = entry.getValue();
                    if(!data.contains("name")) continue;
                    if(data.getString("name").equalsIgnoreCase(playerName)) {
                        uuidString = entry.getKey().toString();
                        offlinePlayer = Bukkit.getOfflinePlayer(uuidString);
                    }
                }

                if(offlinePlayer == null) {
                    AOutput.error(player, "&cPlayer not found!");
                    return;
                }


                try {
                    File inventoryFile = new File("world/playerdata/" + uuidString + ".dat");
                    NBTTagCompound nbt = NBTCompressedStreamTools.a(new FileInputStream(inventoryFile));
                    NBTTagList inventory = (NBTTagList) nbt.get("Inventory");
//                Inventory inv = new CraftInventoryCustom(player, inventory.size());
                    AInventoryBuilder builder = new AInventoryBuilder(player, 36, "Player Inventory");
                    for (int i = 0; i < inventory.size(); i++) {
                        NBTTagCompound compound = inventory.get(i);
                        if (!compound.isEmpty()) {
                            ItemStack itemStack = CraftItemStack.asBukkitCopy(net.minecraft.server.v1_8_R3.ItemStack.createStack(compound));

                            if(Misc.isAirOrNull(itemStack)) continue;
                            builder.getInventory().setItem(i, itemStack);
                        }
                    }
                    player.openInventory(builder.getInventory());

                } catch (IOException e) {
                    e.printStackTrace();
                }

                offlinePlayer = null;
                uuidString = null;


            }
        }.runTaskLaterAsynchronously(PitSim.INSTANCE, 1L);

    }
}
