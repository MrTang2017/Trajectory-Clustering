package com.traclustering;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Use Geolife trajectory data,apply DBSCAN algorithm to discover clusters
 * 
 * @author TJ
 *
 */

public class TestDbscanCluster {
	private static final double EPS = 0.36;    
	private static final int MIN_SUP = 25;
	private static final String FILE_PATH = "E:\\dataset";

	public static void main(String[] args) throws FileNotFoundException {
		Map<Integer, Segment> segmentMap = new HashMap<>();
		// read data set from file to segmentMap
		ReadDataset.read(FILE_PATH,segmentMap);  
		int totalNumber = segmentMap.size();
		
		//filter some invalid segments
		List<Integer> InvalidSegments = new ArrayList<>();//store invalid segments
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
		Dbscan dbscan = new Dbscan(EPS, MIN_SUP);
		int[] clusters;
		clusters = dbscan.myDbscan(totalNumber, segmentMap, sortedSegments, InvalidSegments, sortedSegmentsID);
		System.out.println("------------Finish clustering------------");

		// save the number of each cluster
		Map<Integer, Integer> clusterNum = new HashMap<>();
		for (Integer c : clusters) {
			if (!clusterNum.containsKey(c)) {
				clusterNum.put(c, 1);
			} else {
				clusterNum.put(c, clusterNum.get(c) + 1);
			}
		}

		System.out.println("the number of noise is " + clusterNum.get(-1));
		clusterNum.remove(-1);

		// save the segment of each cluster
		Map<Integer, List<Integer>> clustersMap = new HashMap<>();
		List<Integer> currentCluster=null;
		for (Integer j : clusterNum.keySet()) {
			currentCluster = new ArrayList<>();
			for (int i = 0; i < clusters.length; i++) {
				if (clusters[i] == j)
					currentCluster.add(i);
			}
			clustersMap.put(j, currentCluster);
		}
		
		for (Integer c : clusterNum.keySet()) {
			if (clusterNum.get(c) < MIN_SUP) {
				clustersMap.remove(c);
			}
		}

		for (Integer c : clustersMap.keySet()) {
			System.out.println(c + " " + clustersMap.get(c).size());
		}

		System.out.println("-----------------------------------");
		System.out.println("The number of clusters is:" + (clustersMap.size()));
		System.out.println("starting evaluate the clusters........");
		double result = SilhouetteCof.evaluate(segmentMap, clustersMap);
		System.out.println("silhouette coeifficient is : " + result);
		System.out.println("Ready to save clustering results to file.......");
		//save the clustering results to file
		SaveClusterToCsv.save(segmentMap, clustersMap);                                                                                                                                               
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
