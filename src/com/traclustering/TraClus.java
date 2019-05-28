package com.traclustering;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
	
public class TraClus {
		// the neighborhood distance
		private double eps;
		// the least number of segments in the neighborhood
		private int minSup;

		public TraClus(double eps, int minSup) {
			this.eps = eps;
			this.minSup = minSup;
		}

		/**
		 * apply TraClus algorithm to the segments
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
		public int[] traClus(int totalNum, Map<Integer, Segment> map, List<Segment> sortedSegments,
				List<Integer> filterList, List<Integer> segmentIDs) {
			// initialize visit records
			VisitRecord vr = new VisitRecord(totalNum);
			// initialize records of segments assigned to clusters
			int[] segmentsAssign=new int[totalNum];
			initAssign(segmentsAssign);
			int k=0;
			Map<Integer, List<Integer>> clusters=new HashMap<>();
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
				// mark segmentID as visited
				vr.visit(segmentID);
				// create a object
				sr = new SearchRange();
				// determine the initial neighborhood range
				sr.searchRange(sortedSegments, map.get(segmentID).sp.getTime(), map.get(segmentID).ep.getTime());
				int l1 = sr.getLeft(), r1 = sr.getRight();
				if (r1 - l1 < minSup)
					continue;
				// the final neighborhood
				List<Integer> neighbor=new ArrayList<>(128);
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
					segmentsAssign[segmentID]=k;
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
							if (segmentsAssign[segmentID2] == -1) {
								segmentsAssign[segmentID2] = k;
							}
						}
					}
					clusters.put(k++, neighbor);
					
				} else {
					segmentsAssign[segmentID] = -1;
				}

			}
			return segmentsAssign;
		}

		private void initAssign(int[] segmentsAssign) {
			for (int i = 0; i < segmentsAssign.length; i++) {
				segmentsAssign[i]=-1;
			}	
		}
	}

