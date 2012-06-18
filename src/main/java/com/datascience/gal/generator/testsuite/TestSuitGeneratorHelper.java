/**
 * 
 */
package com.datascience.gal.generator.testsuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import com.datascience.gal.generator.RawConfusionMatrixGenerator;

/**
 * @author Michael Arshynov
 *
 */
public class TestSuitGeneratorHelper {

	private static double [] tall = new double [1000];
	/** Makes generation type 'a' (creates data objects)
	 * @param o - path to output file, could be null
	 * @param c - categories count
	 * @param k - number of tests generated
	 * @param priors - path to file with priors, could be null
	 * @return null or error message
	 */
	public static String generateTestA(String o, int c, int k, String priors) {
		StringBuffer sb = new StringBuffer();
		String[] labels = RawConfusionMatrixGenerator.generateLabelNameList("label", c);
		int eachLabelIsPresentAtLeast = k/c;
		int leftToAdd = k%c;
		int eachLabelIsPresentAdditionally = leftToAdd!=0?eachLabelIsPresentAtLeast:0;
		int nextToAddIfZero = eachLabelIsPresentAdditionally;
		
		
		if (priors != null) {
			//validation section
			if (!validatePriors(priors, c, k)) {
				return "'priors' file is invalid.";
			} else
				System.out.println("'priors' file is valid!");
			int normalized[] = new int[tall.length];
			int toto = 0;
			for (int i=0; i<tall.length; i++) {
				normalized[i] = (int) (tall[i] * k);
				toto+=normalized[i];
			}
			int inx = 0;
			int left = c-toto;
			System.out.println("    left"+left);
			for (String l:labels) {
				for (int i=0; i<normalized[inx]; i++) {
					sb.append(l+"\n");
				}
				inx++;
			}
			if (left>0) {
				for (int i=0; i<left; i++) {
					Random rand = new Random();
					int nextInt = rand.nextInt(labels.length);
					sb.append(labels[nextInt]+"\n");
				}
			}
		} else {
			for (String l:labels) {
				nextToAddIfZero--;
				if (nextToAddIfZero==0 && leftToAdd>0) {
					nextToAddIfZero = eachLabelIsPresentAdditionally;
					sb.append(l+"\n");
					leftToAdd--;
				}
				for (int i=0; i<eachLabelIsPresentAtLeast; i++) {
					sb.append(l+"\n");
				}
			}
		}
		System.out.println("Data generation for type 'a' has been done...");
		File f = new File(o);
		FileWriter fw;
		try {
			fw = new FileWriter(f);
			fw.append(sb+"\n");
			fw.close();
		} catch (IOException e) {
			System.out.println("Error writing to the file!");
		};
		System.out.println("Data writing to the file for type 'a' has been done!");
		return "generation finished successfully";
	}
	
	/** Validates priors file
	 * @param priors - path to priors file
	 * @param c - number of categories
	 * @param k - number of tests to be generated
	 * @return true if file is valid
	 */
	private static boolean validatePriors(String priors, int c, int k) {
		Scanner scanner = null;
		int i = 0;
		try {
			scanner = new Scanner(new File(priors));
			while(scanner.hasNext() && scanner.hasNextDouble()){
			   tall[i++] = scanner.nextDouble();
			   if (tall[i-1]>1 && tall[i-1]<0) {
				   System.out.println("Priors file contains wrong value.");
			   }
			}
		} catch (FileNotFoundException e) {
			System.out.println("Priors file is invalid or absent.");
			return false;
		} catch (IllegalStateException isExc) {
			System.out.println("Priors file is invalid. Wrong syntax.");
			return false;
		} finally {
			if (scanner!=null) scanner.close();
		}
		if (i!=c) {
			System.out.println("Priors file is invalid. The number of rows and number of categories should be the same.");
			return false;
		}
		double bit = 1.0;
		for (int ii=0; ii<i; ii++) {
//			System.out.println("|_"+ii+"_|"+tall[ii]+"|");
			bit-=ii;
		}
		if (Math.abs(bit)<=0.05) {
			System.out.println("Priors file is invalid. Wrong sum of priorities (should be eq. 1.0+-0.05).");
			return false;
		}
		return true;
	}

