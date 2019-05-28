package com.traclustering;

import java.util.ArrayList;
import java.util.List;

/**
 * visited record
 * 
 * @author Tang
 *
 */
public class VisitRecord {
	// the number of visited segment
	private int visitedNum;
	// the number of unvisited segment
	private int unvisitedNum;
	// the list store all the unvisited segments
	List<Integer> unvisited = new ArrayList<>(171715);
	// the list store all the visited segments
	List<Integer> visited = new ArrayList<>(171715);
	
	/**
	 * initialization
	 * 
	 * @param count:the
	 *            number of segments
	 * 
	 */
	public VisitRecord(int count) {
		this.unvisitedNum = count;
		this.visitedNum = 0;
		// initial the unvisited list
		for (Integer i = 0; i < count; i++) {
			this.unvisited.add(i);
		}
	}

	/**
	 * update visit record
	 * 
	 * @param segmentID
	 */
	public void visit(Integer segmentID) {
		this.unvisited.remove(segmentID);
		this.visited.add(segmentID);
		this.setVisitNum(this.getVisitNum() + 1);
		this.setUnvisitedNum(this.getUnvisitedNum() - 1);
	}

	public int getUnvisitedNum() {
		return unvisitedNum;
	}

	public void setUnvisitedNum(int unvisitedNum) {
		this.unvisitedNum = unvisitedNum;
	}

	public int getVisitNum() {
		return visitedNum;
	}

	public void setVisitNum(int visitNum) {
		this.visitedNum = visitNum;
	}
}
