package com.datascience.utils.transformations;

import com.datascience.utils.ITransformation;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @Author: artur
 */
public class MapTransform implements ITransformation<Map<String, Double>, String> {

	protected Joiner joiner;
	protected Splitter splitter;

	public MapTransform(String separator){
		joiner = Joiner.on(separator);
		splitter = Splitter.on(separator);
	}

	@Override
	public String transform(Map<String, Double> result) {
		LinkedList<String> stringItems = new LinkedList<String>();
		for (Map.Entry<String, Double> item : result.entrySet()){
			stringItems.add(item.getKey() + ":" + item.getValue());
		}
		return joiner.join(stringItems);
	}

	@Override
	public Map<String, Double> inverse(String object) {
		Map<String, Double> map = new HashMap<String, Double>();
		for (String item : splitter.split(object)){
			String[] kv = item.split(":");
			map.put(kv[0], Double.parseDouble(kv[1]));
		}
		return map;
	}
}
