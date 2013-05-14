package com.datascience.core.results;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: artur
 */
public class  ResultsFactory {

	protected static interface Creator<T> {
		T create();
	}

	public static abstract class DatumResultCreator<T>{
		String clazz;
		abstract T create();
		public DatumResultCreator(){
			clazz = this.getClass().toString();
		}
	}

	public static class DatumResultFactoryCreator {
		final static Map<String, Creator<DatumResultCreator>> FACTORY_CREATOR= new HashMap();
		{
			FACTORY_CREATOR.put("class com.datascience.core.results.ResultsFactory$DatumResultFactory", new Creator<DatumResultCreator>(){
				@Override
				public DatumResultCreator create() {
					return new DatumResultFactory();
				}
			});
			FACTORY_CREATOR.put("class com.datascience.core.results.ResultsFactory$DatumContResultFactory", new Creator<DatumResultCreator>(){
				@Override
				public DatumResultCreator create() {
					return new DatumContResultFactory();
				}
			});
		};
		public DatumResultCreator create(String clazz){
			return FACTORY_CREATOR.get(clazz).create();
		}
	}

	public static class DatumResultFactory extends DatumResultCreator<DatumResult> {
		public DatumResult create(){
			return new DatumResult();
		}
	}

	public static class DatumContResultFactory extends DatumResultCreator<DatumContResults> {
		public DatumContResults create(){
			return new DatumContResults();
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	public static abstract class WorkerResultCreator<T>{
		String clazz;
		abstract T create();
		public WorkerResultCreator(){
			clazz = this.getClass().toString();
		}
	}

	public static class WorkerResultFactoryCreator {
		final static Map<String, Creator<WorkerResultCreator>> FACTORY_CREATOR= new HashMap();
		{
			FACTORY_CREATOR.put("class com.datascience.core.results.ResultsFactory$WorkerResultNominalFactory", new Creator<WorkerResultCreator>(){
				@Override
				public WorkerResultCreator create() {
					return new WorkerResultNominalFactory();
				}
			});
			FACTORY_CREATOR.put("class com.datascience.core.results.ResultsFactory$WorkerContResultFactory", new Creator<WorkerResultCreator>(){
				@Override
				public WorkerResultCreator create() {
					return new WorkerContResultFactory();
				}
			});
		};
		public WorkerResultCreator create(String clazz){
			return FACTORY_CREATOR.get(clazz).create();
		}
	}

	public static class WorkerResultNominalFactory extends WorkerResultCreator<WorkerResult> {

		protected Collection<String> categories;

		public void setCategories(Collection<String> categories){
			this.categories = categories;
		}

		public WorkerResult create(){
			return new WorkerResult(categories);
		}
	}

	public static class WorkerContResultFactory extends WorkerResultCreator<WorkerContResults>{
		public WorkerContResults create(){
			return new WorkerContResults();
		}
	}
}
