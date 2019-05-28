package com.traclustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Dbscan {
	// the neighborhood distance
	private double eps;
	// the least number of segments in the neighborhood
	private int minSup;

	public Dbscan(double eps, int minSup) {
		this.eps = eps;
		this.minSup = minSup;
	}

	/**
	 * apply DBSCAN algorithm to the segments
	 * 
	 * @param map:
	 *            store all segments
	 * @param sortedSegments:
	 *            sorted segments in chronological order
	 * @param index:
	 *            the set of mark in sorted segments
	 * 
	 * @return
	 */
	public int[] myDbscan(int totalNum, Map<Integer, Segment> map, List<Segment> sortedSegments,
			List<Integer> filterList, List<Integer> segmentIDs) {
		// initialize visit records
		VisitRecord vr = new VisitRecord(totalNum);
		// initialize the label of cluster, -1 represent the segment unassigned to any
		// cluster
		int k = -1;
		int[] clusterMark = new int[totalNum];
		for (int i = 0; i < totalNum; i++) {
			clusterMark[i] = -1;
		}
		SearchRange sr = null;
		while (vr.getUnvisitedNum() > 0) {
			// select a segment randomly
			int j = (int) (Math.random() * vr.getUnvisitedNum());
			Integer segmentID = vr.unvisited.get(j);
			// do not handle invalid segment
			if (filterList.contains(segmentID)) {
				vr.visit(segmentID);
				continue;
			}
			System.out.println("剩余个数为： " + vr.getUnvisitedNum());
			// mark segmentID as visited
			vr.visit(segmentID);
			// create a object
			sr = new SearchRange();
			// determine the initial neighborhood range
			sr.searchRange(sortedSegments, map.get(segmentID).sp.getTime(), map.get(segmentID).ep.getTime());
			int l1 = sr.getLeft(), r1 = sr.getRight();
			if (r1 - l1 < minSup)
				continue;
			System.out.println("初始邻域范围为：" + l1 + "----" + r1);
			// determine the final neighborhood range
			List<Integer> neighbor = new ArrayList<>(128);
			SegDistance sd = null;
			for (int i = l1; i <= r1; i++) {
				Integer segmentID1 = 0;
				try {
					segmentID1 = segmentIDs.get(i);
				} catch (IndexOutOfBoundsException e) {
					System.out.println("数组越界！！！");
				}
				sd = new SegDistance(map.get(segmentID), map.get(segmentID1));
				if (sd.totalDist() <= eps) {
					neighbor.add(segmentID1);
				}
			}
			System.out.println("邻域大小为：" + neighbor.size());
			// if the number of neighbor of segmentID >=MINSUP
			if (neighbor.size() >= minSup) {
				// create a new cluster
				k = k + 1;
				clusterMark[segmentID] = k;
				for (int j1 = 0; j1 < neighbor.size(); j1++) {
					Integer segmentID2 = neighbor.get(j1);
					// if segmentID2 unvisited
					if (vr.unvisited.contains(segmentID2)) {
						// mark j1 as visited
						vr.visit(segmentID2);
						sr = new SearchRange();
						sr.searchRange(sortedSegments, map.get(segmentID2).sp.getTime(),
								map.get(segmentID2).ep.getTime());
						int l2 = sr.getLeft(), r2 = sr.getRight();
						if (r2-l2<minSup)
							continue;
						System.out.println("初始邻域范围为：" + l2 + "----" + r2);
						List<Integer> neighbor1 = new ArrayList<>(128);
						for (int j2 = l2; j2 <= r2; j2++) {
							Integer segmentID3 = 0;
							try {
								segmentID3 = segmentIDs.get(j2);
							} catch (Exception e) {
								System.out.println("数组越界！！！");
							}
							sd = new SegDistance(map.get(segmentID3), map.get(segmentID2));
							if (sd.totalDist() <= eps) {
								neighbor1.add(segmentID3);
							}
						}
						if (neighbor1.size() >= minSup) {
							for (Integer i1 : neighbor1) {
								if (!neighbor.contains(i1)) {
									neighbor.add(i1);
								}
							}
						}
						if (clusterMark[segmentID2] == -1) {
							clusterMark[segmentID2] = k;
						}
					}
				}
			} else {
				clusterMark[segmentID] = -1;
			}

		}
		return clusterMark;
	}
}
