package com.traclustering;

import java.util.Iterator;

import java.util.List;
import java.util.Map;

public class FilterInvalidSegments {

	public FilterInvalidSegments() {
	}

	public static void filterSegments(Map<Integer, Segment> map, List<Integer> list) {
		Iterator<Map.Entry<Integer, Segment>> entryIterator = map.entrySet().iterator();
		while (entryIterator.hasNext()) {
			Map.Entry<Integer, Segment> next = entryIterator.next();
			if (next.getValue().distKmeter() == 0 || next.getValue().duration() == 0) {
				list.add(next.getKey());
				entryIterator.remove();
			}
		}
	}
}
