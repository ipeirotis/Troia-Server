package test.java.integration.helpers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class WorkersResultsParser {
	Map<String, HashMap<String, Object>> workersResults;
	Double absoluteEstimationError;
	Double relativeEstimationError;

	public WorkersResultsParser(){
		this.workersResults = new HashMap<String, HashMap<String, Object>>();
		this.absoluteEstimationError = 0.0;
		this.relativeEstimationError = 0.0;
	}
	
	public Map<String, HashMap<String, Object>> getWorkersResults() {
		return this.workersResults;
	}

	public Double getAbsoluteEstimationError() {
		return this.absoluteEstimationError;
	}

	public Double getRelativeEstimationError() {
		return this.relativeEstimationError;
	}

	public void ParseWorkerResultsFile(String filePath) {
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		int lineCount = 0;
		try {
			while ((line = br.readLine()) != null) {
				lineCount ++;
				if (lineCount == 1) {
					continue;
				} 
				else {
					String[] entries = line.split("\t");
					if (entries.length == 8) {
						String[] data = line.split("\t");
						String workerName = data[0];
						HashMap<String, Object> workerInfo = new HashMap<String, Object>();
						workerInfo.put("labels", Integer.parseInt(data[1]));
						workerInfo.put("est_mu", Double.parseDouble(data[2]));
						workerInfo.put("est_sigma",Double.parseDouble(data[3]));
						workerInfo.put("est_rho",Double.parseDouble(data[4]));
						workerInfo.put("true_mu", data[5]);
						workerInfo.put("true_sigma",data[6]);
						workerInfo.put("true_rho", data[7]);
						this.workersResults.put(workerName, workerInfo);
					} 
					else if (line.contains("Average absolute estimation error")) {
						absoluteEstimationError = Double.parseDouble(line.split(":")[1].trim());
					} 
					else if (line.contains("Average relative estimation error")) {
						relativeEstimationError = Double.parseDouble(line.split(":")[1].trim());
					}
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
}
