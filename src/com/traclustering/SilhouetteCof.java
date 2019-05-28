package com.traclustering;

import java.util.List;
import java.util.Map;

public class SilhouetteCof {

	public SilhouetteCof() {}
	
	public static double evaluate(Map<Integer, Segment> segMap, Map<Integer, List<Integer>> clustersMap) {
		double totalSc=0;                                
		// traverse each cluster
		for(Integer c:clustersMap.keySet()) {
			double avgDistance=0;
			double minDist=1;
			// obtain the neighborhood of cluster c
			List<Integer> list=clustersMap.get(c);
			for(Integer s:list) {
				// obtain a segment of cluster c
				Segment segment=segMap.get(s); 
				avgDistance=avgDistance+distanceTo(segment,list,segMap)/(list.size()-1);
			}
			// the average distance in the cluster c
			double al=avgDistance/list.size();
			for(Integer c1:clustersMap.keySet()) {
				if(c.equals(c1))
					continue;
				// Minimum average distance of current cluster to other clusters
				double bl=distanceTo1(c,c1,clustersMap,segMap)/list.size();
				if (bl<=minDist) {
					minDist=bl;	
				}
			}
			totalSc+=(minDist-al)/(Math.max(minDist, al));
		}
		// return silhouette coefficient
		return totalSc/clustersMap.size();
	}
	
	
	
	public static double evaluate2(Map<Integer, Segment> segMap, Map<Segment, List<Integer>> clustersMap) {
		double totalSc=0;                                
		// traverse each cluster
		for(Segment c:clustersMap.keySet()) {
			double avgDistance=0;
			double minDist=1;
			// obtain the neighborhood of cluster c
			List<Integer> list=clustersMap.get(c);
			for(Integer s:list) {
				// obtain a segment of cluster c
				Segment segment=segMap.get(s);
				avgDistance=avgDistance+distanceTo(segment,list,segMap)/(list.size()-1);
			}
			// the average distance in the cluster c
			double al=avgDistance/list.size();
			for(Segment c1:clustersMap.keySet()) {
				if(c.equals(c1))
					continue;
				// Minimum average distance of current cluster to other clusters
				double bl=distanceTo2(c,c1,clustersMap,segMap)/list.size();
				if (bl<=minDist) {
					minDist=bl;	
				}
			}
			totalSc+=(minDist-al)/(Math.max(minDist, al));
		}
		// return silhouette coefficient
		return totalSc/clustersMap.size();
	}
	
	/**
	 * The sum of the distances from one cluster to another
	 * @param c
	 * @param c1
	 * @param clustersMap
	 * @param segMap
	 * @return
	 */
	private static double distanceTo2(Segment c, Segment c1, Map<Segment, List<Integer>> clustersMap,
			Map<Integer, Segment> segMap) {
		double sum=0;
		for(Integer s:clustersMap.get(c)) {
			Segment segment=segMap.get(s);
			sum+=distanceTo(segment, clustersMap.get(c1), segMap);
		}
		return sum;
	}
	
	
	private static double distanceTo1(Integer c, Integer c1, Map<Integer, List<Integer>> clustersMap,
			Map<Integer, Segment> segMap) {
		double sum=0;
		for(Integer s:clustersMap.get(c)) {
			Segment segment=segMap.get(s);
			sum+=distanceTo(segment, clustersMap.get(c1), segMap);
		}
		return sum;
	}
	
	/**
	 * the sum of the distances of all segments in the cluster
	 * @param segment
	 * @param list
	 * @param segMap
	 * @return
	 */
	private static double distanceTo(Segment segment, List<Integer> list, Map<Integer, Segment> segMap) {
		double sumDistance=0;
		for(Integer s:list) {
			Segment curSegment=segMap.get(s);
			sumDistance+=new SegDistance(segment,curSegment).totalDist();
		}
		return sumDistance;
	}
}
