package com.datascience.gal.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import com.datascience.executor.JobCommand;
import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.CategoryValue;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.Datum;
import com.datascience.gal.decision.ILabelProbabilityDistributionCalculator;

/**
 *
 * @author artur
 */
public class DatumCommands {
	
	static public class MarkDataAsGold extends JobCommand<Object, AbstractDawidSkene> {

		private Collection<CorrectLabel> labels;
		
		public MarkDataAsGold(Collection<CorrectLabel> labels){
			super(true);
			this.labels = labels;
		}
		
		@Override
		protected void realExecute() {
			project.markObjectsAsGold(labels);
			setResult("Data marked as gold");
		}
	}
	
	static public class AddGoldData extends JobCommand<Object, AbstractDawidSkene> {

		private Collection<CorrectLabel> labels;
		
		public AddGoldData(Collection<CorrectLabel> labels){
			super(true);
			this.labels = labels;
		}
		
		@Override
		protected void realExecute() {
			project.addCorrectLabels(labels);
			setResult("Correct data added");
		}
	}
	
	static public class GetGoldData extends JobCommand<Collection<CorrectLabel>, AbstractDawidSkene> {
		
		public GetGoldData(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getGoldDatums());
		}
	}
	
	static public class AddEvaluationData extends JobCommand<Object, AbstractDawidSkene> {

		private Collection<CorrectLabel> labels;
		
		public AddEvaluationData(Collection<CorrectLabel> labels){
			super(true);
			this.labels = labels;
		}
		
		@Override
		protected void realExecute() {
			project.addEvaluationDatums(labels);
			setResult("Evaluation datums added");
		}
	}
	
	static public class GetEvaluationData extends JobCommand<Collection<CorrectLabel>, AbstractDawidSkene> {
		
		public GetEvaluationData(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getEvaluationDatums().values());
		}
	}
	
	static public class AddData extends JobCommand<Object, AbstractDawidSkene> {

		private Collection<String> objects;
		
		public AddData(Collection<String> objects){
			super(true);
			this.objects = objects;
		}
		
		@Override
		protected void realExecute() {
			project.addObjects(objects);
			setResult("Object without labels added");
		}
	}
	
	static public class GetData extends JobCommand<Collection<Datum>, AbstractDawidSkene> {
		
		private String type;
		
		public GetData(String type){
			super(false);
			this.type = type;
		}
		
		@Override
		protected void realExecute() {
			if (type.equals("assigned")) {
				setResult(project.getObjects().values());
			}
			else if (type.equals("unassigned")) {
				setResult(project.getObjectsWithNoLabels().values());
			}
			else {
				Collection<Datum> res = new ArrayList<Datum>();
				res.addAll(project.getObjects().values());
				res.addAll(project.getObjectsWithNoLabels().values());
				setResult(res);
			}
		}
	}
	
	static public class GetDatum extends JobCommand<Datum, AbstractDawidSkene> {
		
		private String datumId;
		
		public GetDatum(String datumId){
			super(false);
			this.datumId = datumId;
		}
		
		@Override
		protected void realExecute() {
			setResult(ParamChecking.datum(project, datumId));
		}
	}
	
	static public class GetDatumCategoryProbability extends JobCommand<Collection<CategoryValue>, AbstractDawidSkene> {
		
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
			Datum datum = ParamChecking.datum(project, datumId);
			Collection<CategoryValue> cp = new ArrayList<CategoryValue>();
			for (Entry<String, Double> e : labelProbabilityDistributionCalculator.calculateDistribution(datum, project).entrySet()){
				cp.add(new CategoryValue(e.getKey(), e.getValue()));
			}
			setResult(cp);
		}
	}
}
