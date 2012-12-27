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
	
	static public class AddGoldData extends ProjectCommand<Object> {

		private Collection<CorrectLabel> labels;
		
		public AddGoldData(AbstractDawidSkene ads, Collection<CorrectLabel> labels){
			super(ads, true);
			this.labels = labels;
		}
		
		@Override
		void realExecute() {
			ads.addCorrectLabels(labels);
			setResult("Correct data added");
		}
	}
	
	static public class GetGoldData extends ProjectCommand<Collection<CorrectLabel>> {
		
		public GetGoldData(AbstractDawidSkene ads){
			super(ads, false);
		}
		
		@Override
		void realExecute() {
			setResult(ads.getGoldDatums());
		}
	}
	
	static public class AddEvaluationData extends ProjectCommand<Object> {

		private Collection<CorrectLabel> labels;
		
		public AddEvaluationData(AbstractDawidSkene ads, Collection<CorrectLabel> labels){
			super(ads, true);
			this.labels = labels;
		}
		
		@Override
		void realExecute() {
			ads.addEvaluationDatums(labels);
			setResult("Evaluation datums added");
		}
	}
	
	static public class GetEvaluationData extends ProjectCommand<Collection<CorrectLabel>> {
		
		public GetEvaluationData(AbstractDawidSkene ads){
			super(ads, false);
		}
		
		@Override
		void realExecute() {
			setResult(ads.getEvaluationDatums());
		}
	}
	
	static public class GetData extends ProjectCommand<Collection<Datum>> {
		
		public GetData(AbstractDawidSkene ads){
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
