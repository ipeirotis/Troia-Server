package com.datascience.utils.auxl;

/**
 * @author Michael Arshynov
 *
 */
public class RangePairIntInt {
	private int start1;
	private int start2;
	private int end1;
	private int end2;
	/**
	 * @param start1
	 * @param end1
	 */
	public void setRange1(int start1, int end1) {
		this.start1 = start1;
		this.end1 = end1;
	}
	/**
	 * @param start2
	 * @param end2
	 */
	public void setRange2(int start2, int end2) {
		this.start2 = start2;
		this.end2 = end2;
	}
	/**
	 * @return
	 */
	public int getStart1() {
		return start1;
	}
	/**
	 * @return
	 */
	public int getStart2() {
		return start2;
	}
	/**
	 * @return
	 */
	public int getEnd1() {
		return end1;
	}
	/**
	 * @return
	 */
	public int getEnd2() {
		return end2;
	}
	/**
	 * @return
	 */
	public boolean isValid() {
		return start1<=end1 && start2<=end2;
	}
	
}
