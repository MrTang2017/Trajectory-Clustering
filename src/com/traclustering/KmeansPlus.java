package com.traclustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Kmeans++
 * 随机选取一个样本作为第一个聚类中心 c1;
 * 计算每个样本与当前已有类聚中心最短距离（即与最近一个聚类中心的距离），用 D(x)表示;
 * 这个值越大，表示被选取作为聚类中心的概率较大;
 * 最后，用轮盘法选出下一个聚类中心;
 * 重复步骤二，知道选出 k 个聚类中心。
 * 
 * @author 汤吉
 *
 */
public class KmeansPlus {

	private static final int MAX_ITERATOR_NUMBER = 50;

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
		// 选取k个初始中心
		int size = allSegments.size();
		List<Segment> segmentsList = new ArrayList<>();
		List<Integer> list = null;
		// 首先随机选取一个质心
		int segmentID = (int) Math.random() * size;
		segmentsList.add(allSegments.get(segmentID));
		// 选取剩下的初始质心
		List<Double> minValue = new ArrayList<>();
		double sumMinSqure = 0;
		while (k > 1) {
			for (Integer i : allSegments.keySet()) {
				double min = Double.MAX_VALUE;
				for (Segment s : segmentsList) {
					double dis = new SegDistance(allSegments.get(i), s).totalDist();
					if (dis < min) {
						min = dis;
					}
				}
				sumMinSqure += min * min;
				minValue.add(min);
			}
			double pMax = 0;
			int key = 0;
			for (int j = 0; j < minValue.size(); j++) {
				double p = Math.pow(minValue.get(j), 2) / sumMinSqure;
				if (p > pMax) {
					pMax = p;
					key = j;
				}
			}
			clusters.put(allSegments.get(key), list);
			k--;
		}

		// 计算每个线段到线段质心的距离，达到迭代次数后终止
		int count = 0;
		while (count < MAX_ITERATOR_NUMBER) {
			// 遍历所有的线段
			for (Integer i : allSegments.keySet()) {
				double min = Double.MAX_VALUE;
				Segment segment = null;
				// 找出该线段到对应质心的最短距离，并记录下对应的质心
				for (Segment s : clusters.keySet()) {
					double dis = new SegDistance(allSegments.get(i), s).totalDist();
					if (dis < min) {
						min = dis;
						segment = s;
					}
				}
				// 将该线段分配到对应的簇
				List<Integer> list2 = clusters.get(segment);
				list2.add(i);
				clusters.put(segment, list2);
			}
			// 更新质心
			updateMean(clusters, allSegments);
			count++;
			System.out.println(count);
		}
		return clusters;

	}

	private static void updateMean(Map<Segment, List<Integer>> clusters, Map<Integer, Segment> allSegments) {
		Map<Segment, List<Integer>> newClusters = new HashMap<>();
		List<Integer> list = null;
		Segment segment = null;
		for (Segment s : clusters.keySet()) {
			segment = meansCalculate(clusters.get(s), allSegments);
			list = new ArrayList<>();
			newClusters.put(segment, list);
		}
		clusters = newClusters;
	}

	private static Segment meansCalculate(List<Integer> list, Map<Integer, Segment> allSegments) {
		double startX = 0, startY = 0, endX = 0, endY = 0;
		List<String> timeS = new ArrayList<>();
		List<String> timeE = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			startX += allSegments.get(list.get(i)).sp.getLng();
			startY += allSegments.get(list.get(i)).sp.getLat();
			endX += allSegments.get(list.get(i)).ep.getLng();
			endY += allSegments.get(list.get(i)).ep.getLat();
			timeS.add(allSegments.get(list.get(i)).sp.getTime());
			timeE.add(allSegments.get(list.get(i)).ep.getTime());
		}
		Collections.sort(timeS, new TimeComparator());
		Collections.sort(timeE, new TimeComparator());
		String startTime = timeS.get(timeS.size() / 2);
		String endTime = timeE.get(timeE.size() / 2);
		Point sp = new Point(startX, startY, startTime);
		Point ep = new Point(endX, endY, endTime);
		return new Segment(list.size() / 2, sp, ep);
	}

}
