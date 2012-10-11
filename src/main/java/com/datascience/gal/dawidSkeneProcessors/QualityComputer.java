package com.datascience.gal.dawidSkeneProcessors;

import org.apache.log4j.Logger;

import com.datascience.gal.DawidSkene;
import com.datascience.gal.service.DawidSkeneCache;

public class QualityComputer extends DawidSkeneProcessor {

	protected QualityComputer(String id, DawidSkeneCache cache) {
		super(id, cache);
	}

	@Override
	public void run() {
		logger.info("Executing quality computer for "+this.getDawidSkeneId()+".");
		DawidSkene ds = this.getCache().getDawidSkeneForEditing(this.getDawidSkeneId(),this);
		ds.computeProjectQuality();
		this.getCache().insertDawidSkene(ds,this);
		this.setState(DawidSkeneProcessorState.FINISHED);
		logger.info("Quality computer for "+this.getDawidSkeneId()+" finished.");
	}

	
	private static Logger logger = Logger.getLogger(QualityComputer.class);
}
