package com.datascience.serialization.json;

import com.datascience.core.base.CategoryPair;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.core.stats.MatrixValue;
import com.datascience.core.stats.MultinomialConfusionMatrix;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: konrad
 */
public class MultinominalConfusionMatrixJSON {

	public static class ConfusionMatrixDeserializer implements JsonDeserializer<MultinomialConfusionMatrix> {

		@Override
		public MultinomialConfusionMatrix deserialize(JsonElement json,
				Type type, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject jobject = (JsonObject) json;
			Collection<String> categories =
					context.deserialize(jobject.get("categories"), JSONUtils.stringSetType);


			Collection<MatrixValue> matrixValues =
					context.deserialize(jobject.get("matrix"), JSONUtils.matrixValuesCollectionType);
			Map<CategoryPair, Double> matrix = new HashMap<CategoryPair, Double>();
			for (MatrixValue mv : matrixValues){
				matrix.put(new CategoryPair(mv.from, mv.to), mv.value);
			}


			Collection<CategoryValue> rowDenominatorValues =
					context.deserialize(jobject.get("rowDenominator"), JSONUtils.categoryValuesCollectionType);
			Map<String, Double> rowDenominator = new HashMap<String, Double>();
			for (CategoryValue cv : rowDenominatorValues) {
				rowDenominator.put(cv.categoryName, cv.value);
			}

			return new MultinomialConfusionMatrix(categories, matrix, rowDenominator);
		}
	}

	public static class ConfusionMatrixSerializer implements JsonSerializer<MultinomialConfusionMatrix> {

		@Override
		public JsonElement serialize(MultinomialConfusionMatrix arg0,
				Type arg1, JsonSerializationContext arg2) {
			JsonObject ret = new JsonObject();

			Collection<CategoryValue> cp = new ArrayList<CategoryValue>(arg0.rowDenominator.size());
			for (Map.Entry<String, Double> e : arg0.rowDenominator.entrySet()){
				cp.add(new CategoryValue(e.getKey(), e.getValue()));
			}
			ret.add("rowDenominator", arg2.serialize(cp));

			Collection<MatrixValue> mv = new ArrayList<MatrixValue>(arg0.getMatrix().size());
			for (Map.Entry<CategoryPair, Double> e : arg0.getMatrix().entrySet()){
				mv.add(new MatrixValue(e.getKey().from, e.getKey().to, e.getValue()));
			}
			ret.add("matrix", arg2.serialize(mv));

			ret.add("categories", arg2.serialize(arg0.getCategories()));
			return ret;
		}

	}
}
