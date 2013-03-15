package com.datascience.core;

import com.datascience.core.nominal.NominalAlgorithm;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.base.Category;
import com.datascience.gal.*;
import com.datascience.galc.ContinuousIpeirotis;
import com.datascience.galc.ContinuousProject;
import com.datascience.mv.BatchMV;
import com.datascience.mv.IncrementalMV;
import com.datascience.serialization.ISerializer;
import com.datascience.serialization.json.JSONUtils;
import com.google.common.base.Objects;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Konrad Kurdej
 */
public class JobFactory {

	protected ISerializer serializer;

	public JobFactory(ISerializer serializer){
		this.serializer = serializer;
	}

	protected interface Creator{
		NominalAlgorithm create();
	}

	public static class AlgorithmType{
		String algorithm;
		String type;

		public AlgorithmType(String alg, String type){
			this.algorithm = alg;
			this.type = type;
		}

		public int hashCode() {
			return Objects.hashCode(algorithm, type);
		}

		public boolean equals(Object obj) {
			if (!(obj instanceof AlgorithmType))
				return false;
			AlgorithmType other = (AlgorithmType) obj;
			return Objects.equal(algorithm, other.algorithm) &&
					Objects.equal(type, other.type);
		}
	}

	final static Map<AlgorithmType, Creator> ALG_FACTORY = new HashMap();
	{
		ALG_FACTORY.put(new AlgorithmType("DS", "batch"), new Creator() {

			@Override
			public NominalAlgorithm create() {
				return new BatchDawidSkene();
			}
		});
		ALG_FACTORY.put(new AlgorithmType("DS", "incremental"), new Creator() {
			@Override
			public NominalAlgorithm create() {
				return new IncrementalDawidSkene();
			}
		});
		ALG_FACTORY.put(new AlgorithmType("MV", "batch"), new Creator() {

			@Override
			public NominalAlgorithm create() {
				return new BatchMV();
			}
		});
		ALG_FACTORY.put(new AlgorithmType("MV", "incremental"), new Creator() {
			@Override
			public NominalAlgorithm create() {
				return new IncrementalMV();
			}
		});
	};

	protected NominalProject getNominalProject(Collection<Category> categories, String algorithm, String type){
		algorithm = algorithm.toUpperCase();
		type = type.toLowerCase();
		Creator creator = ALG_FACTORY.get(new AlgorithmType(algorithm, type));
		NominalProject np = new NominalProject(creator.create());
		np.initializeCategories(categories);
		return np;
	}
	
	public Job createNominalJob(JsonObject jo, String id){
		if (!jo.has("categories")){
			throw new IllegalArgumentException("You should provide categories list");
		}
		Collection<Category> categories = serializer.parse(jo.get("categories").toString(), JSONUtils.categorySetType);

		return new Job(
			getNominalProject(
				categories,
				jo.has("algorithm") ? jo.get("algorithm").getAsString() : "DS",
				jo.has("type") ? jo.get("type").getAsString() : "batch"),
			id);
	}
	
	public Job createContinuousJob(JsonObject jo, String id){
		return new Job(new ContinuousProject(new ContinuousIpeirotis()), id);
	}
}
