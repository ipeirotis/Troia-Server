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
import java.util.HashMap;

public class QualityEvaluation {

    public QualityEvaluation(String projectId){
	this.categoryQuality = new HashMap<String,Double>();
	this.projectId=projectId;
    }

    /**
     * Avarage quality for all categories
     */
    private Double avarageQuality;
    
    /**
     * @return Avarage quality for all categories
     */
    public Double getAvarageQuality() {
	return avarageQuality;
    }
    
    /**
     * @param avarageQuality Avarage quality for all categories
     */
    public void setAvarageQuality(Double avarageQuality) {
	this.avarageQuality = avarageQuality;
    }


    /**
     * Map that contains qualities for specific categories
     */
    private Map<String,Double> categoryQuality;
    
    /**
     * @return Map that contains qualities for specific categories
     */
    public Map<String,Double> getCategoryQuality() {
	return categoryQuality;
    }
    
    /**
     * @param categoryQuality Map that contains qualities for specific categories
     */
    public void setCategoryQuality(Map<String,Double> categoryQuality) {
	this.categoryQuality = categoryQuality;
    }

/**
 * Identifier of Dawid-Skene project that this evaluation refers to
 */
    private String projectId;
    
    /**
     * @return Identifier of Dawid-Skene project that this evaluation refers to
     */
    public String getProjectId() {
	return projectId;
    }
    
    /**
     * @param projectId Identifier of Dawid-Skene project that this evaluation refers to
     */
    public void setProjectId(String projectId) {
	this.projectId = projectId;
    }


    @Override
    public String toString(){
	return "Avarage quality = "+this.avarageQuality;
    }

}
