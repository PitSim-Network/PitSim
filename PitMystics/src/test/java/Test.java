import java.text.DecimalFormat;

public class Test {
	public static void main(String[] args) {
		System.out.println(-1/7);
	}

	public static String convert(String hex) {
		DecimalFormat decimalFormat = new DecimalFormat("0.00000");
		int decimal = Integer.parseInt(hex, 16);
		return decimalFormat.format(decimal / 256.0);
	}
}
