package com.datascience.core.base;

import com.datascience.gal.DatumResult;
import com.datascience.galc.DatumContResults;

/**
 * User: artur
 */
public class ResultsFactory {

	protected interface IDatumResultCreator<T, U>{
		U create(LObject<T> obj);
	}

	public static class DatumResultFactory implements IDatumResultCreator<String, DatumResult>{
		public DatumResult create(LObject<String> obj){
			return new DatumResult();
		}
	}

	public static class DatumContResultFactory implements  IDatumResultCreator<ContValue, DatumContResults>{
		public DatumContResults create(LObject<ContValue> obj){
			return new DatumContResults(obj);
		}
	}
}
