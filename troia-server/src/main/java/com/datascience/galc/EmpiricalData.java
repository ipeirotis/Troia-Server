package com.datascience.galc;

import java.util.Map;
import java.util.HashMap;


public class EmpiricalData extends Data {

	private Map<String, DatumCont>	objects_index = new HashMap<String, DatumCont>();
	private Map<String, Worker>	workers_index = new HashMap<String, Worker>();
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

			Worker w = this.workers_index.get(workername);
			if (w == null) {
				w = new Worker(workername);
				this.workers.add(w);
				this.workers_index.put(workername,w);
			}
			w.addAssignedLabel(al);

			DatumCont d = this.objects_index.get(objectname);
			if (d == null) {
				d = new DatumCont(objectname);
				this.objects.add(d);
				this.objects_index.put(objectname,d);
			}
			d.addAssignedLabel(al);
			this.labels.add(al);
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

			DatumCont d = this.objects_index.get(objectname);
			if (d == null) {
				d = new DatumCont(objectname);
				this.objects.add(d);
				this.objects_index.put(objectname,d);
			}
			d.setGold(true);
			d.setGoldValue(correctValue);
			d.setGoldZeta(correctZeta);
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

			Worker w = this.workers_index.get(workername);
			if (w == null) {
				w = new Worker(workername);
				this.workers.add(w);
				this.workers_index.put(workername,w);
			}
			w.setTrueMu(mu);
			w.setTrueSigma(sigma);
			w.setTrueRho(rho);
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

			DatumCont d = this.objects_index.get(objectname);
			if (d == null) {
				d = new DatumCont(objectname);
				this.objects.add(d);
				this.objects_index.put(objectname,d);
			}
			d.setTrueValue(value);
			d.setTrueZeta(zeta);
		}
	}

}
