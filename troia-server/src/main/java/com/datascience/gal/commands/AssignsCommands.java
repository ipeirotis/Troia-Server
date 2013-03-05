package com.datascience.gal.commands;

import java.util.ArrayList;
import java.util.Collection;

import com.datascience.executor.JobCommand;
import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.AssignedLabel;
import com.datascience.gal.Datum;

/**
 *
 * @author konrad
 */
public class AssignsCommands {
	
	static public class AddAssigns extends JobCommand<Object, AbstractDawidSkene> {

		private Collection<AssignedLabel> labels;
		
		public AddAssigns(Collection<AssignedLabel> labels){
			super(true);
			this.labels = labels;
		}
		
		public AddAssigns(AssignedLabel label){
			super(true);
			labels = new ArrayList<AssignedLabel>(1);
			labels.add(label);
		}
		
		@Override
		protected void realExecute() {
			project.addAssignedLabels(labels);
			setResult("Assigns added");
		}
	}
	
	static public class GetAssigns extends JobCommand<Collection<AssignedLabel>, AbstractDawidSkene> {
		
		public GetAssigns(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			Collection<AssignedLabel> labels = new ArrayList<AssignedLabel>();
			for (Datum d : project.getObjects().values()){
				labels.addAll(d.getAssignedLabels());
			}
			setResult(labels);
		}
	}
}
