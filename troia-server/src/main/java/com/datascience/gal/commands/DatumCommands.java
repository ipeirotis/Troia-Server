package com.datascience.gal.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import com.datascience.core.base.LObject;
import com.datascience.core.commands.ParamChecking;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.executor.JobCommand;
import com.datascience.core.nominal.NominalProject;

/**
 *
 * @author artur
 */
public class DatumCommands {

	static public class GetDatumCategoryProbability extends JobCommand<Collection<CategoryValue>, NominalProject> {
		
		private String datumId;

		public GetDatumCategoryProbability(String datumId){
			super(false);
			this.datumId = datumId;
		}
		
		@Override
		protected void realExecute() {
			LObject<String> datum = ParamChecking.datum(project, datumId);
			Collection<CategoryValue> cp = new ArrayList<CategoryValue>();
			for (Entry<String, Double> e : project.getAlgorithm().calculateDistribution(datum).entrySet()){
				cp.add(new CategoryValue(e.getKey(), e.getValue()));
			}
			setResult(cp);
		}
	}
}
