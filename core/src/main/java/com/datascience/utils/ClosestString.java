package com.datascience.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * @Author: konrad
 */
public class ClosestString {

	protected Collection<String> baseElements;
	protected int threshold;

	public ClosestString(Collection<String> elements, int threshold){
		baseElements = elements;
		this.threshold = threshold;
	}

	public ClosestString(Collection<String> elements){
		this(elements, 10);
	}

	public String closest(String guess){
		String closest = baseElements.iterator().next();
		guess = guess.trim();
		int minDistance = Integer.MAX_VALUE;
		for (String el: baseElements){
			int dist = StringUtils.getLevenshteinDistance(el, guess, threshold);
			if (dist != -1 && dist < minDistance) {
				minDistance = dist;
				closest = el;
			}
		}
		return closest;
	}
}
