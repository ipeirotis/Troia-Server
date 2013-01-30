package com.datascience.galc;

import java.util.Set;
import java.util.HashSet;

import com.datascience.utils.objects.Worker;
import com.google.common.base.Objects;

public class WorkerCont extends Worker{

	private WorkerContResults results;
	
	public WorkerCont(String name) {

		super(name);
		results  = new WorkerContResults();
		
	}

	public WorkerContResults getResults() {
		return results;
	}

	public void setResults(WorkerContResults results) {
		this.results = results;
	}

	public void computeZetaValues() {
		Set<AssignedLabel> labels = getLabels();
		int n = labels.size();
		double mu_worker = 0.0;
		double mu_square = 0.0;
		for (AssignedLabel al : labels) {
			mu_worker += al.getLabel();
			mu_square += Math.pow(al.getLabel(), 2);
		}

		results.setEst_mu(mu_worker/n);
		results.setEst_sigma(Math.sqrt((1.0 / n) * (mu_square - Math.pow(mu_worker, 2) / n)));
		//System.out.println(this.toString());
		if(results.getEst_sigma()==0.0) {
			results.setEst_sigma(0.00000000001);
			System.out.println("[Single Label Worker: " +this.getName()+"]");
		}

		for (AssignedLabel al : labels) {
			Double z = (al.getLabel() - results.getEst_mu()) / results.getEst_sigma();
			AssignedLabel zl = new AssignedLabel(al.getWorkerName(), al.getObjectName(), z);
			results.getZetaValues().add(zl);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			       .add("name", getName())
			       .add("est_rho", results.getEst_rho())
			       .add("true_rho", results.getTrueRho())
			       .add("est_mu", results.getEst_mu())
			       .add("true_mu", results.getTrueMu())
			       .add("est_sigma", results.getEst_sigma())
			       .add("true_sigma", results.getTrueSigma())
			       .toString();
	}

}
