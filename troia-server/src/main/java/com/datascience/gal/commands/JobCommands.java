package com.datascience.gal.commands;

import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;

/**
 *
 * @author artur
 */
public class JobCommands {
	
	static public class GetJobInfo extends ProjectCommand<Map<String, String>> {

		public GetJobInfo(AbstractDawidSkene ads){
			super(ads, false);
		}
		
		@Override
		void realExecute() {
			setResult(ads.getInfo());
		}
	}
}
