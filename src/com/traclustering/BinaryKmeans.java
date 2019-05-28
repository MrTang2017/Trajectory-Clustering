package com.traclustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 二分kmeans 将所有点看成一个簇 当簇数目小于 k 时 对于每一个簇 计算总误差 在给定的簇上面进行 KMeans 聚类（k=2）
 * 计算将该簇一分为二之后的总误差 选择使得误差最小的那个簇进行划分操作.
 * 
 * @author 汤吉
 *
 */
public class BinaryKmeans {
	private static final int MAX_ITERATOR_NUMBER = 10;

	/**
	 * 
	 * @param allSegments
	 *            数据集
	 * @param k
	 *            簇的个数
	 * @return
	 */
	public static Map<Segment, List<Integer>> kMeans(Map<Integer, Segment> allSegments, int k) {
		// 存储每个质心所对应的簇
		Map<Segment, List<Integer>> clusters = new HashMap<>();
		@SuppressWarnings("unchecked")
		List<Integer> list = (List<Integer>) allSegments.keySet();
		// 存储每个簇的质心
		List<Segment> center = new ArrayList<>();
		// 先计算整个数据集的质心
		Segment cenSegment = meansCalculate(allSegments);
		center.add(cenSegment);
		clusters.put(cenSegment, list);
		while (center.size() < k) {
			// 遍历每个簇
			int bestSegmentIndex = 0;
			Segment firstCenterSegment = null;
			Segment secondCenterSegment = null;
			double minError = Double.MAX_VALUE;
			List<Integer> list2 = null;
			List<Segment> centerRecord = null;
			for (int i = 0; i < center.size(); i++) {
				double sumError = 0, sumLeft = 0;
				list2 = clusters.get(center.get(i));
				// 计算一分为2的簇的误差和以及质心
				centerRecord = new ArrayList<>();
				sumError = kMeans(2, list2, allSegments, centerRecord);
				// 计算不是当前簇的剩下簇的误差和
				sumLeft = errorSquareSum(center.get(i), clusters,allSegments);
				if (sumError + sumLeft < minError) {
					bestSegmentIndex = i;
					firstCenterSegment = centerRecord.get(0);
					secondCenterSegment = centerRecord.get(1);
					minError = sumError + sumLeft;
				}
			}
			// 移除旧的质心，更新质心
			center.set(bestSegmentIndex, firstCenterSegment);
			center.add(secondCenterSegment);
		}
		return clusters;
	}

	private static double errorSquareSum(Segment segment, Map<Segment, List<Integer>> clusters,Map<Integer, Segment> allSegments) {
		List<Integer> list=null;
		double sum=0;
		for(Segment s:clusters.keySet()) {
			double curSum=0;
			if (s.equals(segment)) {
				continue;
			}
			list=clusters.get(s);
			for(Integer i:list) {
				double dis=new SegDistance(allSegments.get(i), s).totalDist();
				curSum+=dis*dis;
			}
			sum+=curSum;
			
		}
		return sum;
	}

	private static double kMeans(int k, List<Integer> list, Map<Integer, Segment> allSegments, List<Segment> list2) {
		Map<Segment, List<Integer>> clusters = new HashMap<>();
		// 随机选取k个初始中心
		int size = allSegments.size();
		double errorToatl = 0;
		List<Integer> list1 = null;
		while (k > 0) {
			int segmentID = (int) Math.random() * size;
			list1 = new ArrayList<>();
			clusters.put(allSegments.get(segmentID), list1);
			k--;
		}
		// 计算每个线段到线段质心的距离，达到迭代次数后终止
		int count = 0;
		while (count < MAX_ITERATOR_NUMBER) {
			// 遍历所有的线段
			for (Integer i : list) {
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
				List<Integer> list21 = clusters.get(segment);
				list21.add(i);
				clusters.put(segment, list21);
			}
			// 更新质心
			updateMean(clusters, allSegments);
			for (Segment s : clusters.keySet())
				list2.add(s);
			count++;
		}
		errorToatl = errorSquareSum(clusters, allSegments);
		return errorToatl;
	}

	private static double errorSquareSum(Map<Segment, List<Integer>> clusters, Map<Integer, Segment> allSegments) {
		List<Integer> list = null;
		double sum = 0;
		for (Segment segment : clusters.keySet()) {
			double curSum = 0;
			list = clusters.get(segment);
			for (Integer i : list) {
				double dis = new SegDistance(segment, allSegments.get(i)).totalDist();
				curSum += dis * dis;
			}
			sum += curSum;
		}
		return sum;
	}

	private static Segment meansCalculate(Map<Integer, Segment> allSegments) {
		double startX = 0, startY = 0, endX = 0, endY = 0;
		List<String> timeS = new ArrayList<>();
		List<String> timeE = new ArrayList<>();
		for (Integer i : allSegments.keySet()) {
			startX += allSegments.get(i).sp.getLng();
			startY += allSegments.get(i).sp.getLat();
			endX += allSegments.get(i).ep.getLng();
			endY += allSegments.get(i).ep.getLat();
			timeS.add(allSegments.get(i).sp.getTime());
			timeE.add(allSegments.get(i).ep.getTime());
		}
		Collections.sort(timeS, new TimeComparator());
		Collections.sort(timeE, new TimeComparator());
		String startTime = timeS.get(timeS.size() / 2);
		String endTime = timeE.get(timeE.size() / 2);
		Point sp = new Point(startX, startY, startTime);
		Point ep = new Point(endX, endY, endTime);
		return new Segment(allSegments.size() / 2, sp, ep);
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
