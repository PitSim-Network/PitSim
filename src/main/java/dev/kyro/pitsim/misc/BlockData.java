package dev.kyro.pitsim.misc;

import org.bukkit.Material;

public class BlockData {
	public Material material;
	public byte data;

	public BlockData(Material material, byte data) {
		this.material = material;
		this.data = data;
	}
}
