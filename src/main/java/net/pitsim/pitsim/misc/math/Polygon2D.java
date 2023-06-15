package net.pitsim.pitsim.misc.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Polygon2D {
	private final List<Point2D> points;

	public Polygon2D(Point2D... points) {
		this.points = new ArrayList<>(Arrays.asList(points));
	}

	/**
	 * Return true if the given point is contained inside the boundary.
	 * See: http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
	 * @param test The point to check
	 * @return true if the point is inside the boundary, false otherwise
	 */
	public boolean contains(Point2D test) {
		int i;
		int j;
		boolean result = false;
		for(i = 0, j = points.size() - 1; i < points.size(); j = i++) {
			if((points.get(i).getZ() > test.getZ()) != (points.get(j).getZ() > test.getZ()) &&
					(test.getX() < (points.get(j).getX() - points.get(i).getX()) * (test.getZ() - points.get(i).getZ()) / (points.get(j).getZ() - points.get(i).getZ()) + points.get(i).getX())) {
				result = !result;
			}
		}
		return result;
	}
}
