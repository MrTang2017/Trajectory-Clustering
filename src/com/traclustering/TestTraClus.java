package com.traclustering;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestTraClus {
	private static final double EPS = 0.36;
	private static final int MIN_SUP = 25;
	private static final String FILE_PATH = "E:\\dataset";

	public static void main(String[] args) throws FileNotFoundException {
		Map<Integer, Segment> allSegments = new HashMap<>();
		Map<Integer, List<Segment>> segmentsOfTra = new HashMap<>();
		// read data set from file to segmentMap
		ReadDataset.read(FILE_PATH, allSegments, segmentsOfTra);
		int totalNumber = allSegments.size();
		System.out.println(totalNumber);
		System.out.println(segmentsOfTra.size());

		// filter some invalid segments
		List<Integer> InvalidSegments = new ArrayList<>();// store invalid segments  
		FilterInvalidSegments.filterSegments(allSegments, InvalidSegments);

		// sort all segments in chronological order
		Collection<Segment> segCollections = allSegments.values();
		List<Segment> sortedSegments = new ArrayList<Segment>(segCollections);
		Collections.sort(sortedSegments, new SegmentComparator());

		// store the ID of all sortedSegments in chronological order
		List<Integer> sortedSegmentsID = new ArrayList<>();
		for (Segment segment : sortedSegments) {
			sortedSegmentsID.add(segment.getId());
		}

		System.out.println("------------Start clustering------------");
		TraClus traClus = new TraClus(EPS, MIN_SUP);
		// Map<Integer, List<Integer> > clusters;
		int[] clusters;
		clusters = traClus.traClus(totalNumber, allSegments, sortedSegments, InvalidSegments, sortedSegmentsID);
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
		List<Integer> currentCluster = null;
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

		/*
		 * //初次过滤 Iterator<Map.Entry<Integer, List<Integer>>>
		 * entryIterator=clusters.entrySet().iterator(); while (entryIterator.hasNext())
		 * { Map.Entry<Integer, List<Integer>> next = entryIterator.next(); if
		 * (next.getValue().size() < MIN_SUP) { entryIterator.remove(); } }
		 */

		for (Integer c : clustersMap.keySet()) {
			System.out.println(c + " " + clustersMap.get(c).size());
		}
		System.out.println("过滤前，簇的大小为："+clustersMap.size());

		// 再次过滤
		filterClusters(allSegments, clustersMap, segmentsOfTra);

		System.out.println("过滤后.........");
		System.out.println("The number of clusters is:" + (clustersMap.size()));
		System.out.println("starting evaluate the clusters........");
		double result = SilhouetteCof.evaluate(allSegments, clustersMap);
		System.out.println("silhouette coeifficient is : " + result);
		System.out.println("Ready to save clustering results to file.......");
		// save the clustering results to file
		SaveClusterToCsv.save(allSegments, clustersMap);
		System.out.println("clustering results have been saved!");

		// extract representative segment from each cluster
		/*
		 * System.out.println("starting extract features of cluster"); Map<Integer,
		 * ClusterRepresent> crMap = new HashMap<>(); crMa p =
		 * ClusterRepresentExtract.extrFeatureFromClusters(segMap, clustersMap);
		 */
		// save the representation of each cluster to file
		// SaveRepToCsv.save(crMap);
	}

	private static void filterClusters(Map<Integer, Segment> allSegments, Map<Integer, List<Integer>> clusters,
			Map<Integer, List<Segment>> segmentsOfTra) {
		HashSet<Integer> set = null;
		Iterator<Map.Entry<Integer, List<Integer>>> iterator = clusters.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, List<Integer>> next = iterator.next();
			set = new HashSet<>();
			for (Integer id : next.getValue()) {
				for (Integer k : segmentsOfTra.keySet()) {
					if (segmentsOfTra.get(k).contains(allSegments.get(id))) {
						if (!set.contains(k)) {
							set.add(k);
						}
					}
				}
			}
			if (set.size() < MIN_SUP) {
				iterator.remove();
			}
		}

	}

}
