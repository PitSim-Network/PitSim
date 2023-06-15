package net.pitsim.spigot.misc.particles;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CubeParticle {
	public Player displayPlayer;
	public Location point1;
	public Location point2;
	public double stepSize;

	public CubeParticle(Player displayPlayer, Location point1, Location point2, double stepSize) {
		this.displayPlayer = displayPlayer;
		this.point1 = point1;
		this.point2 = point2;
		this.stepSize = stepSize;

		draw();
	}

	public void draw() {
		double x1 = point1.getX();
		double y1 = point1.getY();
		double z1 = point1.getZ();

		double x2 = point2.getX();
		double y2 = point2.getY();
		double z2 = point2.getZ();

		Location loc1 = new Location(point1.getWorld(), x1, y2, z1).add(0, 1, 0);
		Location loc2 = new Location(point1.getWorld(), x1, y2, z2).add(0, 1, 1);
		Location loc3 = new Location(point1.getWorld(), x1, y1, z1).add(0, 0, 0);
		Location loc4 = new Location(point1.getWorld(), x1, y1, z2).add(0, 0, 1);
		Location loc5 = new Location(point1.getWorld(), x2, y2, z1).add(1, 1, 0);
		Location loc6 = new Location(point1.getWorld(), x2, y1, z2).add(1, 0, 1);
		Location loc7 = new Location(point1.getWorld(), x2, y1, z1).add(1, 0, 0);
		Location loc8 = new Location(point1.getWorld(), x2, y2, z2).add(1, 1, 1);

		double distance = stepSize;

//		Vertical
		new LineParticle(displayPlayer, loc1, loc3, distance);
		new LineParticle(displayPlayer, loc2, loc4, distance);
		new LineParticle(displayPlayer, loc5, loc7, distance);
		new LineParticle(displayPlayer, loc6, loc8, distance);

//		Top
		new LineParticle(displayPlayer, loc1, loc5, distance);
		new LineParticle(displayPlayer, loc1, loc2, distance);
		new LineParticle(displayPlayer, loc8, loc5, distance);
		new LineParticle(displayPlayer, loc8, loc2, distance);

//		Bottom
		new LineParticle(displayPlayer, loc3, loc7, distance);
		new LineParticle(displayPlayer, loc3, loc4, distance);
		new LineParticle(displayPlayer, loc6, loc7, distance);
		new LineParticle(displayPlayer, loc6, loc4, distance);
	}
}
