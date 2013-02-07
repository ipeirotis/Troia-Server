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
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

public class SyntheticData extends Data<ContValue> {

	private int				data_points;
	private Double		data_mu;
	private Double		data_sigma;
	private int				data_gold;
	private int				workers_points;
	private Double		worker_mu_down;
	private Double		worker_mu_up;
	private Double		worker_sigma_down;
	private Double		worker_sigma_up;
	private Double		worker_rho_down;
	private Double		worker_rho_up;

	private Generator	muGenerator;
	private Generator	sigmaGenerator;
	private Generator	rhoGenerator;

	private Generator	datumGenerator;

	private static Logger logger = Logger.getLogger(SyntheticData.class);
	
	private Set<WorkerContResults> workerContResults = new HashSet<WorkerContResults>();
	private Set<DatumContResults> objectContResults = new HashSet<DatumContResults>();
	

	public SyntheticData(Boolean verbose, String file) {
		super();
		loadSyntheticOptions(file);
		logger.info("Data points: " + this.data_points);
		logger.info("Data gold: " + this.data_gold);
		logger.info("Workers: " + this.workers_points);
		logger.info("Low rho: " + this.worker_rho_down);
		logger.info("High rho: " + this.worker_rho_up);


	}

	public void initDataParameters() {
		datumGenerator = new Generator(Generator.Distribution.GAUSSIAN);
		datumGenerator.setGaussianParameters(this.data_mu, this.data_sigma);
	}

	public void initWorkerParameters() {

		muGenerator = new Generator(Generator.Distribution.UNIFORM);
		muGenerator.setUniformParameters(this.worker_mu_down, this.worker_mu_up);

		sigmaGenerator = new Generator(Generator.Distribution.UNIFORM);
		sigmaGenerator.setUniformParameters(this.worker_sigma_down, this.worker_sigma_up);

		rhoGenerator = new Generator(Generator.Distribution.UNIFORM);
		rhoGenerator.setUniformParameters(this.worker_rho_down, this.worker_rho_up);
	}

	public void build() {

		createObjects(this.data_points);
		createGold(this.data_gold);
		createWorkers(this.workers_points);
		createLabels();

	}

	private void createGold(int g_gold_objects) {

		//first g_gold_objects will be gold
		int i = 0;
		for (LObject<ContValue> lo : getObjects()) {
			if(i++ < g_gold_objects) {
				//lo.setGoldLabel(new Label<ContValue>(new ContValue(lo.getGoldLabel().getValue(), dcr.getTrueZeta())));
//				ContValue cv = lo.getEvaluationLabel().getValue();
//				lo.setGoldLabel(new Label<ContValue>(new ContValue(cv.getValue(),cv.getZeta())));
				// TODO: FIX
				// d.setResults(dr);
			}
		}
	}

	private void createLabels() {

		// Generate Observation Values y_ij
		for (DatumContResults dcr : objectContResults) {
			for (WorkerContResults wcr : workerContResults) {
				LObject<ContValue> lo = dcr.getObject();
				Worker<ContValue> w = wcr.getWorker();
				Double datum_z = (lo.getGoldLabel().getValue().getValue() - this.data_mu) / this.data_sigma;
				Double label_mu = wcr.getTrueMu() + wcr.getTrueRho() * wcr.getTrueSigma() * datum_z;
				Double label_sigma = Math.sqrt(1 - Math.pow(wcr.getTrueRho(), 2)) * wcr.getTrueSigma();
				Generator labelGenerator = new Generator(Generator.Distribution.GAUSSIAN);
				labelGenerator.setGaussianParameters(label_mu, label_sigma);
				this.addAssign(new AssignedLabel<ContValue>(w, lo, new Label<ContValue>(new ContValue(labelGenerator.nextData()))));
				// this.getLabels().add(al);
				//w.addAssign(new AssignedLabel<ContValue>(w, lo, null));
				// w.addAssignedLabel(al);
				// d.addAssignedLabel(al);
			}
		}
	}

