/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/

package com.datascience.gal.quality;

import java.util.Map;

public class ProjectQualityEvaluation {
    
    public ProjectQualityEvaluation(String projectId){
	super(projectId);
	this.workerEvaluations = new HashMap<String,WorkerQualityEvaluation>();
    }

    /**
     * Map that associates worker names with their evaluation cost.
     */
    private Map<String,WorkerQualityEvaluation> workerEvaluations;
    
    /**
     * @return Map that associates worker names with their evaluation cost.
     */
    public Map<String,WorkerQualityEvaluation> getWorkerEvaluations() {
	return workerEvaluations;
    }
    
    /**
     * @param workerEvaluations Map that associates worker names with their evaluation cost.
     */
    public void setWorkerEvaluations(Map<String,WorkerQualityEvaluation> workerEvaluations) {
	this.workerEvaluations = workerEvaluations;
    }


    public WorkerQualityEvaluation getWorkerEvaluation(String workerName){
	return this.workerEvaluations.get(workerName);
    }

    public void setWorkerEvaluation(String workerName,WorkerQualityEvaluation revaluation){
	this.workerEvaluations.put(workerName,evaluation);
    }
}
