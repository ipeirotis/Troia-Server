package com.datascience.gal.commands;

import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;

/**
 *
 * @author artur
 */
public class JobCommands {
	
	static public class GetJobInfo extends DSCommandBase<Map<String, String>> {

		public GetJobInfo(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(ads.getInfo());
		}
	}
}
