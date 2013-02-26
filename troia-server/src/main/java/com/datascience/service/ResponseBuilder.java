package com.datascience.service;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;

import com.datascience.executor.CommandStatus;
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

	protected Response buildResponse(int status, Map<String, Object> content){
		return Response.status(status).type(serializer.getMediaType())
				.entity(serializer.serialize(content)).build();
	}
	
	public Response makeErrorResponse(int status, String message){
		Map<String, Object> content = initialResponseContent("ERROR", message);
		return buildResponse(status, content);
	}

	protected Map<String, Object> makeExceptionContent(Throwable exception){
		String message = "Internal error: " + exception.getMessage();
		Map<String, Object> content = initialResponseContent("ERROR", message);
		content.put("stacktrace", exception.getStackTrace());
		return content;
	}

	public Response makeExceptionResponse(Throwable exception, Double executionTime){
		Map<String, Object> content = makeExceptionContent(exception);
		content.put("executionTime", executionTime);
		return buildResponse(500, content);
	}

	public Response makeExceptionResponse(Throwable exception){
		return makeExceptionResponse(exception, null);
	}

	public Response makeOKResponse(Object content){
		return makeOKResponse(content, null);
	}

	public Response makeOKResponse(Object content, Double executionTime){
		Map<String, Object> init_content = initialResponseContent("OK", content);
		init_content.put("executionTime", executionTime);
		return buildResponse(200, init_content);
	}

	public Response makeNotReadyResponse(){
		Map<String, Object> init_content = initialResponseContent("NOT_READY", null);
		return buildResponse(200, init_content);
	}
	
	public Response makeRedirectResponse(String newAddress){
		Map<String, Object> init_content = initialResponseContent("OK", null);
		init_content.put("redirect", newAddress);
		return buildResponse(200, init_content);
	}

	public Response makeStatusResponse(CommandStatus status){
		switch(status.getStatus()){
			case OK:
				return makeOKResponse(status.getData(), status.getExecutionTime());
			case ERROR:
				return makeExceptionResponse(status.getError(), status.getExecutionTime());
			case NOT_READY:
				return makeNotReadyResponse();
			default:
				throw new IllegalStateException("Unknown command status: " + status.getStatus());
		}
	}

	public ISerializer getSerializer() {
		return serializer;
	}
}
