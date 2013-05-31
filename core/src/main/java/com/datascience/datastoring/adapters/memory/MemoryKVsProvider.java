package com.datascience.datastoring.adapters.memory;

import com.datascience.datastoring.IBackend;
import com.datascience.datastoring.backends.memory.MemoryBackend;
import com.datascience.datastoring.datamodels.kv.BaseKVsProvider;


/** TODO XXX FIXME FINISH THIS
 */
public class MemoryKVsProvider extends BaseKVsProvider {

	MemoryBackend memoryBackend = new MemoryBackend();

	@Override
	public IBackend getBackend() {
		return memoryBackend;
	}
}
