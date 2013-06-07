package com.datascience.utils.transformations.thrift;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.Worker;
import com.datascience.utils.ITransformation;
import com.datascience.utils.transformations.CollectionElementsTransform;
import com.datascience.utils.transformations.thrift.generated.*;
import com.google.common.base.Throwables;
import org.apache.log4j.Logger;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TIOStreamTransport;

import java.io.*;
import java.util.Collection;
import java.util.List;

/**
 * @Author: konrad
 */
public class ThriftCoreTransforms {
	static Logger logger = Logger.getLogger(ThriftCoreTransforms.class);

	static public class ThriftBaseTransform<T extends TBase> implements ITransformation<T, InputStream> {

		protected T tBase;

		public ThriftBaseTransform(T element){
			tBase = element;
			tBase.clear();
		}

		protected void hadnleError(String errMsg, Throwable t){
			logger.error(errMsg, t);
			Throwables.propagate(t);
		}

		@Override
		public InputStream transform(T object) {
			try {
				PipedOutputStream pipedOutputStream = new PipedOutputStream();
				PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
				object.write(new TBinaryProtocol(new TIOStreamTransport(pipedOutputStream)));
				// Alternative ^^^ could be TMemoryBuffer
				pipedOutputStream.flush();
				return pipedInputStream;
			} catch (IOException e) {
				hadnleError("When transforming with PipedXStream", e);
			} catch (TException e) {
				hadnleError("When transforming with PipedXStream", e);
			}
			return null;
		}

		@Override
		public T inverse(InputStream object) {
			try {
				T newOne = (T) tBase.deepCopy();
				newOne.read(new TBinaryProtocol(new TIOStreamTransport(object)));
				return newOne;
			} catch (TException e) {
				hadnleError("When transforming from stream", e);
			}
			return null;
		}
	}

	static public class LabelTransform<T> implements ITransformation<T, TLabel>{

		@Override
		public TLabel transform(T object) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public T inverse(TLabel object) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}
	}


	static public class AssignsTransform<T> implements ITransformation<Collection<AssignedLabel<T>>, TAssigns> {

		@Override
		public TAssigns transform(Collection<AssignedLabel<T>> object) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Collection<AssignedLabel<T>> inverse(TAssigns object) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}
	}

	static public class WorkerTransform implements ITransformation<Worker, TWorker>{

		@Override
		public TWorker transform(Worker object) {
			return new TWorker(object.getName());
		}

		@Override
		public Worker inverse(TWorker object) {
			return new Worker(object.getWorker());
		}
	}


	static public class WorkersTransform implements ITransformation<Collection<Worker>, TWorkers>{

		ITransformation<Collection<Worker>, Collection<TWorker>> helperTransform =
				new CollectionElementsTransform<Worker, TWorker>(new WorkerTransform());

		@Override
		public TWorkers transform(Collection<Worker> object) {
			return new TWorkers((List) helperTransform.transform(object));
		}

		@Override
		public Collection<Worker> inverse(TWorkers object) {
			return helperTransform.inverse(object.getWorkers());
		}
	}
}
