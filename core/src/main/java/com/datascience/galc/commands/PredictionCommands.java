package com.datascience.galc.commands;

import java.util.*;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.jobs.JobCommand;
import com.datascience.galc.ContinuousProject;
import com.datascience.core.results.DatumContResults;
import com.datascience.core.results.WorkerContResults;
import com.datascience.galc.DatumContPrediction;
import com.datascience.galc.WorkerContQuality;

/**
*
* @author artur
*/
public class PredictionCommands {

	static public class ObjectsPrediction extends JobCommand<Collection<DatumContPrediction>, ContinuousProject> {

		public ObjectsPrediction(){
			super(true);
		}

		@Override
		protected void realExecute() {
			Collection<DatumContPrediction> result = new LinkedList<DatumContPrediction>();
			for (Map.Entry<LObject<ContValue>, DatumContResults> e : project.getResults().getDatumResults(project.getData().getObjects()).entrySet()){
				result.add(new DatumContPrediction(e.getKey().getName(), e.getValue()));
			}
			setResult(result);
		}
	}

    static public class ObjectPrediction extends JobCommand<DatumContPrediction, ContinuousProject> {

        String objectId;
        public ObjectPrediction(String objectId){
            super(true);
            this.objectId = objectId;
        }

        @Override
        protected void realExecute() {
            setResult(new DatumContPrediction(objectId, project.getResults().getDatumResult(project.getData().getObject(objectId))));
        }
    }

	static public class WorkersPrediction extends JobCommand<Collection<WorkerContQuality>, ContinuousProject> {

		public WorkersPrediction(){
			super(true);
		}

		@Override
		protected void realExecute() {
			Collection<WorkerContQuality> result = new LinkedList<WorkerContQuality>();
			for (Map.Entry<Worker<ContValue>, WorkerContResults> e : project.getResults().getWorkerResults(project.getData().getWorkers()).entrySet()){
				result.add(new WorkerContQuality(e.getKey().getName(), e.getValue()));
			}
			setResult(result);
		}
	}

    static public class WorkerPrediction extends JobCommand<WorkerContQuality, ContinuousProject> {

        String workerId;
        public WorkerPrediction(String wid){
            super(true);
            workerId = wid;
        }

        @Override
        protected void realExecute() {
            setResult(new WorkerContQuality(workerId, project.getResults().getWorkerResult(project.getData().getWorker(workerId))));
        }
    }

	static public class GetPredictionZip extends com.datascience.core.commands.PredictionCommands.AbstractGetPredictionZip<ContinuousProject> {

		public GetPredictionZip(String path){
			super(path);
			HashMap<String, GetStatistics> files = new HashMap<String, GetStatistics>();
			files.put("prediction.tsv", new GetDataPrediction());
			files.put("workers_quality.tsv", new GetWorkersQuality());
			setStatisticsFilesMap(files);
		}

		class GetDataPrediction extends GetStatistics {

			@Override
			public List<List<Object>> call(){
				List<List<Object>> ret = new ArrayList<List<Object>>();
				ret.add(Arrays.asList(new Object[]{"name", "estimated value", "estimated zeta"}));

				for (LObject o : project.getData().getObjects()){
					List<Object> line = new ArrayList<Object>();
					line.add(o.getName());
					DatumContResults res = project.getDataPrediction().get(o);
					line.add(res.getEst_value());
					line.add(res.getEst_zeta());
					ret.add(line);
				}

				return ret;
			}
		}

		class GetWorkersQuality extends GetStatistics {

			public List<List<Object>> call(){
				List<List<Object>> ret = new ArrayList<List<Object>>();
				ret.add(Arrays.asList(new Object[]{"name", "estimated mu", "estimated rho", "estimated sigma"}));

				for (Worker w : project.getData().getWorkers()){
					List<Object> line = new ArrayList<Object>();
					line.add(w.getName());
					WorkerContResults res = project.getWorkerPrediction().get(w);
					line.add(res.getEst_mu());
					line.add(res.getEst_rho());
					line.add(res.getEst_sigma());
					ret.add(line);
				}

				return ret;
			}
		}
	}
}