	private void createObjects(int k_objects) {

		// Generate Object Real Values x_i
		for (int i = 0; i < k_objects; i++) {
			LObject<ContValue> lo = new LObject<ContValue>("Object" + (i + 1));
			DatumContResults dcr = new DatumContResults(lo);
			Double v = datumGenerator.nextData();
			Double z = (v-this.data_mu)/this.data_sigma;
			lo.setGoldLabel(new Label<ContValue>(new ContValue(v, z)));
			objects.add(lo);
			objectContResults.add(dcr);
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

	public void writeLabelsToFile(String filename) {

		try {
			File outfile = new File(filename);

			if (outfile.getParent() != null) {
				File parentDir = new File(outfile.getParent());
				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}
			}

			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			for (AssignedLabel<ContValue> al : assigns) {
				String line = al.getWorker().getName() + "\t" + 
						al.getLobject().getName() + "\t" + 
						// TODO: is it correct?
						al.getLabel().getValue().getValue() + "\n";
				bw.write(line);
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeTrueObjectDataToFile(String filename) {

		try {
			File outfile = new File(filename);

			if (outfile.getParent() != null) {
				File parentDir = new File(outfile.getParent());
				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}
			}

			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			for (LObject<ContValue> lo : objects) {
				//DatumContResults dcr = new DatumContResults(lo);
				if (lo.isEvaluation()) {
				ContValue contValue = lo.getEvaluationLabel().getValue();
					String line = lo.getName() + "\t" +
							contValue.getValue() + "\t" +
							contValue.getZeta() + "\n";
					bw.write(line);
					//String line = lo.getName() + "\t" + lo.getGoldLabel().getValue().getValue() + "\t" + lo.getGoldLabel().getValue().getZeta() + "\n";
				}
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeTrueWorkerDataToFile(String filename) {

		try {
			File outfile = new File(filename);

			if (outfile.getParent() != null) {
				File parentDir = new File(outfile.getParent());
				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}
			}

			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			for (WorkerContResults wcr : workerContResults) {
				Worker<ContValue> w = wcr.getWorker();
				String line = w.getName() + "\t" +
						wcr.getTrueRho() + "\t" +
						wcr.getTrueMu() + "\t" +
						wcr.getTrueSigma() + "\t"
						+ "\n";
				bw.write(line);
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeGoldObjectDataToFile(String filename) {

		try {
			File outfile = new File(filename);

			if (outfile.getParent() != null) {
				File parentDir = new File(outfile.getParent());
				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}
			}

			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			for (LObject<ContValue> lo : objects) {
				if (lo.isGold()) {
					ContValue contValue = lo.getGoldLabel().getValue();
					String line = lo.getName() + "\t" +
							contValue.getValue() + "\t" +
							contValue.getZeta() + "\n";
					//String line = lo.getName() + "\t" + lo.getGoldLabel().getValue().getValue() + "\t" + lo.getGoldLabel().getValue().getZeta() + "\n";
					bw.write(line);
				}
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadSyntheticOptions(String filename) {
		String[] lines = Utils.getFile(filename).split("\n");
		for(String line : lines) {
			String[] entries = line.split("=");
			if (entries.length != 2) {
				throw new IllegalArgumentException("Error while loading from synthetic sptions file");
			} else if(entries[0].equals("data_points")) {
				this.data_points = (int)Integer.parseInt(entries[1]);
			} else if (entries[0].equals("data_mu")) {
				this.data_mu = Double.parseDouble(entries[1]);
			} else if (entries[0].equals("data_sigma")) {
				this.data_sigma = Double.parseDouble(entries[1]);
			} else if (entries[0].equals("data_gold")) {
				this.data_gold = (int)Integer.parseInt(entries[1]);
			} else if (entries[0].equals("workers")) {
				this.workers_points = (int)Integer.parseInt(entries[1]);
			} else if (entries[0].equals("worker_mu_down")) {
				this.worker_mu_down = Double.parseDouble(entries[1]);
			} else if (entries[0].equals("worker_mu_up")) {
				this.worker_mu_up = Double.parseDouble(entries[1]);
			} else if (entries[0].equals("worker_sigma_down")) {
				this.worker_sigma_down = Double.parseDouble(entries[1]);
			} else if (entries[0].equals("worker_sigma_up")) {
				this.worker_sigma_up = Double.parseDouble(entries[1]);
			} else if (entries[0].equals("worker_rho_down")) {
				this.worker_rho_down = Double.parseDouble(entries[1]);
			} else if (entries[0].equals("worker_rho_up")) {
				this.worker_rho_up = Double.parseDouble(entries[1]);
			} else {
				System.err.println("Error in synthetic options file variables");
			}

		}

	}

}
