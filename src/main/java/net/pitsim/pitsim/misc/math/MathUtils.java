package net.pitsim.pitsim.misc.math;

import java.util.ArrayList;
import java.util.List;

public class MathUtils {
	public static List<Point3D> getSphere(double offset, int number, double xRadius, double yRadius, double zRadius) {
		List<Point3D> locations = new ArrayList<>();
		for(int i = 0; i < number; i++) {
			double k = i + (offset % 1);
			double phi = Math.acos(1 - 2 * k / number);
			double theta = Math.PI * (1 + Math.sqrt(5)) * k;
			double x = Math.cos(theta) * Math.sin(phi) * xRadius;
			double y = Math.sin(theta) * Math.sin(phi) * yRadius;
			double z = Math.cos(phi) * zRadius;
			locations.add(new Point3D(x, -z, y));
		}
		return locations;
	}
}
