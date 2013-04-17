package com.datascience.gal.commands;

import com.datascience.core.jobs.JobCommand;
import com.datascience.core.nominal.NominalProject;
import com.datascience.utils.CostMatrix;

/**
 *
 * @author konrad
 */
public class CostsCommands {
	
	/** This is not a bug - we are using categories here as they gsoned gives us costs
	 * */
	static public class GetCosts extends JobCommand<CostMatrix<String>, NominalProject> {
		
		public GetCosts(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getCostMatrix());
		}
	}
}
