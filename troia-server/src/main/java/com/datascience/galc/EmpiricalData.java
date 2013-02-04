package com.datascience.galc;


import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Label;
import com.datascience.core.base.Worker;
import java.util.HashMap;
import java.util.Map;


public class EmpiricalData extends Data<Double> {

	private Map<String, LObject<Double>> objectsMap = new HashMap<String, LObject<Double>>();
	private Map<String, Worker<Double>>	workersMap = new HashMap<String, Worker<Double>>();


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

			LObject<Double> lObject = new LObject<Double>(objectname);
			Label<Double> label = new Label<Double>(value);


			Worker<Double> w = this.workersMap.get(workername);
			if (w == null) {
				w = new Worker<Double>(workername);
				this.workers.add(w);
				this.workersMap.put(workername, w);
			}
			AssignedLabel<Double> al = new AssignedLabel<Double>(w, lObject, label);
			w.addAssign(al);

			LObject d = this.objectsMap.get(objectname);
			if (d == null) {
				d = new LObject<Double>(objectname);
				this.objects.add(d);
				this.objectsMap.put(objectname,d);
			}
			assigns.add(al);
			// TODO: FIX
			// datums.put(d, assigns);

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

			LObject d = this.objectsMap.get(objectname);
			if (d == null) {
				d = new LObject<Double>(objectname);
				this.objects.add(d);
				this.objectsMap.put(objectname,d);
			}
			d.setGoldLabel(new Label(correctValue));
			// TODO: FIX
			// d.getResults().setGoldZeta(correctZeta);

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


			Worker<Double> w = this.workersMap.get(workername);
			if (w == null) {
				w = new Worker<Double>(workername);
				this.workers.add(w);
				this.workersMap.put(workername,w);
			}
			// TODO: FIX
			// WorkerResults wr = w.getResults()
			WorkerContResults wr = new WorkerContResults(null);
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


			LObject<Double> d = this.objectsMap.get(objectname);
			if (d == null) {
				d = new LObject<Double>(objectname);
				this.objects.add(d);
				this.objectsMap.put(objectname,d);
			}
			// TODO: FIX
			// d.getResults().setTrueValue(value);
			// d.getResults().setTrueZeta(zeta);

		}
	}

}
