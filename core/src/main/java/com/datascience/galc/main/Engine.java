package com.datascience.galc.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.apache.log4j.Logger;

import com.datascience.galc.ContinuousIpeirotis;

import com.datascience.core.results.DatumContResults;
import com.datascience.galc.EmpiricalData;
import com.datascience.core.results.WorkerContResults;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.IData;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;


class ReportGenerator {

	private ContinuousIpeirotis	ip;
	
	private static Logger logger = Logger.getLogger(ReportGenerator.class);

	public ReportGenerator(ContinuousIpeirotis ip, EngineContext ctx) {
		this.ip = ip;
	}

	/**
	 * 
	 */
	double getCorrelationRelativeError() {

		Double relRhoError = 0.0;
		int n = 0;
		for (WorkerContResults wr: ip.getResults().getWorkerResults(ip.getData().getWorkers()).values()) {
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
		for (WorkerContResults wr: ip.getResults().getWorkerResults(ip.getData().getWorkers()).values()) {
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
		for (Worker w : ip.getData().getWorkers()){
			WorkerContResults wr = ip.getResults().getWorkerResult(w);
			sb.append(w.getName() + "\t" + ip.getData().getWorkerAssigns(w).size() + "\t" + wr.getEst_mu() + "\t" + wr.getEst_sigma() + "\t" + wr.getEst_rho() + "\t" + wr.getTrueMu() + "\t" + wr.getTrueSigma() + "\t" + wr.getTrueRho());
			sb.append("\n");
		}
		
		String out = "\nAverage absolute estimation error for correlation values: " + getCorrelationAbsoluteError() + "\n"
				+ "Average relative estimation error for correlation values: " + getCorrelationRelativeError();
		
		
		logger.info(out);
		
		sb.append(out);
		return sb.toString();
	}

	/**
	 * @return
	 */
	Double estimateDistributionSigma() {

		Double nominator_sigma = 0.0;
		Double denominator_sigma = 0.0;
		for (WorkerContResults wr : ip.getResults().getWorkerResults(ip.getData().getWorkers()).values()) {
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
		for (WorkerContResults wr : ip.getResults().getWorkerResults(ip.getData().getWorkers()).values()) {
			Double b = wr.getBeta();
			Double coef = Math.sqrt(b * b - b);
			Double m = wr.getEst_mu();
			nominator_mu += coef * m;
			denominator_mu += b;
		}
		Double est_mu = nominator_mu / denominator_mu;
		return est_mu;
	}

	public String generateDistributionReport() {

		String out = "Estimated mu = " + estimateDistributionMu() + "\n" + "Estimated sigma = "
				+ estimateDistributionSigma();
		logger.info(out);
		return out;
	}

	double getZetaAbsoluteErrorObject() {

//		Double avgAbsError = 0.0;
//		int n = 0;
//		for (DatumContResults dr : ip.getObjectsResults().values()) {
//			if (  dr.getTrueZeta() == null ) continue;
//			n++;
//			double estZ = dr.getEst_zeta();
//			double realZ = dr.getTrueZeta();
//			double absDiff = Math.abs(realZ - estZ);
//			avgAbsError += absDiff;
//		}
//		return avgAbsError/n;
		return 0;
	}

	double getZetaRelativeErrorObject() {

//		Double avgRelError = 0.0;
//		int n = 0;
//		for (DatumContResults dr : ip.getObjectsResults().values()) {
//			if ( dr.getTrueZeta() == null) continue;
//			n++;
//			double estZ = dr.getEst_zeta();
//			double realZ = dr.getTrueZeta();
//			double absDiff = Math.abs(realZ - estZ);
//			double relDiff = Math.abs(absDiff / realZ);
//
//			avgRelError += relDiff/n;
//		}
//		return avgRelError;
		return 0;
	}

	public String generateObjectReport() {

		double mu = this.estimateDistributionMu();
		double sigma = this.estimateDistributionSigma();
		StringBuffer sb = new StringBuffer();
		for (LObject<ContValue> d : ip.getData().getObjects()){
			DatumContResults dr = ip.getResults().getDatumResult(d);
			dr.setDistributionMu(mu);
			dr.setDistributionSigma(sigma);
			sb.append(d.getName() +"\t" + ip.getAverageLabel(d) + "\t" + dr.getEst_value() + "\t" + dr.getEst_zeta() + "\t"); 
			sb.append(d.getEvaluationLabel() != null ? d.getEvaluationLabel().getValue() : "null" + "\t"); 
			sb.append(d.getEvaluationLabel() != null ? d.getEvaluationLabel().getZeta() : "null");
		  sb.append("\n");
			
		}
		
		
		String out = "\nAverage absolute estimation error for z-values: " + getZetaAbsoluteErrorObject() + "\n"
				+ "Average relative estimation error for z-values: " + getZetaRelativeErrorObject();
		logger.info(out);
		
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

	/**
	 * Logger for this class
	 */
	private static Logger logger = Logger.getLogger(Engine.class);

	public Engine(EngineContext ctx) {
		this.ctx = ctx;
	}

	public void execute() {

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

		ContinuousIpeirotis ip = new ContinuousIpeirotis();
		ip.setIterations(20);
		ip.setEpsilon(1e-5);
		ip.compute();

		ReportGenerator rpt = new ReportGenerator(ip, ctx);

		// Report about distributional estimates
		rpt.writeReportToFile(ctx.getOutputFolder(), "results-distribution.txt", rpt.generateDistributionReport());

		// Give report for objects
		rpt.writeReportToFile(ctx.getOutputFolder(), "results-objects.txt", rpt.generateObjectReport());

		// Give report for workers
		rpt.writeReportToFile(ctx.getOutputFolder(), "results-workers.txt", rpt.generateWorkerReport());

		logger.info("Results in folder: " + ctx.getOutputFolder());
	}


}
