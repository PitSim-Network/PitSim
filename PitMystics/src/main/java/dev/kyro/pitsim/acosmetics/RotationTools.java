package dev.kyro.pitsim.acosmetics;

import org.bukkit.util.Vector;

public class RotationTools {

	public static void rotate(Vector vector, double yaw, double pitch, double roll) {
		yaw = Math.toRadians(yaw);
		pitch = Math.toRadians(pitch);
		roll = Math.toRadians(roll);
		double[][] rotationMatrix = {
				new double[] {cos(yaw) * cos(roll), cos(yaw) * sin(roll) * sin(pitch) - sin(yaw) * cos(pitch), cos(yaw) * sin(roll) * cos(pitch) + sin(yaw) * sin(pitch)},
				new double[] {sin(yaw) * cos(roll), sin(yaw) * sin(roll) * sin(pitch) + cos(yaw) * cos(pitch), sin(yaw) * sin(roll) * cos(pitch) - cos(yaw) * sin(pitch)},
				new double[] {-sin(roll), cos(roll) * sin(pitch), cos(roll) * cos(pitch)}
		};

		double[][] vectorMatrix = {
				new double[] {vector.getX()},
				new double[] {vector.getZ()},
				new double[] {vector.getY()}
		};

		double[][] finalVector = multiplyMatrices(rotationMatrix, vectorMatrix);
		vector.setX(finalVector[0][0]);
		vector.setY(finalVector[2][0]);
		vector.setZ(finalVector[1][0]);
	}

	public static double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) {
		double[][] result = new double[firstMatrix.length][secondMatrix[0].length];

		for (int row = 0; row < result.length; row++) {
			for (int col = 0; col < result[row].length; col++) {
				result[row][col] = multiplyMatricesCell(firstMatrix, secondMatrix, row, col);
			}
		}

		return result;
	}

	public static double multiplyMatricesCell(double[][] firstMatrix, double[][] secondMatrix, int row, int col) {
		double cell = 0;
		for (int i = 0; i < secondMatrix.length; i++) {
			cell += firstMatrix[row][i] * secondMatrix[i][col];
		}
		return cell;
	}

	private static double sin(double angle) {
		return Math.sin(angle);
	}

	private static double cos(double angle) {
		return Math.cos(angle);
	}
}
