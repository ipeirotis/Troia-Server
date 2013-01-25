package com.datascience.gal.service;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.joda.time.DateTime;

/**
 * @author Konrad
 */
public class ResponseBuilder {
	
	private ISerializer serializer;
	
	public ResponseBuilder(ISerializer serializer){
		this.serializer = serializer;
	}

	Map<String, Object> initialResponseContent(String messageStatus, Object messageResult){
		Map<String, Object> responseContent = new HashMap<String, Object>();
		responseContent.put("status", messageStatus);
		if (messageResult != null) {
			responseContent.put("result", messageResult);
		}
		responseContent.put("timestamp", DateTime.now().toString());
		return responseContent;
	}
	
	public Response makeErrorResponse(int status, String message){
		Map<String, Object> content = initialResponseContent("ERROR", message);
		return Response.status(status).type(serializer.getMediaType())
			.entity(serializer.serialize(content)).build();
	}

	public Response makeExceptionResponse(Throwable exception){
		String message = "Internal error: " + exception.getMessage();
		Map<String, Object> content = initialResponseContent("ERROR", message);
		content.put("stacktrace", exception.getStackTrace());
		return Response.status(500).type(serializer.getMediaType())
				.entity(serializer.serialize(content)).build();
	}

	public Response makeOKResponse(Object content){
		Map<String, Object> init_content = initialResponseContent("OK", content);
		return Response.ok(serializer.serialize(init_content))
			.type(serializer.getMediaType()).build();
	}
	
	public Response makeNotReadyResponse(){
		Map<String, Object> init_content = initialResponseContent("NOT_READY", null);
		return Response.ok(serializer.serialize(init_content))
			.type(serializer.getMediaType()).build();
	}
	
	public Response makeRedirectResponse(String commandId, String path){
		Map<String, Object> init_content = initialResponseContent("OK", null);
		init_content.put("redirect", String.format("responses/%s/%s", commandId, path));
		return Response.ok(serializer.serialize(init_content))
			.type(serializer.getMediaType()).build();
	}

	public ISerializer getSerializer() {
		return serializer;
	}
}
