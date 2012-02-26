package com.datascience.utils.auxl;

/**
 * @author Michael Arshynov
 *
 */
public class ArraysIntInt {
	private int[] first;
	private int[] second;
	private int pos = 0;
	
	/**
	 * @param size
	 */
	public ArraysIntInt(int size){
//		System.out.println(size);
		this.first = new int[size];
		this.second = new int[size];
	}
	/**
	 * @param firstVal
	 * @param secondVal
	 */
	public void add(int firstVal, int secondVal) {
//		System.out.println(firstVal+","+secondVal);
//		System.out.println("**"+pos);
		first[pos] = firstVal;
		second[pos++] = secondVal;
//		System.out.println("***"+pos);
	}
	
	/**
	 * @return
	 */
	public int getFirstByIndex(int i) {
		return first[i];
	}
	/**
	 * @return
	 */
	public int getSecondByIndex(int i) {
		return second[i];
	}
	public int len(){
		return pos;
	}
}
