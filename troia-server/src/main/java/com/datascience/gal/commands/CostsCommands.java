package com.datascience.gal.commands;

import java.util.Collection;

import com.datascience.gal.Category;
import com.datascience.gal.MisclassificationCost;

/**
 *
 * @author konrad
 */
public class CostsCommands {
	
	static public class SetCosts extends DSCommandBase<Object> {

		private Collection<MisclassificationCost> costs;
		
		public SetCosts(Collection<MisclassificationCost> costs){
			super(true);
			this.costs = costs;
		}
		
		@Override
		protected void realExecute() {
			ads.addMisclassificationCosts(costs);
			setResult("Costs set");
		}
	}
	
	/** This is not a bug - we are using categories here as they gsoned gives us costs
	 * */
	static public class GetCosts extends DSCommandBase<Collection<Category>> {
		
		public GetCosts(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(ads.getCategories().values());
		}
	}
}
