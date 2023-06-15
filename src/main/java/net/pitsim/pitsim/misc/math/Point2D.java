package net.pitsim.pitsim.misc.math;

public class Point2D {
	private final double x;
	private final double z;

	public Point2D(double x, double z) {
		this.x = x;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public double getZ() {
		return z;
	}

	@Override
	public String toString() {
		return "(" + getX() + ", " + getZ() + ")";
	}
}
