package com.datascience.datastoring;

/**
 * @Author: konrad
 */
public interface IBackendAdapter {

	IBackend getBackend();

	public void clear() throws Exception;
	public void rebuild() throws Exception;

	public String getID();
}
