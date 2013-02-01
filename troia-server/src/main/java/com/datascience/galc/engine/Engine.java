package com.datascience.galc.engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.datascience.galc.Data;
import com.datascience.galc.DatumCont;
import com.datascience.galc.DatumContResults;
import com.datascience.galc.EmpiricalData;
import com.datascience.galc.Ipeirotis;
import com.datascience.galc.WorkerCont;
import com.datascience.galc.WorkerContResults;

class ReportGenerator {

	private Ipeirotis	ip;
	private boolean		verbose;

	public ReportGenerator(Ipeirotis ip, EngineContext ctx) {
		this.ip = ip;
		this.verbose = ctx.isVerbose();
	}

	/**
	 * 
	 */
	double getCorrelationRelativeError() {

		Double relRhoError = 0.0;
		int n = 0;
		for (WorkerCont w : ip.getWorkers()) {
			WorkerContResults wr = w.getResults();
			if (wr.getTrueRho()==null) 
				continue;
			n++;
			double estRho = wr.getEst_rho();
			double realRho = wr.getTrueRho();
			double absDiff = Math.abs(realRho - estRho);
			double relDiff = Math.abs(absDiff / realRho);
			relRhoError += relDiff;
		}
		return relRhoError/n;
	}

	/**
	 * 
	 */
	double getCorrelationAbsoluteError() {

		Double avgRhoError = 0.0;
		int n = 0;
		for (WorkerCont w : ip.getWorkers()) {
			WorkerContResults wr = w.getResults();
			if (wr.getTrueRho() ==null ) 
				continue;
			n++;
			double estRho = wr.getEst_rho();
			double realRho = wr.getTrueRho();
			double absDiff = Math.abs(realRho - estRho);

			avgRhoError += absDiff;
		}
		return avgRhoError/n;
	}

	/**
	 * 
	 */
	public String generateWorkerReport() {

		StringBuffer sb = new StringBuffer();
		sb.append("Name\tLabels\tEstMean\tEstStDev\tEstCorrelation\tTrueMean\tTrueStDev\tTrueCorrelation\n");
		for (WorkerCont w : ip.getWorkers()) {
			WorkerContResults wr = w.getResults();
			sb.append(w.getName() + "\t" + w.getLabels().size() + "\t" + wr.getEst_mu() + "\t" + wr.getEst_sigma() + "\t" + wr.getEst_rho() + "\t" + wr.getTrueMu() + "\t" + wr.getTrueSigma() + "\t" + wr.getTrueRho());
			sb.append("\n");
		}
		
		String out = "Average absolute estimation error for correlation values: " + getCorrelationAbsoluteError() + "\n"
				+ "Average relative estimation error for correlation values: " + getCorrelationRelativeError();
		
		
		if (!this.verbose)
			System.out.println(out);
		
		sb.append(out);
		return sb.toString();
	}

	/**
	 * @return
	 */
	Double estimateDistributionSigma() {

		Double nominator_sigma = 0.0;
		Double denominator_sigma = 0.0;
		for (WorkerCont w : ip.getWorkers()) {
			WorkerContResults wr = w.getResults();
			Double b = wr.getBeta();
			Double coef = Math.sqrt(b * b - b);
			Double s = wr.getEst_sigma();
			nominator_sigma += coef * s;
			denominator_sigma += b;
		}
		Double est_sigma = nominator_sigma / denominator_sigma;
		return est_sigma;
	}

	/**
	 * @return
	 */
	Double estimateDistributionMu() {

		// Estimate mu and sigma of distribution
		Double nominator_mu = 0.0;
		Double denominator_mu = 0.0;
		for (WorkerCont w : ip.getWorkers()) {
			WorkerContResults wr = w.getResults();
			Double b = wr.getBeta();
			Double coef = Math.sqrt(b * b - b);
			Double m = wr.getEst_mu();
			nominator_mu += coef * m;
			denominator_mu += b;
		}
		Double est_mu = nominator_mu / denominator_mu;
		return est_mu;
	}

	/**
	 * @param data_mu
	 * @param data_sigma
	 */
	public String generateDistributionReport() {

		String out = "Estimated mu = " + estimateDistributionMu() + "\n" + "Estimated sigma = "
				+ estimateDistributionSigma();
		if (!this.verbose)
			System.out.println(out);
		return out;
	}

