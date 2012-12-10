/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;

public class Utils {

	public static String cleanLine(String line) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c < 128 && (Character.isLetter(c) || Character.isDigit(c))) {
				buffer.append(c);
			} else {
				// buffer.append('');
			}
		}
		return buffer.toString().toLowerCase();
	}

	public static String getFile(String FileName) {
		StringBuffer buffer = new StringBuffer();

		try {
			BufferedReader dataInput = new BufferedReader(new FileReader(
						new File(FileName)));
			String line;

			while ((line = dataInput.readLine()) != null) {
				// buffer.append(cleanLine(line.toLowerCase()));
				buffer.append(line);
				if (dataInput.ready()) {
					buffer.append('\n');
				}
			}
			dataInput.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}

	public static void writeFile(String in, String filename) {
		try {
			File outfile = new File(filename);

			if (!(new File(outfile.getParent()).exists())) {
				(new File(outfile.getParent())).mkdirs();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			bw.write(in);
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Double round(double d, int decimalPlace) {
		// see the Javadoc about why we use a String in the constructor
		// http://java.sun.com/j2se/1.5.0/docs/api/java/math/BigDecimal.html#BigDecimal(double)
		BigDecimal bd = new BigDecimal(Double.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	public static Double entropy(double[] p) {
		double h = 0;
		for (int i = 0; i < p.length; i++) {
			h += (p[i] > 0) ? p[i] * Math.log(p[i]) : 0.0;
		}
		return -h;
	}

}
