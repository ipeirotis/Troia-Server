package com.datascience.core.commands;

import com.datascience.core.base.*;
import com.datascience.core.storages.DataJSON.ShallowAssign;
import com.datascience.core.storages.DataJSON.ShallowAssignCollection;
import com.datascience.executor.JobCommand;
import com.datascience.galc.commands.ParamChecking;

import java.util.Collection;


/**
 *
 * @author artur
 */
public class AssignsCommands {
	
	static public class AddAssigns<T> extends JobCommand<Object, Project> {

		ShallowAssignCollection<T> assigns;
		
		public AddAssigns(ShallowAssignCollection<T> assigns){
			super(true);
			this.assigns = assigns;
		}
		
		@Override
		protected void realExecute() {
			for (ShallowAssign<T> al : assigns.assigns){
				Data<T> data = project.getData();
				Worker<T> worker = data.getOrCreateWorker(al.worker);
				LObject<T> object = data.getOrCreateObject(al.object);
				data.addAssign(new AssignedLabel<T>(worker, object, al.label));
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