	/**
	 * @param data_mu
	 * @param data_sigma
	 */
	double getZetaAbsoluteErrorObject() {

		Double avgAbsError = 0.0;
		int n = 0;
		for (DatumCont d : ip.getObjects()) {
			DatumContResults dr = d.getResults();
			if (  dr.getTrueZeta() == null ) continue;
			n++;
			double estZ = dr.getEst_zeta();
			double realZ = dr.getTrueZeta();
			double absDiff = Math.abs(realZ - estZ);
			avgAbsError += absDiff;
		}
		return avgAbsError/n;
	}

	/**
	 * @param data_mu
	 * @param data_sigma
	 */
	double getZetaRelativeErrorObject() {

		Double avgRelError = 0.0;
		int n = 0;
		for (DatumCont d : ip.getObjects()) {
			DatumContResults dr = d.getResults();
			if ( dr.getTrueZeta() == null) continue;
			n++;
			double estZ = dr.getEst_zeta();
			double realZ = dr.getTrueZeta();
			double absDiff = Math.abs(realZ - estZ);
			double relDiff = Math.abs(absDiff / realZ);

			avgRelError += relDiff/n;
		}
		return avgRelError;
	}

	public String generateObjectReport() {

		double mu = this.estimateDistributionMu();
		double sigma = this.estimateDistributionSigma();
		StringBuffer sb = new StringBuffer(); 
		for (DatumCont d : ip.getObjects()) {
			DatumContResults dr = d.getResults();
			dr.setDistributionMu(mu);
			dr.setDistributionSigma(sigma);
			sb.append(d.getName() +"\t" + d.getAverageLabel() + "\t" + dr.getEst_value() + "\t" + dr.getEst_zeta() + "\t" + dr.getTrueValue() + "\t" + dr.getTrueZeta());
		  sb.append("\n");
			
		}
		
		
		String out = "Average absolute estimation error for z-values: " + getZetaAbsoluteErrorObject() + "\n"
				+ "Average relative estimation error for z-values: " + getZetaRelativeErrorObject();
		if (!this.verbose)
			System.out.println(out);
		
		sb.append(out);
		return sb.toString();
	}

	public void writeReportToFile(String foldername, String filename, String reportcontent) {

		try {
			File outfile = new File(foldername + "/" + filename);

			if (outfile.getParent() != null) {
				File parentDir = new File(outfile.getParent());
				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			bw.write(reportcontent);
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

public class Engine {

	private EngineContext	ctx;

	public Engine(EngineContext ctx) {
		this.ctx = ctx;
	}

	public void execute() {

		Data data;

		EmpiricalData edata = new EmpiricalData();
		edata.loadLabelFile(ctx.getInputFile());
		if(ctx.hasTrueWorkersFile()) {
			edata.loadTrueWorkerData(ctx.getTrueWorkersFile());
		}
		if(ctx.hasTrueObjectsFile()) {
			edata.loadTrueObjectData(ctx.getTrueObjectsFile());
		}
		if(ctx.hasCorrectFile()) {
			edata.loadGoldLabelsFile(ctx.getCorrectFile());
		}
		data = edata;

		Ipeirotis ip = new Ipeirotis(data, ctx);

		ReportGenerator rpt = new ReportGenerator(ip, ctx);

		// Report about distributional estimates
		rpt.writeReportToFile(ctx.getOutputFolder(), "results-distribution.txt", rpt.generateDistributionReport());

			// Give report for objects
			rpt.writeReportToFile(ctx.getOutputFolder(), "results-objects.txt", rpt.generateObjectReport());

			// Give report for workers
			rpt.writeReportToFile(ctx.getOutputFolder(), "results-workers.txt", rpt.generateWorkerReport());

		if (ctx.isVerbose())
			System.out.println("Results in folder: " + ctx.getOutputFolder());

	}


	public void println(String mask, Object... args) {
		print(mask + "\n", args);
	}

	public void print(String mask, Object... args) {
		if (!ctx.isVerbose())
			return;

		String message;

		if (args.length > 0) {
			message = String.format(mask, args);
		} else {
			// without format arguments, print the mask/string as-is
			message = mask;
		}

		System.out.println(message);
	}
}
