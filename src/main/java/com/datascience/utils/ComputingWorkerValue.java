/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.utils;

/**
 * @author Jing Wang
 *
 */
public class ComputingWorkerValue {
	public static double getAnnotatorCostAdjustedn(double[][] pi, int num)
    {
		double c = 0.0D;
   
    //There are N=num labelers and J labels/categories. So the number of possible label assignment is Math.pow(J, num).
		int n_comb = (int) Math.pow(J,num) - 1;
		//The while loop traverses over all possible assignments
		while (n_comb >= 0)
		{
			double annotated_prior = 0.0;
			//Array "labels" records the label given by each labeler
			int[] labels = new int[num];
			//label assignment---------------------------------------------------------------------------------------------
			int current = n_comb;
			for(int i = num-1; i > 0 ; i --)
			{
				labels[i] = current/(int) Math.pow(J,i);
				current = current%(int) Math.pow(J,i);
				
			}
			labels[0] = current;
		  //-------------------------------------------------------------------------------------------------------------
		  
		  //Sum the probability of the current label assignment over J different true categories--------------------
			for(int j = 0; j < J; j ++)
			{
				double annotated_prob = prior[j];
				for(int l = 0; l < num; l ++)
				{
					annotated_prob*=pi[j][labels[l]];
				}
				annotated_prior += annotated_prob;
			}
			//--------------------------------------------------------------------------------------------------------------
			
			//Compute the probability that the example belongs to each category under the current label assignment------------
			double soft[] = new double[J];
			for(int j = 0; j < J; j ++)
			{
				double annotated_prob = prior[j];
				for(int l = 0; l < num; l ++)
				{
					annotated_prob*=pi[j][labels[l]];
				}
				soft[j] = annotated_prob/annotated_prior;
			}
			//---------------------------------------------------------------------------------------------------------------
			//Add the total average cost by the cost under the current label assignment times the probability of seeing the current label assignment
			c += getSoftLabelCost(soft) * annotated_prior;
			
			n_comb --;
		}
		return c;    
    }
    
//Input: the confusion matrix of a worker
//Constant 1: highest_value---the value for a perfect worker. In my experiment, highest_value = 100;
//Constant 2: upper_lim---the maximum of workers for combination. In my experiment, upper_lim = 10;
//Constant 3: cost_threshold---the required cost level. In my experiment, cost_threshold = 0.01;
//Constant 4: back_num---the number of points for logarithmic regression. In my experiment, back_num = 4;
//Output: the value of this worker
public static double getValue(double[][] pi)
	{
		int x = 1;
		double[] cost_array = new double[upper_lim];
		double[] index = new double[upper_lim];
		for(int i = 0; i < upper_lim; i ++)
			index[i] = i + 1;
		while(x <= upper_lim)
		{
			double cost_adj = getAnnotatorCostAdjustedn(pi,x);
			
			cost_array[x-1] = cost_adj;
			if(cost_adj <= cost_threshold)
				break;
			x++;
		}
		
		if(x == 1)
			return highest_value; 
		else if(x <= upper_lim)
		{
			double y0 = x;
			double y1 = (x-1);
			double x0 = Math.log(cost_array[(int)y0-1]);
			double x1 = Math.log(cost_array[(int)y1-1]);
			return highest_value /(y0  + (Math.log(cost_threshold)-x0)*(y1-y0)/(x1-x0));	
		}
		else
		{
			double[] sub_cost_array = new double[back_num];
			double[] sub_index = new double[back_num];
			for(int i = 0; i < back_num; i ++)
			{
				sub_cost_array[i] = cost_array[i + upper_lim - back_num];
				sub_index[i] = index[i + upper_lim - back_num];
			}
				
			return highest_value / LogarithmicRegression.getValue(sub_index, sub_cost_array, back_num, cost_threshold);
			
		}
		
	}
}
