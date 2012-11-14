/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.gal;

import java.lang.reflect.Type;

import com.datascience.gal.service.JSONUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.google.common.base.Objects;

public class CorrectLabel {

	private String objectName;
	private String correctCategory;

	public static final CorrectLabelDeserializer deserializer = new CorrectLabelDeserializer();

	public CorrectLabel(String objectName, String correctCategory) {
		this.objectName = objectName;
		this.correctCategory = correctCategory;
	}

	/**
	 * @return the objectName
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * @return the correctCategory
	 */
	public String getCorrectCategory() {
		return correctCategory;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(correctCategory, objectName);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CorrectLabel))
			return false;
		CorrectLabel other = (CorrectLabel) obj;
		return Objects.equal(correctCategory, other.correctCategory) &&
				Objects.equal(objectName, other.objectName);
	}

	@Override
	public String toString() {
		return JSONUtils.gson.toJson(this);
	}

	public static class CorrectLabelDeserializer implements
		JsonDeserializer<CorrectLabel> {

		@Override
		public CorrectLabel deserialize(JsonElement json, Type type,
										JsonDeserializationContext context) throws JsonParseException {
			JsonObject jobject = (JsonObject) json;
			return new CorrectLabel(jobject.get("objectName").getAsString(),
									jobject.get("correctCategory").getAsString());
		}

	}
}
