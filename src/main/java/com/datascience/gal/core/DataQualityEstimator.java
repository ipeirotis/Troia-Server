package com.datascience.gal.core;

import java.util.HashMap;
import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.Datum;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.decision.ClassificationAlgorithm;
import com.datascience.gal.decision.ExpectedCostDecisionAlgorithm;
import com.datascience.gal.decision.LabelProbabilityDistributionCalculator;
import com.datascience.gal.decision.LabelProbabilityDistributionCalulators;
import com.datascience.gal.decision.MinCostDecisionAlgorithm;
import com.datascience.gal.decision.ObjectLabelDecisionAlgorithm;

/**
 * @author Konrad Kurdej
 */
public class DataQualityEstimator {
    // TODO: make this single object in system - probably with singleton pattern but there might be better option

    private static Map<String, ClassificationAlgorithm> CLASSIFICATION_ALGORITHMS =
            new HashMap<String, ClassificationAlgorithm>();
    static {
        LabelProbabilityDistributionCalculator ds = new LabelProbabilityDistributionCalulators.DS();
        LabelProbabilityDistributionCalculator mv = new LabelProbabilityDistributionCalulators.MV();
        ObjectLabelDecisionAlgorithm expected = new ExpectedCostDecisionAlgorithm();
        ObjectLabelDecisionAlgorithm minimalized = new MinCostDecisionAlgorithm();
        
        CLASSIFICATION_ALGORITHMS.put("ExpectedCost", new ClassificationAlgorithm(ds, expected));
        CLASSIFICATION_ALGORITHMS.put("ExpectedMVCost", new ClassificationAlgorithm(mv, expected));
        CLASSIFICATION_ALGORITHMS.put("MinCost", new ClassificationAlgorithm(ds, minimalized));
        CLASSIFICATION_ALGORITHMS.put("MinMVCost", new ClassificationAlgorithm(mv, minimalized));
    }

    public double estimateMissclassificationCost(DawidSkene ds, String method,
            String object_id) {
        // Ugly as hell but I don't see any other way ...
        AbstractDawidSkene ads = (AbstractDawidSkene) ds;
        Datum datum = ads.getObject(object_id);
        return CLASSIFICATION_ALGORITHMS.get(method).predictedLabelCost(datum, ads);
    }
}
