package com.datascience.core.results;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;

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

	public static abstract class DatumResultCreator<T, U>{
		String clazz;
		abstract U create(LObject<T> obj);
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

	public static class DatumResultFactory extends DatumResultCreator<String, DatumResult> {
		public DatumResult create(LObject<String> obj){
			return new DatumResult();
		}
	}

	public static class DatumContResultFactory extends DatumResultCreator<ContValue, DatumContResults> {
		public DatumContResults create(LObject<ContValue> obj){
			return new DatumContResults(obj);
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	public static abstract class WorkerResultCreator<T, U>{
		String clazz;
		abstract U create(Worker<T> w);
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

	public static class WorkerResultNominalFactory extends WorkerResultCreator<String, WorkerResult> {

		protected Collection<String> categories;

		public void setCategories(Collection<String> categories){
			this.categories = categories;
		}

		public WorkerResult create(Worker<String> obj){
			return new WorkerResult(categories);
		}
	}

	public static class WorkerContResultFactory extends WorkerResultCreator<ContValue, WorkerContResults>{
		public WorkerContResults create(Worker<ContValue> obj){
			return new WorkerContResults(obj);
		}
	}
}
