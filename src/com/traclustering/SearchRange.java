package com.traclustering;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SearchRange {
	private int left;
	private int right;
	
	public SearchRange() {}
	
	/**
	 * search the range for specified time 
	 * @param sortedSegments: sorted segments in chronological order 
	 * @param t1: start time
	 * @param t2: end time
	 */
	public void searchRange(List<Segment> sortedSegments, String t1, String t2) {
		String []t=new String[2];
		t=searchTime(t1,t2);
		setLeft(binarySearchLeftTime(t[0],sortedSegments));
		setRight(binarySearchRightTime(t[1],sortedSegments));
	}
	
	/**
	 * use binary search to find position of right time according specified time
	 * @param t: the specified time
	 * @param sortedSegments: sorted segments in chronological order 
	 * @return
	 */
	private int binarySearchRightTime(String t, List<Segment> sortedSegments) {
		int i=0,j=sortedSegments.size()-1;
		while(i<=j) {
			int mid=(i+j)/2;
			Segment segment=sortedSegments.get(mid);
			int cmp=compareTime(t,segment.ep.getTime());
			if (cmp<0) {
				j=mid-1;	
			}else if (cmp>0) {
				i=mid+1;
			}
			else {
				return mid;
			}		
	   }	
		return i-1;
	}

	/**
	 * use binary search to find position of left time according specified time
	 * @param t
	 * @param sortedSegments
	 * @return
	 */
	private int binarySearchLeftTime(String t, List<Segment> sortedSegments) {
		int i=0,j=sortedSegments.size()-1;
		while(i<=j) {
			int mid=(i+j)/2;
			Segment segment=sortedSegments.get(mid);
			int cmp=compareTime(t,segment.sp.getTime());
			if (cmp<0) {
				j=mid-1;	
			}else if (cmp>0) {
				i=mid+1;
			}
			else {
				return mid;
			}		
	   }	
		return j+1;
	}

	/**
	 * compare t1 to t2
	 * @param t1
	 * @param t2
	 * @return
	 */
	private int compareTime(String t1, String t2) {
		Date date1=new Date(0);
		Date date2=new Date(0);
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str="2018-12-21 "+t2;
		int cmp=0;
		try {
			date1=(Date) df.parse(t1);
			date2=(Date) df.parse(str);
			cmp=date1.compareTo(date2);
		} catch (ParseException e) {
			System.err.println("Unparseable using"+df);
		}
		return cmp;
	}
	
	private String[] searchTime(String t1, String t2) {
		String []time=new String[2];
		Date date1 = new Date(0);
		Date date2 = new Date(0);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str1 = "2018-12-21 " + t1;
		String str2 = "2018-12-21 " + t2;
		try {
			date1 = df.parse(str1);
			date2 = df.parse(str2);
			Calendar calendar1=Calendar.getInstance();
			Calendar calendar2=Calendar.getInstance();
			calendar1.setTime(date1);
			calendar2.setTime(date2);
			calendar1.add(Calendar.MINUTE, -30);
			calendar2.add(Calendar.MINUTE, 30);
			time[0]=df.format(calendar1.getTime());
			time[1]=df.format(calendar2.getTime());	
		} catch (ParseException e) {
			System.out.println("unparseable using" + df);
		}
		return time;
	}
	public int getRight() {
		return right;
	}
	public void setRight(int right) {
		this.right = right;
	}
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}

}
