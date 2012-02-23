package com.ipeirotis.gal;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.ipeirotis.gal.service.JSONUtils;

/**
 * TODO: implement with logistic regression TODO: bayesian multinomial
 * 
 * @author panos and josh
 * 
 */
public class MultinomialConfusionMatrix implements ConfusionMatrix {

    public static final ConfusionMatrixDeserializer deserializer = new ConfusionMatrixDeserializer();
    private Set<String> categories;
    private Map<CategoryPair, Double> matrix;
    public Map<String, Double> rowDenominator;

    private MultinomialConfusionMatrix(Collection<String> categories,
            Map<CategoryPair, Double> matrix, Map<String, Double> rowDenominator) {
        this.categories = new HashSet<String>(categories);
        this.matrix = matrix;
        this.rowDenominator = rowDenominator;
    }

    public MultinomialConfusionMatrix(Collection<Category> categories) {
        this.categories = new HashSet<String>();
        for (Category c : categories) {
            this.categories.add(c.getName());
        }

        this.matrix = new HashMap<CategoryPair, Double>();
        rowDenominator = new HashMap<String, Double>();

        // We now initialize the confusion matrix
        // and we set it to 0.9 in the diagonal and 0.0 elsewhere
        for (String from : this.categories) {
            for (String to : this.categories) {
                double value = 0.1 / (double) (this.categories.size() - 1);
                if (from.equals(to)) {
                    value = .9;
                }
                setErrorRate(from, to, value);
                incrementRowDenominator(from, value);
            }
        }
        normalize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ipeirotis.gal.ConfusionMatrix#incrementRowDenominator(java.lang.String
     * , double)
     */
    public void incrementRowDenominator(String from, double value) {
        double old = rowDenominator.containsKey(from) ? rowDenominator
                .get(from) : 0;
        rowDenominator.put(from, old + value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ipeirotis.gal.ConfusionMatrix#decrementRowDenominator(java.lang.String
     * , double)
     */
    public void decrementRowDenominator(String from, double value) {
        double old = rowDenominator.containsKey(from) ? rowDenominator
                .get(from) : 0;
        rowDenominator.put(from, Math.max(0., old - value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipeirotis.gal.ConfusionMatrix#empty()
     */
    public void empty() {
        // for (String from : this.categories) {
        // for (String to : this.categories) {
        // rowDenominator.put(from, 0.);
        // setErrorRate(from, to, 0.0);
        // }
        // }
        matrix = new HashMap<CategoryPair, Double>();
        rowDenominator = new HashMap<String, Double>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipeirotis.gal.ConfusionMatrix#normalize()
     */
    public void normalize() {

        for (String from : this.categories) {
            double from_marginal = rowDenominator.containsKey(from) ? rowDenominator
                    .get(from) : 0;

            for (String to : this.categories) {
                double error = getErrorRateBatch(from, to);
                double error_rate;

                // If the marginal across the "from" category is 0
                // this means that the worker has not even seen an object of the
                // "from"
                // category. In this case, we switch to Laplacean smoothing for
                // computing the matrix
                if (from_marginal == 0.0) {
                    // error_rate = Double.NaN;
                    // System.out.println(from_marginal);
                    error_rate = (error + 1)
                            / (from_marginal + this.categories.size());
                } else {
                    error_rate = error / from_marginal;
                }
                setErrorRate(from, to, error_rate);
            }
            rowDenominator.put(from, 1.);
            System.out.println(from + " " + rowDenominator.get(from));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipeirotis.gal.ConfusionMatrix#normalizeLaplacean()
     */
    public void normalizeLaplacean() {
        for (String from : this.categories) {
            double from_marginal = rowDenominator.get(from);
            for (String to : this.categories) {
                double error = getErrorRateBatch(from, to);
                setErrorRate(from, to, (error + 1)
                        / (from_marginal + this.categories.size()));
            }
            rowDenominator.put(from, 1.);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipeirotis.gal.ConfusionMatrix#addError(java.lang.String,
     * java.lang.String, java.lang.Double)
     */
    public void addError(String from, String to, Double error) {
        CategoryPair cp = new CategoryPair(from, to);
        double currentError = matrix.containsKey(cp) ? matrix.get(cp) : 0;
        incrementRowDenominator(from, error);
        this.matrix.put(cp, currentError + error);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipeirotis.gal.ConfusionMatrix#removeError(java.lang.String,
     * java.lang.String, java.lang.Double)
     */
    public void removeError(String from, String to, Double error) {
        CategoryPair cp = new CategoryPair(from, to);
        double currentError = matrix.containsKey(cp) ? matrix.get(cp) : 0;
        decrementRowDenominator(from, error);
        this.matrix.put(cp, Math.max(0, currentError - error));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ipeirotis.gal.ConfusionMatrix#getErrorRateBatch(java.lang.String,
     * java.lang.String)
     */
    public double getErrorRateBatch(String from, String to) {
        CategoryPair cp = new CategoryPair(from, to);
        return matrix.containsKey(cp) ? matrix.get(cp) : 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ipeirotis.gal.ConfusionMatrix#getNormalizedErrorRate(java.lang.String
     * , java.lang.String)
     */
    public double getNormalizedErrorRate(String from, String to) {
        CategoryPair cp = new CategoryPair(from, to);
        return matrix.get(cp) / rowDenominator.get(from);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ipeirotis.gal.ConfusionMatrix#getLaplaceNormalizedErrorRate(java.
     * lang.String, java.lang.String)
     */
    public double getLaplaceNormalizedErrorRate(String from, String to) {
        CategoryPair cp = new CategoryPair(from, to);
        return (1. + matrix.get(cp))
                / (rowDenominator.get(from) + categories.size());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ipeirotis.gal.ConfusionMatrix#getIncrementalErrorRate(java.lang.String
     * , java.lang.String)
     */
    public double getIncrementalErrorRate(String from, String to) {
        CategoryPair cp = new CategoryPair(from, to);
        return matrix.get(cp) / rowDenominator.get(from);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipeirotis.gal.ConfusionMatrix#setErrorRate(java.lang.String,
     * java.lang.String, java.lang.Double)
     */
    public void setErrorRate(String from, String to, Double cost) {
        CategoryPair cp = new CategoryPair(from, to);
        matrix.put(cp, cost);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipeirotis.gal.ConfusionMatrix#toString()
     */
    @Override
    public String toString() {
        return JSONUtils.gson.toJson(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipeirotis.gal.ConfusionMatrix#getCategories()
     */
    public Set<String> getCategories() {
        return new HashSet<String>(categories);
    }

    public static class ConfusionMatrixDeserializer implements
            JsonDeserializer<MultinomialConfusionMatrix> {

        @Override
        public MultinomialConfusionMatrix deserialize(JsonElement json,
                Type type, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jobject = (JsonObject) json;
            Collection<String> categories = JSONUtils.gson.fromJson(
                    jobject.get("categories"), JSONUtils.stringSetType);
            Map<CategoryPair, Double> matrix = JSONUtils.gson.fromJson(
                    jobject.get("matrix"), JSONUtils.categoryPairDoubleMapType);
            Map<String, Double> rowDenominator = JSONUtils.gson.fromJson(
                    jobject.get("rowDenominator"),
                    JSONUtils.stringDoubleMapType);

            return new MultinomialConfusionMatrix(categories, matrix,
                    rowDenominator);
        }
    }

}
