package com.datascience.datastoring;

/**
 * @Author: konrad
 */
public interface IBackendAdapter {

	IBackend getBackend();

	void clear() throws Exception;
	void rebuild() throws Exception;
	void test() throws Exception;

	String getID();
}
