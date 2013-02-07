package com.datascience.galc.dataGenerator;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Label;
import com.datascience.core.base.Worker;
import com.datascience.galc.DatumContResults;
import com.datascience.galc.Utils;
import com.datascience.galc.WorkerContResults;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

public class SyntheticData extends Data<ContValue> {

	private int	pointsCount;
	private int	goldCount;
	private int	workersCount;
	
	private Double dataMu;
	private Double dataSigma;
	
	private Double workerMuLower;
	private Double workerMuUpper;
	private Double workerSigmaLower;
	private Double workerSigmaUpper;
	private Double workerRhoLower;
	private Double workerRhoUpper;

	private Generator muGenerator;
	private Generator sigmaGenerator;
	private Generator rhoGenerator;
	private Generator datumGenerator;

	private static Logger logger = Logger.getLogger(SyntheticData.class);
	
	private Set<WorkerContResults> workerContResults = new HashSet<WorkerContResults>();
	private Set<DatumContResults> objectContResults = new HashSet<DatumContResults>();
	

	public SyntheticData(Boolean verbose, String file) {
		super();
		loadSyntheticOptions(file);
		logger.info("Data points: " + this.pointsCount);
		logger.info("Data gold: " + this.goldCount);
		logger.info("Workers: " + this.workersCount);
		logger.info("Low rho: " + this.workerRhoLower);
		logger.info("High rho: " + this.workerRhoUpper);


	}

	public void initDataParameters() {
		datumGenerator = new Generator(Generator.Distribution.GAUSSIAN);
		datumGenerator.setGaussianParameters(this.dataMu, this.dataSigma);
	}

	public void initWorkerParameters() {

		muGenerator = new Generator(Generator.Distribution.UNIFORM);
		muGenerator.setUniformParameters(this.workerMuLower, this.workerMuUpper);

		sigmaGenerator = new Generator(Generator.Distribution.UNIFORM);
		sigmaGenerator.setUniformParameters(this.workerSigmaLower, this.workerSigmaUpper);

		rhoGenerator = new Generator(Generator.Distribution.UNIFORM);
		rhoGenerator.setUniformParameters(this.workerRhoLower, this.workerRhoUpper);
	}

	public void build() {

		createObjects(this.pointsCount);
		createGold(this.goldCount);
		createWorkers(this.workersCount);
		createLabels();

	}

	private void createGold(int g_gold_objects) {

		//first g_gold_objects will be gold
		int i = 0;
		for (LObject<ContValue> lo : getObjects()) {
			if(i++ < g_gold_objects) {
				ContValue cv = lo.getEvaluationLabel().getValue();
				lo.setGoldLabel(new Label<ContValue>(new ContValue(cv.getValue(),cv.getZeta())));
			}
		}
	}

	private void createLabels() {

		// Generate Observation Values y_ij
		for (DatumContResults dcr : objectContResults) {
			for (WorkerContResults wcr : workerContResults) {
				LObject<ContValue> lo = dcr.getObject();
				Worker<ContValue> w = wcr.getWorker();
				Label<ContValue> label = lo.isGold() ? lo.getGoldLabel() : lo.getEvaluationLabel();
				Double datum_z = (label.getValue().getValue() - this.dataMu) / this.dataSigma;
				Double label_mu = wcr.getTrueMu() + wcr.getTrueRho() * wcr.getTrueSigma() * datum_z;
				Double label_sigma = Math.sqrt(1 - Math.pow(wcr.getTrueRho(), 2)) * wcr.getTrueSigma();
				Generator labelGenerator = new Generator(Generator.Distribution.GAUSSIAN);
				labelGenerator.setGaussianParameters(label_mu, label_sigma);
				addAssign(new AssignedLabel<ContValue>(w, lo, new Label<ContValue>(new ContValue(labelGenerator.nextData()))));
			}
		}
	}

	private void createObjects(int k_objects) {

		// Generate Object Real Values x_i
		for (int i = 0; i < k_objects; i++) {
			LObject<ContValue> lo = new LObject<ContValue>("Object" + (i + 1));
			DatumContResults dcr = new DatumContResults(lo);
			Double v = datumGenerator.nextData();
			Double z = (v-this.dataMu)/this.dataSigma;
			lo.setEvaluationLabel(new Label<ContValue>(new ContValue(v, z)));
			objects.add(lo);
			objectContResults.add(dcr);
			//System.out.println("lo = " + lo + " isEvaluation " + lo.isEvaluation());
		}
	}

	private void createWorkers(int l_workers) {

		// Generate Worker Characteristics
		for (int i = 0; i < l_workers; i++) {
			Worker<ContValue> w = new Worker<ContValue>("Worker" + (i + 1));
			WorkerContResults wcr = new WorkerContResults(w);
			wcr.setTrueMu(muGenerator.nextData());
			wcr.setTrueSigma(sigmaGenerator.nextData());
			wcr.setTrueRho(rhoGenerator.nextData());
			workers.add(w);
			workerContResults.add(wcr);
		}
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

	public void writeLabelsToFile(String filename) throws IOException {

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

	public void writeTrueWorkerDataToFile(String filename) throws IOException {

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
	
	public void writeTrueObjectDataToFile(String filename) throws IOException {

		BufferedWriter bw = openFile(filename);
		try {
			for (LObject<ContValue> lo : objects) {
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

	public void writeGoldObjectDataToFile(String filename) throws IOException {

		BufferedWriter bw = openFile(filename);
		try {
			for (LObject<ContValue> lo : objects) {
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

	public void loadSyntheticOptions(String filename) {
		String[] lines = Utils.getFile(filename).split("\n");
		for(String line : lines) {
			String[] entries = line.split("=");
			if (entries.length != 2) {
				throw new IllegalArgumentException("Error while loading from synthetic sptions file");
			} else if(entries[0].equals("data_points")) {
				this.pointsCount = (int)Integer.parseInt(entries[1]);
			} else if (entries[0].equals("data_mu")) {
				this.dataMu = Double.parseDouble(entries[1]);
			} else if (entries[0].equals("data_sigma")) {
				this.dataSigma = Double.parseDouble(entries[1]);
			} else if (entries[0].equals("data_gold")) {
				this.goldCount = (int)Integer.parseInt(entries[1]);
			} else if (entries[0].equals("workers")) {
				this.workersCount = (int)Integer.parseInt(entries[1]);
			} else if (entries[0].equals("worker_mu_down")) {
				this.workerMuLower = Double.parseDouble(entries[1]);
			} else if (entries[0].equals("worker_mu_up")) {
				this.workerMuUpper = Double.parseDouble(entries[1]);
			} else if (entries[0].equals("worker_sigma_down")) {
				this.workerSigmaLower = Double.parseDouble(entries[1]);
			} else if (entries[0].equals("worker_sigma_up")) {
				this.workerSigmaUpper = Double.parseDouble(entries[1]);
			} else if (entries[0].equals("worker_rho_down")) {
				this.workerRhoLower = Double.parseDouble(entries[1]);
			} else if (entries[0].equals("worker_rho_up")) {
				this.workerRhoUpper = Double.parseDouble(entries[1]);
			} else {
				System.err.println("Error in synthetic options file variables");
			}

		}

	}

}
