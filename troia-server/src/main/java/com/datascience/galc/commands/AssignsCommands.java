package com.datascience.galc.commands;

import java.util.Collection;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.galc.ContinuousProject;


/**
 *
 * @author artur
 */
public class AssignsCommands {
	
	static public class AddAssigns extends GALCommandBase<Object> {

		private AssignedLabel<ContValue> assignedLabel;
		public AddAssigns(ContinuousProject cp, AssignedLabel<ContValue> al){
			super(cp, true);
			this.assignedLabel = al;
		}
		
		@Override
		protected void realExecute() {
			project.getData().addAssign(assignedLabel);
			setResult("Assigns added");
		}
	}
	
	static public class GetAssigns extends GALCommandBase<Collection<AssignedLabel<ContValue>>> {
		
		public GetAssigns(ContinuousProject cp){
			super(cp, false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getAssigns());
		}
	}
}
