/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.gal.service;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.datascience.gal.AssignedLabel;
import com.datascience.gal.Category;
import com.datascience.gal.CategoryPair;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.Datum;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.DawidSkeneDeserializer;
import com.datascience.gal.MisclassificationCost;
import com.datascience.gal.MultinomialConfusionMatrix;
import com.datascience.gal.Worker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * utility class for json deserialization TODO: add direct methods rather than
 * method access through the gson member
 * 
 * @author josh
 * 
 */
public class JSONUtils {
    public static final Gson gson;

    public static final Type assignedLabelSetType = new TypeToken<Collection<AssignedLabel>>() {
    }.getType();
    public static final Type correctLabelSetType = new TypeToken<Collection<CorrectLabel>>() {
    }.getType();
    public static final Type categorySetType = new TypeToken<Collection<Category>>() {
    }.getType();
    public static final Type stringSetType = new TypeToken<Collection<String>>() {
    }.getType();
    public static final Type categoryProbMapType = new TypeToken<Map<Category, Double>>() {
    }.getType();
    public static final Type stringDoubleMapType = new TypeToken<Map<String, Double>>() {
    }.getType();
    public static final Type misclassificationCostSetType = new TypeToken<Collection<MisclassificationCost>>() {
    }.getType();
    public static final Type assignedLabelType = new TypeToken<AssignedLabel>() {
    }.getType();
    public static final Type correctLabelType = new TypeToken<CorrectLabel>() {
    }.getType();
    public static final Type categoryType = new TypeToken<Category>() {
    }.getType();
    public static final Type misclassificationCostType = new TypeToken<MisclassificationCost>() {
    }.getType();
    public static final Type datumType = new TypeToken<Datum>() {
    }.getType();
    public static final Type workerType = new TypeToken<Worker>() {
    }.getType();
    public static final Type dawidSkeneType = new TypeToken<DawidSkene>() {
    }.getType();
    public static final Type categoryPairType = new TypeToken<CategoryPair>() {
    }.getType();
    public static final Type categoryPairDoubleMapType = new TypeToken<Map<CategoryPair, Double>>() {
    }.getType();
    public static final Type confusionMatrixType = new TypeToken<MultinomialConfusionMatrix>() {
    }.getType();
    public static final Type stringIntegerMapType = new TypeToken<Map<String, Integer>>() {
    }.getType();
    public static final Type stringCategoryMapType = new TypeToken<Map<String, Category>>() {
    }.getType();
    public static final Type stringDatumMapType = new TypeToken<Map<String, Datum>>() {
    }.getType();
    public static final Type strinWorkerMapType = new TypeToken<Map<String, Worker>>() {
    }.getType();
    public static final Type stringStringMapType = new TypeToken<Map<String, String>>() {
    }.getType();
    public static final Type booleanType = new TypeToken<Boolean>() {
    }.getType();
    public static final Type stringStringSetMapType = new TypeToken<Map<String, Set<String>>>() {
    }.getType();
    public static final Type stringStringDoubleMapType = new TypeToken<Map<String, Map<String, Double>>>() {
    }.getType();

    static {

        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(assignedLabelType,
                AssignedLabel.deserializer);
        builder.registerTypeAdapter(correctLabelType, CorrectLabel.deserializer);
        builder.registerTypeAdapter(categoryType, Category.deserializer);
        builder.registerTypeAdapter(misclassificationCostType,
                MisclassificationCost.deserializer);
        builder.registerTypeAdapter(datumType, Datum.deserializer);
        builder.registerTypeAdapter(categoryPairType, CategoryPair.deserializer);
        builder.registerTypeAdapter(categoryPairType, CategoryPair.serializer);
        builder.registerTypeAdapter(confusionMatrixType,
                MultinomialConfusionMatrix.deserializer);
        builder.registerTypeAdapter(workerType, Worker.deserializer);
        builder.registerTypeAdapter(dawidSkeneType,
                DawidSkeneDeserializer.deserializer);

        gson = builder.create();

    }

    private JSONUtils() {
    }

    public static String toJson(Object src) {
        return gson.toJson(src);
    }
}
