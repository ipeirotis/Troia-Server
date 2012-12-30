package com.datascience.gal.commands;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.AssignedLabel;
import com.datascience.gal.Datum;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author konrad
 */
public class AssignsCommands {
	
	static public class AddAssigns extends ProjectCommand<Object> {

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
		void realExecute() {
			ads.addAssignedLabels(labels);
			setResult("Assigns added");
		}
	}
	
	static public class GetAssigns extends ProjectCommand<Map<String, Datum>> {
		
		public GetAssigns(AbstractDawidSkene ads){
			super(ads, false);
		}
		
		@Override
		void realExecute() {
			setResult(ads.getObjects());
		}
	}
}
