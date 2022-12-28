import java.text.DecimalFormat;

public class Test {
	public static void main(String[] args) {
		for(int i = 0; i < 10; i++) {
			System.out.println(Math.random() * 1.5 / 2);
		}
	}

	public static String convert(String hex) {
		DecimalFormat decimalFormat = new DecimalFormat("0.00000");
		int decimal = Integer.parseInt(hex, 16);
		return decimalFormat.format(decimal / 256.0);
	}
}
