package com.traclustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestKmeans {   
		private static final int NUMBERS = 10;
		private static final String FILE_PATH = "E:\\dataset";
		public static void main(String[] args) {
			Map<Integer, Segment> segmentMap = new HashMap<>();
			// read data set from file to segmentMap
			ReadDataset.read(FILE_PATH,segmentMap);
			
			//filter some invalid segments
			List<Integer> InvalidSegments = new ArrayList<>();//store invalid segments
			FilterInvalidSegments.filterSegments(segmentMap, InvalidSegments);
			
			System.out.println("------------Start clustering------------");
			Map<Segment, List<Integer>> clusters=Kmeans.kMeans(segmentMap,NUMBERS);
			System.out.println("------------Finish clustering------------");
			
			for (Segment c : clusters.keySet()) {
				System.out.println(clusters.get(c).size());
			}
			
			System.out.println("-----------------------------------");
			System.out.println("The number of clusters is:" + (clusters.size()));
			System.out.println("starting evaluate the clusters........");
			double result = SilhouetteCof.evaluate2(segmentMap, clusters);
			System.out.println("silhouette coeifficient is : " + result);

		}

}