	/** Validates input data for generation type 'a' (creates data objects)
	 * @param o - path to output file, could be null
	 * @param c - categories count
	 * @param k - number of tests generated
	 * @param priors - path to file with priors, could be null
	 * @return null or error message
	 */
	public static String validateTestA(String o, int c, int k, String priors) {
		final int MAX_K = 1000;
		final int MAX_C = 100;
		if (c>k) {
			return "'c' should be less than 'k' or at least equal.";
		}
		if (c<0 || k<0) {
			return "'c' and 'k' should be positive.";
		}
		if (k>1000) {
			return "'k' should be not greater than "+MAX_K+".";
		}
		if (c>MAX_C) {
			return "'c' should be not greater than "+MAX_C+".";
		}
		if(o==null) {
			
		}
		if(priors==null) {
			
		}
		return null;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	/** Validates input data for generation type 'b' (creates workers)
	 * @param o - path to output file, could be null
	 * @param c - categories count
	 * @param qa - bottom level of the expected quality of the workers
	 * @param qb - top level of the expected quality of the workers
	 * @return  null or error message
	 */
	public static String validateTestB(String o, int c, double qa, double qb) {
		// TODO Auto-generated method stub
		return null;
	}

	/** Makes generation type 'b' (creates workers)
	 * @param o - path to output file, could be null
	 * @param c - categories count
	 * @param qa - bottom level of the expected quality of the workers
	 * @param qb - top level of the expected quality of the workers
	 * @return  null or error message
	 */
	public static String generateTestB(String o, int c, double qa, double qb) {
		// TODO Auto-generated method stub
		return null;
	}

	/**  Validates input data for generation type 'c' (creates assigned labels)
	 * @param o - path to output file, could be null
	 * @param w - path to output file which contains Workers
	 * @param cd - path to output file which contains Correct Data
	 * @param l - count of labels assigned to each example
	 * @return  null or error message
	 */
	public static String validateTestC(String o, String w, String cd, int l) {
		// TODO Auto-generated method stub
		return null;
	}

	/**  Makes generation type 'c' (creates assigned labels)
	 * @param o - path to output file, could be null
	 * @param w - path to output file which contains Workers
	 * @param cd - path to output file which contains Correct Data
	 * @param l - count of labels assigned to each example
	 * @return  null or error message
	 */
	public static String generateTestC(String o, String w, String cd, int l) {
		// TODO Auto-generated method stub
		return null;
	}

	/** Validates input data for generation type 'd' (assigned labels)
	 * @param o - path to output file, could be null
	 * @param cd - path to output file which contains Correct Data
	 * @param p - number of lines, 0 or 1
	 * @return  null or error message
	 */
	public static String validateTestD(String o, String cd, int p) {
		// TODO Auto-generated method stub
		return null;
	}

	/** Makes generation type 'd' (creates Gold labels)
	 * @param o - path to output file, could be null
	 * @param cd - path to output file which contains Correct Data
	 * @param p - number of lines, 0 or 1
	 * @return  null or error message
	 */
	public static String generateTestD(String o, String cd, int p) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param o - path to output file, could be null
	 * @param cd - path to output file which contains Correct Data
	 * @param cddsas - path to output file which contains Correct Data, output from DSaS algorithm
	 * @return  null or error message
	 */
	public static String validateTestE(String o, String cd, String cddsas) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param o - path to output file, could be null
	 * @param cd - path to output file which contains Correct Data
	 * @param cddsas - path to output file which contains Correct Data, output from DSaS algorithm
	 * @return  null or error message
	 */
	public static String generateTestE(String o, String cd, String cddsas) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param o - path to output file, could be null
	 * @param w - path to output file which contains Workers
	 * @param wdsas - path to output file which contains Workers, output from DSaS algorithm
	 * @return  null or error message
	 */
	public static String validateTestF(String o, String w, String wdsas) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param o - path to output file, could be null
	 * @param w - path to output file which contains Workers
	 * @param wdsas - path to output file which contains Workers, output from DSaS algorithm
	 * @return  null or error message
	 */
	public static String generateTestF(String o, String w, String wdsas) {
		// TODO Auto-generated method stub
		return null;
	}

}
