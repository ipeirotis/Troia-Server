package com.datascience.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.datascience.utils.ProbabilityDistributions;
import org.junit.Test;

import com.datascience.utils.CostMatrix;

/**
 * The class <code>ProbabilityDistributionsTest</code> contains tests for the class {@link <code>ProbabilityDistributions</code>}
 *
 * @pattern JUnit Test Case
 *
 * @generatedBy CodePro at 10/2/12 11:02 AM
 *
 * @author Konrad Kurdej
 *
 * @version $Revision$
 */
public class ProbabilityDistributionsTest {

	/**
	 * Run the Double calculateLabelCost(String, Map<String,Double>,
	 * CostMatrix<String>) method test
	 */
	@Test
	public void testCalculateLabelCost() {
		String calcLabel = "OK";
		Map<String, Double> labelProbabilities = new HashMap<String, Double>();
		labelProbabilities.put("OK", 0.5);
		labelProbabilities.put("LOK", 0.0);
		labelProbabilities.put("HOK", 1.);

		CostMatrix<String> costMatrix = new CostMatrix<String>();
		costMatrix.add("OK", "OK", 1.);
		costMatrix.add("LOK", "OK", 10.);
		costMatrix.add("HOK", "OK", 20.);

		Double result = ProbabilityDistributions.calculateLabelCost(calcLabel, labelProbabilities,
				costMatrix);
		assertEquals(20.5, result, 0.0);
	}

	/**
	 * Run the Map<T,Double> generateConstantDistribution(Collection<T>, double)
	 * method test
	 */
	@Test
	public void testGenerateConstantDistribution() {
		Collection<String> objects = new LinkedList<String>();
		for (int i = 0; i < 10; i++) {
			objects.add("" + i);
		}
		double value = Math.PI;
		Map<String, Double> result = ProbabilityDistributions.generateConstantDistribution(
				objects, value);
		for (String s : objects) {
			assertEquals(Math.PI, result.get(s), 0.0);
		}
	}

	/**
	 * Run the Map<T,Double> generateUniformDistribution(Collection<T>) method
	 * test
	 */
	@Test
	public void testGenerateUniformDistribution() {
		Collection<String> objects = new LinkedList<String>();
		for (int i = 0; i < 8; i++) {
			objects.add("" + i);
		}
		Map<String, Double> result = ProbabilityDistributions.generateUniformDistribution(objects);
		for (String s : objects) {
			assertEquals(1. / 8, result.get(s), 0.0);
		}
	}
}
