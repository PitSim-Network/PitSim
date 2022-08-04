package dev.kyro.pitsim.misc;

import com.sk89q.worldedit.EditSession;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TempBlockHelper {
    public static List<EditSession> sessions = new ArrayList<>();

    public static List<Block> blocks = new ArrayList<>();

    public static List<Material> preState = new ArrayList<>();

    public static void init() {
        System.out.println();
    }

    public static void addBlockSession(Block block, Material originalMaterial){

        blocks.add(block);
        preState.add(originalMaterial);

    }

    public static void removeBlockSession(Block block){

        block.setType(preState.get(blocks.indexOf(block)));

        blocks.remove(block);
        preState.remove(Math.max(blocks.indexOf(block), 0));
    }

    public static void restoreSessions(){
        for(EditSession session : TempBlockHelper.sessions) session.undo(session);
        for(Block block : blocks) for(Material material : preState) block.setType(material);
    }


}
