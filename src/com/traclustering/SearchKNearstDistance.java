package com.traclustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchKNearstDistance {
	public SearchKNearstDistance() {
	}

	public static List<Double> searchKNearstDistance(List<Segment> sortedSegments, List<Double> kD, int k) {
		int n = sortedSegments.size();
		Map<Integer, List<Double>> map=new HashMap<>(n);
		
		for (int i = 0; i < n; i++) {
			List<Double> list=new ArrayList<>(n);
			System.out.println(i);		
			int j = 0;
			if (i > j) {
				while (j < i) {
					list.add(map.get(j).get(i-j-1));
					j++;
				}
			}
			for (int j2 = i + 1; j2 < n; j2++) {
				double distance = new SegDistance(sortedSegments.get(i), sortedSegments.get(j2)).totalDist();
				list.add(distance);
			}
			// 对第i个轨迹线段的所有距离进行排序，第k个距离
			map.put(i, list);
			Collections.sort(list);
			kD.add(list.get(k-1));
		}
		return kD;
	}

}
