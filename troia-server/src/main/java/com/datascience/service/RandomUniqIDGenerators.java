package com.datascience.service;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author konrad
 */
public class RandomUniqIDGenerators {

	public static class NumberAndDate implements IRandomUniqIDGenerator{
		
		protected Numbers numberGenerator;
		
		public NumberAndDate(){
			numberGenerator = new Numbers();
		}

		@Override
		public String getID() {
			return "" + System.currentTimeMillis() + numberGenerator.getID();
		}
	}
	
	public static class Numbers implements IRandomUniqIDGenerator {
		
		protected AtomicLong numbers;
		
		public Numbers(){
			numbers = new AtomicLong();
		}
		
		@Override
		public String getID() {
			return "" + numbers.incrementAndGet();
		}
	}
	
	public static class PrefixAdderDecorator implements IRandomUniqIDGenerator {
		
		protected String prefix;
		protected IRandomUniqIDGenerator prefixed;
		
		
		public PrefixAdderDecorator(String prefix, IRandomUniqIDGenerator prefixed){
			this.prefix = prefix;
			this.prefixed = prefixed;
		}
		
		@Override
		public String getID(){
			return prefix + prefixed.getID();
		}
	}
}
