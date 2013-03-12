package com.datascience.gal.commands;

import java.util.Collection;

import com.datascience.executor.JobCommand;
import com.datascience.core.base.Category;
import com.datascience.core.nominal.NominalProject;

/**
 *
 * @author konrad
 */
public class CostsCommands {
	
//	static public class SetCosts extends JobCommand<Object, NominalProject> {
//
//		private Collection<MisclassificationCost> costs;
//
//		public SetCosts(Collection<MisclassificationCost> costs){
//			super(true);
//			this.costs = costs;
//		}
//
//		@Override
//		protected void realExecute() {
//			project.addMisclassificationCosts(costs);
//			setResult("Costs set");
//		}
//	}
	
	/** This is not a bug - we are using categories here as they gsoned gives us costs
	 * */
	static public class GetCosts extends JobCommand<Collection<Category>, NominalProject> {
		
		public GetCosts(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getCategories());
		}
	}
}
