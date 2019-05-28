package com.traclustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReadDataset {

	public ReadDataset() {}
	/**   
	 * Read data set
	 * 
	 * @param filePath
	 * @return
	 */
	public static void read(String filePath, Map<Integer, Segment> allSegments) {
		File fileDir = new File(filePath);
		File[] files=null;
		if (fileDir.isDirectory()) {
			 files= fileDir.listFiles();
		}
		BufferedReader br = null;
		int count = 0;
		int k=0;
		// traverse each file
		for (File file1 : files) {
			k=k+1;
			// all trajectory data of current node
			File[] nodeFiles = file1.listFiles();
			for (File file2 : nodeFiles) {
				// traverse current trajectory data
				try {
					br = new BufferedReader(new FileReader(file2));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				String line = " ";
				String everyLine = " ";
				try {
					List<Point> list = new ArrayList<>(2048);
					while ((line = br.readLine()) != null) {
						everyLine = line;
						String[] lineData = everyLine.split(",");
						double x = Double.valueOf(lineData[0]);
						double y = Double.valueOf(lineData[1]);
						Point point = new Point(x, y, lineData[2]);
						list.add(point);
					}
					for (int i = 0; i < list.size() - 1; i++) {
						Segment segment = new Segment(count, list.get(i), list.get(i + 1));
						allSegments.put(count, segment);
						count += 1;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//只读入前k个数据集
			if (k==4) {
				break;
			}
		}
	}
	
	public  static void read(String filePath, Map<Integer, Segment> allSegments, Map<Integer, List<Segment>> segmentsOfTra) {
		File fileDir = new File(filePath);
		File[] files=null;
		if (fileDir.isDirectory()) {
			 files= fileDir.listFiles();
		}
		BufferedReader br = null;
		int count = 0;
		int k=0;
		int traNumber=0;
		// traverse each file
		for (File file1 : files) {
			k=k+1;
			// all trajectory data of current node
			File[] nodeFiles = file1.listFiles();
			for (File file2 : nodeFiles) {
				traNumber+=1;
				// traverse current trajectory data
				try {
					br = new BufferedReader(new FileReader(file2));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				String line = " ";
				String everyLine = " ";
				try {
					List<Point> list = new ArrayList<>(2048);
					List<Segment> list2=new ArrayList<>(2048);
					while ((line = br.readLine()) != null) {
						everyLine = line;
						String[] lineData = everyLine.split(",");
						double x = Double.valueOf(lineData[0]);
						double y = Double.valueOf(lineData[1]);
						Point point = new Point(x, y, lineData[2]);
						list.add(point);
					}
					for (int i = 0; i < list.size() - 1; i++) {
						Segment segment = new Segment(count, list.get(i), list.get(i + 1));
						allSegments.put(count++, segment);
						list2.add(segment);
					}
					segmentsOfTra.put(traNumber, list2);	
				} catch (IOException e) {
					e.printStackTrace();
				}	
				
				
			}
			//只读入前k个数据集
			if (k==4){
				break;
			}
		}
	}
}
