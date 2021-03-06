package rp.robotics.mapping;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import lejos.geom.Line;
import lejos.geom.Rectangle;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GridMapTest {

	/**
	 * Creates a grid map to match the training map as of 6/3/2013.
	 * 
	 * @return
	 */
	static GridMap createTestMap() {
		float height = 238;
		float width = 366;

		// junction numbers
		int xJunctions = 12;
		int yJunctions = 7;

		// position of 0,0 junction wrt to top left of map
		int xInset = 24;
		int yInset = 24;

		// length of edges between junctions.
		int junctionSeparation = 30;

		ArrayList<Line> lines = new ArrayList<Line>();

		// these are the walls for the world outline
		lines.add(new Line(0f, 0f, width, 0f));
		lines.add(new Line(width, 0f, width, height));
		lines.add(new Line(width, height, 0f, height));
		lines.add(new Line(0f, height, 0f, 0f));

		lines.add(new Line(75.0f, 133f, 100f, 133f));
		lines.add(new Line(75.0f, 193.0f, 100f, 193.0f));
		lines.add(new Line(100f, 133f, 100f, 193.0f));
		lines.add(new Line(75.0f, 133f, 75.0f, 193.0f));

		lines.addAll(LocalisationUtils.lineToBox(42f, 67f, 287f, 67f));
		lines.add(new Line(287f, 0, 287f, 67f));
		lines.add(new Line(257f, 0, 257f, 67f));

		lines.addAll(LocalisationUtils.lineToBox(135f, 129f, 255f, 129f));
		lines.addAll(LocalisationUtils.lineToBox(135f, 129f, 135f, height));

		lines.addAll(LocalisationUtils.lineToBox(194f, 191f, 254f, 191f));
		lines.addAll(LocalisationUtils.lineToBox(194f, 191f, 194f, height));

		lines.add(new Line(width - 42f, 99f, width, 99f));
		lines.add(new Line(width - 42f, 159f, width, 159f));
		lines.add(new Line(width - 42f, 99f, width - 42f, 159f));
		Line[] lineArray = new Line[lines.size()];
		lines.toArray(lineArray);

		return new GridMap(xJunctions, yJunctions, xInset, yInset,
				junctionSeparation, lineArray, new Rectangle(0, 0, width,
						height));
	}

	@Test
	public void testMapTest() {

		GridMap map = createTestMap();
		int width = map.getGridWidth();
		int height = map.getGridHeight();

		HashSet<Point> blocked = new HashSet<Point>();
		blocked.add(new Point(8, 0));
		blocked.add(new Point(8, 1));
		blocked.add(new Point(10, 3));
		blocked.add(new Point(11, 3));
		blocked.add(new Point(10, 4));
		blocked.add(new Point(11, 4));
		blocked.add(new Point(2, 4));
		blocked.add(new Point(2, 5));

		HashMap<Point, Point> invalid = new HashMap<Point, Point>();
		invalid.put(new Point(1, 1), new Point(1, 2));
		invalid.put(new Point(2, 1), new Point(2, 2));
		invalid.put(new Point(3, 1), new Point(3, 2));
		invalid.put(new Point(4, 1), new Point(4, 2));
		invalid.put(new Point(5, 1), new Point(5, 2));
		invalid.put(new Point(6, 1), new Point(6, 2));
		invalid.put(new Point(7, 1), new Point(7, 2));
		invalid.put(new Point(4, 3), new Point(4, 4));
		invalid.put(new Point(5, 3), new Point(5, 4));
		invalid.put(new Point(6, 3), new Point(6, 4));
		invalid.put(new Point(7, 3), new Point(7, 4));

		invalid.put(new Point(3, 4), new Point(4, 4));
		invalid.put(new Point(3, 5), new Point(4, 5));
		invalid.put(new Point(3, 6), new Point(4, 6));

		invalid.put(new Point(5, 6), new Point(6, 6));
		invalid.put(new Point(6, 5), new Point(6, 6));
		invalid.put(new Point(7, 5), new Point(7, 6));

		for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {

				Point from = new Point(x, y);

				if (blocked.contains(from)) {

					// an in place transition should be fine
					Assert.assertFalse(map.isValidTransition(from.x, from.y,
							from.x, from.y));
				} else {
					if (x > 0) {

						Point to = new Point(x - 1, y);

						if (blocked.contains(to)
								|| !isManuallyApproved(from, to, invalid)) {
							Assert.assertFalse(map.isValidTransition(from.x,
									from.y, to.x, to.y));
						} else {
							Assert.assertTrue(map.isValidTransition(from.x,
									from.y, to.x, to.y));
						}
					}

					if (x < width - 1) {

						Point to = new Point(x + 1, y);

						if (blocked.contains(to)
								|| !isManuallyApproved(from, to, invalid)) {
							Assert.assertFalse(map.isValidTransition(from.x,
									from.y, to.x, to.y));
						} else {
							Assert.assertTrue(map.isValidTransition(from.x,
									from.y, to.x, to.y));
						}
					}

					if (y > 0) {

						Point to = new Point(x, y - 1);

						if (blocked.contains(to)
								|| !isManuallyApproved(from, to, invalid)) {
							Assert.assertFalse(map.isValidTransition(from.x,
									from.y, to.x, to.y));
						} else {
							Assert.assertTrue(map.isValidTransition(from.x,
									from.y, to.x, to.y));
						}
					}

					if (y < height - 1) {

						Point to = new Point(x, y + 1);

						if (blocked.contains(to)
								|| !isManuallyApproved(from, to, invalid)) {
							Assert.assertFalse(map.isValidTransition(from.x,
									from.y, to.x, to.y));
						} else {
							Assert.assertTrue(map.isValidTransition(from.x,
									from.y, to.x, to.y), "from " + from
									+ " to " + to);
						}
					}
				}
			}
		}

	}

	private boolean isManuallyApproved(Point _from, Point _to,
			HashMap<Point, Point> _invalid) {

		Point to = _invalid.get(_from);
		if (to != null && to.equals(_to)) {
			return false;
		}

		Point from = _invalid.get(_to);
		if (from != null && from.equals(_from)) {
			return false;
		}

		return true;
	}

	@Test
	public void rectangularMapTest() {
		int width = 10;
		int height = 10;

		GridMap map = LocalisationUtils.createRectangularGridMap(width, height,
				30);

		for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {

				int toX = x;
				int toY = y;

				// an in place transition should be fine
				Assert.assertTrue(map.isValidTransition(x, y, toX, toY));

				if (x > 0) {
					toX = x - 1;
					Assert.assertTrue(map.isValidTransition(x, y, toX, toY));
				}

				if (x < width - 1) {
					toX = x + 1;
					Assert.assertTrue(map.isValidTransition(x, y, toX, toY));
				}

				if (y > 0) {
					toY = y - 1;
					Assert.assertTrue(map.isValidTransition(x, y, toX, toY));
				}

				if (y < height - 1) {
					toY = y + 1;
					Assert.assertTrue(map.isValidTransition(x, y, toX, toY));
				}

			}
		}
	}

	@Test
	private void rangeToObstaceTest() {

		float sep = 30f;
		float target = sep / 2f;
		GridMap map = LocalisationUtils.createRectangularGridMap(1, 1, sep);
		Assert.assertEquals(
				map.rangeToObstacleFromGridPoint(0, 0, Heading.PLUS_X), target,
				0f);
		Assert.assertEquals(
				map.rangeToObstacleFromGridPoint(0, 0, Heading.PLUS_Y), target,
				0f);
		Assert.assertEquals(
				map.rangeToObstacleFromGridPoint(0, 0, Heading.MINUS_X),
				target, 0f);
		Assert.assertEquals(
				map.rangeToObstacleFromGridPoint(0, 0, Heading.MINUS_Y),
				target, 0f);
		
		map = LocalisationUtils.create2014Map1();

		Assert.assertEquals(
				map.rangeToObstacleFromGridPoint(0, 0, Heading.PLUS_X),
				map.getBoundingRect().width - map.getXStart(), 0f);
			
		Assert.assertEquals(
				map.rangeToObstacleFromGridPoint(0, 0, Heading.PLUS_Y),
				map.getBoundingRect().height - map.getYStart(), 0f);
		
		Assert.assertEquals(
				map.rangeToObstacleFromGridPoint(0, 0, Heading.MINUS_X),
				map.getXStart(), 0f);
			
		Assert.assertEquals(
				map.rangeToObstacleFromGridPoint(0, 0, Heading.MINUS_Y),
				map.getYStart(), 0f);
		
	}

}
