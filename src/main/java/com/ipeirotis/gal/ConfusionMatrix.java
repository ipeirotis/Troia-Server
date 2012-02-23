package com.ipeirotis.gal;

import java.util.Set;

public interface ConfusionMatrix {

    public abstract void incrementRowDenominator(String from, double value);

    public abstract void decrementRowDenominator(String from, double value);

    public abstract void empty();

    /**
     * Makes the matrix to be row-stochastic: In other words, for a given "from"
     * category, if we sum the errors across all the "to" categories, we get 1.0
     */
    public abstract void normalize();

    /**
     * Makes the matrix to be row-stochastic: In other words, for a given "from"
     * category, if we sum the errors across all the "to" categories, we get
     * 1.0.
     * 
     * We use Laplace smoothing
     */
    public abstract void normalizeLaplacean();

    public abstract void addError(String from, String to, Double error);

    public abstract void removeError(String from, String to, Double error);

    public abstract double getErrorRateBatch(String from, String to);

    public abstract double getNormalizedErrorRate(String from, String to);

    public abstract double getLaplaceNormalizedErrorRate(String from, String to);

    public abstract double getIncrementalErrorRate(String from, String to);

    public abstract void setErrorRate(String from, String to, Double cost);

    public abstract String toString();

    public abstract Set<String> getCategories();

}