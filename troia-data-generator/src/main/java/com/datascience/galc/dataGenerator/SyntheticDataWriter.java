package com.datascience.galc.dataGenerator;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.galc.WorkerContResults;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class SyntheticDataWriter {
	
	private final SyntheticData data;
	
	public SyntheticDataWriter(SyntheticData data) {
		this.data = data;
	}
	
	public void writeToFile(SyntheticData data) throws IOException {
		writeAssignedLabelsToFile(data.getAssigns(), null);
		writeTrueWorkerDataToFile(data.getWorkerContResults(), null);
		writeTrueObjectDataToFile(data.getObjects(), null);
		writeGoldObjectDataToFile(data.getGoldObjects(), null);
	}
	
	private BufferedWriter openFile(String filename) throws IOException {
		File outfile = new File(filename);
		if (outfile.getParent() != null) {
			File parentDir = new File(outfile.getParent());
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}
		}
		return new BufferedWriter(new FileWriter(outfile));
	}

	public void writeAssignedLabelsToFile(Set<AssignedLabel<ContValue>> assigns, 
			String filename) throws IOException {

		BufferedWriter bw = openFile(filename);
		try {
			for (AssignedLabel<ContValue> al : assigns) {
				String line = al.getWorker().getName() + "\t" + 
						al.getLobject().getName() + "\t" + 
						// TODO: is it correct?
						al.getLabel().getValue().getValue() + "\n";
				bw.write(line);
			}
		} finally {
			bw.close();
		}
	}

	public void writeTrueWorkerDataToFile(
			Set<WorkerContResults> workerContResults, 
			String filename) throws IOException {

		BufferedWriter bw = openFile(filename);
		try {
			for (WorkerContResults wcr : workerContResults) {
				Worker<ContValue> w = wcr.getWorker();
				String line = w.getName() + "\t" +
						wcr.getTrueRho() + "\t" +
						wcr.getTrueMu() + "\t" +
						wcr.getTrueSigma() + "\t"
						+ "\n";
				bw.write(line);
			}
		} finally {
			bw.close();
		}
	}
	
	public void writeTrueObjectDataToFile(Set<LObject<ContValue>> lObjects, 
			String filename) throws IOException {

		BufferedWriter bw = openFile(filename);
		try {
			for (LObject<ContValue> lo : lObjects) {
				if (lo.isEvaluation()) {
					ContValue contValue = lo.getEvaluationLabel().getValue();
					String line = lo.getName() + "\t" +
							contValue.getValue() + "\t" +
							contValue.getZeta() + "\n";
					bw.write(line);
				}
			}
		} finally {
			bw.close();
		}
	}

	public void writeGoldObjectDataToFile(Set<LObject<ContValue>> lObjects, 
			String filename) throws IOException {

		BufferedWriter bw = openFile(filename);
		try {
			for (LObject<ContValue> lo : lObjects) {
				if (lo.isGold()) {
					ContValue contValue = lo.getGoldLabel().getValue();
					String line = lo.getName() + "\t" +
							contValue.getValue() + "\t" +
							contValue.getZeta() + "\n";
					bw.write(line);
				}
			}
		} finally {
			bw.close();
		}
	}
	
}
