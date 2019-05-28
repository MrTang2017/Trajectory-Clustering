package com.traclustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *传统的Kmeans
 */
public class Kmeans {
	
	private static final int MAX_ITERATOR_NUMBER=50;
	/**
	 * 
	 * @param allSegments
	 *            数据集
	 * @param k
	 *            簇的个数
	 * @return
	 */
	public static Map<Segment, List<Integer>> kMeans(Map<Integer, Segment> allSegments, int k) {

		Map<Segment, List<Integer>> clusters = new HashMap<>();
		// 随机选取k个初始中心
		int size = allSegments.size();
		List<Integer> list = null;
		while (k > 0) {
			int segmentID = (int) (Math.random() * size);
			list = new ArrayList<>();
			clusters.put(allSegments.get(segmentID), list);
			k--;
		}
		System.out.println(clusters.size());
		//计算每个线段到线段质心的距离，达到迭代次数后终止
		int count=0;
		while(count++<MAX_ITERATOR_NUMBER) {
			//遍历所有的线段
			for(Integer i:allSegments.keySet()) {
				double min=Double.MAX_VALUE;
				Segment segment=null;
				//找出该线段到对应质心的最短距离，并记录下对应的质心
				for(Segment s:clusters.keySet()) {
					double dis=new SegDistance(allSegments.get(i), s).totalDist();
					if(dis<min) {
						min=dis;
						segment=s;
					}
				}
				//将该线段分配到对应的簇
				List<Integer> list2= clusters.get(segment);
				list2.add(i);
				clusters.put(segment,list2);	
			}			
			//更新质心
			if (count==MAX_ITERATOR_NUMBER) {
				break;
			}
			clusters=updateMean(clusters,allSegments);
			System.out.println(count);
		}	
		return clusters;

	}
	private static Map<Segment, List<Integer>> updateMean(Map<Segment, List<Integer>> clusters,Map<Integer, Segment> allSegments) {
		Map<Segment, List<Integer>> newClusters=new HashMap<>();
		List<Integer> list=null;
		Segment segment=null;
		for(Segment s:clusters.keySet()) { 
			segment=meansCalculate(clusters.get(s),allSegments);
			list=new ArrayList<>();
			newClusters.put(segment, list);
		}
		return newClusters;
	}
	private static Segment meansCalculate(List<Integer> list, Map<Integer, Segment> allSegments) {
		double startX=0,startY=0,endX=0,endY=0;
		List<String> timeS=new ArrayList<>();
		List<String> timeE=new ArrayList<>();
		for(int i=0;i<list.size();i++) {
			startX+=allSegments.get(list.get(i)).sp.getLng();
			startY+=allSegments.get(list.get(i)).sp.getLat();
			endX+=allSegments.get(list.get(i)).ep.getLng();
			endY+=allSegments.get(list.get(i)).ep.getLat();
			timeS.add(allSegments.get(list.get(i)).sp.getTime());
			timeE.add(allSegments.get(list.get(i)).ep.getTime());
		}
		Collections.sort(timeS,new TimeComparator());
		Collections.sort(timeE, new TimeComparator());
		String startTime=timeS.get(timeS.size()/2);
		String endTime=timeE.get(timeE.size()/2);
		Point sp=new Point(startX, startY, startTime);
		Point ep=new Point(endX, endY, endTime);
		return new Segment(list.size()/2, sp, ep);
	}
		
}
