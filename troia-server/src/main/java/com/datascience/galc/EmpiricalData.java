package com.datascience.galc;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;


import java.util.Map;
import java.util.HashMap;


public class EmpiricalData {

	private Map<String, LObject<ContValue>>	objects_index = new HashMap<String, LObject<ContValue>>();
	private Map<String, Worker<ContValue>>	workers_index = new HashMap<String, Worker<ContValue>>();



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

			AssignedLabel al = new AssignedLabel(workername, objectname, value);

//			TODO: FIX
//			WorkerCont w = this.workers_index.get(workername);
//			if (w == null) {
//				w = new WorkerCont(workername);
//				this.workers.add(w);
//				this.workers_index.put(workername,w);
//			}
//			w.addAssignedLabel(al);
//
//			DatumCont d = this.objects_index.get(objectname);
//			if (d == null) {
//				d = new DatumCont(objectname);
//				this.objects.add(d);
//				this.objects_index.put(objectname,d);
//			}
//			d.addAssignedLabel(al);
//			this.labels.add(al);
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

//			TODO: FIX
//			DatumCont d = this.objects_index.get(objectname);
//			if (d == null) {
//				d = new DatumCont(objectname);
//				this.objects.add(d);
//				this.objects_index.put(objectname,d);
//			}
//			d.getResults().setGold(true);
//			d.getResults().setGoldValue(correctValue);
//			d.getResults().setGoldZeta(correctZeta);
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

//			TODO: FIX
//			WorkerCont w = this.workers_index.get(workername);
//			if (w == null) {
//				w = new WorkerCont(workername);
//				this.workers.add(w);
//				this.workers_index.put(workername,w);
//			}
//			WorkerContResults wr = w.getResults();
//			wr.setTrueMu(mu);
//			wr.setTrueSigma(sigma);
//			wr.setTrueRho(rho);
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

//			TODO: FIX
//			DatumCont d = this.objects_index.get(objectname);
//			if (d == null) {
//				d = new DatumCont(objectname);
//				this.objects.add(d);
//				this.objects_index.put(objectname,d);
//			}
//			d.getResults().setTrueValue(value);
//			d.getResults().setTrueZeta(zeta);
		}
	}

}
