/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.gal.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.datascience.gal.Category;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.multipart.FormDataParam;

/**
 * @author Michael Arshynov
 *
 */
@Path("compose")
public class ServiceHelper {
    @Context
    ServletContext context;
    private static Logger logger = Logger.getLogger(ServiceHelper.class);

    @GET
    @Path("composeDSForm")
    @Produces(MediaType.TEXT_HTML)
    public Viewable  composeDSForm() {
		return new Viewable("/composeDSForm");
    }
    
    @POST
    @Path("composeDSForm")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response  uploadFile(
    		@FormDataParam("action") String actionParam,
    		@FormDataParam("categoryName") String categoryNameParam,
    		@FormDataParam("categoryNames") String categoryNamesParam,
    		@FormDataParam("categoryJson") String categoryJsonParam,
    		@FormDataParam("categoriesJson") String categoriesJsonParam
    	) {
    	String categoryJson = new String("");
    	String categoriesJson = new String("");
    	if (categoryNameParam!=null && categoryNameParam.trim().length()>0) {
    		Category category = new Category(categoryNameParam);
    		categoryJson = category.toString();
    	}
    	if (categoryNamesParam!=null && categoryNamesParam.trim().length()>0) {
    		String[] names = categoryNamesParam.split(",");
    		Set<Category> set = new HashSet<Category>();
    		for (String name: names) {
    			String normalized = name.trim();
    			if (normalized.length()>0) {
    				Category category = new Category(normalized);
    				set.add(category);
    			}
    		}
    		if (set!=null) {
				categoriesJson = JSONUtils.gson.toJson(set,
	                    JSONUtils.categorySetType);
    		}
    	}
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("action", actionParam);
    	map.put("categoryName", categoryNameParam);
    	map.put("categoryJson", categoryJson);
    	map.put("categoryNames", categoryNamesParam);
    	map.put("categoriesJson", categoriesJson);
    	map.put("c_no_changes", "disabled='disabled'");
    	return Response.ok(new Viewable("/composeDSForm", map)).build();
    }
    		
    
    
}
