package com.traclustering;

public class ClusterRepresent {
	// 
	private Point startPoint = null;
	private Point endPoint = null;
	private double avgVelocity;
	private double avgAngle;

	public ClusterRepresent(Point startPoint, Point endPoint, double avgVelocity, double avgAngle) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.avgVelocity = avgVelocity;
		this.avgAngle = avgAngle;
	}

	public Point getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Point startPoint) {
		this.startPoint = startPoint;
	}

	public Point getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Point endPoint) {
		this.endPoint = endPoint;
	}

	public double getAvgVelocity() {
		return avgVelocity;
	}

	public void setAvgVelocity(double avgVelocity) {
		this.avgVelocity = avgVelocity;
	}

	public double getAvgAngle() {
		return avgAngle;
	}

	public void setAvgAngle(double avgAngle) {
		this.avgAngle = avgAngle;
	}

}
