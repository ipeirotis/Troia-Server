package com.datascience.datastoring.datamodels.kv;

import com.datascience.datastoring.adapters.kv.DefaultSafeKVStorage;
import com.datascience.datastoring.adapters.kv.IKVStorage;
import com.datascience.datastoring.adapters.kv.ISafeKVStorage;
import com.datascience.datastoring.adapters.kv.KVKeyPrefixingWrapper;
import com.google.gson.JsonObject;

public abstract class BaseKVsProvider implements IKVsProvider {

	protected ISafeKVStorage<JsonObject> jobSettings;
	protected ISafeKVStorage<String> jobTypes;

	protected String multiRowId(String id){
		return id + "_";
	}

	protected <T> ISafeKVStorage<T> makeSafeKV(IKVStorage<T> kvStorage){
		return new DefaultSafeKVStorage<T>(kvStorage, "BaseKVsProvider");
	}

	protected <T> IKVStorage<T> prefix(String prefix, IKVStorage<T> kvStorage){
		return new KVKeyPrefixingWrapper<T>(kvStorage, prefix);
	}

	protected <T> ISafeKVStorage<T> safePrefixed(String prefix, IKVStorage<T> kvStorage){
		return makeSafeKV(this.prefix(prefix, kvStorage));
	}

	abstract protected ISafeKVStorage<JsonObject> _getSettingsKV();
	abstract protected ISafeKVStorage<String> _getKindsKV();

	@Override
	public ISafeKVStorage<JsonObject> getSettingsKV() {
		if (jobSettings == null) {
			synchronized (this) {
				if (jobSettings == null) {
					jobSettings = _getSettingsKV();
				}
			}
		}
		return jobSettings;
	}

	@Override
	public ISafeKVStorage<String> getKindsKV() {
		if (jobTypes == null) {
			synchronized (this) {
				if (jobTypes == null) {
					jobTypes = _getKindsKV();
				}
			}
		}
		return jobTypes;
	}

}
