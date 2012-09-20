package main.com.troiaTester;

/**
 * This interface provides functionality used for generating noised categories
 *
 * @author piotr.gnys@10clouds.com
 */
public interface NoisedLabelGenerator {


    /**
     * This function adds noises to labeling process. Noise is determined by
     * confusion matrix with represents probabilities of specific
     * misclassifications. If category given as parameter is not present in
     * confusion matrix unnoised category is returned.
     *
     * @param correctCategory
     *            Correct category of object for with noise will be generated
     * @return Label with noise created by this confusion matrix
     */
    public String getCategoryWithNoise(String correctCategory);
}
