package com.traclustering;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Segment implements Comparable<Segment> {
	// the mark of a segment
	private int id;
	// the start point of a segment
	public Point sp;
	// the end point of a segment
	public Point ep;

	public Segment(int id, Point sp, Point ep) {
		this.id=id;
		this.sp = sp;
		this.ep = ep;
	}

	/**
	 * the length of a segment(unit:m)
	 * 
	 * @return
	 */
	public double distMeter() {
		return this.sp.distMeter(ep);
	}

	/**
	 * the length of a segment(unit:km)
	 * 
	 * @return
	 */
	public double distKmeter() {
		return this.sp.distKmeter(ep);
	}

	/**
	 * the time interval of a segment(unit:seconds)
	 * 
	 * @return
	 */
	public double duration() {
		Date date1 = new Date(0);
		Date date2 = new Date(0);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str1 = "2018-12-21 " + this.sp.getTime();
		String str2 = "2018-12-21 " + this.ep.getTime();
		double interval = 0;
		try {
			date1 = df.parse(str1);
			date2 = df.parse(str2);
			double start = date1.getTime();
			double end = date2.getTime();
			interval = end - start;
		} catch (ParseException e) {
			System.out.println("unparseable using" + df);
		}
		return interval / 1000;
	}

	/**
	 * the velocity of a segment(unit:m/s)
	 * 
	 * @return
	 */
	public double velocity() {
		return this.distMeter() / this.duration();
	}

	/**
	 * compare the start time of a segment to another segment
	 */
	@Override
	public int compareTo(Segment that) {
		String t1 = this.sp.getTime();
		String t2 = that.sp.getTime();
		Date date1 = new Date(0);
		Date date2 = new Date(0);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str1 = "2018-12-21 " + t1;
		String str2 = "2018-12-21 " + t2;
		int cmp = 0;
		try {
			date1 = (Date) df.parse(str1);
			date2 = (Date) df.parse(str2);
			cmp = date1.compareTo(date2);
		} catch (ParseException e) {
			System.err.println("Unparseable using" + df);
		}
		return cmp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
