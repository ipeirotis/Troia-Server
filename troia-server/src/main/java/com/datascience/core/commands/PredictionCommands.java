package com.datascience.core.commands;

import com.datascience.executor.JobCommand;
import com.google.common.base.Joiner;
import org.joda.time.DateTime;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * User: artur
 */
public class PredictionCommands {

	static public abstract class AbstractGetPredictionZip<T> extends JobCommand<String, T> {

		private String path;
		private Map<String, GetStatistics> statisticsFilesMap;

		public AbstractGetPredictionZip(String path){
			super(false);
			this.path = path;
		}

		public void setStatisticsFilesMap(Map<String, GetStatistics> statisticsMap){
			this.statisticsFilesMap = statisticsMap;
		}

		public abstract class GetStatistics {

			public List<List<Object>> call() {
				return null;
			}
			public void generateToStream(List<List<Object>> data, String separator, OutputStream os) throws IOException {
				Joiner j = Joiner.on(separator);
				for (List<Object> lo : data){
					os.write((j.join(lo) + "\n").getBytes());
				}
			}
		}

		@Override
		protected void realExecute(){

			String fileName = "";
			try{
				fileName = DateTime.now() + "_" + ".zip"; //append job id in the future
				FileOutputStream fos = new FileOutputStream(path + fileName);
				ZipOutputStream zos = new ZipOutputStream(fos);

				for(Map.Entry<String, GetStatistics> e : statisticsFilesMap.entrySet()){
					ZipEntry ze= new ZipEntry(e.getKey());
					zos.putNextEntry(ze);
					e.getValue().generateToStream(e.getValue().call(), "\t", zos);
				}
				zos.closeEntry();
				zos.close();
			} catch (FileNotFoundException e) {
				Logger.getAnonymousLogger().warning(e.getLocalizedMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Logger.getAnonymousLogger().warning(e.getLocalizedMessage());
				e.printStackTrace();
			}
			setResult("/media/downloads/"+fileName);
		}
	}
}
