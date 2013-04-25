package test.java.integration.helpers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ObjectsResultsParser {

	Map <String, Map<String, Double>> resultsObjects;
	double avgAbsoluteEstimationError;
	double avgRelativeEstimationError;

	
	public ObjectsResultsParser() {
		this.resultsObjects = new HashMap<String, Map<String,Double>>();
		this.avgAbsoluteEstimationError = 0.0;
		this.avgRelativeEstimationError = 0.0;
	}

	public void ParseResultsObjectsFile(String filePath) {
		FileInputStream fstream = null;

		try {
			fstream = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		try {
			while ((line = br.readLine()) != null) {
				String[] entries = line.split("\t");
				if (entries.length != 6) {
					if (line.contains("Average absolute estimation error for z-values:")) {
						double val = Double.parseDouble(line.split(":")[1].trim());
						this.avgAbsoluteEstimationError = val;
					} 
					if (line.contains("Average relative estimation error for z-values:")) {
							double val = Double.parseDouble(line.split(":")[1].trim());
							this.avgRelativeEstimationError = val;
					}
				} else {
					String objectName = entries[0];
					Map<String,Double> estimatedObjectValues = new HashMap<String,Double>();
					estimatedObjectValues.put("avgLabel", Double.parseDouble(entries[1]));
					estimatedObjectValues.put("estValue", Double.parseDouble(entries[2]));
					estimatedObjectValues.put("estZeta", Double.parseDouble(entries[3]));
					estimatedObjectValues.put("trueValue", Double.parseDouble(entries[4]));
					estimatedObjectValues.put("trueZeta", Double.parseDouble(entries[5]));
					resultsObjects.put(objectName,  estimatedObjectValues);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double getAvgAbsoluteEstimationError() {
		return this.avgAbsoluteEstimationError;
	}

	public double getAvgRelativeEstimationError() {
		return this.avgRelativeEstimationError;
	}
	
	public Map <String, Map<String, Double>> getEstimatedObjectValues(){
		return this.resultsObjects;
	}

}
