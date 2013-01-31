package com.datascience.gal.commands;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.Category;
import com.datascience.gal.MisclassificationCost;
import java.util.Collection;

/**
 *
 * @author konrad
 */
public class CostsCommands {
	
	static public class SetCosts extends DSCommandBase<Object> {

		private Collection<MisclassificationCost> costs;
		
		public SetCosts(AbstractDawidSkene ads, Collection<MisclassificationCost> costs){
			super(ads, true);
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
		
		public GetCosts(AbstractDawidSkene ads){
			super(ads, false);
		}
		
		@Override
		protected void realExecute() {
			setResult(ads.getCategories().values());
		}
	}
}
