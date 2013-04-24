package test.java.integration.helpers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class SummaryResultsParser {

	HashMap<String, String> parameters;
	HashMap<String, String> data;
	HashMap<String, String> dataQuality;
	HashMap<String, String> workerQuality;
	
	public SummaryResultsParser() {
		this.parameters = new HashMap<String, String>();
		this.data = new HashMap<String, String>();
		this.dataQuality = new HashMap<String, String>();
		this.workerQuality = new HashMap<String, String>();
	}

	public SummaryResultsParser(String path) {
		this();
		ParseSummaryFile(path);
	}
	
	public HashMap<String, String> getParameters() {
		return parameters;
	}


	public HashMap<String, String> getData() {
		return data;
	}


	public HashMap<String, String> getDataQuality() {
		return dataQuality;
	}


	public HashMap<String, String> getWorkerQuality() {
		return workerQuality;
	}

	public HashMap<String, String> transformToHashMap(ArrayList<String> originalData){
		HashMap<String, String> finalData = new HashMap<String, String>();
		for (int i = 1; i < originalData.size(); i++){
			String[] splittedData = originalData.get(i).split(": ");
			finalData.put(splittedData[0], splittedData[1]);
		}
		return finalData;
	}

	public void ParseSummaryFile(String filePath) {
		ArrayList<String> parametersList = new ArrayList<String>();
		ArrayList<String> dataList = new ArrayList<String>();
		ArrayList<String> dataQualityList = new ArrayList<String>();
		ArrayList<String> workerQualityList = new ArrayList<String>();
		FileInputStream fstream = null;
		
		try {
			fstream = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		int currentBlock = 0;
		try {
			while ((line = br.readLine()) != null) {
				if (!(line.equals(""))) {
					if (currentBlock == 1) {
						parametersList.add(line);
					} else if (currentBlock == 2) {
						dataList.add(line);
					} else if (currentBlock == 3) {
						dataQualityList.add(line);
					} else if (currentBlock == 4) {
						workerQualityList.add(line);
					}
				} else {
					currentBlock++;
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
		
		//put the data into the corresponding structures 
		this.parameters = transformToHashMap(parametersList);
		this.data = transformToHashMap(dataList);
		this.dataQuality = transformToHashMap(dataQualityList);
		this.workerQuality = transformToHashMap(workerQualityList);
	}
}
