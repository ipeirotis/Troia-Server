package com.datascience.gal.dataGenerator;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class represents actual confusion matrix of artificial worker.
 *
 * @author piotr.gnys@10clouds.com
 */
public class RouletteNoisedLabelGenerator implements NoisedLabelGenerator {

    public RouletteNoisedLabelGenerator(Map<String,Map<Double,String>> roulette) {
        super();
        this.roulette = roulette;
    }

    /**
     * This function adds noises to labbeling process. Noise is determined by
     * confusion matrix with represents probabilities of specific
     * misclassifications. If category given as parameter is not present in
     * confusion matrix unnoised rcategory is returned.
     *
     * @param correctCategory
     *            Correct category of object for with noise will be generated
     * @return Label with noise created by this confusion matrix
     */
    public String getCategoryWithNoise(String correctCategory) {
        Map<Double, String> confusionVector = this.roulette
                                              .get(correctCategory);
        double randomVal = Math.random();
        Collection<Double> limiters = confusionVector.keySet();
        String noisedCategory = null;
        boolean generated = false;
        for (Double limiter : limiters) {
            if (randomVal <= limiter.doubleValue()) {
                noisedCategory = confusionVector.get(limiter);
                generated = true;
                logger.debug("Generated noised category \"" + noisedCategory
                             + "\" for \"" + correctCategory
                             + "\" with roulette algorithm.");
                break;
            }
        }
        // Probabilities do not always add perfectly to one so this part of
        // function
        // bumps up probability of first incorrect category to fill it.
        if (!generated) {
            for (Double limiter : limiters) {
                noisedCategory = confusionVector.get(limiter);
                if (!noisedCategory.equalsIgnoreCase(correctCategory)) {
                    generated = true;
                    logger.debug("Generated noised category \""
                                 + noisedCategory + "\" for \"" + correctCategory
                                 + "\" with last chance algorithm.");
                    break;
                }
            }
        }

        if (!generated) {
            noisedCategory = correctCategory;
            logger.error("Unable to create noise for category "
                         + correctCategory);
        }
        return noisedCategory;
    }


    /**
     * Confusion map used for generating categories with noise
     */
    private Map<String, Map<Double, String>> roulette;

    /**
     * Logger for this class
     */
    private static Logger logger = Logger
                                   .getLogger(RouletteNoisedLabelGenerator.class);
}
