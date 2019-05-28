package com.traclustering;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Ordering points to identify cluster structure
 * 
 * @author 汤吉
 *
 */
public class Optics {
	private double eps;
	private int MinPts;

	public Optics(double eps, int MinPts) {
		this.eps = eps;
		this.MinPts = MinPts;
	}
/**
 * 
 * @param allSegments 有效的轨迹段
 * @param sortedSegments 按照时间排序的轨迹段
 * @param sortedSegmentsID 按照时间排序的轨迹段的ID
 * @return
 */
	public Map<Integer, Integer> optics(Map<Integer, Segment> allSegments, List<Segment> sortedSegments,
			List<Integer> sortedSegmentsID) {
		List<Integer> orderList = new ArrayList<>();//有序遍历的轨迹段
		Map<Integer, Integer> classID = new HashMap<>();// 给每个轨迹段分配簇的ID  
		Map<Integer, Double> coreDist = new HashMap<>();// 每个轨迹段的核心距离
		Map<Integer, Double> reachDist = new HashMap<>();// 每个轨迹段的可达距离
		Map<Integer, String> visitRecord = new HashMap<>();// 每个轨迹段的访问记录
		init(allSegments, coreDist, reachDist, visitRecord, classID);//初始化
		for (Integer sID : allSegments.keySet()) {
			if (visitRecord.get(sID).equals("unprocessed")) {
				// 得到邻居集合
				List<Integer> neighbors = getNeighbors(sID, allSegments, sortedSegments, sortedSegmentsID);
				// 标记访问过
				visitRecord.put(sID, "processed");
				// 加入有序列表中
				orderList.add(sID);
				if (neighbors.size() >= MinPts) {
					// 按照值排序的键值对集合
					TreeMap<Integer, Double> priorityMap = new TreeMap<>();
					update(sID, neighbors, priorityMap, allSegments, visitRecord, reachDist, coreDist);
					// 将proirityMap.entrySet()转换list
					List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>(
							priorityMap.entrySet());
					// 通过比较器实现排序
					Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
						// 升序排序
						@Override
						public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
							return o1.getValue().compareTo(o2.getValue());
						}
					});
					
					while (list.size()>0) {
						int s=list.get(0).getKey();
						list.remove(0);
						priorityMap.remove(s);
						List<Integer> neighbors2 = getNeighbors(s, allSegments, sortedSegments, sortedSegmentsID);
						visitRecord.put(s, "processed");
						orderList.add(s);
						if (neighbors2.size() >= MinPts) {
							//更新
							try {
								update(s, neighbors2, priorityMap, allSegments, visitRecord, reachDist, coreDist);
							} catch (ConcurrentModificationException e) {
								System.out.println("并发修改异常！");
							}
							
							// 将proirityMap.entrySet()转换list
							List<Map.Entry<Integer, Double>> list1 = new ArrayList<Map.Entry<Integer, Double>>(
									priorityMap.entrySet());
							// 通过比较器实现排序
							Collections.sort(list1, new Comparator<Map.Entry<Integer, Double>>() {
								// 升序排序
								@Override
								public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
									return o1.getValue().compareTo(o2.getValue());
								}
							});
						    list=list1;//赋值给list
						}
						
						
					}

				}

			}
		}
		getClusters(classID, orderList, coreDist, reachDist);

		return classID;
	}

	/**
	 * 从聚类的输出得到聚类结果
	 * 
	 * @param classID
	 * @param orderList
	 * @param coreDist
	 * @param reachDist
	 */
	private void getClusters(Map<Integer, Integer> classID, List<Integer> orderList, Map<Integer, Double> coreDist,
			Map<Integer, Double> reachDist) {
		double lessEps = 0.35;
		int k = 1;
		int clusterId = -1;
		for (int i = 0; i < orderList.size(); i++) {
			int sID = orderList.get(i);
			if (reachDist.get(sID) > lessEps || reachDist.get(sID) == Double.MAX_VALUE) {
				if (coreDist.get(sID) < lessEps && coreDist.get(sID) != Double.MAX_VALUE) {
					clusterId = k;
					k += 1;
					classID.put(sID, clusterId);
				} else {
					classID.put(sID, -1);
				}

			} else {
				classID.put(sID, clusterId);
			}
		}
	}

	private void update(int s, List<Integer> neighbors, TreeMap<Integer, Double> priorityMap,
			Map<Integer, Segment> allSegments, Map<Integer, String> visitRecord, Map<Integer, Double> reachDist,
			Map<Integer, Double> coreDist) {
		double coreDis = getCoreDist(s, neighbors, allSegments);
		coreDist.put(s, coreDis);
		for (Integer sID : neighbors) {
			if (visitRecord.get(sID).equals("unprocessed")) {
				double dis = new SegDistance(allSegments.get(sID), allSegments.get(s)).totalDist();
				double reachDis = Math.max(coreDis, dis);
				if (reachDist.get(sID) == Double.MAX_VALUE) {
					reachDist.put(sID, reachDis);
					priorityMap.put(sID, reachDis);

				} else if (reachDis < reachDist.get(sID)) {
					reachDist.put(sID, reachDis);
					priorityMap.put(sID, reachDis);
				}
			}
		}
	}

	/**
	 * 求核心距离
	 * 
	 * @param s
	 * @param neighbors
	 * @param allSegments
	 * @return
	 */
	private double getCoreDist(int s, List<Integer> neighbors, Map<Integer, Segment> allSegments) {

		double[] distance = new double[neighbors.size()];
		for (int i = 0; i < neighbors.size(); i++) {
			distance[i] = new SegDistance(allSegments.get(neighbors.get(i)), allSegments.get(s)).totalDist();
		}
		Arrays.sort(distance);
		return distance[MinPts - 1];
	}

	/**
	 * 获取邻域
	 * 
	 * @param s
	 * @param allSegments
	 * @param sortedSegments
	 * @param sortedSegmentsID
	 * @return
	 */
	private List<Integer> getNeighbors(int s, Map<Integer, Segment> allSegments, List<Segment> sortedSegments,
			List<Integer> sortedSegmentsID) {
		SearchRange sr = new SearchRange();
		// determined the initial neighborhood range
		sr.searchRange(sortedSegments, allSegments.get(s).sp.getTime(), allSegments.get(s).ep.getTime());
		int l = sr.getLeft(), r = sr.getRight();
		System.out.println(l+" "+r);
		// determined the final neighborhood range
		List<Integer> neighbor = new ArrayList<>(100);
		SegDistance sd =null;
		for (int i = l; i <= r; i++) {
			Integer key = 0;
			try {
				key=sortedSegmentsID.get(i);
			} catch (Exception e) {
				System.out.println(i+"数组越界！！！");
			}
			sd = new SegDistance(allSegments.get(key), allSegments.get(s));
			if (sd.totalDist() <= eps)
				neighbor.add(key);
		}

		return neighbor;
	}

	/**
	 * 初始化
	 * 
	 * @param allSegments
	 * @param reachDist
	 * @param visitRecord
	 */
	private void init(Map<Integer, Segment> allSegments, Map<Integer,Double> coreDist, Map<Integer, Double> reachDist,
			Map<Integer, String> visitRecord, Map<Integer, Integer> classID) {
		for (Integer sID : allSegments.keySet()) {
			reachDist.put(sID, Double.MAX_VALUE);
			coreDist.put(sID, Double.MAX_VALUE);
			visitRecord.put(sID, "unprocessed");
			classID.put(sID, -1);
		}

	}

	public double getEps() {
		return eps;
	}

	public void setEps(double eps) {
		this.eps = eps;
	}

	public int getMinPts() {
		return MinPts;
	}

	public void setMinPts(int minPts) {
		MinPts = minPts;
	}

}
