package com.datascience.gal.commands;

import java.util.Collection;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.Datum;

/**
 *
 * @author artur
 */
public class DatumCommands {
	
	static public class AddGoldDatums extends ProjectCommand<Object> {

		private Collection<CorrectLabel> labels;
		
		public AddGoldDatums(AbstractDawidSkene ads, Collection<CorrectLabel> labels){
			super(ads, true);
			this.labels = labels;
		}
		
		@Override
		void realExecute() {
			ads.addCorrectLabels(labels);
			setResult("Correct datums added");
		}
	}
	
	static public class GetGoldDatums extends ProjectCommand<Collection<CorrectLabel>> {
		
		public GetGoldDatums(AbstractDawidSkene ads){
			super(ads, false);
		}
		
		@Override
		void realExecute() {
			setResult(ads.getGoldDatums());
		}
	}
	
	static public class AddEvaluationDatums extends ProjectCommand<Object> {

		private Collection<CorrectLabel> labels;
		
		public AddEvaluationDatums(AbstractDawidSkene ads, Collection<CorrectLabel> labels){
			super(ads, true);
			this.labels = labels;
		}
		
		@Override
		void realExecute() {
			ads.addEvaluationDatums(labels);
			setResult("Evaluation datums added");
		}
	}
	
	static public class GetEvaluationDatums extends ProjectCommand<Collection<CorrectLabel>> {
		
		public GetEvaluationDatums(AbstractDawidSkene ads){
			super(ads, false);
		}
		
		@Override
		void realExecute() {
			setResult(ads.getEvaluationDatums());
		}
	}
	
	static public class GetDatums extends ProjectCommand<Collection<Datum>> {
		
		public GetDatums(AbstractDawidSkene ads){
			super(ads, false);
		}
		
		@Override
		void realExecute() {
			setResult(ads.getObjects().values());
		}
	}
	
	static public class GetDatum extends ProjectCommand<Datum> {
		
		private String datumId;
		
		public GetDatum(AbstractDawidSkene ads, String datumId){
			super(ads, false);
			this.datumId = datumId;
		}
		
		@Override
		void realExecute() {
			setResult(ads.getObject(datumId));
		}
	}
}
