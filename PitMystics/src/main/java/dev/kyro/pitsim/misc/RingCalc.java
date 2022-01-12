package dev.kyro.pitsim.misc;

public class RingCalc {

//	public static void main(String[] args) {
//		for(int i = 0; i < 25; i++) {
//			System.out.println("Number " + i + ":");
//			XYCoords coords = getPosInRing(i);
//
//			List<String> lines = new ArrayList<>();
//			for(int j = -2; j < 3; j++) {
//				String line = "";
//				for(int k = -2; k < 3; k++) {
//					line += k == coords.x && j == coords.y ? "X" : "#";
//				}
//				lines.add(line);
//			}
//			for(int j = lines.size() - 1; j >= 0; j--) {
//				System.out.println(lines.get(j));
//			}
//		}
//	}

//	Spirals starting from (0, 0) and moving up first
	public static XYCoords getPosInRing(int position) {
		if(position == 0) return new XYCoords(0, 0);
		if(position == 1) return new XYCoords(0, 500);
		int x = 0;
		int y = 1;
		int currentPosition = 1;

		int spiralHalfLength = 1;
		while(true) {
			for(int i = 0; i < spiralHalfLength; i++) {
				x++;
				if(++currentPosition == position) return new XYCoords(x * 500, y * 500);
			}
			for(int i = 0; i < spiralHalfLength * 2; i++) {
				y--;
				if(++currentPosition == position) return new XYCoords(x * 500, y * 500);
			}
			for(int i = 0; i < spiralHalfLength * 2; i++) {
				x--;
				if(++currentPosition == position) return new XYCoords(x * 500, y * 500);
			}
			for(int i = 0; i < spiralHalfLength * 2; i++) {
				y++;
				if(++currentPosition == position) return new XYCoords(x * 500, y * 500);
			}
			for(int i = 0; i < spiralHalfLength - 1; i++) {
				x++;
				if(++currentPosition == position) return new XYCoords(x * 500, y * 500);
			}
			spiralHalfLength++;
			y++; x++;
			if(++currentPosition == position) return new XYCoords(x * 500, y * 500);
		}
	}

	public static class XYCoords {
		public int x;
		public int y;

		public XYCoords(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
