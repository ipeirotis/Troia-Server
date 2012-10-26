/**
 *
 */
package com.datascience.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.datascience.utils.auxl.TestDataManager;

/**
 * @author Michael Arshynov
 *
 */
@Ignore
public class UtilsTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.datascience.utils.Utils#cleanLine(java.lang.String)}.
	 */
	@Test
	public final void testCleanLine() {
		assertTrue("method has no one reference", true);
	}

	/**
	 * Test method for {@link com.datascience.utils.Utils#getFile(java.lang.String)}.
	 */
	@Test
	public final void testGetFile() {
		String data = "test\nrow2\nLast row.";
		String property = "java.io.tmpdir";
		String fname = new String("dfgdfsgdfshfr.txt");
		String fullpath = System.getProperty(property)+File.separatorChar+fname;
		File toCheck = new File(fullpath);
		if (toCheck.exists()) toCheck.delete();
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(toCheck));
			bw.write(data);
			bw.close();
		} catch (IOException e) {
			fail(e.getMessage());
		};
		String fgetFileReturned = Utils.getFile(fullpath);
		int isTheSameContent = data.compareTo(fgetFileReturned);
		System.out.println("In=["+data+"]");
		System.out.println("Out=["+fgetFileReturned+"]");
		toCheck.delete();
		assertTrue("getFile adds new line at the end. Len in="+
				   data.length()+", Len out="+fgetFileReturned.length(),isTheSameContent==0);
	}

	/**
	 * Test method for {@link com.datascience.utils.Utils#writeFile(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testWriteFile() {
		String property = "java.io.tmpdir";
		String fname = new String("dfgdfsgdfshfr.txt");
		String fullpath = System.getProperty(property)+File.separatorChar+fname;
		String in = "something1\nsecond line\n\t3rd line\n\nlast line.";
		System.out.println("In=["+in+"]");
		Utils.writeFile(in, fullpath);
		File fToReadFromAndDeleteAfterwards = new File(fullpath);
		if (fToReadFromAndDeleteAfterwards.exists()) {
			try {
				BufferedReader dataInput = new BufferedReader(new FileReader(
							fToReadFromAndDeleteAfterwards));
				try {
					char[] outCharBuf = new char[1000];
					int len = dataInput.read(outCharBuf);
					String out = new String(outCharBuf,0,len);
					System.out.println("Out=["+out+"]");
					boolean isTheSame =  in.equals(out);
					fToReadFromAndDeleteAfterwards.delete();
					assertTrue(isTheSame);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				fail("file not found, "+e.getMessage());
			}
		} else fail("file does not exist");
	}

	/**
	 * Test method for {@link com.datascience.utils.Utils#round(double, int)}.
	 */
	@Test
	public final void testRound() {
		double ress[] = {
			-10.0, 10.0, -110.0, 110.0, 0.0, -110.0, 0.0, 0.0, 1000.0, -1000.0,
			1000.0, -1000.0, -11.0, 11.0, -105.0, 105.0, 1.0, -105.0, 0.0,
			0.0, 1001.0, -1001.0, 1000.0, -1000.0, -10.5, 10.5, -105.1,
			105.1, 1.1, -105.1, 0.0, 0.0, 1000.5, -1000.5, 1000.5, -1000.5,
			-10.51, 10.51, -105.1, 105.1, 1.05, -105.1, 0.0, 0.0, 1000.5,
			-1000.5, 1000.49, -1000.49, -10.51, 10.51, -105.1, 105.1,
			1.051, -105.1, 0.0, 0.0, 1000.5, -1000.5, 1000.49, -1000.49,
			-10.51, 10.51, -105.1, 105.1, 1.051, -105.1, 0.0001, -0.0001,
			1000.5, -1000.5, 1000.49, -1000.49, -10.51, 10.51, -105.1,
			105.1, 1.051, -105.1, 0.0001, -0.0001, 1000.5, -1000.5,
			1000.49, -1000.49
		};
		double arg1s[] = {	-10.51, +10.51,
							-10.51e+1, +10.51e+1,
							+10.51e-1, -10.51e+1,
							+0.0001, -0.0001,
							+1000.5, -1000.5,
							+1000.49, -1000.49
						 };
		int arg2s[] = {-1, 0,1,2,3,4,5};
		for (int i=0; i<arg2s.length; i++) {
			for (int j=0; j<arg1s.length; j++) {
				double one = arg1s[j];
				int two = arg2s[i];
				double res = Utils.round(one, two);
				int inx = i*arg1s.length+j;
				assertEquals(res, ress[inx], TestDataManager.DELTA_DOUBLE);
			}
		}
	}

	/**
	 * Test method for {@link com.datascience.utils.Utils#entropy(double[])}.
	 */
	@Test
	public final void testEntropy() {
		double p1[] = {1,0.1,0.99,0.2,0.4,0.5};
		double h1 = 1.275185807;

		double p2[] = {0.4, 0.5, 0.1};
		double h2 = 0.943348392;

		double p3[] = {-1, -1};
		double h3 = 0;

		double p4[] = {10,10};
		double h4 = -46.05170186;

		double p5[] = {0.4, 0.5};
		double h5 = 0.713089883;

		double p6[] = {0.1,0.2,0.4,0.5,0.1,0.2,0.4,0.5};
		double h6 = 2.53047195;

		assertTrue(isEntropyTheSame(p1,h1));
		assertTrue(isEntropyTheSame(p2,h2));
		assertTrue(isEntropyTheSame(p3,h3));
		assertTrue(isEntropyTheSame(p4,h4));
		assertTrue(isEntropyTheSame(p5,h5));
		assertTrue(isEntropyTheSame(p6,h6));
	}

	/**
	 * @param p
	 * @param res
	 * @return
	 */
	private boolean isEntropyTheSame(double p[], double res) {
		return Math.abs(Utils.entropy(p)-res)<=TestDataManager.DELTA_DOUBLE;
	}

}
