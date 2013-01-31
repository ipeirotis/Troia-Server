package com.datascience.galc.dataGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.apache.log4j.Logger;

import com.datascience.galc.AssignedLabel;
import com.datascience.galc.Data;
import com.datascience.galc.DatumCont;
import com.datascience.galc.DatumContResults;
import com.datascience.galc.Utils;
import com.datascience.galc.WorkerCont;
import com.datascience.galc.WorkerContResults;

public class SyntheticData extends Data {

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

	public SyntheticData(Boolean verbose, String file) {

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

		for (DatumCont d : this.getObjects()) {
			if(i++ < g_gold_objects) {
				DatumContResults dr = d.getResults();
				dr.setGold(true);
				dr.setGoldValue(dr.getTrueValue());
				dr.setGoldZeta(dr.getTrueZeta());
				d.setResults(dr);
			}
		}
	}

	private void createLabels() {

		// Generate Observation Values y_ij

		for (DatumCont d : this.getObjects()) {
			for (WorkerCont w : this.getWorkers()) {
				
				DatumContResults dr = d.getResults();
				Double datum_z = (dr.getTrueValue() - this.data_mu) / this.data_sigma;
				WorkerContResults wr = w.getResults();
				Double label_mu = wr.getTrueMu() + wr.getTrueRho() * wr.getTrueSigma() * datum_z;
				Double label_sigma = Math.sqrt(1 - Math.pow(wr.getTrueRho(), 2)) * wr.getTrueSigma();

				Generator labelGenerator = new Generator(Generator.Distribution.GAUSSIAN);
				labelGenerator.setGaussianParameters(label_mu, label_sigma);
				Double label = labelGenerator.nextData();

				AssignedLabel al = new AssignedLabel(w.getName(), d.getName(), label);
				this.getLabels().add(al);
				w.addAssignedLabel(al);
				d.addAssignedLabel(al);
			}
		}
	}

	private void createObjects(int k_objects) {

		// Generate Object Real Values x_i
		for (int i = 0; i < k_objects; i++) {
			DatumCont d = new DatumCont("Object" + (i + 1));
			DatumContResults dr = d.getResults();
			Double v = datumGenerator.nextData();
			Double z = (v-this.data_mu)/this.data_sigma;
			dr.setTrueValue(v);
			dr.setTrueZeta(z);
			this.getObjects().add(d);
		}
	}

	private void createWorkers(int l_workers) {

		// Generate Worker Characteristics
		for (int i = 0; i < l_workers; i++) {
			WorkerCont w = new WorkerCont("Worker" + (i + 1));
			WorkerContResults wr = w.getResults();
			wr.setTrueMu(muGenerator.nextData());
			wr.setTrueSigma(sigmaGenerator.nextData());
			wr.setTrueRho(rhoGenerator.nextData());
			this.getWorkers().add(w);
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
			for (AssignedLabel al : this.getLabels()) {
				String line = al.getWorkerName() + "\t" + al.getObjectName() + "\t" + al.getLabel() + "\n";
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
			for (DatumCont d: this.getObjects()) {
				DatumContResults dr = d.getResults();
				String line = d.getName() + "\t" + dr.getTrueValue() + "\t" + dr.getTrueZeta() + "\n";
				bw.write(line);
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
			for (WorkerCont w : this.getWorkers()) {
				WorkerContResults wr = w.getResults();
				String line = w.getName() + "\t" + wr.getTrueRho() + "\t" + wr.getTrueMu() + "\t" + wr.getTrueSigma() + "\t"
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
			for (DatumCont d: this.getObjects()) {
				DatumContResults dr = d.getResults();
				if(dr.isGold()) {
					String line = d.getName() + "\t" + dr.getTrueValue() + "\t" + dr.getTrueZeta() + "\n";
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
