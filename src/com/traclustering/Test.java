package com.traclustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Test {

	public static void main(String[] args) {

		Map<Integer, Integer> map = new TreeMap<>();
		map.put(0, 2);
		map.put(9, 5);
		map.put(4, 3);
		map.put(6, 7);

		List<Map.Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer, Integer>>(map.entrySet());
		// 通过比较器实现排序
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
			// 升序排序
			@Override
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		
		while (list.size() > 0) {
			int key=list.get(0).getKey();
			int s=list.get(0).getValue();
			System.out.println(s);
			list.remove(0);
            map.remove(key);

		}
	}

}
