package main.com.troiaTester;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Object of this class represents matrixes of misclassification probabilities.
 * 
 * @author piotr.gnys@10clouds.com
 */
public class ConfusionMatrix {

	
	
	
	/**
	 * @param matrix
	 */
	public ConfusionMatrix(Map<String, Map<String, Double>> matrix) {
		super();
		this.matrix = matrix;
	}

	public ConfusionMatrix() {
		this(new HashMap<String,Map<String,Double>>());
	}
	
	/**
	 * @return the matrix
	 */
	public Map<String, Map<String, Double>> getMatrix() {
		return matrix;
	}

	/**
	 * @param matrix the matrix to set
	 */
	public void setMatrix(Map<String, Map<String, Double>> matrix) {
		this.matrix = matrix;
	}

	public double getMisclassificationProbability(String from,String to){
		Map<String,Double> vector = this.matrix.get(from);
		Double probability = null;
		double retVal;
		if(vector!=null){
			probability = vector.get(to);
			if(probability!=null){
				retVal = probability.doubleValue();
			}else{
				retVal = 0;
			}
		}else{
			retVal = 0;
		}
		return retVal;
	}
	
	public void setMisclassificationProbability(String from,String to,double probability) throws Exception{
		if(probability>=0&&probability<=1){
			Map<String,Double> vector = this.matrix.get(from);
			if(vector==null){
				vector = new HashMap<String,Double>();
				this.matrix.put(from, vector);
			}
			vector.put(to, new Double(probability));
		}else{
			throw new Exception("Attempt to set probability value to "+probability);
		}
	}
	
	
	/**
	 * Map that assigns probability of incorrect association between 
	 * two categories. Calling function matirix.get("Category A").get("Category B")
	 * will return probability of assigning Category B to object of Category A
	 */
	Map<String,Map<String,Double>> matrix;
}
