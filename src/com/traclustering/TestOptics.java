package com.traclustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestOptics {
	private static final double EPS = 0.36;
	private static final int MIN_SUP = 25;
	private static final String FILE_PATH = "E:\\dataset";

	public static void main(String[] args) {
		Map<Integer, Segment> segmentMap = new HashMap<>();
		// read data set from file to segmentMap
		ReadDataset.read(FILE_PATH, segmentMap);

		// filter some invalid segments
		List<Integer> InvalidSegments = new ArrayList<>();// store invalid segments
		FilterInvalidSegments.filterSegments(segmentMap, InvalidSegments);

		// sort all segments in chronological order
		Collection<Segment> segCollections = segmentMap.values();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
		List<Segment> sortedSegments = new ArrayList<Segment>(segCollections);
		Collections.sort(sortedSegments, new SegmentComparator());

		// store the ID of all sortedSegments in chronological order
		List<Integer> sortedSegmentsID = new ArrayList<>();
		for (Segment segment : sortedSegments) {
			sortedSegmentsID.add(segment.getId());
		}

		System.out.println("------------Start clustering------------");
		Optics optics = new Optics(EPS, MIN_SUP);
		Map<Integer, Integer> clusters;
		clusters = optics.optics(segmentMap, sortedSegments, sortedSegmentsID);
		System.out.println("------------Finish clustering------------");

		// save the number of each cluster
		Map<Integer, List<Integer>> clusterNum = new HashMap<>();
		List<Integer> list=null;
		for (Integer c : clusters.keySet() ) {
			if (!clusterNum.containsKey(clusters.get(c))) {
				list=new ArrayList<>();
				list.add(c);
				clusterNum.put(clusters.get(c), list);
			} else {
				List<Integer> list1=clusterNum.get(clusters.get(c));
				list1.add(c);
				clusterNum.put(clusters.get(c), list1);
			}
		}

		System.out.println("the number of noise is " + clusterNum.get(-1).size());
		clusterNum.remove(-1);

		// save the segment of each cluster

		for (Integer c : clusterNum.keySet()) {
			System.out.println(c + " " + clusterNum.get(c).size());
		}

		System.out.println("-----------------------------------");
		System.out.println("The number of clusters is:" + (clusterNum.size()));
		System.out.println("starting evaluate the clusters........");
		double result = SilhouetteCof.evaluate(segmentMap, clusterNum);
		System.out.println("silhouet te coeifficient is : " + result);
		System.out.println("Ready to save clustering results to file.......");
		// save the clustering results to file
		SaveClusterToCsv.save(segmentMap, clusterNum);
		System.out.println("clustering results have been saved!");

		// extract representative segment from each cluster
		/*
		 * System.out.println("starting extract features of cluster"); Map<Integer,
		 * ClusterRepresent> crMap = new HashMap<>(); crMap =
		 * ClusterRepresentExtract.extrFeatureFromClusters(segMap, clustersMap);
		 */
		// save the representation of each cluster to file
		// SaveRepToCsv.save(crMap);

	} 

}
