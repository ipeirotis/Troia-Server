package com.datascience.galc.dataGenerator;

import com.datascience.utils.Utils;
import org.apache.log4j.Logger;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.results.DatumContResults;
import com.datascience.core.results.WorkerContResults;

import java.util.Map;

public class SyntheticDataGenerator {

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

	private static Logger logger = Logger.getLogger(SyntheticDataGenerator.class);
	
	public SyntheticDataGenerator() {
		this("data/synthetic/synthetic-options.txt");
	}
	
	public SyntheticDataGenerator(String file) {
		loadSyntheticOptions(file);
		initDataParameters();
		initWorkerParameters();
		logger.info("Data points: " + this.pointsCount);
		logger.info("Data gold: " + this.goldCount);
		logger.info("Workers: " + this.workersCount);
		logger.info("Low rho: " + this.workerRhoLower);
		logger.info("High rho: " + this.workerRhoUpper);
	}

	
	public SyntheticData generate() {
		
		SyntheticData data = new SyntheticData();
		
		// Generate objects (real values x_i).
		for (int i = 0; i < pointsCount; i++) {
			LObject<ContValue> lo = new LObject<ContValue>("Object" + (i + 1));
			DatumContResults dcr = new DatumContResults();
			Double v = datumGenerator.nextData();
			Double z = (v-this.dataMu)/this.dataSigma;
			lo.setEvaluationLabel(new ContValue(v, z));
			data.addObjectResult(lo, dcr);
		}
		
		// First n objects mark as gold.
		int currentGoldCount = 0;
		for (LObject<ContValue> lo : data.getObjects()) {
			if(currentGoldCount++ < goldCount) {
				ContValue cv = lo.getEvaluationLabel();
				lo.setGoldLabel(new ContValue(cv.getValue(),cv.getZeta()));
			}
		}
		
		for (int i = 0; i < workersCount; i++) {
			Worker<ContValue> w = new Worker<ContValue>("Worker" + (i + 1));
			WorkerContResults wcr = new WorkerContResults();
			wcr.setTrueMu(muGenerator.nextData());
			wcr.setTrueSigma(sigmaGenerator.nextData());
			wcr.setTrueRho(rhoGenerator.nextData());
			data.addWorkerResult(w, wcr);
		}
		
		// Generate assigned labels (observation values y_ij).
		for (Map.Entry<LObject<ContValue>, DatumContResults> dr : data.getObjectContResults().entrySet()){
			for (Map.Entry<Worker<ContValue>, WorkerContResults> wr : data.getWorkerContResults().entrySet()){
				LObject<ContValue> lo = dr.getKey();
				DatumContResults dcr = dr.getValue();
				Worker<ContValue> w = wr.getKey();
				WorkerContResults wcr = wr.getValue();
				ContValue label = lo.isGold() ? lo.getGoldLabel() : lo.getEvaluationLabel();
				Double datum_z = (label.getValue() - this.dataMu) / this.dataSigma;
				Double label_mu = wcr.getTrueMu() + wcr.getTrueRho() * wcr.getTrueSigma() * datum_z;
				Double label_sigma = Math.sqrt(1 - Math.pow(wcr.getTrueRho(), 2)) * wcr.getTrueSigma();
				Generator labelGenerator = new Generator(Generator.Distribution.GAUSSIAN);
				labelGenerator.setGaussianParameters(label_mu, label_sigma);
				data.addAssign(new AssignedLabel<ContValue>(w, lo, new ContValue(labelGenerator.nextData())));
			}
		}
		
		return data;
	}
	
	final public void initDataParameters() {
		datumGenerator = new Generator(Generator.Distribution.GAUSSIAN);
		datumGenerator.setGaussianParameters(this.dataMu, this.dataSigma);
	}

	final public void initWorkerParameters() {

		muGenerator = new Generator(Generator.Distribution.UNIFORM);
		muGenerator.setUniformParameters(this.workerMuLower, this.workerMuUpper);

		sigmaGenerator = new Generator(Generator.Distribution.UNIFORM);
		sigmaGenerator.setUniformParameters(this.workerSigmaLower, this.workerSigmaUpper);

		rhoGenerator = new Generator(Generator.Distribution.UNIFORM);
		rhoGenerator.setUniformParameters(this.workerRhoLower, this.workerRhoUpper);
	}

	final public void loadSyntheticOptions(String filename) {
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
