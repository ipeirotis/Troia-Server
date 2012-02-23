package com.datascience.gal.scripts;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;

import com.datascience.gal.AssignedLabel;
import com.datascience.gal.Category;
import com.datascience.gal.ConfusionMatrix;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.Datum;
import com.datascience.gal.MultinomialConfusionMatrix;
import com.datascience.gal.Worker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class SerializationSanityChecks {

    /**
     * @param args
     */
    public static void main(String[] args) {
        AssignedLabel al = new AssignedLabel("foo", "bar", "baz");
        System.out.println(al);
        String json = al.toString();
        GsonBuilder builder = new GsonBuilder();

        Type type = new TypeToken<AssignedLabel>() {
        }.getType();
        Type ctype = new TypeToken<CorrectLabel>() {
        }.getType();
        Type atype = new TypeToken<Collection<AssignedLabel>>() {
        }.getType();
        Type cattype = new TypeToken<Category>() {
        }.getType();
        Type catstype = new TypeToken<Collection<Category>>() {
        }.getType();
        Type mattype = new TypeToken<MultinomialConfusionMatrix>() {
        }.getType();

        builder.registerTypeAdapter(type, AssignedLabel.deserializer);
        builder.registerTypeAdapter(ctype, CorrectLabel.deserializer);
        builder.registerTypeAdapter(cattype, Category.deserializer);
        builder.registerTypeAdapter(mattype,
                MultinomialConfusionMatrix.deserializer);

        Gson gson = builder.create();
        System.out.println(gson.fromJson(json, type));
        System.out.println(gson.fromJson(json, type).getClass());
        CorrectLabel cl = new CorrectLabel("foo", "bar");
        System.out.println(cl);
        String t = cl.toString();
        System.out.println(gson.fromJson(t, ctype));
        System.out.println(gson.fromJson(t, ctype).getClass());

        Collection<AssignedLabel> col = new HashSet<AssignedLabel>();
        for (int i = 0; i < 10; i++) {
            String foo = i + "";
            col.add(new AssignedLabel(foo, foo, foo));
        }
        String tmp = gson.toJson(col);
        System.out.println(gson.fromJson(tmp, atype).getClass());

        Collection<Category> cats = new HashSet<Category>();
        for (int i = 0; i < 5; i++) {
            cats.add(new Category("" + i));
        }

        System.out.println(gson.toJson(cats));
        String foo = gson.toJson(cats);
        System.out.println(gson.fromJson(foo, catstype).getClass());

        Datum datum = new Datum("foo", new HashSet<Category>(cats));

        System.out.println(datum);

        Worker worker = new Worker("foo", new HashSet<Category>(cats));

        System.out.println(worker);

        ConfusionMatrix mat = new MultinomialConfusionMatrix(cats);
        String matrix = gson.toJson(mat);
        System.out.println(matrix);
        ConfusionMatrix mat2 = gson.fromJson(matrix, mattype);
        System.out.println(mat2.getClass());

    }
}
