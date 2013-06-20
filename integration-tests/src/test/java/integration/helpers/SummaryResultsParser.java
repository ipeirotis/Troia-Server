package test.java.integration.helpers;

import java.io.*;
import java.util.HashMap;

public class SummaryResultsParser {

    HashMap<String, String> parameters;
    HashMap<String, String> data;
    HashMap<String, Double> dataQuality;
    HashMap<String, Double> workerQuality;

    public SummaryResultsParser() {
        this.parameters = new HashMap<String, String>();
        this.data = new HashMap<String, String>();
        this.dataQuality = new HashMap<String, Double>();
        this.workerQuality = new HashMap<String, Double>();
    }

    public SummaryResultsParser(String path) {
        this();
        try {
            parseSummaryFile(path);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public HashMap<String, Double> getDataQuality() {
        return dataQuality;
    }

    public HashMap<String, Double> getWorkerQuality() {
        return workerQuality;
    }

    public Double DoubleValue(String value){
        Double dValue = 0.0;
        int position = value.indexOf("%");
        if (value.indexOf("%") > 0){
            dValue = Double.valueOf(value.substring(0, position))/100;
        }
        else
        {
            if (value.equals("N/A")){
                dValue = Double.NaN;
            }
            else
            {
                dValue = Double.valueOf(value);
            }
        }
        return dValue;
    }

    public void parseSummaryFile(String filePath) throws UnsupportedEncodingException {
        FileInputStream fstream = null;

        try {
            fstream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

        String line;
        int currentBlock = 0;
        try {
            while ((line = br.readLine()) != null) {
                if ((line.equals(""))) {
                    currentBlock++;
                }
                else{
                    if (line.indexOf(": ") > 0) {
                        String[] splittedData = line.split(": ");
                        if (currentBlock == 1) {
                            parameters.put(splittedData[0], splittedData[1]);
                        } else if (currentBlock == 2) {
                            data.put(splittedData[0], splittedData[1]);
                        } else if (currentBlock == 3) {
                            dataQuality.put(splittedData[0], DoubleValue(splittedData[1]));
                        } else if (currentBlock == 4) {
                            workerQuality.put(splittedData[0], DoubleValue(splittedData[1]));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
