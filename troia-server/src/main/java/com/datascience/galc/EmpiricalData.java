package com.datascience.galc;


import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;


public class EmpiricalData extends Data<ContValue> {

	public EmpiricalData() {
		super();
	}

	public void loadLabelFile(String filename) {

		String[] lines = Utils.getFile(filename).split("\n");

		for (String line : lines) {
			String[] entries = line.split("\t");
			if (entries.length != 3) {
				throw new IllegalArgumentException("Error while loading from assigned labels file");
			}

			String workername = entries[0];
			String objectname = entries[1];
			Double value = Double.parseDouble(entries[2]);

			LObject<ContValue> lObject = getOrCreateObject(objectname);

			Worker<ContValue> worker = getOrCreateWorker(workername);
			AssignedLabel<ContValue> al = new AssignedLabel<ContValue>(worker, lObject, new ContValue(value));
			addAssign(al);
		}
	}

	public void loadGoldLabelsFile(String filename) {

		if(filename==null)
			return;

		String[] lines = Utils.getFile(filename).split("\n");

		for (String line : lines) {
			String[] entries = line.split("\t");
			if (entries.length != 3) {
				throw new IllegalArgumentException("Error while loading from gold labels file");
			}
			String objectname = entries[0];
			Double correctValue = Double.parseDouble(entries[1]);
			Double correctZeta = Double.parseDouble(entries[2]);

			LObject<ContValue> d = getOrCreateObject(objectname);
			d.setGoldLabel(new ContValue(correctValue, correctZeta));
			addGoldObject(d);
		}
	}

	public void loadTrueWorkerData(String filename) {

		String[] lines = Utils.getFile(filename).split("\n");

		for (String line : lines) {
			String[] entries = line.split("\t");
			if (entries.length != 4) {
				throw new IllegalArgumentException("Error while loading from assigned labels file");
			}

			String workername = entries[0];
			Double rho = Double.parseDouble(entries[1]);
			Double mu = Double.parseDouble(entries[2]);
			Double sigma = Double.parseDouble(entries[3]);

			Worker<ContValue> w = getOrCreateWorker(workername);
			WorkerContResults wr = new WorkerContResults(w);
			wr.setTrueMu(mu);
			wr.setTrueSigma(sigma);
			wr.setTrueRho(rho);
		}
	}

	public void loadTrueObjectData(String filename) {

		String[] lines = Utils.getFile(filename).split("\n");

		for (String line : lines) {
			String[] entries = line.split("\t");
			if (entries.length != 3) {
				throw new IllegalArgumentException("Error while loading from assigned labels file");
			}

			String objectname = entries[0];
			Double value = Double.parseDouble(entries[1]);
			Double zeta = Double.parseDouble(entries[2]);

			LObject<ContValue> d = getOrCreateObject(objectname);
			d.setEvaluationLabel(new ContValue(value, zeta));
			addEvaluationObject(d);
		}
	}
}
