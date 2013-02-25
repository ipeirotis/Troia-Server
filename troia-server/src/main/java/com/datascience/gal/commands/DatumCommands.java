package com.datascience.gal.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import com.datascience.gal.CategoryValue;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.Datum;
import com.datascience.gal.decision.ILabelProbabilityDistributionCalculator;

/**
 *
 * @author artur
 */
public class DatumCommands {
	
	static public class MarkDataAsGold extends DSCommandBase<Object> {

		private Collection<CorrectLabel> labels;
		
		public MarkDataAsGold(Collection<CorrectLabel> labels){
			super(true);
			this.labels = labels;
		}
		
		@Override
		protected void realExecute() {
			ads.markObjectsAsGold(labels);
			setResult("Data marked as gold");
		}
	}
	
	static public class AddGoldData extends DSCommandBase<Object> {

		private Collection<CorrectLabel> labels;
		
		public AddGoldData(Collection<CorrectLabel> labels){
			super(true);
			this.labels = labels;
		}
		
		@Override
		protected void realExecute() {
			ads.addCorrectLabels(labels);
			setResult("Correct data added");
		}
	}
	
	static public class GetGoldData extends DSCommandBase<Collection<CorrectLabel>> {
		
		public GetGoldData(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(ads.getGoldDatums());
		}
	}
	
	static public class AddEvaluationData extends DSCommandBase<Object> {

		private Collection<CorrectLabel> labels;
		
		public AddEvaluationData(Collection<CorrectLabel> labels){
			super(true);
			this.labels = labels;
		}
		
		@Override
		protected void realExecute() {
			ads.addEvaluationDatums(labels);
			setResult("Evaluation datums added");
		}
	}
	
	static public class GetEvaluationData extends DSCommandBase<Collection<CorrectLabel>> {
		
		public GetEvaluationData(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(ads.getEvaluationDatums().values());
		}
	}
	
	static public class AddData extends DSCommandBase<Object> {

		private Collection<String> objects;
		
		public AddData(Collection<String> objects){
			super(true);
			this.objects = objects;
		}
		
		@Override
		protected void realExecute() {
			ads.addObjects(objects);
			setResult("Object without labels added");
		}
	}
	
	static public class GetData extends DSCommandBase<Collection<Datum>> {
		
		private String type;
		
		public GetData(String type){
			super(false);
			this.type = type;
		}
		
		@Override
		protected void realExecute() {
			if (type.equals("assigned")) {
				setResult(ads.getObjects().values());
			}
			else if (type.equals("unassigned")) {
				setResult(ads.getObjectsWithNoLabels().values());
			}
			else {
				Collection<Datum> res = new ArrayList<Datum>();
				res.addAll(ads.getObjects().values());
				res.addAll(ads.getObjectsWithNoLabels().values());
				setResult(res);
			}
		}
	}
	
	static public class GetDatum extends DSCommandBase<Datum> {
		
		private String datumId;
		
		public GetDatum(String datumId){
			super(false);
			this.datumId = datumId;
		}
		
		@Override
		protected void realExecute() {
			setResult(ParamChecking.datum(ads, datumId));
		}
	}
	
	static public class GetDatumCategoryProbability extends DSCommandBase<Collection<CategoryValue>> {
		
		private String datumId;
		private ILabelProbabilityDistributionCalculator labelProbabilityDistributionCalculator;
		
		public GetDatumCategoryProbability(String datumId,
				ILabelProbabilityDistributionCalculator type){
			super(false);
			this.datumId = datumId;
			labelProbabilityDistributionCalculator = type;
		}
		
		@Override
		protected void realExecute() {
			Datum datum = ParamChecking.datum(ads, datumId);
			Collection<CategoryValue> cp = new ArrayList<CategoryValue>();
			for (Entry<String, Double> e : labelProbabilityDistributionCalculator.calculateDistribution(datum, ads).entrySet()){
				cp.add(new CategoryValue(e.getKey(), e.getValue()));
			}
			setResult(cp);
		}
	}
}
