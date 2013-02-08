package com.datascience.core.base;

import com.google.common.base.Objects;

/**
 * In equal, hashcode we ignore evaluation and gold label
 * @Author: konrad
 */
public class LObject<T> {

	protected String name;
	protected Label<T> goldLabel;
	protected Label<T> evaluationLabel;

	public LObject(String name){
		this.name = name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public boolean isGold(){
		return goldLabel != null;
	}

	public boolean isEvaluation(){
		return evaluationLabel != null;
	}

	public Label<T> getGoldLabel(){
		return goldLabel;
	}

	public void setGoldLabel(Label<T> label){
		goldLabel = label;
	}

	public Label<T> getEvaluationLabel(){
		return evaluationLabel;
	}

	public void setEvaluationLabel(Label<T> label){
		evaluationLabel = label;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(name);
	}

	@Override
	public boolean equals(Object other){
		if (other instanceof LObject) {
			return Objects.equal(name, ((LObject) other).name);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("name", name)
				.add("goldLabel", goldLabel)
				.add("evaluationLabel", evaluationLabel)
				.toString();
	}
}
