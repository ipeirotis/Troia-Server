package com.datascience.gal.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * @author Michael Arshynov
 *
 */
@Path("compute")
public class Computer {
    @Context
    ServletContext context;
    
    /**
     * @return
     */
    @GET
    @Path("formOne")
    @Produces(MediaType.TEXT_HTML)
    public Viewable  formOne() {
    	return new Viewable("/formOne");
    }
    
    /**
     * @param categoriesParam
     * @param unlabeledParam
     * @param goldParam
     * @param costsParam
     * @param iterationsParam
     * @param uploadedInputStreamCategories
     * @param fileDetailCategories
     * @param uploadedInputStreamUnlabeled
     * @param fileDetailUnlabeled
     * @param uploadedInputStreamGold
     * @param fileDetailGold
     * @param uploadedInputStreamCosts
     * @param fileDetailCosts
     * @return
     * @throws IOException
     */
    @POST
    @Path("formOne")
//    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response  uploadFile(
    		@FormDataParam("categories") String categoriesParam,
    		@FormDataParam("unlabeled") String unlabeledParam,
    		@FormDataParam("gold") String goldParam,
    		@FormDataParam("costs") String costsParam,
    		@FormDataParam("iterations") String iterationsParam,
    		
    		@FormDataParam("cmuds_categories") InputStream uploadedInputStreamCategories,
    		@FormDataParam("cmuds_categories") FormDataContentDisposition fileDetailCategories,
    		
    		@FormDataParam("cmuds_unlabeled") InputStream uploadedInputStreamUnlabeled,
    		@FormDataParam("cmuds_unlabeled") FormDataContentDisposition fileDetailUnlabeled,
    		
    		@FormDataParam("cmuds_gold") InputStream uploadedInputStreamGold,
    		@FormDataParam("cmuds_gold") FormDataContentDisposition fileDetailGold,
    		
    		@FormDataParam("cmuds_costs") InputStream uploadedInputStreamCosts,
    		@FormDataParam("cmuds_costs") FormDataContentDisposition fileDetailCosts    		
    		) throws IOException {
    	Map<String, Object> map = new HashMap<String, Object>();
    	
    	String categories = leftTheSameOrimportFromFile(categoriesParam, uploadedInputStreamCategories, fileDetailCategories);
    	String unlabeled = leftTheSameOrimportFromFile(unlabeledParam, uploadedInputStreamUnlabeled, fileDetailUnlabeled);
    	String gold = leftTheSameOrimportFromFile(goldParam, uploadedInputStreamGold, fileDetailGold);
    	String costs = leftTheSameOrimportFromFile(costsParam, uploadedInputStreamCosts, fileDetailCosts);
    	
    	map.put("categories", categories);
    	map.put("unlabeled", unlabeled);
    	map.put("gold", gold);
    	map.put("costs", costs);
    	map.put("iterations", iterationsParam);
    	
    	String result = ComputerHelper.compute(categories, unlabeled, gold, costs, iterationsParam);
    	
    	map.put("results", result);
    	return Response.ok(new Viewable("/formOne", map)).build();
    }
    
    /**
     * @param currentContent
     * @param uploadedInputStream
     * @param fileDetail
     * @return
     */
    private String leftTheSameOrimportFromFile(String currentContent, InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
    	String result = new String("file is too large to upload");
    	if (currentContent!=null && currentContent.trim().length()>0) {
    		return currentContent;
    	}
    	if (fileDetail.getFileName()!=null && fileDetail.getFileName().length()>0) {
	    	final int MAX_SIZE = 4096;
	    	byte[] bytes = new byte[MAX_SIZE];
			try {
				int size = uploadedInputStream.read(bytes);
		    	if (size<=MAX_SIZE && size>0) {
		    		result = new String(bytes, 0, size);
		    	}
			} catch (IOException e) {
				e.printStackTrace();
			}
    	} else return "";
		return result;
    }
}
