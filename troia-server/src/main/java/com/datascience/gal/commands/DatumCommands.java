package com.datascience.gal.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import com.datascience.core.base.LObject;
import com.datascience.core.commands.ParamChecking;
import com.datascience.executor.JobCommand;
import com.datascience.gal.CategoryValue;
import com.datascience.gal.NominalProject;
import com.datascience.gal.decision.ILabelProbabilityDistributionCalculator;

/**
 *
 * @author artur
 */
public class DatumCommands {

	static public class GetDatumCategoryProbability extends JobCommand<Collection<CategoryValue>, NominalProject> {
		
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
			LObject<String> datum = ParamChecking.datum(project, datumId);
			Collection<CategoryValue> cp = new ArrayList<CategoryValue>();
			for (Entry<String, Double> e : labelProbabilityDistributionCalculator.calculateDistribution(datum, project).entrySet()){
				cp.add(new CategoryValue(e.getKey(), e.getValue()));
			}
			setResult(cp);
		}
	}
}
