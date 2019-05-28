package com.traclustering;

/**
 * A class of GPS Point
 * 
 * @author Tang
 *
 */
public class Point {
	// xCoordinate of a GPS point
	private double x;
	// yCoordinate of a GPS point
	private double y;
	// the time of recording the Point
	private String t;

	public Point(double x, double y, String t) {
		this.x = x;
		this.y = y;
		this.t = t;
	}

	public double getLng() {
		return x;
	}

	public void setLng(double x) {
		this.x = x;
	}

	public double getLat() {
		return y;
	}

	public void setLat(double y) {
		this.y = y;
	}

	public String getTime() {
		return t;
	}

	public void setTime(String t) {
		this.t = t;
	}

	/*
	 * the actual distance between two points in real world
	 */
	public double distMeter(Point that) {
		double xDist = this.x - that.x;
		double yDist = this.y - that.y;
		double dist = Math.sqrt(xDist * xDist + yDist * yDist) * 1000;
		return dist;
	}

	/*
	 * the distance between two points on the coordinate system
	 */
	public double distKmeter(Point that) {
		double xDist = this.x - that.x;
		double yDist = this.y - that.y;
		double dist = Math.sqrt(xDist * xDist + yDist * yDist);
		return dist;
	}
}
