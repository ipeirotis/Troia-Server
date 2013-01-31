package com.datascience.gal.commands;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.AssignedLabel;
import com.datascience.gal.Datum;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author konrad
 */
public class AssignsCommands {
	
	static public class AddAssigns extends DSCommandBase<Object> {

		private Collection<AssignedLabel> labels;
		
		public AddAssigns(AbstractDawidSkene ads, Collection<AssignedLabel> labels){
			super(ads, true);
			this.labels = labels;
		}
		
		public AddAssigns(AbstractDawidSkene ads, AssignedLabel label){
			super(ads, true);
			labels = new ArrayList<AssignedLabel>(1);
			labels.add(label);
		}
		
		@Override
		protected void realExecute() {
			ads.addAssignedLabels(labels);
			setResult("Assigns added");
		}
	}
	
	static public class GetAssigns extends DSCommandBase<Collection<AssignedLabel>> {
		
		public GetAssigns(AbstractDawidSkene ads){
			super(ads, false);
		}
		
		@Override
		protected void realExecute() {
			Collection<AssignedLabel> labels = new ArrayList<AssignedLabel>();
			for (Datum d : ads.getObjects().values()){
				labels.addAll(d.getAssignedLabels());
			}
			setResult(labels);
		}
	}
}
