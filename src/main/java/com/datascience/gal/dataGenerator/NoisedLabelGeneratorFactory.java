package com.datascience.gal.dataGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Factory that creates NoisedLabelGenerator objects
 *
 * @author piotr.gnys@10clouds.com
 */
public class NoisedLabelGeneratorFactory {

    public NoisedLabelGenerator getRouletteGenerator(ConfusionMatrix confusion) {
        Map<String, Map<Double, String>> roulette = new HashMap<String, Map<Double, String>>();
        Map<String, Map<String, Double>> confMatrix = confusion.getMatrix();
        Map<Double, String> limiterVector;
        Collection<String> correctClasses = confMatrix.keySet();
        for (String correctClass : correctClasses) {
            Map<String, Double> confVector = confMatrix.get(correctClass);
            Collection<String> labeledClasses = confVector.keySet();
            limiterVector = new TreeMap<Double, String>();
            double limiter = 0;
            for (String labeledClass : labeledClasses) {
                double prob = confVector.get(labeledClass).doubleValue();
                limiter += prob;
                if (prob != 0)
                    limiterVector.put(limiter, labeledClass);
            }
            roulette.put(correctClass, limiterVector);
        }
        return new RouletteNoisedLabelGenerator(roulette);
    }

    public Map<ArtificialWorker, NoisedLabelGenerator> getRouletteGeneratorsForWorkers(
        Collection<ArtificialWorker> workers) {
        Map<ArtificialWorker, NoisedLabelGenerator> generators = new HashMap<ArtificialWorker, NoisedLabelGenerator>();
        for (ArtificialWorker worker : workers) {
            generators.put(worker, this.getRouletteGenerator(worker.getConfusionMatrix()));
        }
        return generators;
    }

    public static NoisedLabelGeneratorFactory getInstance() {
        return instance;
    }

    private static NoisedLabelGeneratorFactory instance = new NoisedLabelGeneratorFactory();

    private NoisedLabelGeneratorFactory() {

    }

}
