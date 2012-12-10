package com.datascience.gal.dawidSkeneProcessors;

import org.apache.log4j.Logger;

import com.datascience.gal.DawidSkene;
import com.datascience.gal.service.DawidSkeneCache;

public class Evaluator extends DawidSkeneProcessor {

	
	protected Evaluator(String id, DawidSkeneCache cache) {
		super(id, cache);
	}

	@Override
	public void run() {
		DawidSkene ds = null;
		try {
			logger.info("Executing evaluator for "
					+ this.getDawidSkeneId() + ".");
			ds = this.getCache().getDawidSkeneForEditing(
					this.getDawidSkeneId(), this);
			ds.computeProjectQualityWithEvaluationData();
			logger.info("Evaluator finished.");
		}catch (Exception e) {
			logger.error("Failed to execute evaluator because : "
					+ e.getMessage());
		} finally {
			this.getCache().insertDawidSkene(ds, this);
			this.setState(DawidSkeneProcessorState.FINISHED);
		}
	}
	
	private static Logger logger = Logger.getLogger(Evaluator.class);

}
