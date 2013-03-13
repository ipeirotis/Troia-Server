package com.datascience.core.commands;

import com.datascience.core.base.*;
import com.datascience.serialization.json.DataJSON.ShallowAssign;
import com.datascience.executor.JobCommand;

import java.util.Collection;


/**
 *
 * @author artur
 */
public class AssignsCommands {
	
	static public class AddAssigns extends JobCommand<Object, Project> {

		Collection<ShallowAssign> assigns;
		
		public AddAssigns(Collection<ShallowAssign> assigns){
			super(true);
			this.assigns = assigns;
		}
		
		@Override
		protected void realExecute() {
			Data data = project.getData();
			for (ShallowAssign al : assigns){
				Worker worker = data.getOrCreateWorker(al.worker);
				LObject object = data.getOrCreateObject(al.object);
				data.addAssign(new AssignedLabel(worker, object, al.label));
				setResult("Assigns added");
			}
		}
	}
	
	static public class GetAssigns<T> extends JobCommand<Collection<AssignedLabel<T>>, Project> {
		
		public GetAssigns(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getAssigns());
		}
	}
	
	static public class GetWorkerAssigns<T> extends JobCommand<Collection<AssignedLabel<T>>, Project> {
		
		String workerId;
		public GetWorkerAssigns(String workerId){
			super(false);
			this.workerId = workerId;
		}
		
		@Override
		protected void realExecute() {
			Worker w = ParamChecking.worker(project.getData(), workerId);
			setResult(w.getAssigns());
		}
	}
}
