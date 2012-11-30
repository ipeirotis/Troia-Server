/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.datascience.gal.service;

import com.datascience.core.storages.JSONUtils;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author konrad
 */
public class GSONSerializer implements ISerializer{

	@Override
	public String serialize(Object object) {
		return JSONUtils.gson.toJson(object);
	}

	@Override
	public String getMediaType() {
		return MediaType.APPLICATION_JSON;
	}

	@Override
	public <T> T parse(String input, Type type) {
		return JSONUtils.gson.fromJson(input, type);
	}
}
