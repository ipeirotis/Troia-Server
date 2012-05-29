package com.datascience.gal.generator;

import java.math.BigDecimal;
import java.util.Random;

public class RawConfusionMatrixGenerator {

	public final static int MAX_DIMENSION = 100000;
	public final int SCALE = 2;

	private final static int MAX_COUNT_OF_WORKERS = 3000;
	private final static int MAX_COUNT_OF_CATEGORIES = 10;
	private final static int MAX_COUNT_OF_OBJECTS = 3000;
	/**
	 * @param d
	 * @return
	 */
	private static double roundTwoDecimals(double d) {
		BigDecimal roundfinalPrice = new BigDecimal(d).setScale(2,BigDecimal.ROUND_HALF_UP);
		Double withPrecision= new Double(roundfinalPrice.doubleValue()); 
		return withPrecision;
	}
	
	/**
	 * @param matrix
	 */
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
		Double m[][] = generateCM(size);
		print(m);
	}
	
	/**
	 * Confusion Matrix Generator
	 * Generates random matrix n x n, diagonal elements are the highest in row.
	 * The sum of the elements in row is equal 1.0
	 * @param dimension (integer) Range of values: 2..n, where n< MAX_DIMENSION
	 * @return matrix
	 */
	public static Double[][] generateCM(int dimension) {
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
	
	/**
	 * Generates Answers Matrix
	 * @param wc - count of workers
	 * @param oc - count of objects
	 * @param cc - count of categories
	 * @return
	 */
	public static int[][] generateAM(int wc, int oc, int cc) {
		int [][] ret = null;
		if (wc>1 && cc>1 && oc>1 && wc<=MAX_COUNT_OF_WORKERS && cc<=MAX_COUNT_OF_CATEGORIES && oc<=MAX_COUNT_OF_OBJECTS) {
			ret = new int[wc][oc];
			Random r = new Random();
			for (int w=0; w<wc; w++) {
				for (int o=0; o<oc; o++) {
					ret[w][o] = r.nextInt(cc);
				}
			}
		}
		return ret;
	}
	
	/**
	 * @param prefix
	 * @param cOUNT_OF_THE_LABELS
	 * @return array of label names
	 */
	public static String[] generateLabelNameList(String prefix,
			int cOUNT_OF_THE_LABELS) {
		String ll[] = null;
		if (prefix!=null && cOUNT_OF_THE_LABELS>1) {
			ll = new String[cOUNT_OF_THE_LABELS];
			for (int i=0; i<cOUNT_OF_THE_LABELS; i++) {
				ll[i] = new String(prefix+i);
			}
		}
		return ll;
	}
	
	/**
	 * @param prefix
	 * @param cOUNT_OF_THE_WORKERS
	 * @return array of worker names 
	 */
	public static String[] generateWorkerNameList(String prefix,
			int cOUNT_OF_THE_WORKERS) {
		String wl[] = null;
		if (prefix!=null && cOUNT_OF_THE_WORKERS>1) {
			wl = new String[cOUNT_OF_THE_WORKERS];
			for (int i=0; i<cOUNT_OF_THE_WORKERS; i++) {
				wl[i] = new String(prefix+i);
			}
		}
		return wl;
	}
}
