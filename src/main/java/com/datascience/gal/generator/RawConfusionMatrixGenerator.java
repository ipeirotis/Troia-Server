package com.datascience.gal.generator;

import java.math.BigDecimal;
import java.util.Random;

public class RawConfusionMatrixGenerator {

	public final static int MAX_DIMENSION = 5;
	public final int SCALE = 2;

	private static double roundTwoDecimals(double d) {
		BigDecimal roundfinalPrice = new BigDecimal(d).setScale(2,BigDecimal.ROUND_HALF_UP);
		Double withPrecision= new Double(roundfinalPrice.doubleValue()); 
		return withPrecision;
	}
	
	public static void printOnScreen(Double[][] matrix) {
		System.out.println(print(matrix));
	}
	
	public static String print(Double[][] matrix) {
		StringBuffer ret = new StringBuffer();
		String sp = "  ";
		for (int i=0; i<matrix.length; i++) {
			for (int j=0; j<matrix.length; j++) {
				String toPrint = "["+i+"]["+j+"]="+matrix[i][j];
				if (i!=j)
					ret.append(sp+toPrint+sp);
				else
					ret.append("("+toPrint+")");
			}
			ret.append("\n");
		}
		return ret.toString();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int size = 4;
		Double m[][] = generate(size);
		print(m);
	}
	
	/**
	 * Generates random matrix n x n, diagonal elements are the highest in row.
	 * The sum of the elements in row is equal 1.0
	 * @param dimension (integer) Range of values: 2..n, where n< MAX_DIMENSION
	 * @return matrix
	 */
	public static Double[][] generate(int dimension) {
//		return new Double[][]{{0.7,0.2,0.1},{0.4, 0.5,0.1},{0.3,0.3,0.4}};
		double mixToNumerator = 0.5;
		
		Double[][] matrix = null;
		Random rand = new Random();
		if (dimension<=MAX_DIMENSION) {
			matrix = new Double[dimension][dimension];
			for (int i=0; i<dimension; i++) {
				matrix[i][i] = roundTwoDecimals((rand.nextDouble()+1.1)/2.2);
			}
			for (int i=0; i<dimension; i++) {
				double left = 1.0-matrix[i][i];
				for (int j=0; j<dimension; j++) {
					if (i!=j) {
						boolean isLastElementinRow = (j==dimension-1 || (i==dimension-1 && j==dimension-2));
						if (!isLastElementinRow) {
							double denominator = (1+mixToNumerator)/left;
							double value = roundTwoDecimals(rand.nextDouble()/denominator);
							left-=value;
							matrix[i][j] = value;
						}
						if (isLastElementinRow) {
							matrix[i][j] = roundTwoDecimals(left);
						} 
					}
				}
			}
 		}
		return matrix;
	}

}
