package com.datascience.utils.transformations.thrift;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.Worker;
import com.datascience.utils.ITransformation;
import com.datascience.utils.transformations.CollectionElementsTransform;
import com.datascience.utils.transformations.thrift.generated.Assign;
import com.datascience.utils.transformations.thrift.generated.Assigns;
import com.datascience.utils.transformations.thrift.generated.Workers;
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


	static public class AssignsTransform<T> implements ITransformation<Collection<AssignedLabel<T>>, Assigns> {

		@Override
		public Assigns transform(Collection<AssignedLabel<T>> object) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Collection<AssignedLabel<T>> inverse(Assigns object) {
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}
	}

	static public class WorkerTransform implements ITransformation<Worker, com.datascience.utils.transformations.thrift.generated.Worker>{

		@Override
		public com.datascience.utils.transformations.thrift.generated.Worker transform(Worker object) {
			return new com.datascience.utils.transformations.thrift.generated.Worker(object.getName());
		}

		@Override
		public Worker inverse(com.datascience.utils.transformations.thrift.generated.Worker object) {
			return new Worker(object.getWorker());
		}
	}


	static public class WorkersTransform implements ITransformation<Collection<Worker>, com.datascience.utils.transformations.thrift.generated.Workers>{

		ITransformation<Collection<Worker>, Collection<com.datascience.utils.transformations.thrift.generated.Worker>> helperTransform =
				new CollectionElementsTransform<Worker, com.datascience.utils.transformations.thrift.generated.Worker>(new WorkerTransform());

		@Override
		public com.datascience.utils.transformations.thrift.generated.Workers transform(Collection<Worker> object) {
			return new Workers((List) helperTransform.transform(object));
		}

		@Override
		public Collection<Worker> inverse(com.datascience.utils.transformations.thrift.generated.Workers object) {
			return helperTransform.inverse(object.getWorkers());
		}
	}
}
