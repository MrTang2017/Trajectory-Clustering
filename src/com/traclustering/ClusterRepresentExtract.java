package com.traclustering;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusterRepresentExtract {
	// the maximum start time of a segment
	private static String MAX_START_TIME="00:00:00";
	// the minimum start time of a segment
	private static String MIN_START_TIME="23:59:59";
	// the maximum end time of a segment
	private static String MAX_END_TIME="00:00:00";
	// the minimum end time of a segment
	private static String MIN_END_TIME="23:59:59";
	
	public ClusterRepresentExtract() {}
	
	/**
	 * 
	 * @param segMap the mapping of  all the segments
	 * @param clustersMap the neighbor set of every cluster
	 * @return
	 */
	public static Map<Integer, ClusterRepresent> extrFeatureFromClusters(Map<Integer, Segment> segMap, 
			Map<Integer, List<Integer>> clustersMap) {
		// the sum of xCoodinate of a start point
		double startXSum=0;
		// the sum of xCoordinate of a end point
		double endXSum=0;
		// the sum of yCoordinate of a start point
		double startYsum=0;
		// the sum of yCoordinate of a end point
		double endYSum=0;
		// the sum angle between a vector segment and a fixed vector
		double angleSum=0;
		// the sum of velocity 
		double velocitySum=0;
		//traverse all the clusters, i represent the label of every cluster
		Map<Integer, ClusterRepresent> crMap=new HashMap<>();
		for(int i:clustersMap.keySet()) {
			//obtain the neighbor set of current cluster
			List<Integer> curClusterList=clustersMap.get(i);
			for(int j:curClusterList) {
				Segment segment=segMap.get(j);
				startXSum+=segment.sp.getLng();
				endXSum+=segment.ep.getLng();
				startYsum+=segment.sp.getLat();
				endYSum+=segment.ep.getLat();
				velocitySum+=segment.distMeter()/segment.duration();
				angleSum+=angle(segment);	
				String startTime=segment.sp.getTime();
				String endTime=segment.ep.getTime();
				if (compare(startTime,MAX_START_TIME)>=0)
					MAX_START_TIME=startTime;
				if (compare(startTime, MIN_START_TIME)<=0) 
					MIN_START_TIME=startTime;
				if (compare(endTime, MAX_END_TIME)>=0) 
					MAX_END_TIME=endTime;
				if (compare(endTime, MIN_END_TIME)<=0) 
					MIN_END_TIME=endTime;	
			}
			int N=curClusterList.size();
			
			double avgAngle=angleSum/N;
			double avgVelocity=velocitySum/N;
			double avgStartX=startXSum/N;
			double avgStartY=startYsum/N;
			double avgEndX=endXSum/N;
			double avgEndY=endYSum/N;
			
			String resetStime=addInterval(MIN_END_TIME,MAX_START_TIME);
			String resetEtime=addInterval(MIN_END_TIME,MAX_END_TIME);
			
			Point startPoint=new Point(avgStartX, avgStartY, resetStime);
			Point endPoint=new Point(avgEndX, avgEndY, resetEtime);
			
			ClusterRepresent cRepresent=new ClusterRepresent(startPoint,endPoint,avgVelocity, avgAngle);
			crMap.put(i, cRepresent);
			startXSum=0;
		    endXSum=0;
		    startYsum=0;
		    endYSum=0;
			angleSum=0;
			velocitySum=0;		
		}
			
		return crMap;		
	}

	private static String addInterval(String mIN_END_TIME2, String mAX_START_TIME2) {
		Date date1 = new Date(0);
		Date date2 = new Date(0);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str1 = "2018-12-21 " + mIN_END_TIME2;
		String str2 = "2018-12-21 " + mAX_START_TIME2;
		double interval = 0;
		try {
			date1 = df.parse(str1);
			date2 = df.parse(str2);
			double start = date1.getTime();
			double end = date2.getTime();
			interval = (start - end)/(1000*60);
		} catch (ParseException e) {
			System.err.println("unparseable using" + df);
		}
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date1);
		calendar.add(Calendar.MINUTE, (int)interval);
		String time=df.format(calendar.getTime()).split(" ")[1];
		return time;
	}

	/**
	 * compare t to t0
	 * @param t current time
	 * @param t0 a fixed time
	 * @return
	 */
	private static int compare(String t, String t0) {
		Date Date1 = new Date(0);
		Date Date2 = new Date(0);

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str1 = "2018-12-21 " + t;
		String str2 = "2018-12-21 " + t0;
		int cmp=0;
		try {
			Date1 = (Date) df.parse(str1);
			Date2 = (Date) df.parse(str2);
			cmp=Date1.compareTo(Date2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cmp;
	}

	/**
	 * calculate the angle of between segment and a fixed vector
	 * @param segment
	 * @return
	 */
	private static double angle(Segment segment) {
		double y1=segment.sp.getLat();
		double y2=segment.ep.getLat();
		double xdif=segment.ep.getLng()-segment.sp.getLng();
		double ydif=segment.ep.getLat()-segment.sp.getLat();
		double angle=0;
		if(y1<=y2) {
			angle=Math.acos((xdif*1+ydif*0)/(1.0*segment.distKmeter()));
		}
		else {
			angle=2*Math.PI-Math.acos((xdif*1+ydif*0)/(1.0*segment.distKmeter()));
		}

		return angle;
	}

}
