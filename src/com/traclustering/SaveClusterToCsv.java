package com.traclustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * save the clusters to file
 * 
 * @author ÌÀ¼ª
 *
 */
public class SaveClusterToCsv {

	private static final String FILE_DIRECTORY = "./clusters/";

	public SaveClusterToCsv() {
	}

	public static void save(Map<Integer, Segment> segMap, Map<Integer, List<Integer>> clustersMap) {

		File file = new File(FILE_DIRECTORY);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
		for (Integer c : clustersMap.keySet()) {
			String clusterFileName = Integer.toString(c) + ".csv";
			String clusterFilePath = FILE_DIRECTORY + clusterFileName;
			File file2 = new File(clusterFilePath);
			BufferedWriter bWriter = null;
			try {
				bWriter = new BufferedWriter(new FileWriter(file2, true));
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (Integer s : clustersMap.get(c)) {
				// obtain current segment
				Segment segment = segMap.get(s);
				String x1 = Double.toString(segment.sp.getLng());
				String y1 = Double.toString(segment.sp.getLat());
				String t1 = segment.sp.getTime();
				String x2 = Double.toString(segment.ep.getLng());
				String y2 = Double.toString(segment.ep.getLat());
				String t2 = segment.ep.getTime();
				try {
					bWriter.write(x1 + "," + y1 + "," + t1 + "," + x2 + "," + y2 + "," + t2);
					bWriter.flush();
					bWriter.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				bWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
			
			Map<Integer, Segment> sMap = new HashMap<Integer, Segment>();
			Point pa = new Point(4.25, 2.53, "12:00:15");
			Point pb = new Point(5.65, 10.24, "12:30:45");
			Segment segment1 = new Segment(0, pa, pb);
			Point pc = new Point(4.26, 2.535, "11:50:25");
			Point pd = new Point(5.64, 10.25, "12:45:10");
			Segment segment2 = new Segment(1, pc, pd);
			sMap.put(0, segment1);
			sMap.put(1, segment2);
			Map<Integer, List<Integer>> map = new HashMap<>();
			List<Integer> list=new ArrayList<>();
			list.add(0);
			list.add(1);
			map.put(0, list);
			save(sMap, map);		
		}

}
